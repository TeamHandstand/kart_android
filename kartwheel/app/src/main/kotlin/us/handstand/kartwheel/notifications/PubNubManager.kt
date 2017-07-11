package us.handstand.kartwheel.notifications

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsAddChannelResult
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsRemoveChannelResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.squareup.sqlbrite.BriteDatabase
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.AlertManager
import java.util.concurrent.CountDownLatch


object PubNubManager : SubscribeCallback() {
    val TAG = PubNubManager::class.java.simpleName!!
    val pub: PubNub
    var db: BriteDatabase? = null

    enum class PubNubChannelType {
        userChannel, eventChannel, raceChannel, raceRoomChannel, miniGameRoomChannel
    }

    init {
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = BuildConfig.PUBNUB_SUBSCRIPTION_KEY
        pnConfiguration.publishKey = BuildConfig.PUBNUB_PUBLISH_KEY
        pnConfiguration.isSecure = true
        pub = PubNub(pnConfiguration)
        pub.addListener(this)
    }

    fun setup(db: BriteDatabase?) {
        PubNubManager.db = db
        pub.subscribe()
                .channelGroups(mutableListOf(Storage.pubNubChannelGroup))
                .execute()
        pub.addChannelsToChannelGroup()
                .channelGroup(Storage.pubNubChannelGroup)
                .channels(mutableListOf(
                        channelForType(PubNubChannelType.userChannel, Storage.userId),
                        channelForType(PubNubChannelType.eventChannel, Storage.eventId)
                ))
                .async(object : PNCallback<PNChannelGroupsAddChannelResult>() {
                    override fun onResponse(result: PNChannelGroupsAddChannelResult?, status: PNStatus?) {
                        // TODO
                        /*
                        pub.addPushNotificationsOnChannels()
                                .deviceId(Storage.fcmToken)
                                .pushType(PNPushType.GCM)
                                .channels(mutableListOf(
                                        channelForType(PubNubChannelType.userChannel, Storage.userId)
                                ))
                         */
                    }
                })
    }

    fun tearDown() {
        pub.unsubscribeAll()
    }

    fun subscribe(channelType: PubNubChannelType, id: String) {
        pub.addChannelsToChannelGroup()
                .channelGroup(Storage.pubNubChannelGroup)
                .channels(mutableListOf(channelForType(channelType, id)))
                .async(object : PNCallback<PNChannelGroupsAddChannelResult>() {
                    override fun onResponse(result: PNChannelGroupsAddChannelResult?, status: PNStatus?) {
                        Log.e(TAG, "Channels ${status?.affectedChannels} status: ${status?.statusCode}")
                    }
                })
    }

    fun unsubscribe(channelType: PubNubChannelType, id: String) {
        pub.removeChannelsFromChannelGroup()
                .channelGroup(Storage.pubNubChannelGroup)
                .channels(mutableListOf(channelForType(channelType, id)))
                .async(object : PNCallback<PNChannelGroupsRemoveChannelResult>() {
                    override fun onResponse(result: PNChannelGroupsRemoveChannelResult?, status: PNStatus?) {
                        Log.e(TAG, "Channel removed: ${status?.affectedChannels}")
                    }
                })
    }

    override fun status(pubNub: PubNub, status: PNStatus) {
        Log.e(TAG, "PubNub status category: ${status.category.ordinal}")
    }

    override fun presence(pubNub: PubNub, presence: PNPresenceEventResult) {
        Log.e(TAG, "PubNub presence state: ${presence.state.asString}")
    }

    override fun message(pubNub: PubNub, message: PNMessageResult) {
        Log.e(TAG, "message: ${message.message}")
        parsePubNubMessage(API.gson.fromJson(message.message.asString, JsonObject::class.java), message.channel)
    }

    fun parsePubNubMessage(results: JsonObject, channel: String) {
        parsePubArray(results["userRaceInfos"], Array<UserRaceInfo>::class.java, channel)
        parsePubArray(results["itemZones"], Array<ItemZone>::class.java, channel)
        parsePubArray(results["droppedItems"], Array<DroppedItem>::class.java, channel)
        parsePubArray(results["races"], Array<Race>::class.java, channel)
        parsePubArray(results["events"], Array<Event>::class.java, channel, notify = true)

        showAlert(results["raceLogEvent"])
    }

    fun showAlert(raceEventJson: Any?) {
        if (raceEventJson is String) {
            val raceEvent = API.gson.fromJson(raceEventJson, RaceEvent::class.java)
            AlertManager.showAlert(raceEvent.message(), raceEvent.vibrate(), raceEvent.type() == "USER_PICKUP_ITEM", raceEvent.soundName())
            if (raceEvent.type() == "USER_IN_ITEM_ZONE_NO_ITEM") {
                // TODO: NotificationCenter.default.post(name:.userMissedItemZone, object: nil)
            }
        }
    }

    fun <T : Insertable> parsePubArray(results: Any?, type: Class<Array<T>>, channel: String, notify: Boolean = false) {
        if (results is JsonArray) {
            db?.newTransaction()?.use {
                val array = API.gson.fromJson(results, type)
                val latch = CountDownLatch(array.size)
                array.forEach { it.update(db, channel, latch) }
                latch.await()
                it.markSuccessful()
            }
            if (notify) {
                // TODO: NotificationCenter.default.post(name:.newEventInfoReceived, object: nil)
            }
        }
    }

    fun channelForType(channelType: PubNubChannelType, id: String): String {
        when (channelType) {
            PubNubChannelType.userChannel -> return "user-${id.toLowerCase()}"
            PubNubChannelType.eventChannel -> return "event-${id.toLowerCase()}"
            PubNubChannelType.raceChannel -> return "race-${id.toLowerCase()}"
            PubNubChannelType.raceRoomChannel -> return "race-room-${id.toLowerCase()}"
            PubNubChannelType.miniGameRoomChannel -> return "mini-game-room-${id.toLowerCase()}"
        }
    }

    fun idFromChannel(channelString: String?): String? {
        return channelString
                ?.removePrefix("race-room-")
                ?.removePrefix("race-")
                ?.removePrefix("user-")
                ?.removePrefix("event-")
                ?.removePrefix("mini-game-room-")
    }


}

package us.handstand.kartwheel.controller

import com.squareup.sqlbrite.BriteDatabase
import rx.Subscription
import us.handstand.kartwheel.model.*
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.notifications.PubNubManager

interface RaceSignUpListener {
    fun onParticipantsUpdated(participantImageUrls: List<String>)
    fun onRaceUpdated(race: Race)
    fun onTopThreeUpdated(topThree: List<User>)
}

class RaceSignUpController(val db: BriteDatabase?, val eventId: String, val raceId: String, val listener: RaceSignUpListener) {

    private var raceSubscription: Subscription? = null
    private var registrantSubscription: Subscription? = null
    private var topThreeSubscription: Subscription? = null
    var race: Race? = null
    var userInRace = false

    fun subscribe() {
        // Listen for Race updates
        val raceQuery = Race.FACTORY.select_for_id(raceId)
        raceSubscription = db?.createQuery(RaceModel.TABLE_NAME, raceQuery.statement, *raceQuery.args)
                ?.mapToOne { Race.FACTORY.select_for_idMapper().map(it) }
                ?.subscribe {
                    this.race = it
                    listener.onRaceUpdated(it)
                }
        // Listen for registrant updates
        val registrantQuery = User.FACTORY.select_for_race_id(raceId)
        registrantSubscription = db?.createQuery(UserModel.TABLE_NAME, registrantQuery.statement, *registrantQuery.args)
                ?.mapToList { User.FACTORY.select_for_race_idMapper().map(it) }
                ?.subscribe { onRegistrantsUpdated(it) }
        // Listen for the top three registrants for this race
        val topThreeQuery = User.FACTORY.select_top_three_for_race(raceId)
        topThreeSubscription = db?.createQuery(UserModel.TABLE_NAME, topThreeQuery.statement, *topThreeQuery.args)
                ?.mapToList { User.FACTORY.select_top_three_for_raceMapper().map(it) }
                ?.subscribe { listener.onTopThreeUpdated(it) }
        API.getRaceParticipants(eventId, raceId)
        API.getUserRaceInfos(eventId, raceId) {
            it?.forEach { User.updateRaceId(db, it.userId(), it.raceId()) }
        }
        // TODO: API.getTopCourseTimes(eventId, raceId)
        PubNubManager.subscribe(PubNubManager.PubNubChannelType.raceRoomChannel, raceId)
    }

    fun unsubscribe() {
        raceSubscription?.unsubscribe()
        registrantSubscription?.unsubscribe()
        topThreeSubscription?.unsubscribe()
    }

    private fun onRegistrantsUpdated(users: List<User>) {
        val imageUrls = mutableListOf<String>()
        val userId = Storage.userId
        userInRace = false
        for (user in users) {
            if (user.imageUrl() != null) {
                imageUrls.add(user.imageUrl()!!)
            }
            if (user.id() == userId) {
                userInRace = true
            }
        }
        listener.onParticipantsUpdated(imageUrls)
    }
}
package us.handstand.kartwheel.network

import com.pubnub.api.PubNub
import com.pubnub.api.PNConfiguration
import us.handstand.kartwheel.BuildConfig


class PubNub {
    val pub: com.pubnub.api.PubNub

    init {
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = BuildConfig.PUBNUB_SUBSCRIPTION_KEY
        pnConfiguration.isSecure = false
        pub = com.pubnub.api.PubNub(pnConfiguration)
    }
}

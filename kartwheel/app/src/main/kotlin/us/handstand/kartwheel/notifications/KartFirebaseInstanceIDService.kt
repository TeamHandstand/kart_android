package us.handstand.kartwheel.notifications

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import us.handstand.kartwheel.model.Storage


class KartFirebaseInstanceIDService : FirebaseInstanceIdService() {
    val TAG = KartFirebaseInstanceIDService::class.java.simpleName
    override fun onTokenRefresh() {
        super.onTokenRefresh()
//        Storage.fcmToken = FirebaseInstanceId.getInstance().token ?: ""
//        Log.e(TAG, "Token: ${Storage.fcmToken}")
        // TODO: Send registration to server (I don't need this)
    }
}
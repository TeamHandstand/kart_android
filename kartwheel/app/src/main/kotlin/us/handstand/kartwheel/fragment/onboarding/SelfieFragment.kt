package us.handstand.kartwheel.fragment.onboarding

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonObject
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.network.storage.TransferListener
import us.handstand.kartwheel.network.storage.TransferObserver
import us.handstand.kartwheel.network.storage.TransferState
import us.handstand.kartwheel.util.Photos
import java.lang.Exception

class SelfieFragment : Fragment(), OnboardingActivity.OnboardingFragment, TransferListener {
    lateinit var selfie: CircularImageView
    val INVALID_TRANSFER_ID = -1;

    var transferObserver: TransferObserver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        selfie = ViewUtil.findView(fragment, R.id.image)
        selfie.setOnClickListener { Photos.takeSelfie(this) }
        selfie.setImageUrl(Storage.userImageUrl, BuildConfig.PLAYER_NO_IMAGE_URL, R.drawable.onboarding_camera)
        return fragment
    }

    override fun onResume() {
        super.onResume()
        if (Storage.selfieTransferId != INVALID_TRANSFER_ID) {
            transferObserver = API.storageProvider.getTransferById(Storage.selfieTransferId)
        }
        transferObserver?.setTransferListener(this)
    }

    override fun onPause() {
        super.onPause()
        transferObserver?.cleanTransferListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Photos.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Wait for the user to click the advance button before uploading
            val uri = Uri.parse(Storage.selfieUri)
            activity.revokeUriPermission(uri, Photos.requestPermissions)
            selfie.setImageUri(uri)
        }
    }

    /**
     * @return true is we started the uploadPhoto, or if the uploadPhoto is in progress
     */
    fun startUpload(): Boolean {
        // No image taken
        if (isEmpty(Storage.selfieUri) && isEmpty(Storage.userImageUrl)) {
            Toast.makeText(activity, R.string.need_selfie, LENGTH_LONG).show()
            return false
        }
        if (uploadInProgress()) {
            Toast.makeText(activity, R.string.wait_for_selfie_upload, LENGTH_LONG).show()
        } else {
            // Don't let user take another photo until uploadPhoto finishes
            selfie.isEnabled = false
            // Start new uploadPhoto
            Photos.executor.execute {
                transferObserver = API.storageProvider.uploadPhoto(Uri.parse(Storage.selfieUri), activity)
                transferObserver?.setTransferListener(this)
            }
        }
        return true
    }

    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        val progress = (bytesCurrent.toDouble() * 100 / bytesTotal).toInt()
        // TODO: Progress UI
    }

    override fun onStateChanged(id: Int, state: TransferState) {
        when (state) {
            TransferState.COMPLETED -> {
                // Update the User object on the server with the imageUrl from our selfie uploadPhoto
                API.updateUser(API.gson.fromJson("{\"imageUrl\":\"${BuildConfig.AWS_BUCKET_URL + "/user-profile-pictures/" + transferObserver?.key}\"}", JsonObject::class.java), object : API.APICallback<User> {
                    override fun onSuccess(response: User) {
                        Storage.userImageUrl = response.imageUrl()!!
                        activity.runOnUiThread { controller.onStepCompleted(SELFIE) }
                    }
                })
            }
            TransferState.FAILED, TransferState.CANCELED -> {
                activity.runOnUiThread {
                    Toast.makeText(activity, R.string.selfie_upload_failed, Toast.LENGTH_LONG).show()
                    selfie.isEnabled = true
                    activity.findViewById(R.id.button).isEnabled = true
                    transferObserver = null
                }
            }
        }
    }

    override fun onError(id: Int, ex: Exception?) {
        Crashlytics.logException(ex)
        Toast.makeText(activity, R.string.selfie_upload_failed, Toast.LENGTH_LONG).show()
    }

    private fun uploadInProgress(): Boolean {
        return !isEmpty(Storage.selfieUri) && transferObserver != null && transferObserver?.bytesTotal != transferObserver?.bytesTransferred
    }

    override fun readyForNextStep(): Boolean {
        return !isEmpty(Storage.userImageUrl)
    }
}

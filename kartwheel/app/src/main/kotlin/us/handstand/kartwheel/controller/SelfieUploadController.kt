package us.handstand.kartwheel.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils.isEmpty
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonObject
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.R
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.network.storage.TransferListener
import us.handstand.kartwheel.network.storage.TransferObserver
import us.handstand.kartwheel.network.storage.TransferState
import us.handstand.kartwheel.util.Photos
import javax.inject.Inject


open class SelfieUploadController @Inject constructor(val context: Context) : TransferListener {
    open var listener: SelfieUploadControllerListener? = null
    private val INVALID_TRANSFER_ID = -1;
    private var transferObserver: TransferObserver? = null

    interface SelfieUploadControllerListener {
        fun onStateChanged(state: TransferState)
        fun showMessage(stringResId: Int)
    }

    fun onResume() {
        if (Storage.selfieTransferId != INVALID_TRANSFER_ID) {
            transferObserver = API.storageProvider.getTransferById(Storage.selfieTransferId)
        }
        transferObserver?.setTransferListener(this)
        // TODO: Should we post the state of the transfer when we set the TransferListener?
    }

    fun onPause() {
        transferObserver?.cleanTransferListener()
    }

    fun onCameraResult(requestCode: Int, resultCode: Int, data: Intent?): Uri? {
        if (requestCode == Photos.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Wait for the user to click the advance button before uploading
            val uri = Uri.parse(Storage.selfieUri)
            context.revokeUriPermission(uri, Photos.requestPermissions)
            return uri
        }
        return null
    }

    fun upload() {
        // No image taken
        if (isEmpty(Storage.selfieUri) && isEmpty(Storage.userImageUrl)) {
            listener?.showMessage(R.string.need_selfie)
            listener?.onStateChanged(TransferState.FAILED)
        }
        if (uploadInProgress()) {
            listener?.showMessage(R.string.wait_for_selfie_upload)
            listener?.onStateChanged(TransferState.PART_COMPLETED)
        } else {
            // Image already exists, let the user continue to next step
            if (isEmpty(Storage.selfieUri)) {
                listener?.onStateChanged(TransferState.COMPLETED)
            } else {
                // Upload photo to StorageProvider
                Photos.executor.execute {
                    transferObserver = API.storageProvider.uploadPhoto(Uri.parse(Storage.selfieUri), context)
                    transferObserver?.setTransferListener(this)
                }
                listener?.onStateChanged(TransferState.IN_PROGRESS)
            }
        }
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
                        listener?.onStateChanged(TransferState.COMPLETED)
                    }
                })
            }
            TransferState.FAILED, TransferState.CANCELED -> {
                transferObserver = null
                listener?.showMessage(R.string.selfie_upload_failed)
                listener?.onStateChanged(TransferState.FAILED)
            }
            else -> listener?.onStateChanged(state)
        }
    }

    override fun onError(id: Int, ex: Exception?) {
        Crashlytics.logException(ex)
        listener?.showMessage(R.string.selfie_upload_failed)
    }

    private fun uploadInProgress(): Boolean {
        return !isEmpty(Storage.selfieUri) && transferObserver != null && transferObserver?.bytesTotal != transferObserver?.bytesTransferred
    }
}
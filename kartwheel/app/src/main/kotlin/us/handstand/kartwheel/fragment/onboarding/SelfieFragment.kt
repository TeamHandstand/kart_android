package us.handstand.kartwheel.fragment.onboarding

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.SelfieUploadController
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.network.storage.TransferState
import us.handstand.kartwheel.util.Photos
import javax.inject.Inject

class SelfieFragment : Fragment(), OnboardingActivity.OnboardingFragment, SelfieUploadController.SelfieUploadControllerListener {
    lateinit var selfie: CircularImageView
    @Inject lateinit var selfieController: SelfieUploadController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        KartWheel.injector.inject(this)
        selfieController.listener = this
        selfie = ViewUtil.findView(fragment, R.id.image)
        selfie.setOnClickListener { Photos.takeSelfie(this) }
        selfie.setImageUrl(Storage.userImageUrl, BuildConfig.PLAYER_NO_IMAGE_URL, R.drawable.onboarding_camera)
        return fragment
    }

    override fun onResume() {
        super.onResume()
        selfieController.onResume()
    }

    override fun onPause() {
        super.onPause()
        selfieController.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val uri = selfieController.onCameraResult(requestCode, resultCode, data)
        if (uri != null) {
            selfie.setImageUri(uri)
        } else {
            selfie.setImageUrl(Storage.userImageUrl, placeholder = R.drawable.onboarding_camera)
        }
        updateOnboardingState()
    }

    override fun readyForNextStep(): Boolean {
        return !isEmpty(Storage.userImageUrl) || !isEmpty(Storage.selfieUri)
    }

    override fun onStateChanged(state: TransferState) {
        activity.runOnUiThread {
            when (state) {
                TransferState.FAILED, TransferState.CANCELED -> {
                    button.isEnabled = true
                    selfie.isEnabled = true
                }
                TransferState.IN_PROGRESS, TransferState.PART_COMPLETED -> {
                    button.isEnabled = false
                    selfie.isEnabled = false
                }
                TransferState.COMPLETED -> controller.onStepCompleted(OnboardingController.SELFIE)
            }
        }
    }

    override fun showMessage(stringResId: Int) {
        activity.runOnUiThread { Toast.makeText(activity, stringResId, Toast.LENGTH_LONG).show() }
    }
}

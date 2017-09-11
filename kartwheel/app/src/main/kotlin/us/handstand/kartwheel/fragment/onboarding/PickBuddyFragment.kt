package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.*
import android.support.v4.app.Fragment
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.PickBuddyAdapter
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API
import us.handstand.kartwheel.util.SnackbarUtil

class PickBuddyFragment : Fragment(), OnboardingActivity.OnboardingFragment, View.OnClickListener {
    lateinit var buddy: CircularImageView
    lateinit var adapter: PickBuddyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_onboarding_pick_buddy, container, false) as ViewGroup
        buddy = ViewUtil.findView(fragment, R.id.image)
        buddy.setOnClickListener(this)
        buddy.setImageUrl(Storage.userBuddyUrl, placeholder = R.drawable.buddy_placeholder)
        pickBuddyBehaviorCallback.delegate = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Set the emoji icon to the one that the user has selected
                if ((newState == STATE_COLLAPSED || newState == STATE_HIDDEN) && !isEmpty(Storage.selectedBuddyUrl)) {
                    updateOnboardingState()
                    buddy.setImageUrl(Storage.selectedBuddyUrl, placeholder = R.drawable.buddy_placeholder, crop = false)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
        return fragment
    }

    /**
     * @return true if uploadPhoto started or is ongoing.
     */
    fun uploadBuddyEmoji(): Boolean {
        if (isEmpty(Storage.selectedBuddyUrl)) {
            SnackbarUtil.show(activity, R.string.need_buddy)
            return false
        } else {
            // Upload the selectedBuddyUrl and move onto the next step, once the uploadPhoto is completed.
            // If not completed, then show an error message
            buddy.isEnabled = false
            API.updateUser(API.gson.fromJson("{\"buddyUrl\":\"${Storage.selectedBuddyUrl}\"}", JsonObject::class.java), object : API.APICallback<User> {
                override fun onSuccess(response: User) {
                    Storage.userBuddyUrl = response.buddyUrl()!!
                    Storage.selectedBuddyUrl = ""
                    controller.onStepCompleted(PICK_BUDDY)
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    super.onFailure(errorCode, errorResponse)
                    activity.runOnUiThread {
                        button.isEnabled = true
                        buddy.isEnabled = true
                    }
                    SnackbarUtil.show(activity, R.string.buddy_upload_failed)
                }
            })
            return true
        }
    }

    // Toggle the BottomSheet's visibility whenever the emoji icon is clicked
    override fun onClick(v: View?) {
        recyclerViewBehavior.state = if (recyclerViewBehavior.state == STATE_EXPANDED) STATE_HIDDEN else STATE_EXPANDED
    }

    override fun readyForNextStep(): Boolean {
        return !isEmpty(Storage.userBuddyUrl) || !isEmpty(Storage.selectedBuddyUrl)
    }
}

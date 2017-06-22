package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.JsonObject
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.layout.recyclerview.adapter.EmojiAdapter
import us.handstand.kartwheel.model.Storage
import us.handstand.kartwheel.model.User
import us.handstand.kartwheel.network.API

class EmojiFragment : Fragment(), OnboardingActivity.OnboardingFragment, View.OnClickListener {
    lateinit var emoji: CircularImageView
    lateinit var adapter: EmojiAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        emoji = ViewUtil.findView(fragment, R.id.image)
        emoji.setOnClickListener(this)
        emoji.setImageUrl(Storage.userBuddyUrl, BuildConfig.DEFAULT_BUDDY_URL, R.drawable.buddy_placeholder)
        adapter = EmojiAdapter()
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Set the emoji icon to the one that the user has selected
                if (newState == STATE_COLLAPSED && adapter.selectedEmojiUrl != null) {
                    emoji.setImageUrl(adapter.selectedEmojiUrl, placeholder = R.drawable.buddy_placeholder)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        val recyclerView = (activity.findViewById(R.id.bottomSheet) as RecyclerView)
        recyclerView.adapter = adapter
    }

    /**
     * @return true if uploadPhoto started or is ongoing.
     */
    fun uploadBuddyEmoji(): Boolean {
        if (adapter.selectedEmojiUrl == null) {
            activity.runOnUiThread { Toast.makeText(activity, R.string.need_buddy, Toast.LENGTH_LONG).show() }
            return false
        } else {
            // Upload the buddyUrl and move onto the next step, once the uploadPhoto is completed.
            // If not completed, then show an error message
            API.updateUser(API.gson.fromJson("{\"buddyUrl\":\"${adapter.selectedEmojiUrl}\"}", JsonObject::class.java), object : API.APICallback<User> {
                override fun onSuccess(response: User) {
                    Storage.userBuddyUrl = response.buddyUrl()!!
                    controller.onStepCompleted(PICK_BUDDY)
                }

                override fun onFailure(errorCode: Int, errorResponse: String) {
                    super.onFailure(errorCode, errorResponse)
                    activity.runOnUiThread { Toast.makeText(activity, R.string.buddy_upload_failed, Toast.LENGTH_LONG).show() }
                }
            })
            return true
        }
    }

    // Toggle the BottomSheet's visibility whenever the emoji icon is clicked
    override fun onClick(v: View?) {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state == STATE_EXPANDED) STATE_COLLAPSED else STATE_EXPANDED
    }

    override fun readyForNextStep(): Boolean {
        return !isEmpty(Storage.userBuddyUrl)
    }
}

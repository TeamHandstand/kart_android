package us.handstand.kartwheel.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import us.handstand.kartwheel.KartWheel
import us.handstand.kartwheel.R
import us.handstand.kartwheel.controller.OnboardingController
import us.handstand.kartwheel.controller.OnboardingController.Companion.BUDDY_EXPLANATION
import us.handstand.kartwheel.controller.OnboardingController.Companion.ERROR
import us.handstand.kartwheel.controller.OnboardingController.Companion.NONE
import us.handstand.kartwheel.controller.OnboardingController.Companion.PICK_BUDDY
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
import us.handstand.kartwheel.controller.OnboardingController.Companion.VIDEO
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage


class OnboardingActivity : AppCompatActivity(), View.OnClickListener, OnboardingController.Companion.OnboardingStepCompletionListener {
    lateinit var title: TextView
    lateinit var description: TextView
    lateinit var pageNumber: TextView
    lateinit var imageContainer: View
    lateinit var image: ImageView
    lateinit var imageDescription: TextView
    lateinit var emojiRecyclerView: RecyclerView
    lateinit var button: Button
    lateinit var makeItRainText: TextView

    val controller = OnboardingController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        title = ViewUtil.findView(this, R.id.title)
        description = ViewUtil.findView(this, R.id.description)
        pageNumber = ViewUtil.findView(this, R.id.pageNumber)
        imageContainer = ViewUtil.findView(this, R.id.imageContainer)
        image = ViewUtil.findView(this, R.id.image)
        imageDescription = ViewUtil.findView(this, R.id.imageText)
        emojiRecyclerView = ViewUtil.findView(this, R.id.emojiRecyclerView)
        button = ViewUtil.findView(this, R.id.button)
        makeItRainText = ViewUtil.findView(this, R.id.makeItRainDescription)

        button.setOnClickListener(this)
        image.setOnClickListener(this)

        controller.transition(NONE, Storage.lastOnboardingState)
    }

    override fun showNextStep(previous: Long, next: Long) {
        // Make sure that we're starting with fresh data.
        if (next == ERROR) {
            KartWheel.logout()
        }
        pageNumber.text = next.toString() + " of 5"
        var pageNumberVisibility = VISIBLE
        var emojiRecyclerViewVisibility = GONE
        var makeItRainVisibility = INVISIBLE
        var imageContainerVisibility = VISIBLE
        var imageTextVisibility = GONE
        when (next) {
            STARTED -> {
                title.text = resources.getString(R.string.onboarding_started_title)
                description.text = resources.getString(R.string.onboarding_started_description)
                button.text = resources.getString(R.string.onboarding_started_button)
                emojiRecyclerViewVisibility = VISIBLE
                pageNumberVisibility = INVISIBLE
                imageContainerVisibility = GONE
            }

            SELFIE -> {
                title.text = resources.getString(R.string.onboarding_selfie_title)
                description.text = resources.getString(R.string.onboarding_selfie_description)
                button.text = resources.getString(R.string.onboarding_selfie_button)
                imageTextVisibility = VISIBLE
            }

            PICK_BUDDY -> {
                title.text = resources.getString(R.string.onboarding_pick_buddy_title)
                description.text = resources.getString(R.string.onboarding_pick_buddy_description)
                button.text = resources.getString(R.string.onboarding_pick_buddy_button)
            }

            BUDDY_EXPLANATION -> {
                title.text = resources.getString(R.string.onboarding_buddy_explanation_title)
                description.text = resources.getString(R.string.onboarding_buddy_explanation_description)
                button.text = resources.getString(R.string.onboarding_buddy_explanation_button)
            }

            POINT_SYSTEM -> {
                title.text = resources.getString(R.string.onboarding_points_title)
                description.text = resources.getString(R.string.onboarding_points_description)
                button.text = resources.getString(R.string.onboarding_points_button)
                makeItRainVisibility = VISIBLE
            }

            VIDEO -> {
                title.text = resources.getString(R.string.onboarding_video_title)
                description.text = resources.getString(R.string.onboarding_video_description)
                button.text = resources.getString(R.string.onboarding_video_button)
            }
        }
        emojiRecyclerView.visibility = emojiRecyclerViewVisibility
        makeItRainText.visibility = makeItRainVisibility
        imageContainer.visibility = imageContainerVisibility
        imageDescription.visibility = imageTextVisibility
        pageNumber.visibility = pageNumberVisibility

        Storage.lastOnboardingState = next
    }

    override fun onClick(v: View) {
        // TODO: Enable/Disable button based on state of selfie/buddy upload
        if (v.isEnabled && v.id == R.id.button) {
            controller.onStepCompleted(Storage.lastOnboardingState)
        }
    }

    override fun showDialog(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOnboardingFragmentStateChanged() {
        // TODO: Do I need this, since we're not using fragments?
    }
}

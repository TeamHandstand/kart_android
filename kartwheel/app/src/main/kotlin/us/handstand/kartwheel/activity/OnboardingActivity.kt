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
import us.handstand.kartwheel.controller.OnboardingController.Companion.ERROR
import us.handstand.kartwheel.controller.OnboardingController.Companion.NONE
import us.handstand.kartwheel.controller.OnboardingController.Companion.POINT_SYSTEM
import us.handstand.kartwheel.controller.OnboardingController.Companion.SELFIE
import us.handstand.kartwheel.controller.OnboardingController.Companion.STARTED
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
        var pageNumberVisibility = VISIBLE
        var emojiRecyclerViewVisibility = GONE
        var makeItRainVisibility = INVISIBLE
        var imageContainerVisibility = VISIBLE
        var imageTextVisibility = GONE
        when (next) {
            STARTED -> {
                emojiRecyclerViewVisibility = VISIBLE
                pageNumberVisibility = INVISIBLE
                imageContainerVisibility = GONE
            }
            SELFIE -> imageTextVisibility = VISIBLE
            POINT_SYSTEM -> makeItRainVisibility = VISIBLE
        }
        title.text = resources.getString(OnboardingController.getTitleStringResIdForStep(next))
        description.text = resources.getString(OnboardingController.getDescriptionStringResIdForStep(next))
        button.text = resources.getString(OnboardingController.getButtonStringResIdForStep(next))
        pageNumber.text = next.toString() + " of 5"
        pageNumber.visibility = pageNumberVisibility
        emojiRecyclerView.visibility = emojiRecyclerViewVisibility
        makeItRainText.visibility = makeItRainVisibility
        val imageResId = OnboardingController.getImageResIdForStep(next)
        if (imageResId == -1) {
            imageContainer.visibility = INVISIBLE
        } else {
            imageContainer.visibility = VISIBLE
            image.setImageResource(imageResId)
        }
        imageDescription.visibility = imageTextVisibility

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

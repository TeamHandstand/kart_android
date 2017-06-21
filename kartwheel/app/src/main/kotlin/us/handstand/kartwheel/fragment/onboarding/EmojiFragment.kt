package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.BuildConfig
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil
import us.handstand.kartwheel.model.Storage

class EmojiFragment : Fragment(), OnboardingActivity.OnboardingFragment, View.OnClickListener {
    lateinit var emoji: CircularImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        emoji = ViewUtil.findView(fragment, R.id.image)
        emoji.setOnClickListener(this)
        emoji.setImageUrl(Storage.userBuddyUrl, default = BuildConfig.DEFAULT_BUDDY_URL)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

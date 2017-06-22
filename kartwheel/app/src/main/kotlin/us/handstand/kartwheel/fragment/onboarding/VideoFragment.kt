package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.layout.ViewUtil

class VideoFragment : Fragment(), OnboardingActivity.OnboardingFragment, View.OnClickListener {
    lateinit var video: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        video = ViewUtil.findView(fragment, R.id.image)
        video.setImageResource(R.drawable.onboarding_play_button)
        video.setOnClickListener(this)
        return fragment
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

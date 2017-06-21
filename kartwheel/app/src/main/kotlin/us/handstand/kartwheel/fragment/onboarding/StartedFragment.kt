package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity

class StartedFragment : Fragment(), OnboardingActivity.OnboardingFragment {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboarding_emoji, container, false)
    }

    override fun readyForNextStep(): Boolean {
        return true
    }
}

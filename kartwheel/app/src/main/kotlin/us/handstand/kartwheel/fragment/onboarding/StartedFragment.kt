package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity

class StartedFragment : Fragment(), OnboardingActivity.OnboardingFragment {
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recyclerView = inflater.inflate(R.layout.fragment_onboarding_started, container, false) as RecyclerView
        return recyclerView
    }

    override fun onResume() {
        super.onResume()
        recyclerView.scrollToPosition(0)
        recyclerView.smoothScrollToPosition(Int.MAX_VALUE - 1)
    }

    override fun onPause() {
        super.onPause()
        recyclerView.stopScroll()
    }

    override fun readyForNextStep(): Boolean {
        return true
    }
}

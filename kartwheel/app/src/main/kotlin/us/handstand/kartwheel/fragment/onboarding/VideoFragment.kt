package us.handstand.kartwheel.fragment.onboarding

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil

class VideoFragment : Fragment(), OnboardingActivity.OnboardingFragment, View.OnClickListener {
    lateinit var video: CircularImageView
    lateinit var layout: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        layout.findViewById(R.id.imageText).visibility = View.GONE
        video = ViewUtil.findView(layout, R.id.image)
        video.setImageResource(R.drawable.onboarding_play_button, R.drawable.onboarding_play_button)
        video.setOnClickListener(this)
        return layout
    }

    override fun onClick(v: View?) {
//        val exoPlayerView = LayoutInflater.from(activity).inflate(R.layout.layout_onboarding_video_player, layout, false) as SimpleExoPlayerView
        val handler = Handler()

        // Prepare ExoPlayer
//        val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter()))
//        val exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
//        exoPlayerView.player = exoPlayer

//         Prepare media
//        val dataSourceFactory = DefaultDataSourceFactory(activity, "todo-user-agent")
//        val videoSource = ExtractorMediaSource(Uri.parse("android.resource://" + activity.packageName + "/" + R.raw.onboarding_video), dataSourceFactory, DefaultExtractorsFactory(), null, null)
//        exoPlayer.prepare(videoSource)
    }
}

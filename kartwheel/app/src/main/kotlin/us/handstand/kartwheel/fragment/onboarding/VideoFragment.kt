package us.handstand.kartwheel.fragment.onboarding

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil


class VideoFragment : Fragment(), OnboardingActivity.OnboardingFragment {
    private lateinit var video: CircularImageView
    private lateinit var layout: ViewGroup
    private lateinit var exoPlayerView: SimpleExoPlayerView
    private var resumeWindow: Int = 0
    private var resumePosition: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        layout.findViewById(R.id.imageText).visibility = View.GONE
        video = ViewUtil.findView(layout, R.id.image)
        video.setImageResource(R.drawable.onboarding_play_button, R.drawable.onboarding_play_button)
        video.setOnClickListener { videoBehavior.state = BottomSheetBehavior.STATE_EXPANDED }
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        exoPlayerView = ViewUtil.findView(activity, R.id.video)
        videoBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Set the emoji icon to the one that the user has selected
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    releasePlayer()
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    preparePlayer()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    override fun onPause() {
        videoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        releasePlayer()
        super.onPause()
    }

    private fun preparePlayer() {
        // Prepare ExoPlayer
        if (exoPlayerView.player == null) {
            exoPlayerView.setControlDispatcher(PlaybackControlView.DEFAULT_CONTROL_DISPATCHER)
            val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter()))
            val exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
            exoPlayerView.player = exoPlayer

            // Prepare media for playback
            val dataSourceFactory = DefaultDataSourceFactory(activity, Util.getUserAgent(context, activity.applicationContext.javaClass.simpleName))
            for (asset in activity.assets.list("")) {
                if (asset.endsWith(".mp4")) {
                    val videoSource = ExtractorMediaSource(Uri.parse("asset:///$asset"), dataSourceFactory, DefaultExtractorsFactory(), null, null)
                    val haveResumePosition = resumeWindow != C.INDEX_UNSET
                    if (haveResumePosition) {
                        exoPlayerView.player.seekTo(resumeWindow, resumePosition)
                    }
                    exoPlayer.prepare(videoSource, !haveResumePosition, false)
                    exoPlayer.playWhenReady = true
                    break;
                }
            }
        } else {
            exoPlayerView.player.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        resumeWindow = exoPlayerView.player?.currentWindowIndex ?: 0
        resumePosition = if (exoPlayerView.player?.isCurrentWindowSeekable ?: false) Math.max(0, exoPlayerView.player.currentPosition) else C.TIME_UNSET
        exoPlayerView.player?.playWhenReady = false
        exoPlayerView.player?.release()
        exoPlayerView.player = null
    }
}

package us.handstand.kartwheel.fragment.onboarding

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import us.handstand.kartwheel.R
import us.handstand.kartwheel.activity.OnboardingActivity
import us.handstand.kartwheel.layout.CircularImageView
import us.handstand.kartwheel.layout.ViewUtil


class VideoFragment : Fragment(), OnboardingActivity.OnboardingFragment {
    private lateinit var layout: ViewGroup
    private lateinit var startVideoButton: CircularImageView
    private var resumeWindow: Int = 0
    private var resumePosition: Long = 0L
    private var wasVideoSeen = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_onboarding_circle_image, container, false) as ViewGroup
        layout.findViewById(R.id.imageText).visibility = View.GONE
        startVideoButton = ViewUtil.findView(layout, R.id.image)
        startVideoButton.setImageResource(R.drawable.onboarding_play_button, R.drawable.onboarding_play_button)
        startVideoButton.setOnClickListener { videoBehavior.state = BottomSheetBehavior.STATE_EXPANDED }
        videoBehaviorCallback.delegate = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Set the emoji icon to the one that the user has selected
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    releasePlayer()
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    preparePlayer()
                    wasVideoSeen = true
                    updateOnboardingState()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
        return layout
    }

    override fun onPause() {
        videoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        releasePlayer()
        super.onPause()
    }

    private fun preparePlayer() {
        // Prepare ExoPlayer
        if (video.player == null) {
            video.setControlDispatcher(PlaybackControlView.DEFAULT_CONTROL_DISPATCHER)
            val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter()))
            val exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
            video.player = exoPlayer
            video.player?.addListener(object : ExoPlayer.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        activity.runOnUiThread { videoBehavior.state = BottomSheetBehavior.STATE_HIDDEN }
                    }
                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
                override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
                override fun onPlayerError(error: ExoPlaybackException?) {}
                override fun onLoadingChanged(isLoading: Boolean) {}
                override fun onPositionDiscontinuity() {}
                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}
            })
            // Prepare media for playback
            val dataSourceFactory = DefaultDataSourceFactory(activity, Util.getUserAgent(context, activity.applicationContext.javaClass.simpleName))
            for (asset in activity.assets.list("")) {
                if (asset.endsWith(".mp4")) {
                    val videoSource = ExtractorMediaSource(Uri.parse("asset:///$asset"), dataSourceFactory, DefaultExtractorsFactory(), null, null)
                    val haveResumePosition = resumeWindow != C.INDEX_UNSET
                    if (haveResumePosition) {
                        video.player.seekTo(resumeWindow, resumePosition)
                    }
                    exoPlayer.prepare(videoSource, !haveResumePosition, false)
                    exoPlayer.playWhenReady = true
                    break;
                }
            }
        } else {
            video.player.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        if (!isDetached) {
            resumeWindow = video.player?.currentWindowIndex ?: 0
            resumePosition = if (video.player?.isCurrentWindowSeekable ?: false) Math.max(0, video.player.currentPosition) else C.TIME_UNSET
            video.player?.playWhenReady = false
            video.player?.release()
            video.player = null
        }
    }

    override fun readyForNextStep(): Boolean {
        return wasVideoSeen
    }
}

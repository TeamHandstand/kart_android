package us.handstand.kartwheel.util

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.util.SparseArray


object Audio : SoundPool.OnLoadCompleteListener {
    private lateinit var applicationContext: Context
    private val soundPool: SoundPool = SoundPool(2, AudioManager.STREAM_NOTIFICATION, 0)
    private val playQueue = mutableMapOf<String, AudioFuture>()
    private val SOUNDS = SparseArray<String>()
    private val STREAMS = mutableMapOf<String, Int>()

    private val PREFIX = "sounds/"
    val ONBOARDING = "onboarding.m4a"
    val CLICK_BUTTON = "user-click-button.m4a"

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        soundPool.setOnLoadCompleteListener(this)
    }

    fun play(sound: String, priority: Int = 1, loop: Int = 0) {
        if (SOUNDS.indexOfValue(sound) >= 0) {
            playAndStoreStream(sound, priority, loop)
        } else if (!playQueue.containsKey(sound)) {
            val soundId = soundPool.load(applicationContext.assets.openFd(PREFIX + sound), 1)
            SOUNDS.put(soundId, sound)
            playQueue[sound] = AudioFuture(priority, loop)
        }
    }

    fun resume(sound: String) {
        if (STREAMS.contains(sound)) {
            soundPool.resume(STREAMS[sound]!!)
        }
    }


    fun stop(sound: String) {
        val streamId = STREAMS.remove(sound)
        if (streamId != null) {
            soundPool.stop(streamId)
        }
    }

    fun pause(sound: String) {
        if (STREAMS.contains(sound)) {
            soundPool.pause(STREAMS[sound]!!)
        }
    }

    private fun playAndStoreStream(sound: String, priority: Int, loop: Int) {
        val index = SOUNDS.indexOfValue(sound)
        val soundId = SOUNDS.keyAt(index)
        val streamId = soundPool.play(soundId, 1f, 1f, priority, loop, 1f)
        STREAMS[sound] = streamId
    }

    override fun onLoadComplete(soundPool: SoundPool?, sampleId: Int, status: Int) {
        val sound = SOUNDS.get(sampleId)
        val audioFuture = playQueue.remove(sound)
        if (audioFuture != null) {
            playAndStoreStream(sound, audioFuture.priority, audioFuture.loop)
        }
    }

    private data class AudioFuture(val priority: Int, val loop: Int)

}
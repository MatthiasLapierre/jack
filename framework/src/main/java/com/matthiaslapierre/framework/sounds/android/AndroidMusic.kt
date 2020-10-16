package com.matthiaslapierre.framework.sounds.android

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import com.matthiaslapierre.framework.sounds.Music
import java.io.IOException

class AndroidMusic(
    assetDescriptor: AssetFileDescriptor
) : Music, OnCompletionListener, OnPreparedListener {

    private var mIsPrepared = false

    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        try {
            setDataSource(
                assetDescriptor.fileDescriptor,
                assetDescriptor.startOffset,
                assetDescriptor.length
            )
            prepare()
            mIsPrepared = true
            isLooping = true
            setOnCompletionListener(this@AndroidMusic)
            setOnPreparedListener(this@AndroidMusic)
        } catch (e: Exception) {
            throw RuntimeException("Couldn't load music")
        }
    }

    override var isLooping: Boolean
        get() = mediaPlayer.isLooping
        set(isLooping) {
            mediaPlayer.isLooping = isLooping
        }

    override val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    override val isStopped: Boolean
        get() = !mediaPlayer.isPlaying

    override fun dispose() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun play() {
        if (mediaPlayer.isPlaying) return
        try {
            if (!mIsPrepared) {
                mediaPlayer.prepare()
            }
            mediaPlayer.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun setVolume(volume: Float) {
        mediaPlayer.setVolume(volume, volume)
    }

    override fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mIsPrepared = false
        }
    }

    override fun onCompletion(player: MediaPlayer) {
        synchronized(this) {
            mIsPrepared = false
        }
    }

    override fun onPrepared(player: MediaPlayer) {
        mIsPrepared = true
    }

}
package com.matthiaslapierre.framework.sounds.android

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.matthiaslapierre.framework.FrameworkConstants
import com.matthiaslapierre.framework.sounds.Audio
import com.matthiaslapierre.framework.sounds.Music
import com.matthiaslapierre.framework.sounds.Sound
import java.io.IOException

open class AndroidAudio(
    context: Context
) : Audio {

    private val mAssets: AssetManager = context.assets
    private val mSoundPool: SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val audioAttributes = AudioAttributes
            .Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()
        SoundPool
            .Builder()
            .setMaxStreams(FrameworkConstants.SOUND_MAX_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()
    } else {
        SoundPool(
            FrameworkConstants.SOUND_MAX_STREAMS,
            AudioManager.STREAM_MUSIC,
            0
        )
    }

    override fun load() {

    }

    override fun createMusic(file: String): Music {
        return try {
            val assetDescriptor = mAssets.openFd(file)
            AndroidMusic(assetDescriptor)
        } catch (e: IOException) {
            throw RuntimeException("Couldn't load music '$file'")
        }
    }

    override fun createSound(file: String): Sound {
        return try {
            val assetDescriptor = mAssets.openFd(file)
            val soundId = mSoundPool.load(assetDescriptor, 0)
            AndroidSound(mSoundPool, soundId)
        } catch (e: IOException) {
            throw RuntimeException("Couldn't load sound '$file'")
        }
    }

}
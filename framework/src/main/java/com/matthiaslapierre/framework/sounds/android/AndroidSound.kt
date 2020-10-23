package com.matthiaslapierre.framework.sounds.android

import android.media.SoundPool
import com.matthiaslapierre.framework.sounds.Sound
import java.util.concurrent.Executor

open class AndroidSound(
    private val soundPool: SoundPool,
    private val soundId: Int,
    private val executor: Executor
): Sound {

    override fun play(volume: Float) {
        executor.execute {
            soundPool.play(soundId, volume, volume, 0, 0, 1f)
        }
    }

    override fun dispose() {
        soundPool.unload(soundId)
    }

}
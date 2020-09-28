package com.matthiaslapierre.framework.sounds.android

import android.media.SoundPool
import com.matthiaslapierre.framework.sounds.Sound

open class AndroidSound(
    var soundPool: SoundPool,
    var soundId: Int
): Sound {

    override fun play(volume: Float) {
        soundPool.play(soundId, volume, volume, 0, 0, 1f)
    }

    override fun dispose() {
        soundPool.unload(soundId)
    }

}
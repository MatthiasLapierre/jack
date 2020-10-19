package com.matthiaslapierre.core

import android.content.Context
import com.matthiaslapierre.framework.sounds.android.AndroidAudio

abstract class SoundManager(
    context: Context
) : AndroidAudio(context) {

    abstract fun playStageEnterSound()

    abstract fun playStageClearSound()

    abstract fun playJumpSound()

    abstract fun playDestroyEnemy()

    abstract fun playGetBonusPointsSound()

    abstract fun playGetCoinSound()

    abstract fun playDieSound()

    abstract fun playHitSound()

    abstract fun playRocketFlightSound()

    abstract fun playCopterFlightSound()

    abstract fun playMenuMusic()

    abstract fun playGameMusic()

    abstract fun playGameOverMusic()

    abstract fun stopFlightSound()

    abstract fun stop()

    abstract fun enableMusic(enable: Boolean)

    abstract fun enableSounds(enable: Boolean)

}
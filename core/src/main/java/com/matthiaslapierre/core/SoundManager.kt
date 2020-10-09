package com.matthiaslapierre.core

import android.content.Context
import com.matthiaslapierre.framework.sounds.android.AndroidAudio

abstract class SoundManager(
    context: Context
) : AndroidAudio(context) {

    abstract fun playStageEnterSound()

    abstract fun playStageClearSound()

    abstract fun playJumpSound()

    abstract fun playGetBonusPointsSound()

    abstract fun playGetCoinSound()

    abstract fun playDieSound()

    abstract fun playMenuMusic()

    abstract fun playGameMusic()

    abstract fun resumeMusic()

    abstract fun pauseMusic()

    abstract fun stopMusic()

    abstract fun enableMusic(enable: Boolean)

    abstract fun enableSounds(enable: Boolean)

}
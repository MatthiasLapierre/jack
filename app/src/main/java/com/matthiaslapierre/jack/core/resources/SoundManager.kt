package com.matthiaslapierre.jack.core.resources

import android.content.Context
import com.matthiaslapierre.framework.sounds.android.AndroidAudio

/**
 * Manages sounds.
 */
abstract class SoundManager(
    context: Context
) : AndroidAudio(context) {

    /**
     * Plays the jump sound effect.
     */
    abstract fun playJumpSound()

    /**
     * Plays the sound effect after destroying an enemy.
     */
    abstract fun playDestroyEnemySound()

    /**
     * Plays the sound effect after getting a bonus candy.
     */
    abstract fun playGetBonusCandySound()

    /**
     * Plays the sound effect after collecting a candy.
     */
    abstract fun playCollectCandySound()

    /**
     * Plays the die sound effect.
     */
    abstract fun playDieSound()

    /**
     * Plays the hit sound effect.
     */
    abstract fun playHitSound()

    /**
     * Plays the rocket ambient sound.
     */
    abstract fun playRocketFlightSound()

    /**
     * Plays the copter ambient sound.
     */
    abstract fun playCopterFlightSound()

    /**
     * Plays magic sound after getting power-up.
     */
    abstract fun playGetPowerUpSound()

    /**
     * Plays magic sound after pressing button.
     */
    abstract fun playButtonPressedSound()

    /**
     * Plays the menu theme music.
     */
    abstract fun playMenuMusic()

    /**
     * Plays the game theme music.
     */
    abstract fun playGameMusic()

    /**
     * Plays the game over theme music.
     */
    abstract fun playGameOverMusic()

    /**
     * Stops to play rocket or copter ambient sound.
     */
    abstract fun stopFlightSound()

    /**
     * Stops musics and sound effects.
     */
    abstract fun stop()

    /**
     * Enables / Disables musics
     */
    abstract fun enableMusic(enable: Boolean)

    /**
     * Enables / Disables sounds.
     */
    abstract fun enableSounds(enable: Boolean)

}
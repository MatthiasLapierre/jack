package com.matthiaslapierre.jack.core.resources.impl

import android.content.Context
import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.jack.core.resources.SoundManager
import com.matthiaslapierre.framework.sounds.Music
import com.matthiaslapierre.framework.sounds.Sound
import com.matthiaslapierre.jack.utils.Utils

class JackSoundManager(context: Context): SoundManager(context) {

    private val soundVolume = Constants.SOUND_VOLUME
    private val musicVolume = Constants.MUSIC_VOLUME

    private var musicEnabled = true
    private var soundEnabled = true

    private var menuMusic: Music? = null
    private var gameMusic: Music? = null
    private var gameOverMusic: Music? = null
    private var rocketSound: Music? = null
    private var copterSound: Music? = null
    private var jumpSound: Sound? = null
    private var destroyEnemySound: Sound? = null
    private var hitSound: Sound? = null
    private var getBonusPointSound: Sound? = null
    private var getCoinSound: Sound? = null
    private var dieSound: Sound? = null
    private var getPowerUpSound: Sound? = null
    private var buttonPressedSound: Sound? = null
    private var buttonPressed2Sound: Sound? = null

    private var currentPowerUpSound: Music? = null
    private var currentMusic: Music? = null

    override fun load() {
        getCoinSound = createSound("sounds/get_coin.mp3")
        dieSound = createSound("sounds/die.mp3")
        jumpSound = createSound("sounds/jump.mp3")
        destroyEnemySound = createSound("sounds/destroy_enemy.mp3")
        hitSound = createSound("sounds/hit.ogg")
        rocketSound = createMusic("sounds/rocket.mp3")
        copterSound = createMusic("sounds/copter.mp3")
        getPowerUpSound = createSound("sounds/power_up.mp3")
        buttonPressedSound = createSound("sounds/ui.mp3")
        buttonPressed2Sound = createSound("sounds/ui2.mp3")

        menuMusic = createMusic("musics/soundtrack.mp3")
        gameMusic = createMusic("musics/game.mp3")
        gameOverMusic = createMusic("musics/game_over.mp3")
    }

    override fun pause() {
        currentMusic?.pause()
        currentPowerUpSound?.pause()
    }

    override fun stop() {
        currentMusic?.stop()
        currentPowerUpSound?.stop()
        currentMusic = null
        currentPowerUpSound = null
    }

    override fun dispose() {
        getCoinSound?.dispose()
        dieSound?.dispose()
        jumpSound?.dispose()
        destroyEnemySound?.dispose()
        hitSound?.dispose()
        rocketSound?.dispose()
        copterSound?.dispose()
        getPowerUpSound?.dispose()
        buttonPressedSound?.dispose()
        buttonPressed2Sound?.dispose()
        menuMusic?.dispose()
        gameMusic?.dispose()
        gameOverMusic?.dispose()
    }

    override fun playJumpSound() {
        if(soundEnabled) {
            jumpSound?.play(soundVolume)
        }
    }

    override fun playDestroyEnemySound() {
        if(soundEnabled) {
            destroyEnemySound?.play(soundVolume)
        }
    }

    override fun playGetBonusCandySound() {
        if(soundEnabled) {
            getBonusPointSound?.play(soundVolume)
        }
    }

    override fun playCollectCandySound() {
        if(soundEnabled) {
            getCoinSound?.play(soundVolume)
        }
    }

    override fun playDieSound() {
        if(soundEnabled) {
            dieSound?.play(soundVolume)
        }
    }

    override fun playHitSound() {
        if(soundEnabled) {
            hitSound?.play(soundVolume)
        }
    }

    override fun playRocketFlightSound() {
        if(soundEnabled) {
            playPowerUpSound(rocketSound)
        }
    }

    override fun playCopterFlightSound() {
        if(soundEnabled) {
            playPowerUpSound(copterSound)
        }
    }

    override fun playGetPowerUpSound() {
        if (soundEnabled) {
            getPowerUpSound?.play(soundVolume)
        }
    }

    override fun playButtonPressedSound() {
        if (soundEnabled) {
            val random = Utils.getRandomInt(1, 3)
            if (random == 1) {
                buttonPressedSound?.play(soundVolume)
            } else {
                buttonPressed2Sound?.play(soundVolume)
            }
        }
    }

    override fun stopFlightSound() {
        currentPowerUpSound?.stop()
        currentPowerUpSound = null
    }

    override fun playMenuMusic() = playMusic(menuMusic)

    override fun playGameMusic() = playMusic(gameMusic)

    override fun playGameOverMusic() = playMusic(gameOverMusic)

    override fun resume() {
        if (musicEnabled) {
            currentMusic?.play()
        }
        if (soundEnabled) {
            currentPowerUpSound?.play()
        }
    }

    override fun enableMusic(enable: Boolean) {
        musicEnabled = enable
        if (enable) {
            currentMusic?.play()
        } else {
            currentMusic?.stop()
        }
    }

    override fun enableSounds(enable: Boolean) {
        soundEnabled = enable
        if (enable) {
            currentPowerUpSound?.play()
        } else {
            currentPowerUpSound?.stop()
        }
    }

    private fun playMusic(music: Music?) {
        if(currentMusic == music) return
        currentMusic?.stop()
        currentMusic = music
        currentMusic?.setVolume(musicVolume)
        if(musicEnabled) currentMusic?.play()
    }

    private fun playPowerUpSound(sound: Music?) {
        if(currentPowerUpSound == sound) return
        currentPowerUpSound?.stop()
        currentPowerUpSound = sound
        currentPowerUpSound?.setVolume(soundVolume)
        if(soundEnabled) currentPowerUpSound?.play()
    }

}
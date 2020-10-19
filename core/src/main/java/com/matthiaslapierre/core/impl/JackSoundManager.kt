package com.matthiaslapierre.core.impl

import android.content.Context
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.SoundManager
import com.matthiaslapierre.framework.sounds.Music
import com.matthiaslapierre.framework.sounds.Sound

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
    private var stageEnterSound: Sound? = null
    private var stageClearSound: Sound? = null
    private var jumpSound: Sound? = null
    private var destroyEnemySound: Sound? = null
    private var hitSound: Sound? = null
    private var getBonusPointSound: Sound? = null
    private var getCoinSound: Sound? = null
    private var dieSound: Sound? = null

    private var currentPowerUpSound: Music? = null
    private var currentMusic: Music? = null

    override fun load() {
        getCoinSound = createSound("sounds/get_coin.mp3")
        dieSound = createSound("sounds/die.mp3")
        jumpSound = createSound("sounds/jump.wav")
        destroyEnemySound = createSound("sounds/destroy_enemy.mp3")
        hitSound = createSound("sounds/hit.ogg")
        rocketSound = createMusic("sounds/rocket.mp3")
        copterSound = createMusic("sounds/copter.mp3")

        menuMusic = createMusic("musics/soundtrack.mp3")
        gameMusic = createMusic("musics/game.mp3")
        gameOverMusic = createMusic("musics/game_over.mp3")
    }

    override fun playStageEnterSound() {
        if(soundEnabled) {
            stageEnterSound?.play(soundVolume)
        }
    }

    override fun playStageClearSound() {
        if(soundEnabled) {
            stageClearSound?.play(soundVolume)
        }
    }

    override fun playJumpSound() {
        if(soundEnabled) {
            jumpSound?.play(soundVolume)
        }
    }

    override fun playDestroyEnemy() {
        if(soundEnabled) {
            destroyEnemySound?.play(soundVolume)
        }
    }

    override fun playGetBonusPointsSound() {
        if(soundEnabled) {
            getBonusPointSound?.play(soundVolume)
        }
    }

    override fun playGetCoinSound() {
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
package com.matthiaslapierre.core.impl

import android.content.Context
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.framework.sounds.Music
import com.matthiaslapierre.framework.sounds.Sound

class JackSoundManager(context: Context): com.matthiaslapierre.core.SoundManager(context) {

    private val soundVolume = Constants.SOUND_VOLUME
    private val musicVolume = Constants.MUSIC_VOLUME

    private var musicEnabled = true
    private var soundEnabled = true

    private var menuMusic: Music? = null
    private var gameMusic: Music? = null
    private var stageEnterSound: Sound? = null
    private var stageClearSound: Sound? = null
    private var jumpSound: Sound? = null
    private var getBonusPointSound: Sound? = null
    private var getCoinSound: Sound? = null
    private var dieSound: Sound? = null

    private var currentMusic: Music? = null

    override fun load() {

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

    override fun playMenuMusic() = playMusic(menuMusic)

    override fun playGameMusic() = playMusic(gameMusic)

    override fun resumeMusic() {
        currentMusic?.play()
    }

    override fun pauseMusic() {
        currentMusic?.pause()
    }

    override fun stopMusic() {
        currentMusic?.stop()
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
    }

    private fun playMusic(music: Music?) {
        if(currentMusic == music) return
        currentMusic?.stop()
        currentMusic = music
        currentMusic?.setVolume(musicVolume)
        if(musicEnabled) currentMusic?.play()
    }

}
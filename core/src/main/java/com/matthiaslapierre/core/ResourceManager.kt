package com.matthiaslapierre.core

import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Image
import java.util.*

interface ResourceManager: GameResources {

    enum class PlayerState {
        DEAD,
        FALL,
        IDLE,
        JUMP,
        COPTER
    }

    enum class JumpPlatformState {
        IDLE,
        BOUNCE
    }

    enum class Character(private val string: String) {
        JACK("jack");

        override fun toString(): String = string
    }

    enum class PowerUp(private val string: String) {
        ARMORED("armored"),
        COPTER("copter"),
        MAGNET("magnet"),
        ROCKET("rocket");

        override fun toString(): String = string
    }

    var logoJumperJack: Image?
    var textTapToLaunch: Image?

    var btnSound: Image?
    var btnSoundPressed: Image?
    var btnMusic: Image?
    var btnMusicPressed: Image?
    var btnFacebook: Image?
    var btnFacebookPressed: Image?
    var btnTwitter: Image?
    var btnTwitterPressed: Image?
    var btnPlay: Image?
    var btnPlayPressed: Image?
    var btnPause: Image?
    var btnPausePressed: Image?
    var btnMoreGames: Image?
    var btnMoreGamesPressed: Image?
    var btnShop: Image?
    var btnScores: Image?
    var btnScoresPressed: Image?

    var bgTop: Image?
    var candyIndicator: Image?
    var digits: Array<Image?>?

    // Background
    var bgJump: Image?
    var clouds: Array<Image>?

    // Jumping platforms
    var jumpingPlatforms: Array<Hashtable<JumpPlatformState, Array<Image>>>?

    // Collectibles
    var candies: Array<Image>?
    var powerUps: Hashtable<PowerUp, Image>?

    // Obstacles
    var bat: Array<Image>?

    // Player
    var player: Hashtable<PlayerState, Array<Image?>>?

    // Power-up
    var armor: Image?
    var magnet: Image?
    var rocket: Array<Image>?

    var collectibleExplosion: Array<Image>?

    fun getRandomCandy(): Image

    fun getRandomCloud(): Image

}
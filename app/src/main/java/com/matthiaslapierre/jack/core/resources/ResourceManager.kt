package com.matthiaslapierre.jack.core.resources

import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.jack.core.JumpPlatformState
import com.matthiaslapierre.jack.core.PlayerState
import java.util.*

/**
 * Manages images.
 */
interface ResourceManager: GameResources {

    enum class CharacterResId(private val string: String) {
        JACK("jack");

        override fun toString(): String = string
    }

    enum class PowerUpResId(private val string: String) {
        ARMORED("armored"),
        COPTER("copter"),
        MAGNET("magnet"),
        ROCKET("rocket");

        override fun toString(): String = string
    }

    // Logo
    var logoJumperJack: Image?

    // Text
    var textTapToLaunch: Image?

    // Buttons
    var btnSound: Image?
    var btnSoundPressed: Image?
    var btnSoundDisabled: Image?
    var btnMusic: Image?
    var btnMusicPressed: Image?
    var btnMusicDisabled: Image?
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
    var btnReplay: Image?
    var btnReplayPressed: Image?
    var btnResume: Image?
    var btnResumePressed: Image?
    var btnExit: Image?
    var btnExitPressed: Image?

    // Top bar
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
    var powerUpsResId: Hashtable<PowerUpResId, Image>?

    // Obstacles
    var bat: Array<Image>?
    var spike: Image?

    // Player
    var player: Hashtable<PlayerState, Array<Image?>>?

    // Power-up
    var armor: Image?
    var magnet: Image?
    var rocket: Array<Image>?

    // Badges
    var badges: Hashtable<PowerUpResId, Image>?

    // Windows
    var windowGameOver: Image?
    var windowPause: Image?
    var windowHighScores: Image?
    var windowScoreItem: Image?

    // Explosions / smoke effects
    var collectibleExplosion: Array<Image>?
    var smoke: Array<Image>?

    fun getRandomCandy(): Image

    fun getRandomCloud(): Image

}
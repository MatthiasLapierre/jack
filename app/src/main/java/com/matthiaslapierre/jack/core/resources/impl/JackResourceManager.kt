package com.matthiaslapierre.jack.core.resources.impl

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.android.AndroidImage
import com.matthiaslapierre.jack.core.JumpPlatformState
import com.matthiaslapierre.jack.core.PlayerState
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.jack.core.resources.ResourceManager.*
import com.matthiaslapierre.jack.utils.Utils
import java.util.*

class JackResourceManager(
    context: Context
): ResourceManager {

    private val assets: AssetManager = context.assets

    override var logoJumperJack: Image? = null
    override var textTapToLaunch: Image? = null

    override var btnSound: Image? = null
    override var btnSoundPressed: Image? = null
    override var btnSoundDisabled: Image? = null
    override var btnMusic: Image? = null
    override var btnMusicPressed: Image? = null
    override var btnMusicDisabled: Image? = null
    override var btnFacebook: Image? = null
    override var btnFacebookPressed: Image? = null
    override var btnTwitter: Image? = null
    override var btnTwitterPressed: Image? = null
    override var btnPlay: Image? = null
    override var btnPlayPressed: Image? = null
    override var btnPause: Image? = null
    override var btnPausePressed: Image? = null
    override var btnMoreGames: Image? = null
    override var btnMoreGamesPressed: Image? = null
    override var btnShop: Image? = null
    override var btnScores: Image? = null
    override var btnScoresPressed: Image? = null
    override var btnReplay: Image? = null
    override var btnReplayPressed: Image? = null
    override var btnResume: Image? = null
    override var btnResumePressed: Image? = null
    override var btnExit: Image? = null
    override var btnExitPressed: Image? = null

    override var bgTop: Image? = null
    override var candyIndicator: Image? = null
    override var digits: Array<Image>? = null

    override var bgJump: Image? = null
    override var clouds: Array<Image>? = null

    override var jumpingPlatforms: Array<Hashtable<JumpPlatformState, Array<Image>>>? = null

    override var candies: Array<Image>? = null
    override var powerUpsResId: Hashtable<PowerUpResId, Image>? = null

    override var bat: Array<Image>? = null
    override var spike: Image? = null

    override var player: Hashtable<PlayerState, Array<Image>>? = null

    override var armor: Image? = null
    override var magnet: Image? = null
    override var rocket: Array<Image>? = null

    override var badges: Hashtable<PowerUpResId, Image>? = null

    override var windowGameOver: Image? = null
    override var windowPause: Image? = null
    override var windowHighScores: Image? = null
    override var windowScoreItem: Image? = null

    override var collectibleExplosion: Array<Image>? = null
    override var smoke: Array<Image>? = null

    override fun load() {
        logoJumperJack = loadImage("images/ui/logos/jumper_jack.png")
        textTapToLaunch = loadImage("images/ui/texts/text_tap_to_launch.png")

        btnSound = loadImage("images/ui/buttons/btn_sound_normal.png")
        btnSoundPressed = loadImage("images/ui/buttons/btn_sound_pressed.png")
        btnSoundDisabled = loadImage("images/ui/buttons/btn_sound_disabled.png")
        btnMusic = loadImage("images/ui/buttons/btn_music_normal.png")
        btnMusicPressed = loadImage("images/ui/buttons/btn_music_pressed.png")
        btnMusicDisabled = loadImage("images/ui/buttons/btn_music_disabled.png")
        btnFacebook = loadImage("images/ui/buttons/btn_facebook_normal.png")
        btnFacebookPressed = loadImage("images/ui/buttons/btn_facebook_pressed.png")
        btnTwitter = loadImage("images/ui/buttons/btn_twitter_normal.png")
        btnTwitterPressed = loadImage("images/ui/buttons/btn_twitter_pressed.png")
        btnPlay = loadImage("images/ui/buttons/btn_play_normal.png")
        btnPlayPressed = loadImage("images/ui/buttons/btn_play_pressed.png")
        btnPause = loadImage("images/ui/buttons/btn_pause_normal.png")
        btnPausePressed = loadImage("images/ui/buttons/btn_pause_pressed.png")
        btnMoreGames = loadImage("images/ui/buttons/btn_more_games_normal.png")
        btnMoreGamesPressed = loadImage("images/ui/buttons/btn_more_games_pressed.png")
        btnShop = loadImage("images/ui/buttons/btn_shop_normal.png")
        btnScores = loadImage("images/ui/buttons/btn_scores_normal.png")
        btnScoresPressed = loadImage("images/ui/buttons/btn_scores_pressed.png")
        btnReplay = loadImage("images/ui/buttons/btn_replay_normal.png")
        btnReplayPressed = loadImage("images/ui/buttons/btn_replay_pressed.png")
        btnResume = loadImage("images/ui/buttons/btn_resume_normal.png")
        btnResumePressed = loadImage("images/ui/buttons/btn_resume_pressed.png")
        btnExit = loadImage("images/ui/buttons/btn_exit_normal.png")
        btnExitPressed = loadImage("images/ui/buttons/btn_exit_pressed.png")

        bgTop = loadImage("images/ui/scores/bg_top.png")
        candyIndicator = loadImage("images/ui/scores/indicator_candy.png")
        digits = (0..9).map { digit ->
            loadImage("images/ui/numbers/$digit.png")!!
        }.toTypedArray()

        bgJump = loadImage("images/bg/jump/bg.png")
        clouds = loadClouds()

        jumpingPlatforms = loadJumpingPlatforms()

        candies = loadCandies()
        powerUpsResId = loadPowerUps()

        bat = loadBat()
        spike = loadImage("images/objects/obstacles/spike.png")

        player = loadPlayer(CharacterResId.JACK)

        armor = loadImage("images/player/accessories/Armor.png")
        magnet = loadImage("images/player/accessories/Magnet.png")
        rocket = (1..4).map { digit ->
            loadImage("images/player/accessories/Rocket Animated ($digit).png")!!
        }.toTypedArray()

        badges = loadBadges()

        windowGameOver = loadImage("images/ui/windows/window_game_over.png")
        windowPause = loadImage("images/ui/windows/window_pause.png")
        windowHighScores = loadImage("images/ui/windows/window_high_scores.png")
        windowScoreItem = loadImage("images/ui/windows/window_score_item.png")

        collectibleExplosion = (1..8).map { digit ->
            loadImage("images/objects/explosion/collectibles/Collect ($digit).png")!!
        }.toTypedArray()
        smoke = (1..8).map { digit ->
            loadImage("images/objects/explosion/smoke/Smoke ($digit).png")!!
        }.toTypedArray()
    }

    override fun dispose() {
        logoJumperJack?.dispose()
        logoJumperJack?.dispose()
        textTapToLaunch?.dispose()
        btnSound?.dispose()
        btnSoundPressed?.dispose()
        btnSoundDisabled?.dispose()
        btnMusic?.dispose()
        btnMusicPressed?.dispose()
        btnMusicDisabled?.dispose()
        btnFacebook?.dispose()
        btnFacebookPressed?.dispose()
        btnTwitter?.dispose()
        btnTwitterPressed?.dispose()
        btnPlay?.dispose()
        btnPlayPressed?.dispose()
        btnPause?.dispose()
        btnPausePressed?.dispose()
        btnMoreGames?.dispose()
        btnMoreGamesPressed?.dispose()
        btnShop?.dispose()
        btnScores?.dispose()
        btnScoresPressed?.dispose()
        btnReplay?.dispose()
        btnReplayPressed?.dispose()
        btnResume?.dispose()
        btnResumePressed?.dispose()
        btnExit?.dispose()
        btnExitPressed?.dispose()
        bgTop?.dispose()
        candyIndicator?.dispose()
        digits?.map { it.dispose() }
        bgJump?.dispose()
        clouds?.map { it.dispose() }
        jumpingPlatforms?.map { statesFrames -> statesFrames.map { stateFrames -> stateFrames.value.map { frame -> frame.dispose() } } }
        candies?.map { it.dispose() }
        powerUpsResId?.map { it.value.dispose() }
        bat?.map { it.dispose() }
        spike?.dispose()
        player?.map { stateFrames -> stateFrames.value.map { frame -> frame.dispose() } }
        armor?.dispose()
        magnet?.dispose()
        rocket?.map { it.dispose() }
        badges?.map { it.value.dispose() }
        windowGameOver?.dispose()
        windowPause?.dispose()
        windowHighScores?.dispose()
        windowScoreItem?.dispose()
        collectibleExplosion?.map { it.dispose() }
        smoke?.map { it.dispose() }
    }

    override fun getRandomCandy(): Image {
        val randomInt = Utils.getRandomInt(0,3)
        return candies!![randomInt]
    }

    override fun getRandomCloud(): Image {
        val randomInt = Utils.getRandomInt(0,4)
        return clouds!![randomInt]
    }

    private fun loadClouds(): Array<Image> = (1..4).map {
        loadImage("images/bg/jump/layers/cloud$it.png")!!
    }.toTypedArray()

    private fun loadJumpingPlatforms(): Array<Hashtable<JumpPlatformState, Array<Image>>> =
        (1..3).map { index ->
            val cache: Hashtable<JumpPlatformState, Array<Image>> = Hashtable()
            cache.run {
                put(
                    JumpPlatformState.IDLE,
                    arrayOf(loadImage("images/objects/jumping_platform/$index/Idle.png")!!)
                )
                put(
                    JumpPlatformState.BOUNCE,
                    (1..5).map { frame ->
                        loadImage("images/objects/jumping_platform/$index/Bounce ($frame).png")!!
                    }.toTypedArray()
                )
            }
            cache
        }.toTypedArray()

    private fun loadCandies(): Array<Image> = (1..3).map { index ->
        loadImage("images/objects/collectibles/candy/Candy ($index).png")!!
    }.toTypedArray()

    private fun loadPowerUps(): Hashtable<PowerUpResId, Image> =
        Hashtable<PowerUpResId, Image>().apply {
            put(PowerUpResId.ARMORED, loadImage("images/objects/collectibles/power_up/Shield.png"))
            put(PowerUpResId.ROCKET, loadImage("images/objects/collectibles/power_up/Rocket.png"))
            put(PowerUpResId.MAGNET, loadImage("images/objects/collectibles/power_up/Magnet.png"))
            put(PowerUpResId.COPTER, loadImage("images/objects/collectibles/power_up/Copter.png"))
        }

    private fun loadBat(): Array<Image> =  (1..4).map { index ->
        loadImage("images/objects/obstacles/bat/Bat ($index).png")!!
    }.toTypedArray()

    private fun loadPlayer(characterResId: CharacterResId): Hashtable<PlayerState, Array<Image>> {
        val cache: Hashtable<PlayerState, Array<Image>> = Hashtable()
        cache.run {
            put(PlayerState.DEAD, (1..8).map {
                loadImage("images/player/$characterResId/Dead ($it).png")!!
            }.toTypedArray())
            put(PlayerState.FALL, (1..4).map {
                loadImage("images/player/$characterResId/Fall ($it).png")!!
            }.toTypedArray())
            put(PlayerState.IDLE, arrayOf(loadImage("images/player/$characterResId/Idle (1).png")!!))
            put(PlayerState.JUMP, (1..4).map {
                loadImage("images/player/$characterResId/Jump ($it).png")!!
            }.toTypedArray())
            put(PlayerState.COPTER, (1..5).map {
                loadImage("images/player/$characterResId/Copter ($it).png")!!
            }.toTypedArray())
        }
        return cache
    }

    private fun loadBadges(): Hashtable<PowerUpResId, Image> =
        Hashtable<PowerUpResId, Image>().apply {
            put(PowerUpResId.ARMORED, loadImage("images/ui/badges/badge_shield.png"))
            put(PowerUpResId.ROCKET, loadImage("images/ui/badges/badge_rocket.png"))
            put(PowerUpResId.MAGNET, loadImage("images/ui/badges/badge_magnet.png"))
            put(PowerUpResId.COPTER, loadImage("images/ui/badges/badge_copter.png"))
        }

    private fun loadImage(path: String): Image? {
        val bitmapResult = runCatching {
            assets.open(path).use { inputStream ->
                BitmapFactory.decodeStream(
                    inputStream,
                    null,
                    BitmapFactory.Options()
                )
            }
        }
        return if(bitmapResult.isSuccess) {
            AndroidImage(bitmapResult.getOrNull()!!)
        } else {
            null
        }
    }

}
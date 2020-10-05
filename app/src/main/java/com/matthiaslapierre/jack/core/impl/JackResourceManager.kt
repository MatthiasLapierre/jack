package com.matthiaslapierre.jack.core.impl

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.android.AndroidImage
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.core.ResourceManager.*
import java.util.*

class JackResourceManager(
    context: Context
): ResourceManager {

    private val assets: AssetManager = context.assets

    override var logoJumperJack: Image? = null
    override var textTapToLaunch: Image? = null

    override var btnSound: Image? = null
    override var btnSoundPressed: Image? = null
    override var btnMusic: Image? = null
    override var btnMusicPressed: Image? = null
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

    override var bgTop: Image? = null
    override var candyIndicator: Image? = null
    override var digits: Array<Image?>? = null

    override var bgJump: Image? = null
    override var clouds: Array<Image?>? = null

    override var player: Hashtable<PlayerState, Array<Image?>>? = null
    override var playerMagnet: Hashtable<PlayerState, Array<Image?>>? = null
    override var playerCopter: Hashtable<PlayerState, Array<Image?>>? = null
    override var playerArmored: Hashtable<PlayerState, Array<Image?>>? = null

    override fun load() {
        logoJumperJack = loadImage("images/ui/logos/jumper_jack.png")
        textTapToLaunch = loadImage("images/ui/texts/text_tap_to_launch.png")

        btnSound = loadImage("images/ui/buttons/btn_sound_normal.png")
        btnSoundPressed = loadImage("images/ui/buttons/btn_sound_pressed.png")
        btnMusic = loadImage("images/ui/buttons/btn_music_normal.png")
        btnMusicPressed = loadImage("images/ui/buttons/btn_music_pressed.png")
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

        bgTop = loadImage("images/ui/scores/bg_top.png")
        candyIndicator = loadImage("images/ui/scores/indicator_candy.png")
        digits = (0..9).map { digit ->
            loadImage("images/ui/numbers/$digit.png")
        }.toTypedArray()

        bgJump = loadImage("images/bg/jump/bg.png")
        clouds = (0..5).map {
            loadImage("images/bg/jump/layers/cloud$it.png")
        }.toTypedArray()

        player = loadPlayer(Character.JACK, PlayerPowerUp.NORMAL)
        playerArmored = loadPlayer(Character.JACK, PlayerPowerUp.ARMORED)
        playerCopter = loadPlayer(Character.JACK, PlayerPowerUp.COPTER)
        playerMagnet = loadPlayer(Character.JACK, PlayerPowerUp.MAGNET)
    }

    private fun loadPlayer(character: Character, playerPowerUp: PlayerPowerUp): Hashtable<PlayerState, Array<Image?>> {
        val cache: Hashtable<PlayerState, Array<Image?>> = Hashtable()
        cache.run {
            put(PlayerState.DEAD, (1..8).map {
                loadImage("images/player/$character/$playerPowerUp/Dead ($it).png")
            }.toTypedArray())
            put(PlayerState.FALL, (1..4).map {
                loadImage("images/player/$character/$playerPowerUp/Fall ($it).png")
            }.toTypedArray())
            put(PlayerState.IDLE, (1..4).map {
                loadImage("images/player/$character/$playerPowerUp/Idle ($it).png")
            }.toTypedArray())
            put(PlayerState.JUMP, (1..4).map {
                loadImage("images/player/$character/$playerPowerUp/Jump ($it).png")
            }.toTypedArray())
            put(PlayerState.LAUNCH, (1..9).map {
                loadImage("images/player/$character/$playerPowerUp/Launch ($it).png")
            }.toTypedArray())
            put(PlayerState.LEAN_LEFT, (1..5).map {
                loadImage("images/player/$character/$playerPowerUp/Lean Left ($it).png")
            }.toTypedArray())
            put(PlayerState.LEAN_RIGHT, (1..5).map {
                loadImage("images/player/$character/$playerPowerUp/Lean Right ($it).png")
            }.toTypedArray())
            put(PlayerState.ROCKET, (1..4).map {
                loadImage("images/player/$character/$playerPowerUp/Rocket ($it).png")
            }.toTypedArray())
        }
        return cache
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
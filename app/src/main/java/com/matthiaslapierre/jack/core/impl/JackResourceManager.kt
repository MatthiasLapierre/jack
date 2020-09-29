package com.matthiaslapierre.jack.core.impl

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.android.AndroidImage
import com.matthiaslapierre.jack.core.ResourceManager

class JackResourceManager(
    context: Context
): ResourceManager {

    private val assets: AssetManager = context.assets

    override var logoJumperJack: Image? = null
    override var bgJump: Image? = null
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
    override var digits: Array<Image>? = null
    override var bgGate: Image? = null
    override var bgGraveyardBottom: Image? = null
    override var bgGraveyardTop: Image? = null
    override var bgGraveyardFar: Image? = null
    override var bgHills1: Image? = null
    override var bgHills2: Image? = null
    override var bgHills3: Image? = null
    override var bgHills4: Image? = null
    override var bgHills5: Image? = null
    override var bgMoon: Image? = null

    override fun load() {
        logoJumperJack = loadImage("images/ui/logos/jumper_jack.png")
        bgJump = loadImage("images/bg/jump/bg.png")
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
        bgGate = loadImage("images/bg/jump/layers/1.png")
        bgGraveyardBottom = loadImage("images/bg/jump/layers/2.png")
        bgGraveyardTop = loadImage("images/bg/jump/layers/3.png")
        bgGraveyardFar = loadImage("images/bg/jump/layers/4.png")
        bgHills1 = loadImage("images/bg/jump/layers/5.png")
        bgHills2 = loadImage("images/bg/jump/layers/6.png")
        bgHills3 = loadImage("images/bg/jump/layers/7.png")
        bgHills4 = loadImage("images/bg/jump/layers/8.png")
        bgHills5 = loadImage("images/bg/jump/layers/9.png")
        bgMoon = loadImage("images/bg/jump/layers/10.png")
    }

    private fun loadImage(path: String): Image {
        val bitmap = assets.open(path).use { inputStream ->
            BitmapFactory.decodeStream(
                inputStream,
                null,
                BitmapFactory.Options()
            )
        }
        return AndroidImage(bitmap!!)
    }

}
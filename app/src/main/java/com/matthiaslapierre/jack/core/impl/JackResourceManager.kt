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
    override var btnMoreGames: Image? = null
    override var btnMoreGamesPressed: Image? = null
    override var btnShop: Image? = null
    override var btnScores: Image? = null
    override var btnScoresPressed: Image? = null

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
        btnMoreGames = loadImage("images/ui/buttons/btn_more_games_normal.png")
        btnMoreGamesPressed = loadImage("images/ui/buttons/btn_more_games_pressed.png")
        btnShop = loadImage("images/ui/buttons/btn_shop_normal.png")
        btnScores = loadImage("images/ui/buttons/btn_scores_normal.png")
        btnScoresPressed = loadImage("images/ui/buttons/btn_scores_pressed.png")
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
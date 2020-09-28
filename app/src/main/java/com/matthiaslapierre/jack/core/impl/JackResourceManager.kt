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
    override var btnMusic: Image? = null
    override var btnFacebook: Image? = null
    override var btnTwitter: Image? = null
    override var btnPlay: Image? = null
    override var btnPause: Image? = null
    override var btnMoreGames: Image? = null
    override var btnShop: Image? = null
    override var btnScores: Image? = null

    override fun load() {
        logoJumperJack = loadImage("images/ui/logos/jumper_jack.png")
        bgJump = loadImage("images/bg/jump/bg.png")
        btnSound = loadImage("images/ui/buttons/btn_sound_normal.png")
        btnMusic = loadImage("images/ui/buttons/btn_music_normal.png")
        btnFacebook = loadImage("images/ui/buttons/btn_facebook_normal.png")
        btnTwitter = loadImage("images/ui/buttons/btn_twitter_normal.png")
        btnPlay = loadImage("images/ui/buttons/btn_play_normal.png")
        btnPause = loadImage("images/ui/buttons/btn_pause_normal.png")
        btnMoreGames = loadImage("images/ui/buttons/btn_more_games_normal.png")
        btnShop = loadImage("images/ui/buttons/btn_shop_normal.png")
        btnScores = loadImage("images/ui/buttons/btn_scores_normal.png")
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
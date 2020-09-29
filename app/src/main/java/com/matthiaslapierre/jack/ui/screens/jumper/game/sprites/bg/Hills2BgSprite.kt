package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg

import android.graphics.Bitmap
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg.BgSprite

class Hills2BgSprite(
    private val resourceManager: ResourceManager
) : BgSprite() {

    override fun getBackgroundBitmap(): Bitmap = resourceManager.bgHills2!!.bitmap

}
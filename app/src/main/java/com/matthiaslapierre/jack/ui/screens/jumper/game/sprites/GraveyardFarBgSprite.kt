package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites

import android.graphics.Bitmap
import com.matthiaslapierre.jack.core.ResourceManager

class GraveyardFarBgSprite(
    private val resourceManager: ResourceManager
) : BgSprite() {

    override fun getBackgroundBitmap(): Bitmap = resourceManager.bgGraveyardFar!!.bitmap

}
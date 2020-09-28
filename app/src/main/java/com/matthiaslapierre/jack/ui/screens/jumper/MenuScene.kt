package com.matthiaslapierre.jack.ui.screens.jumper

import android.graphics.Paint
import android.graphics.Rect
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.core.ResourceManager

class MenuScene(
    game: Game
): Screen(game) {

    override fun update() {

    }

    override fun paint() {
        val resourceManager = (game.getGameResources() as ResourceManager)
        val graphics = game.getGraphics()
        val bgImage = resourceManager.bgJump!!
        val canvas = game.getGraphics().getCanvas()

        val screenWidth = graphics.getWidth()
        val screenHeight = graphics.getHeight()
        val originalWidth = bgImage.width
        val originalHeight = bgImage.height
        val scale = screenWidth / bgImage.width.toFloat()
        val finalHeight = (originalHeight * scale).toInt()


        canvas.drawBitmap(
            bgImage.bitmap,
            Rect(
                0,
                0,
                originalWidth,
                originalHeight
            ),
            Rect(
                0,
                screenHeight - finalHeight,
                screenWidth,
                screenHeight
            ),
            Paint()
        )
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onBackPressed() {

    }

}
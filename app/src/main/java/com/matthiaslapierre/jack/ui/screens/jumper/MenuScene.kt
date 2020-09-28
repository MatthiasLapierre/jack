package com.matthiaslapierre.jack.ui.screens.jumper

import android.graphics.Canvas
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

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        drawBackground(canvas)

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onBackPressed() {

    }

    private fun drawBackground(canvas: Canvas) {
        val resourceManager = (game.getGameResources() as ResourceManager)
        val bgImage = resourceManager.bgJump!!

        val screenWidth = canvas.width
        val screenHeight = canvas.height
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

}
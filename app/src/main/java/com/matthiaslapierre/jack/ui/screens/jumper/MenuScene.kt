package com.matthiaslapierre.jack.ui.screens.jumper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.core.ResourceManager

class MenuScene(
    game: Game
): Screen(game) {

    companion object {
        private const val LOGO_RATIO = 0.95f
        private const val PRIMARY_BTN_RATIO = 0.33f
        private const val SECONDARY_BTN_RATIO = 0.25f
        private const val TERNARY_BTN_RATIO = 0.15f
    }

    private var topDstRect: Rect? = null
    private var logoDstRect: Rect? = null
    private var playBtnDstRect: Rect? = null
    private var moreGamesBtnDstRect: Rect? = null
    private var scoreBtnDstRect: Rect? = null

    private var bgImage: Image? = null
    private var logoImage: Image? = null
    private var playBtnImage: Image? = null
    private var moreGamesBtnImage: Image? = null
    private var scoreBtnImage: Image? = null

    override fun update() {
        val resourceManager = (game.getGameResources() as ResourceManager)

        bgImage = resourceManager.bgJump
        logoImage = resourceManager.logoJumperJack
        playBtnImage = resourceManager.btnPlay
        moreGamesBtnImage = resourceManager.btnMoreGames
        scoreBtnImage = resourceManager.btnScores
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        computeDrawingRects(canvas.width, canvas.height)
        drawBackground(canvas, globalPaint)
        drawLogo(canvas, globalPaint)
        drawBtns(canvas, globalPaint)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onBackPressed() {

    }

    private fun computeDrawingRects(screenWidth: Int, screenHeight: Int) {
        var x: Int
        var y: Int = (screenWidth * TERNARY_BTN_RATIO).toInt()

        val titleWidth = (screenWidth * LOGO_RATIO).toInt()
        val titleHeight = (titleWidth * logoImage!!.height / logoImage!!.width.toFloat()).toInt()

        val primaryBtnSize = (screenWidth * PRIMARY_BTN_RATIO).toInt()
        val secondaryBtnSize = (screenWidth * SECONDARY_BTN_RATIO).toInt()
        val btnContainerHeight = primaryBtnSize + secondaryBtnSize

        val space = ((screenHeight - y - titleHeight - btnContainerHeight) / 2f).toInt()

        topDstRect = Rect(0, 0, screenWidth, y)

        x = ((screenWidth - titleWidth) / 2f).toInt()
        logoDstRect = Rect(
            x,
            y,
            x + titleWidth,
            y + titleHeight
        )

        x = ((screenWidth - primaryBtnSize) / 2f).toInt()
        y += titleHeight + space
        playBtnDstRect = Rect(
            x,
            y,
            x + primaryBtnSize,
            y + primaryBtnSize
        )

        y += primaryBtnSize

        x = ((screenWidth - ( 3 * secondaryBtnSize)) / 2f).toInt()
        moreGamesBtnDstRect = Rect(
            x,
            y,
            x + secondaryBtnSize,
            y + secondaryBtnSize
        )

        x = ((screenWidth + (secondaryBtnSize)) / 2f).toInt()
        scoreBtnDstRect = Rect(
            x,
            y,
            x + secondaryBtnSize,
            y + secondaryBtnSize
        )
    }

    private fun drawBackground(canvas: Canvas, globalPaint: Paint) {
        val screenWidth = canvas.width
        val screenHeight = canvas.height
        val originalWidth = bgImage!!.width
        val originalHeight = bgImage!!.height
        val scale = screenWidth / bgImage!!.width.toFloat()
        val finalHeight = (originalHeight * scale).toInt()

        canvas.drawBitmap(
            bgImage!!.bitmap,
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
            globalPaint
        )
    }

    private fun drawTopBar(
        canvas: Canvas,
        globalPaint: Paint) {

    }

    private fun drawLogo(canvas: Canvas, globalPaint: Paint) {
        drawImage(
            canvas,
            globalPaint,
            logoImage!!,
            logoDstRect!!
        )
    }

    private fun drawBtns(canvas: Canvas, globalPaint: Paint) {
        drawImage(
            canvas,
            globalPaint,
            playBtnImage!!,
            playBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            moreGamesBtnImage!!,
            moreGamesBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            scoreBtnImage!!,
            scoreBtnDstRect!!
        )
    }

    private fun drawImage(
        canvas: Canvas,
        globalPaint: Paint,
        image: Image,
        dst: Rect
    ) {
        canvas.drawBitmap(
            image.bitmap,
            Rect(
                0,
                0,
                image.width,
                image.height
            ),
            dst,
            globalPaint
        )
    }

}
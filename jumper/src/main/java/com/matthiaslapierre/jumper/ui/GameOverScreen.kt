package com.matthiaslapierre.jumper.ui

import android.graphics.*
import android.view.MotionEvent
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jumper.utils.JumperUtils

class GameOverScreen(
    game: Game,
    private val lastScore: Int,
    private val highScore: Int,
    private var screenShotBitmap: Bitmap
): Screen(game) {

    companion object {
        private const val WINDOW_WIDTH = 0.90f
        private const val PRIMARY_BTN_WIDTH = 0.5f
        private const val PRIMARY_BTN_MARGIN_TOP = 0.04f
        private const val SECONDARY_BTN_WIDTH = 0.20f
        private const val SECONDARY_BTN_SPACE = 0.02f
        private const val SECONDARY_BTN_BOTTOM = 0.05f
        private const val SCORE_DIGITS_HEIGHT = 0.1f
        private const val LAST_SCORE_Y = .56f
        private const val HIGH_SCORE_Y = .783f
    }

    private var windowImage: Image? = null
    private var replayBtnImage: Image? = null
    private var moreGamesBtnImage: Image? = null
    private var highScoresBtnImage: Image? = null
    private var facebookBtnImage: Image? = null
    private var twitterBtnImage: Image? = null
    private var lastScoreBitmap: Bitmap? = null
    private var highScoreBitmap: Bitmap? = null

    private var facebookBtnIsPressed: Boolean = false
    private var twitterBtnIsPressed: Boolean = false
    private var replayBtnIsPressed: Boolean = false
    private var moreGamesBtnIsPressed: Boolean = false
    private var highScoresBtnIsPressed: Boolean = false

    private var windowDstRect: Rect? = null
    private var replayBtnDstRect: Rect? = null
    private var facebookBtnDstRect: Rect? = null
    private var twitterBtnDstRect: Rect? = null
    private var moreGamesBtnDstRect: Rect? = null
    private var highScoresBtnDstRect: Rect? = null
    private var lastScoreDstRect: Rect? = null
    private var highScoreDstRect: Rect? = null

    override fun update() {
        val resourceManager = (game.getGameResources() as ResourceManager)
        windowImage = resourceManager.windowGameOver
        facebookBtnImage = if(facebookBtnIsPressed) resourceManager.btnFacebookPressed else
            resourceManager.btnFacebook
        twitterBtnImage = if(twitterBtnIsPressed) resourceManager.btnTwitterPressed else
            resourceManager.btnTwitter
        replayBtnImage = if(replayBtnIsPressed) resourceManager.btnReplayPressed else
            resourceManager.btnReplay
        moreGamesBtnImage = if(moreGamesBtnIsPressed) resourceManager.btnMoreGamesPressed else
            resourceManager.btnMoreGames
        highScoresBtnImage = if(highScoresBtnIsPressed) resourceManager.btnScoresPressed else
            resourceManager.btnScores
        lastScoreBitmap = JumperUtils.generateScoreBitmap(resourceManager, lastScore)
        highScoreBitmap = JumperUtils.generateScoreBitmap(resourceManager, highScore)
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        computeDrawingRects(canvas.width, canvas.height)
        drawBackground(canvas, globalPaint)
        drawWindow(canvas, globalPaint)
        drawReplayBtn(canvas, globalPaint)
        drawFooterBtns(canvas, globalPaint)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {
        lastScoreBitmap?.recycle()
        lastScoreBitmap = null
        highScoreBitmap?.recycle()
        highScoreBitmap = null
        screenShotBitmap.recycle()
    }

    override fun onTouch(event: MotionEvent) {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when(event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                replayBtnIsPressed = btnIsPressed(replayBtnDstRect, touchX, touchY)
                facebookBtnIsPressed = btnIsPressed(facebookBtnDstRect, touchX, touchY)
                twitterBtnIsPressed = btnIsPressed(twitterBtnDstRect, touchX, touchY)
                moreGamesBtnIsPressed = btnIsPressed(moreGamesBtnDstRect, touchX, touchY)
                highScoresBtnIsPressed = btnIsPressed(highScoresBtnDstRect, touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                when {
                    replayBtnIsPressed -> game.setScreen(GameScreen(game))
                }
                replayBtnIsPressed = false
                facebookBtnIsPressed = false
                twitterBtnIsPressed = false
                moreGamesBtnIsPressed = false
                highScoresBtnIsPressed = false
            }
        }
    }

    override fun onBackPressed() {
        game.setScreen(GameScreen(game))
    }

    private fun computeDrawingRects(screenWidth: Int, screenHeight: Int) {
        val windowWidth = (screenWidth * WINDOW_WIDTH).toInt()
        val windowHeight = (windowWidth * windowImage!!.height / windowImage!!.width.toFloat()).toInt()

        val primaryBtnWidth = (screenWidth * PRIMARY_BTN_WIDTH).toInt()
        val primaryBtnHeight = (primaryBtnWidth * replayBtnImage!!.height / replayBtnImage!!.width.toFloat()).toInt()
        val primaryBtnMarginTop = (screenWidth * PRIMARY_BTN_MARGIN_TOP).toInt()

        val secondaryBtnWidth = (screenWidth * SECONDARY_BTN_WIDTH).toInt()
        val secondaryBtnHeight = (secondaryBtnWidth * facebookBtnImage!!.height / facebookBtnImage!!.width.toFloat()).toInt()
        val secondaryBtnSpace = (screenWidth * SECONDARY_BTN_SPACE).toInt()
        val secondaryBtnBottom = (screenWidth * SECONDARY_BTN_BOTTOM).toInt()

        val footerX = ((screenWidth - ((secondaryBtnWidth * 4) + (secondaryBtnSpace * 4))) / 2f).toInt()
        val footerY = screenHeight - secondaryBtnBottom - secondaryBtnHeight
        var secondaryBtnX = footerX
        moreGamesBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )
        secondaryBtnX += secondaryBtnWidth + secondaryBtnSpace
        highScoresBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )
        secondaryBtnX += secondaryBtnWidth + secondaryBtnSpace
        facebookBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )
        secondaryBtnX += secondaryBtnWidth + secondaryBtnSpace
        twitterBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )

        val primaryContentSpace = ((screenHeight - secondaryBtnBottom - secondaryBtnHeight - primaryBtnHeight - primaryBtnMarginTop - windowHeight) / 2f).toInt()

        val windowX = ((screenWidth - windowWidth) / 2f).toInt()
        val windowY = primaryContentSpace
        windowDstRect = Rect(
            windowX,
            windowY,
            windowX + windowWidth,
            windowY + windowHeight
        )


        if (lastScoreBitmap != null && highScoreBitmap != null) {
            val scoreHeight = (screenWidth * SCORE_DIGITS_HEIGHT).toInt()
            val lastScoreWidth =
                (scoreHeight * lastScoreBitmap!!.width / lastScoreBitmap!!.height.toFloat()).toInt()
            val lastScoreX = ((screenWidth - lastScoreWidth) / 2f).toInt()
            val lastScoreY = (windowY + windowHeight * LAST_SCORE_Y).toInt()
            lastScoreDstRect = Rect(
                lastScoreX,
                lastScoreY,
                lastScoreX + lastScoreWidth,
                lastScoreY + scoreHeight
            )

            val highScoreWidth =
                (scoreHeight * highScoreBitmap!!.width / highScoreBitmap!!.height.toFloat()).toInt()
            val highScoreX = ((screenWidth - highScoreWidth) / 2f).toInt()
            val highScoreY = (windowY + windowHeight * HIGH_SCORE_Y).toInt()
            highScoreDstRect = Rect(
                highScoreX,
                highScoreY,
                highScoreX + highScoreWidth,
                highScoreY + scoreHeight
            )
        }

        val replayBtnX = ((screenWidth - primaryBtnWidth) / 2f).toInt()
        val replayBtnY = windowDstRect!!.bottom + secondaryBtnSpace
        replayBtnDstRect = Rect(
            replayBtnX,
            replayBtnY,
            replayBtnX + primaryBtnWidth,
            replayBtnY + primaryBtnHeight
        )

    }

    private fun drawBackground(canvas: Canvas, globalPaint: Paint) {
        if (!screenShotBitmap.isRecycled) {
            canvas.drawBitmap(screenShotBitmap, 0f, 0f, globalPaint)
        }
        canvas.drawARGB(150,0,0,0)
    }

    private fun drawWindow(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            windowImage!!.bitmap,
            windowImage!!.rect,
            windowDstRect!!,
            globalPaint
        )

        if (lastScoreBitmap != null) {
            canvas.drawBitmap(
                lastScoreBitmap!!,
                Rect(
                    0,
                    0,
                    lastScoreBitmap!!.width,
                    lastScoreBitmap!!.height
                ),
                lastScoreDstRect!!,
                globalPaint
            )
        }
        if (highScoreBitmap != null) {
            canvas.drawBitmap(
                highScoreBitmap!!,
                Rect(
                    0,
                    0,
                    highScoreBitmap!!.width,
                    highScoreBitmap!!.height
                ),
                highScoreDstRect!!,
                globalPaint
            )
        }
    }

    private fun drawReplayBtn(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            replayBtnImage!!.bitmap,
            replayBtnImage!!.rect,
            replayBtnDstRect!!,
            globalPaint
        )
    }

    private fun drawFooterBtns(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            moreGamesBtnImage!!.bitmap,
            moreGamesBtnImage!!.rect,
            moreGamesBtnDstRect!!,
            globalPaint
        )

        canvas.drawBitmap(
            highScoresBtnImage!!.bitmap,
            highScoresBtnImage!!.rect,
            highScoresBtnDstRect!!,
            globalPaint
        )

        canvas.drawBitmap(
            facebookBtnImage!!.bitmap,
            facebookBtnImage!!.rect,
            facebookBtnDstRect!!,
            globalPaint
        )

        canvas.drawBitmap(
            twitterBtnImage!!.bitmap,
            twitterBtnImage!!.rect,
            twitterBtnDstRect!!,
            globalPaint
        )
    }

    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

}
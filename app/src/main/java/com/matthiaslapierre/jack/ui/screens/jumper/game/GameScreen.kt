package com.matthiaslapierre.jack.ui.screens.jumper.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg.*
import com.matthiaslapierre.jack.utils.Utils

class GameScreen(
    game: Game
): Screen(game) {

    companion object {
        private const val INDICATOR_RATIO = 0.40f
        private const val TERNARY_BTN_RATIO = 0.14f
        private const val TOP_BAR_INSET_X = .13f
        private const val TOP_BAR_INSET_Y = .18f
    }

    private var topBgImage: Image? = null
    private var candyIndicatorImage: Image? = null
    private var pauseBtnImage: Image? = null

    private var workSprites: MutableList<Sprite> = mutableListOf()

    private var score: Int = 0
    private var currentStatus: Sprite.Status = Sprite.Status.STATUS_NOT_STARTED

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    private var pauseBtnIsPressed: Boolean = false

    override fun update() {
        val resourceManager = getResourceManager()
        topBgImage = resourceManager.bgTop
        candyIndicatorImage = resourceManager.candyIndicator
        pauseBtnImage = if(pauseBtnIsPressed) resourceManager.btnPausePressed else
            resourceManager.btnPause
        updateSprites()
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width
        screenHeight = canvas.height
        drawSprites(canvas, globalPaint)
        drawTopBar(canvas, globalPaint)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onTouch(event: MotionEvent) {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when(event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                pauseBtnIsPressed = btnIsPressed(getPauseBtnRect(), touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                pauseBtnIsPressed = false
            }
        }
    }

    override fun onBackPressed() {

    }

    private fun updateSprites() {
        if(workSprites.size == 0) {
            setBackground()
        }
    }

    private fun setBackground() {
        val resourceManager = getResourceManager()
        workSprites.add(
            MoonBgSprite(
                resourceManager
            )
        )
        workSprites.add(
            Hills5BgSprite(
                resourceManager
            )
        )
        workSprites.add(
            Hills4BgSprite(
                resourceManager
            )
        )
        workSprites.add(
            Hills3BgSprite(
                resourceManager
            )
        )
        workSprites.add(
            Hills2BgSprite(
                resourceManager
            )
        )
        workSprites.add(
            Hills1BgSprite(
                resourceManager
            )
        )
        workSprites.add(
            GraveyardFarBgSprite(
                resourceManager
            )
        )
        workSprites.add(
            GraveyardTopBgSprite(
                resourceManager
            )
        )
        workSprites.add(
            GraveyardBottomBgSprite(
                resourceManager
            )
        )
        workSprites.add(
            GateBgSprite(
                resourceManager
            )
        )
    }

    private fun drawTopBar(canvas: Canvas, globalPaint: Paint) {
        drawTopBarBackground(canvas, globalPaint)
        drawCandyIndicator(canvas, globalPaint)
        drawPauseBtn(canvas, globalPaint)
    }

    private fun drawSprites(canvas: Canvas, globalPaint: Paint) {
        val iterator: MutableListIterator<Sprite> = workSprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isAlive()) {
                sprite.onDraw(canvas, globalPaint, currentStatus)
            } else {
                iterator.remove()
                sprite.onDispose()
            }
        }
    }

    private fun drawTopBarBackground(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            topBgImage!!.bitmap,
            Rect(
                0,
                0,
                topBgImage!!.width,
                topBgImage!!.height
            ),
            getTopBarBackgroundRect(),
            globalPaint
        )
    }

    private fun drawCandyIndicator(canvas: Canvas, globalPaint: Paint) {
        val candyIndicatorRect = getCandyIndicatorRect()

        canvas.drawBitmap(
            candyIndicatorImage!!.bitmap,
            Rect(
                0,
                0,
                candyIndicatorImage!!.width,
                candyIndicatorImage!!.height
            ),
            candyIndicatorRect,
            globalPaint
        )

        val scoreBitmap = Utils.generateScoreBitmap(score, getResourceManager())
        val originalScoreWidth = scoreBitmap.width
        val originalScoreHeight = scoreBitmap.height
        val targetScoreHeight = (candyIndicatorRect.height() * .4f).toInt()
        val targetScoreWidth = (targetScoreHeight * originalScoreWidth / originalScoreHeight.toFloat()).toInt()
        val scoreX = (candyIndicatorRect.right - targetScoreWidth - (candyIndicatorRect.width() * .08f)).toInt()
        val scoreY = (candyIndicatorRect.top + ((candyIndicatorRect.height() - targetScoreHeight) / 2f)
                + (candyIndicatorRect.height() * .05f)).toInt()
        canvas.drawBitmap(
            scoreBitmap,
            Rect(0, 0, originalScoreWidth, originalScoreHeight),
            Rect(scoreX, scoreY, scoreX + targetScoreWidth, scoreY + targetScoreHeight),
            globalPaint
        )
        scoreBitmap.recycle()
    }

    private fun drawPauseBtn(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            pauseBtnImage!!.bitmap,
            Rect(
                0,
                0,
                pauseBtnImage!!.width,
                pauseBtnImage!!.height
            ),
            getPauseBtnRect(),
            globalPaint
        )
    }

    private fun getTopBarBackgroundRect(): Rect {
        val originalWidth = topBgImage!!.width
        val originalHeight = topBgImage!!.height
        val barHeight = screenWidth * originalHeight / originalWidth
        return Rect(
            0,
            0,
            screenWidth,
            barHeight
        )
    }

    private fun getCandyIndicatorRect(): Rect {
        val fromWidth = candyIndicatorImage!!.width
        val fromHeight = candyIndicatorImage!!.height
        val toWidth = (screenWidth * INDICATOR_RATIO).toInt()
        val toHeight = (toWidth * fromHeight / fromWidth.toFloat()).toInt()
        val xOutset = (toHeight * TOP_BAR_INSET_X).toInt()
        val yOutset = (toHeight * TOP_BAR_INSET_Y).toInt()
        return Rect(
            xOutset,
            yOutset,
            toWidth + xOutset,
            toHeight + yOutset
        )
    }

    private fun getPauseBtnRect(): Rect {
        val fromWidth = pauseBtnImage!!.width
        val fromHeight = pauseBtnImage!!.height
        val toWidth =  (screenWidth * TERNARY_BTN_RATIO).toInt()
        val toHeight = (toWidth * fromHeight / fromWidth.toFloat()).toInt()
        val xOutset = (toHeight * TOP_BAR_INSET_X).toInt()
        val yOutset = (toHeight * TOP_BAR_INSET_Y).toInt()
        return Rect(
            screenWidth - toWidth - xOutset,
            yOutset,
            screenWidth - xOutset,
            toHeight + yOutset
        )
    }

    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

    private fun getResourceManager(): ResourceManager = game.getGameResources() as ResourceManager

}
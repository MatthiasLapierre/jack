package com.matthiaslapierre.jumper.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.MotionEvent
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.ACCELEROMETER_SENSITIVITY
import com.matthiaslapierre.jumper.core.GameProcessor
import com.matthiaslapierre.jumper.utils.JumperUtils

class GameScreen(
    game: Game
): Screen(game), SensorEventListener {

    companion object {
        private const val INDICATOR_WIDTH = 0.40f
        private const val TERNARY_BTN_WIDTH = 0.14f
        private const val TOP_BAR_INSET_X = .13f
        private const val TOP_BAR_INSET_Y = .18f
        private const val BADGES_SIZE = .12f
        private const val BADGES_X = 0.025f
        private const val BADGES_Y = .22f
    }

    private val sensorManager: SensorManager = (game as Context).getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val gameProcessor =
        GameProcessor(game.getGameResources() as ResourceManager)

    private var topBgImage: Image? = null
    private var candyIndicatorImage: Image? = null
    private var pauseBtnImage: Image? = null

    private var screenWidth: Float = 0f

    private var pauseBtnIsPressed: Boolean = false

    override fun update() {
        val resourceManager = getResourceManager()
        topBgImage = resourceManager.bgTop
        candyIndicatorImage = resourceManager.candyIndicator
        pauseBtnImage = if(pauseBtnIsPressed) resourceManager.btnPausePressed else
            resourceManager.btnPause
        gameProcessor.setFrameRateAdjustFactor(frameRateAdjustFactor)
        gameProcessor.process()
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width.toFloat()
        gameProcessor.paint(canvas, globalPaint)
        drawTopBar(canvas, globalPaint)
    }

    override fun pause() {
        sensorManager.unregisterListener(this)
        gameProcessor.pause()
    }

    override fun resume() {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
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
                if (touchY > getPauseBtnRect().bottom
                    && gameProcessor.getGameStatus() == Sprite.Status.STATUS_NOT_STARTED) {
                    gameProcessor.startGame()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(gameProcessor.getGameStatus() != Sprite.Status.STATUS_PLAY) {
            game.setScreen(MenuScreen(game))
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (Sensor.TYPE_ACCELEROMETER == event?.sensor?.type) {
            val xAcceleration = event.values[0] * ACCELEROMETER_SENSITIVITY * (frameTime / 1000f)
            gameProcessor.moveX(xAcceleration)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun drawTopBar(canvas: Canvas, globalPaint: Paint) {
        drawTopBarBackground(canvas, globalPaint)
        drawCandyIndicator(canvas, globalPaint)
        drawPauseBtn(canvas, globalPaint)
        drawBadges(canvas, globalPaint)
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

        val scoreBitmap = JumperUtils.generateScoreBitmap(
            getResourceManager(),
            gameProcessor.getCandiesCollected()
        )
        val originalScoreWidth = scoreBitmap.width
        val originalScoreHeight = scoreBitmap.height
        val targetScoreHeight = (candyIndicatorRect.height() * .4f).toInt()
        val targetScoreWidth = (targetScoreHeight * originalScoreWidth / originalScoreHeight.toFloat()).toInt()
        val scoreX = (candyIndicatorRect.left + (candyIndicatorRect.width() * .49f)).toInt()
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

    private fun drawBadges(canvas: Canvas, globalPaint: Paint) {
        val badgesBitmap = JumperUtils.generateBadgesBitmap(
            getResourceManager(),
            gameProcessor.getPowerUps()
        )
        val originalWidth = badgesBitmap.width
        val originalHeight = badgesBitmap.height
        val width = screenWidth * BADGES_SIZE
        val height = width * originalHeight / originalWidth
        val x = screenWidth * BADGES_X
        val y = screenWidth * BADGES_Y
        canvas.drawBitmap(
            badgesBitmap,
            Rect(
                0,
                0,
                originalWidth,
                originalHeight
            ),
            RectF(
                x,
                y,
                x + width,
                y + height
            ),
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
            screenWidth.toInt(),
            barHeight.toInt()
        )
    }

    private fun getCandyIndicatorRect(): Rect {
        val fromWidth = candyIndicatorImage!!.width
        val fromHeight = candyIndicatorImage!!.height
        val toWidth = (screenWidth * INDICATOR_WIDTH).toInt()
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
        val toWidth =  (screenWidth * TERNARY_BTN_WIDTH).toInt()
        val toHeight = (toWidth * fromHeight / fromWidth.toFloat()).toInt()
        val xOutset = (toHeight * TOP_BAR_INSET_X).toInt()
        val yOutset = (toHeight * TOP_BAR_INSET_Y).toInt()
        return Rect(
            (screenWidth - toWidth - xOutset).toInt(),
            yOutset,
            (screenWidth - xOutset).toInt(),
            toHeight + yOutset
        )
    }

    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

    private fun getResourceManager(): ResourceManager = game.getGameResources() as ResourceManager

}
package com.matthiaslapierre.jack.ui.screens

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
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.jack.core.resources.SoundManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.ACCELEROMETER_SENSITIVITY
import com.matthiaslapierre.jack.JackApp
import com.matthiaslapierre.jack.utils.Utils
import com.matthiaslapierre.jack.core.game.GameListener
import com.matthiaslapierre.jack.core.game.GameLogic
import com.matthiaslapierre.jack.core.scores.Scores

/**
 * Game screen.
 */
class GameScreen(
    game: Game
): Screen(game), SensorEventListener,
    GameListener {

    companion object {
        private const val INDICATOR_WIDTH = 0.4f
        private const val SCORE_HEIGHT = .4f
        private const val SCORE_INSET_X = .49f
        private const val SCORE_INSET_Y = .05f
        private const val TERNARY_BTN_WIDTH = 0.14f
        private const val TOP_BAR_INSET_X = .13f
        private const val TOP_BAR_INSET_Y = .18f
        private const val BADGES_SIZE = .12f
        private const val BADGES_X = 0.025f
        private const val BADGES_Y = .22f
    }

    /**
     * Manages the game's logic (map generation, ...).
     */
    private var gameLogic: GameLogic = ((game as Context).applicationContext as JackApp)
        .appContainer.gameLogicFactory
        .gameListener(this)
        .create()

    /**
     * Manages scores.
     */
    private var scores: Scores = ((game as Context).applicationContext as JackApp).appContainer.scores

    /**
     * Lets access the device's sensor.
     */
    private val sensorManager: SensorManager = (game as Context).getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Images to display.
    private var topBgImage: Image? = null
    private var candyIndicatorImage: Image? = null
    private var pauseBtnImage: Image? = null

    // Screen width.
    private var screenWidth: Float = 0f

    // Determines if the pause button has been pressed.
    private var pauseBtnIsPressed: Boolean = false

    override fun update() {
        val resourceManager = getResourceManager()
        topBgImage = resourceManager.bgTop
        candyIndicatorImage = resourceManager.candyIndicator
        pauseBtnImage = getPauseBtnImage()
        gameLogic.gameProcessor.setFrameRateAdjustFactor(frameRateAdjustFactor)
        gameLogic.gameProcessor.process()
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width.toFloat()
        gameLogic.gameProcessor.paint(canvas, globalPaint)
        drawTopBar(canvas, globalPaint)
    }

    override fun pause() {
        sensorManager.unregisterListener(this)
        gameLogic.gameProcessor.pause()
        if (gameLogic.gameStates.currentStatus == Sprite.Status.STATUS_PAUSE) {
            game.getAudio().pause()
        }
    }

    override fun resume() {
        if (gameLogic.gameStates.currentStatus == Sprite.Status.STATUS_PAUSE) {
            game.getAudio().pause()
        }
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun dispose() {
        gameLogic.gameProcessor.dispose()
    }

    override fun onTouch(event: MotionEvent) {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when(event.action) {
            // Determines if the pause/play button has been pressed.
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                pauseBtnIsPressed = btnIsPressed(getPauseBtnRect(), touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                if (pauseBtnIsPressed) {
                    togglePauseResume()
                    (game.getAudio() as SoundManager).playButtonPressedSound()
                } else if (touchY > getPauseBtnRect().bottom
                    && gameLogic.gameProcessor.getGameStatus() == Sprite.Status.STATUS_NOT_STARTED) {
                    startGame()
                }
                pauseBtnIsPressed = false
            }
        }
    }

    override fun onBackPressed() {
        if(gameLogic.gameProcessor.getGameStatus() != Sprite.Status.STATUS_PLAY) {
            game.setScreen(MenuScreen(game))
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (Sensor.TYPE_ACCELEROMETER == event?.sensor?.type) {
            val xAcceleration = event.values[0] * ACCELEROMETER_SENSITIVITY * (frameTime / 1000f)
            gameLogic.gameProcessor.moveX(xAcceleration)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    //region JumperGameListener

    override fun onJump() {
        (game.getAudio() as SoundManager).playJumpSound()
    }

    override fun onGameOver(candiesCollected: Int) {
        // Save the high score.
        if (scores.isNewBestScore(candiesCollected)) {
            scores.storeHighScore(candiesCollected)
        }
        // Stop sounds.
        (game.getAudio() as SoundManager).stopFlightSound()
        // Play the "Game Over" theme music.
        (game.getAudio() as SoundManager).playGameOverMusic()
        // Show the "Game over" screen.
        game.setScreen(
            GameOverScreen(
                game,
                candiesCollected,
                scores.highScore(),
                game.takeScreenShot()
            )
        )
    }

    override fun onDie() {
        (game.getAudio() as SoundManager).playDieSound()
    }

    override fun onCollectCandies() {
        (game.getAudio() as SoundManager).playCollectCandySound()
    }

    override fun onGetPowerUp() {
        (game.getAudio() as SoundManager).playGetPowerUpSound()
    }

    override fun onHit() {
        (game.getAudio() as SoundManager).playHitSound()
    }

    override fun onDestroyEnemy() {
        (game.getAudio() as SoundManager).playDestroyEnemySound()
    }

    override fun onRocketFlight() {
        (game.getAudio() as SoundManager).playRocketFlightSound()
    }

    override fun onCopterFlight() {
        (game.getAudio() as SoundManager).playCopterFlightSound()
    }

    override fun onNoFlight() {
        (game.getAudio() as SoundManager).stopFlightSound()
    }

    //endregion JumperGameListener


    /**
     * Starts the game.
     */
    private fun startGame() {
        (game.getAudio() as SoundManager).playGameMusic()
        gameLogic.gameProcessor.startGame()
    }

    /**
     * Changes the "Pause" / "Play" button state depending on the state of the game.
     */
    private fun togglePauseResume() {
        if (gameLogic.gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            gameLogic.gameProcessor.pause()
            (game.getAudio() as SoundManager).pause()
        } else if (gameLogic.gameStates.currentStatus == Sprite.Status.STATUS_PAUSE) {
            gameLogic.gameProcessor.resume()
            (game.getAudio() as SoundManager).resume()
        }
    }

    /**
     * Returns the image to show in the "Pause" / "Play" button.
     */
    private fun getPauseBtnImage(): Image {
        val resourceManager = getResourceManager()
        return if (gameLogic.gameStates.currentStatus == Sprite.Status.STATUS_PAUSE) {
            if (pauseBtnIsPressed) {
                resourceManager.btnPlayPressed!!
            } else {
                resourceManager.btnPlay!!
            }
        } else {
            if (pauseBtnIsPressed) {
                resourceManager.btnPausePressed!!
            } else {
                resourceManager.btnPause!!
            }
        }
    }

    /**
     * Draws the top bar containing the scores, the pause button and the badges.
     */
    private fun drawTopBar(canvas: Canvas, globalPaint: Paint) {
        drawTopBarBackground(canvas, globalPaint)
        drawCandyIndicator(canvas, globalPaint)
        drawPauseBtn(canvas, globalPaint)
        drawBadges(canvas, globalPaint)
    }

    /**
     * Draws the top bar.
     */
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

    /**
     * Draws the score.
     */
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

        val scoreBitmap = Utils.generateScoreBitmap(
            getResourceManager(),
            gameLogic.gameProcessor.getCandiesCollected()
        )
        val originalScoreWidth = scoreBitmap.width
        val originalScoreHeight = scoreBitmap.height
        val targetScoreHeight = (candyIndicatorRect.height() * SCORE_HEIGHT).toInt()
        val targetScoreWidth = (targetScoreHeight * originalScoreWidth / originalScoreHeight.toFloat()).toInt()
        val scoreX = (candyIndicatorRect.left + (candyIndicatorRect.width() * SCORE_INSET_X)).toInt()
        val scoreY = (candyIndicatorRect.top + ((candyIndicatorRect.height() - targetScoreHeight) / 2f)
                + (candyIndicatorRect.height() * SCORE_INSET_Y)).toInt()
        canvas.drawBitmap(
            scoreBitmap,
            Rect(0, 0, originalScoreWidth, originalScoreHeight),
            Rect(scoreX, scoreY, scoreX + targetScoreWidth, scoreY + targetScoreHeight),
            globalPaint
        )
        scoreBitmap.recycle()
    }

    /**
     * Draws the pause / play button.
     */
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

    /**
     * Draws the badges representing the power-up enabled.
     */
    private fun drawBadges(canvas: Canvas, globalPaint: Paint) {
        val badgesBitmap = Utils.generateBadgesBitmap(
            getResourceManager(),
            gameLogic.gameProcessor.getPowerUps()
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

    /**
     * Draws the top bar background.
     */
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

    /**
     * Draws the score.
     */
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

    /**
     * Gets the image to display in the pause/play button.
     */
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

    /**
     * If the location specified is pressed.
     */
    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

    private fun getResourceManager(): ResourceManager = game.getGameResources() as ResourceManager

}
package com.matthiaslapierre.jack.core.game.impl

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.jack.core.PlayerState
import com.matthiaslapierre.jack.core.game.GameListener
import com.matthiaslapierre.jack.core.game.GameMap
import com.matthiaslapierre.jack.core.game.GameProcessor
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.impl.sprites.bg.BgSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.bg.CloudSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.bg.FloorSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.collectibles.CandySprite
import com.matthiaslapierre.jack.core.game.impl.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.obstacles.BatSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.obstacles.SpikeSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.player.PlayerSprite
import com.matthiaslapierre.jack.core.game.impl.sprites.text.TapToLaunchSprite
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.jack.utils.hasFlag
import java.util.*

internal class JackGameProcessor(
    override val resourceManager: ResourceManager,
    override val gameStates: GameStates,
    override val gameMap: GameMap,
    override var gameListener: GameListener?
): GameProcessor {

    companion object {
        private const val MIN_CLOUDS = 20
    }

    private val backgroundSprites: MutableList<Sprite> = mutableListOf()
    private val foregroundSprites: MutableList<Sprite> = mutableListOf()
    private val playerSprite: PlayerSprite = PlayerSprite(resourceManager, gameStates)

    private var tapToLaunchSprite: TapToLaunchSprite? = null
    private var bgSprite: BgSprite? = null
    private var floorSprite: FloorSprite? = null
    private var cloudInterval: Float = Constants.UNDEFINED
    private var countClouds: Int = 0

    private var freeFall: Float? = null

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var powerUpTimersRunnable: Runnable? = null
    private var powerUpTimers: Hashtable<Int, Int> = Hashtable()

    override fun process() {
        if (gameStates.currentStatus == Sprite.Status.STATUS_PAUSE) {
            return
        } else if (gameStates.currentStatus == Sprite.Status.STATUS_GAME_OVER
            && playerSprite.y > screenHeight) {
            gameListener?.onGameOver(gameStates.candiesCollected)
        }

        updateSprites()
        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            checkCollisions()
            catchFreeFall()
        }
        updatePowerUps()
        updateStates()
    }

    override fun resume() {
        if (gameStates.currentStatus == Sprite.Status.STATUS_PAUSE) {
            gameStates.currentStatus = Sprite.Status.STATUS_PLAY
        }
    }

    override fun pause() {
        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            gameStates.currentStatus = Sprite.Status.STATUS_PAUSE
            stopPowerUpTimers()
        }
    }

    override fun dispose() {
        gameListener = null
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width.toFloat()
        screenHeight = canvas.height.toFloat()
        gameStates.setScreenSize(screenWidth)
        gameMap.setScreenSize(screenWidth, screenHeight)
        cloudInterval = screenWidth * Constants.CLOUD_INTERVAL
        drawSprites(canvas, globalPaint)
    }

    override fun startGame() {
        gameStates.currentStatus = Sprite.Status.STATUS_PLAY
        gameStates.playerState = PlayerState.JUMP
        gameStates.jump()
    }

    override fun gameOver() {
        gameStates.gameOver()
        gameListener?.onDie()
    }

    override fun moveX(xAcceleration: Float) {
        gameStates.moveX(xAcceleration)
    }

    override fun getGameStatus(): Sprite.Status = gameStates.currentStatus

    override fun getCandiesCollected(): Int = gameStates.candiesCollected

    override fun getPowerUps(): Int = gameStates.powerUp

    override fun setFrameRateAdjustFactor(frameRateAdjustFactor: Float) {
        gameStates.frameRateAdjustFactor = frameRateAdjustFactor
    }

    /**
     * Updates sprites position. Creates a new background and new floor if needed.
     * Generates the new sprites.
     */
    private fun updateSprites() {
        if(screenHeight == 0f) return
        addBackgroundLayers()
        setFloor()
        foregroundSprites.addAll(gameMap.generate())
        if (getGameStatus() == Sprite.Status.STATUS_NOT_STARTED) {
            setTapToLaunch()
        }
    }

    /**
     * Updates the player position.
     */
    private fun updateStates() {
        gameStates.update(playerSprite.y, playerSprite.lowestY, playerSprite.highestY)
    }

    /**
     * Catches free fall and updates the game state if needed.
     */
    private fun catchFreeFall() {
        freeFall = if (gameStates.direction == GameStates.Direction.DOWN) {
            if (freeFall != null) {
                freeFall!! - gameStates.globalSpeedY
            } else {
                0f
            }
        } else {
            null
        }

        val freeFallMax = screenHeight * Constants.FREE_FALL_MAX
        if (freeFall != null && freeFall!! > freeFallMax) {
            gameOver()
        }
    }

    /**
     * Enables a power-up. Updates timers.
     */
    private fun addPowerUp(powerUp: Int) {
        gameListener?.onGetPowerUp()
        gameStates.addPowerUp(powerUp)
        val timer = when (powerUp) {
            GameStates.POWER_UP_ROCKET -> {
                Constants.ROCKET_TIMER
            }
            GameStates.POWER_UP_MAGNET -> {
                Constants.MAGNET_TIMER
            }
            GameStates.POWER_UP_COPTER -> {
                Constants.COPTER_TIMER
            }
            else -> {
                -1
            }
        }
        powerUpTimers[powerUp] = timer
    }

    /**
     * Adds the background to the list of sprites.
     */
    private fun addBackgroundLayers() {
        if (cloudInterval == Constants.UNDEFINED) {
            return
        }

        if (bgSprite == null) {
            bgSprite = BgSprite(resourceManager, gameStates)
            backgroundSprites.add(bgSprite!!)
        }

        var nextCloudY = -(screenWidth * Constants.FIRST_CLOUD_Y)
        if(backgroundSprites.size > 1) {
            val lastCloudSprite = backgroundSprites.last()
            nextCloudY = lastCloudSprite.y - cloudInterval
        }
        while(countClouds < MIN_CLOUDS) {
            backgroundSprites.add(CloudSprite(resourceManager, gameStates, nextCloudY))
            nextCloudY -= cloudInterval
            countClouds++
        }
    }

    /**
     * Checks collisions between the player and the others sprites.
     */
    private fun checkCollisions() {
        val iterator: MutableListIterator<Sprite> = foregroundSprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isHit(playerSprite)) {
                when(sprite) {
                    is CandySprite -> {
                        // Update the score and destroy the candy.
                        collectCandies(sprite.getScore())
                        sprite.isCollected = true
                    }
                    is PowerUpSprite -> {
                        // Update the score and destroy the candy.
                        // Enable the associated power-up
                        collectCandies(sprite.getScore())
                        addPowerUp(sprite.powerUp)
                        sprite.isConsumed = true
                    }
                    is JumpingPlatformSprite -> {
                        gameListener?.onJump()
                        // Update the player speed.
                        gameStates.jump()
                        // Start the bounce animation.
                        sprite.bounce()
                    }
                    is BatSprite -> {
                        if (gameStates.powerUp.hasFlag(GameStates.POWER_UP_ARMORED)
                            || gameStates.powerUp.hasFlag(GameStates.POWER_UP_ROCKET)) {
                            // Power-ups will be lost if damage is taken.
                            sprite.destroy()
                            // Destroy the enemy.
                            gameStates.removeAllPowerUps()
                            gameListener?.onDestroyEnemy()
                        } else if (sprite.blowOnTheHead(playerSprite)) {
                            // The player jumped on the head of the bat.
                            // Destroy the bat.
                            sprite.destroy()
                            gameStates.jump()
                            gameListener?.onDestroyEnemy()
                        } else {
                            // Game Over.
                            gameListener?.onHit()
                            gameOver()
                        }
                    }
                    is SpikeSprite -> {
                        if (gameStates.powerUp.hasFlag(GameStates.POWER_UP_ARMORED)
                            || gameStates.powerUp.hasFlag(GameStates.POWER_UP_ROCKET)) {
                            // Power-ups will be lost if damage is taken.
                            // Disables the enemy.
                            sprite.destroy()
                            gameStates.removeAllPowerUps()
                        } else {
                            // Game Over.
                            gameListener?.onHit()
                            gameOver()
                        }
                    }
                    is FloorSprite -> {
                        gameStates.jump()
                    }
                }
            } else if (gameStates.powerUp.hasFlag(GameStates.POWER_UP_MAGNET)
                && sprite is CandySprite
            ) {
                // Collect candies in the surrounding area.
                if (playerSprite.getMagnetRangeRectF().intersect(sprite.getRectF())
                    && !sprite.isCollected) {
                    collectCandies(sprite.getScore())
                    sprite.isCollected = true
                }
            }
        }
    }

    /**
     * Adds the floor sprite.
     */
    private fun setFloor() {
        if (floorSprite == null) {
            floorSprite = FloorSprite(gameStates)
            foregroundSprites.add(floorSprite!!)
        }
    }

    /**
     * Shows the "Tap to launch" text.
     */
    private fun setTapToLaunch() {
        if (tapToLaunchSprite == null) {
            tapToLaunchSprite = TapToLaunchSprite(resourceManager)
            foregroundSprites.add(tapToLaunchSprite!!)
        }
    }

    /**
     * Draws sprites.
     */
    private fun drawSprites(canvas: Canvas, globalPaint: Paint) {
        drawSprites(canvas, globalPaint, backgroundSprites)
        drawSprites(canvas, globalPaint, foregroundSprites)
        playerSprite.onDraw(canvas, globalPaint, gameStates.currentStatus)
    }

    /**
     * Draws a collection of sprites.
     */
    private fun drawSprites(canvas: Canvas, globalPaint: Paint, sprites: MutableList<Sprite>) {
        val iterator: MutableListIterator<Sprite> = sprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isAlive()) {
                sprite.onDraw(canvas, globalPaint, gameStates.currentStatus)
            } else {
                when (sprite) {
                    is CloudSprite -> countClouds--
                }
                iterator.remove()
                sprite.onDispose()
            }
        }
    }

    /**
     * Updates the score.
     */
    private fun collectCandies(candies: Int) {
        gameStates.collectCandies(candies)
        gameListener?.onCollectCandies()
    }

    /**
     * Updates the list of power-ups enabled.
     */
    private fun updatePowerUps() {
        if (gameStates.hasPowerUps() && gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            startPowerUpTimers()
        } else {
            stopPowerUpTimers()
        }
        when {
            gameStates.powerUp.hasFlag(GameStates.POWER_UP_ROCKET) -> {
                gameListener?.onRocketFlight()
            }
            gameStates.powerUp.hasFlag(GameStates.POWER_UP_COPTER) -> {
                gameListener?.onCopterFlight()
            }
            else -> {
                gameListener?.onNoFlight()
            }
        }
    }

    /**
     * Starts the timer to automatically disable power-ups.
     */
    private fun startPowerUpTimers() {
        if (powerUpTimersRunnable == null) {
            powerUpTimersRunnable = Runnable {
                val powerUpFlags = arrayOf(
                    GameStates.POWER_UP_ROCKET,
                    GameStates.POWER_UP_MAGNET,
                    GameStates.POWER_UP_ARMORED,
                    GameStates.POWER_UP_COPTER
                )
                for (flag in powerUpFlags) {
                    powerUpTimers[flag]?.let { timer ->
                        if (timer == 0) {
                            gameStates.removePowerUp(flag)
                        } else if (timer > 0) {
                            powerUpTimers[flag] = timer - 1
                        }
                    }
                }
                if (gameStates.hasPowerUps()) {
                    handler.postDelayed(powerUpTimersRunnable!!, 1000)
                }
            }
            handler.postDelayed(powerUpTimersRunnable!!, 1000)
        }
    }

    /**
     * Stops the timer.
     */
    private fun stopPowerUpTimers() {
        if (powerUpTimersRunnable != null) {
            handler.removeCallbacks(powerUpTimersRunnable!!)
            powerUpTimersRunnable = null
        }
    }

}
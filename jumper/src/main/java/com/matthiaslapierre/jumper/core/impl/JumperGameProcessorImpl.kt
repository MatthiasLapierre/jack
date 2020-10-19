package com.matthiaslapierre.jumper.core.impl

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.core.JumperGameListener
import com.matthiaslapierre.jumper.core.JumperGameMap
import com.matthiaslapierre.jumper.core.JumperGameProcessor
import com.matthiaslapierre.jumper.core.JumperGameStates
import com.matthiaslapierre.jumper.core.impl.sprites.bg.BgSprite
import com.matthiaslapierre.jumper.core.impl.sprites.bg.CloudSprite
import com.matthiaslapierre.jumper.core.impl.sprites.bg.FloorSprite
import com.matthiaslapierre.jumper.core.impl.sprites.collectibles.CandySprite
import com.matthiaslapierre.jumper.core.impl.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jumper.core.impl.sprites.obstacles.BatSprite
import com.matthiaslapierre.jumper.core.impl.sprites.obstacles.SpikeSprite
import com.matthiaslapierre.jumper.core.impl.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.jumper.core.impl.sprites.player.PlayerSprite
import com.matthiaslapierre.jumper.core.impl.sprites.text.TapToLaunchSprite
import com.matthiaslapierre.jumper.utils.hasFlag
import java.util.*

internal class JumperGameProcessorImpl(
    override val resourceManager: ResourceManager,
    override val gameStates: JumperGameStates,
    override val gameMap: JumperGameMap,
    override var gameListener: JumperGameListener?
): JumperGameProcessor {

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
        gameStates.currentStatus = Sprite.Status.STATUS_PLAY
    }

    override fun pause() {
        gameStates.currentStatus = Sprite.Status.STATUS_PAUSE
        stopPowerUpTimers()
    }

    override fun dispose() {
        gameListener = null
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width.toFloat()
        screenHeight = canvas.height.toFloat()
        gameStates.setScreenSize(screenWidth)
        gameMap.setScreenSize(screenWidth, screenHeight)
        cloudInterval = screenWidth * JumperConstants.CLOUD_INTERVAL
        drawSprites(canvas, globalPaint)
    }

    override fun startGame() {
        gameStates.currentStatus = Sprite.Status.STATUS_PLAY
        gameStates.playerState = ResourceManager.PlayerState.JUMP
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

    private fun updateSprites() {
        if(screenHeight == 0f) return
        addBackgroundLayers()
        setFloor()
        foregroundSprites.addAll(gameMap.generate())
        if (getGameStatus() == Sprite.Status.STATUS_NOT_STARTED) {
            setTapToLaunch()
        }
    }

    private fun updateStates() {
        gameStates.update(playerSprite.y, playerSprite.lowestY, playerSprite.highestY)
    }

    private fun catchFreeFall() {
        freeFall = if (gameStates.direction == JumperGameStates.Direction.DOWN) {
            if (freeFall != null) {
                freeFall!! - gameStates.globalSpeedY
            } else {
                0f
            }
        } else {
            null
        }

        val freeFallMax = screenHeight * JumperConstants.FREE_FALL_MAX
        if (freeFall != null && freeFall!! > freeFallMax) {
            gameOver()
        }
    }

    private fun addPowerUp(powerUp: Int) {
        gameStates.addPowerUp(powerUp)
        val timer = when (powerUp) {
            JumperGameStates.POWER_UP_ROCKET -> {
                JumperConstants.ROCKET_TIMER
            }
            JumperGameStates.POWER_UP_MAGNET -> {
                JumperConstants.MAGNET_TIMER
            }
            JumperGameStates.POWER_UP_COPTER -> {
                JumperConstants.COPTER_TIMER
            }
            else -> {
                -1
            }
        }
        powerUpTimers[powerUp] = timer
    }

    private fun addBackgroundLayers() {
        if (cloudInterval == Constants.UNDEFINED) {
            return
        }

        if (bgSprite == null) {
            bgSprite = BgSprite(resourceManager, gameStates)
            backgroundSprites.add(bgSprite!!)
        }

        var nextCloudY = -(screenWidth * JumperConstants.FIRST_CLOUD_Y)
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

    private fun checkCollisions() {
        val iterator: MutableListIterator<Sprite> = foregroundSprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isHit(playerSprite)) {
                when(sprite) {
                    is CandySprite -> {
                        collectCandies(sprite.getScore())
                        sprite.isCollected = true
                    }
                    is PowerUpSprite -> {
                        collectCandies(sprite.getScore())
                        addPowerUp(sprite.powerUp)
                        sprite.isConsumed = true
                    }
                    is JumpingPlatformSprite -> {
                        gameListener?.onJump()
                        gameStates.jump()
                        sprite.bounce()
                    }
                    is BatSprite -> {
                        if (gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_ARMORED)
                            || gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_ROCKET)) {
                            // Power-ups will be lost if damage is taken.
                            sprite.destroy()
                            gameStates.removeAllPowerUps()
                            gameListener?.onDestroyEnemy()
                        } else if (sprite.blowOnTheHead(playerSprite)) {
                            sprite.destroy()
                            gameStates.jump()
                            gameListener?.onDestroyEnemy()
                        } else {
                            gameListener?.onHit()
                            gameOver()
                        }
                    }
                    is SpikeSprite -> {
                        if (gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_ARMORED)
                            || gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_ROCKET)) {
                            // Power-ups will be lost if damage is taken.
                            sprite.destroy()
                            gameStates.removeAllPowerUps()
                        } else {
                            gameListener?.onHit()
                            gameOver()
                        }
                    }
                    is FloorSprite -> {
                        gameStates.jump()
                    }
                }
            } else if (gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_MAGNET)
                && sprite is CandySprite
            ) {
                if (playerSprite.getMagnetRangeRectF().intersect(sprite.getRectF())
                    && !sprite.isCollected) {
                    collectCandies(sprite.getScore())
                    sprite.isCollected = true
                }
            }
        }
    }

    private fun setFloor() {
        if (floorSprite == null) {
            floorSprite = FloorSprite(gameStates)
            foregroundSprites.add(floorSprite!!)
        }
    }

    private fun setTapToLaunch() {
        if (tapToLaunchSprite == null) {
            tapToLaunchSprite = TapToLaunchSprite(resourceManager)
            foregroundSprites.add(tapToLaunchSprite!!)
        }
    }

    private fun drawSprites(canvas: Canvas, globalPaint: Paint) {
        drawSprites(canvas, globalPaint, backgroundSprites)
        drawSprites(canvas, globalPaint, foregroundSprites)
        playerSprite.onDraw(canvas, globalPaint, gameStates.currentStatus)
    }

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

    private fun collectCandies(candies: Int) {
        gameStates.collectCandies(candies)
        gameListener?.onCollectCandies()
    }

    private fun updatePowerUps() {
        if (gameStates.hasPowerUps() && gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            startPowerUpTimers()
        } else {
            stopPowerUpTimers()
        }
        when {
            gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_ROCKET) -> {
                gameListener?.onRocketFlight()
            }
            gameStates.powerUp.hasFlag(JumperGameStates.POWER_UP_COPTER) -> {
                gameListener?.onCopterFlight()
            }
            else -> {
                gameListener?.onNoFlight()
            }
        }
    }

    private fun startPowerUpTimers() {
        if (powerUpTimersRunnable == null) {
            powerUpTimersRunnable = Runnable {
                val powerUpFlags = arrayOf(
                    JumperGameStates.POWER_UP_ROCKET,
                    JumperGameStates.POWER_UP_MAGNET,
                    JumperGameStates.POWER_UP_ARMORED,
                    JumperGameStates.POWER_UP_COPTER
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

    private fun stopPowerUpTimers() {
        if (powerUpTimersRunnable != null) {
            handler.removeCallbacks(powerUpTimersRunnable!!)
            powerUpTimersRunnable = null
        }
    }

}
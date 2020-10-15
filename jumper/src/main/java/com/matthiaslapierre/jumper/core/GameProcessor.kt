package com.matthiaslapierre.jumper.core

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.CLOUD_INTERVAL
import com.matthiaslapierre.jumper.JumperConstants.FIRST_CLOUD_Y
import com.matthiaslapierre.jumper.JumperConstants.FREE_FALL_MAX
import com.matthiaslapierre.jumper.JumperConstants.MAGNET_RANGE_X
import com.matthiaslapierre.jumper.JumperConstants.MAGNET_RANGE_Y
import com.matthiaslapierre.jumper.JumperConstants.ROCKET_TIMER
import com.matthiaslapierre.jumper.core.sprites.bg.BgSprite
import com.matthiaslapierre.jumper.core.sprites.bg.CloudSprite
import com.matthiaslapierre.jumper.core.sprites.bg.FloorSprite
import com.matthiaslapierre.jumper.core.sprites.collectibles.CandySprite
import com.matthiaslapierre.jumper.core.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jumper.core.sprites.obstacles.BatSprite
import com.matthiaslapierre.jumper.core.sprites.obstacles.SpikeSprite
import com.matthiaslapierre.jumper.core.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite
import com.matthiaslapierre.jumper.core.sprites.text.TapToLaunchSprite
import com.matthiaslapierre.jumper.utils.hasFlag

class GameProcessor(
    private val resourceManager: ResourceManager
) {

    companion object {
        private const val MIN_CLOUDS = 20
    }

    private val backgroundSprites: MutableList<Sprite> = mutableListOf()
    private val foregroundSprites: MutableList<Sprite> = mutableListOf()
    private val playerSprite: PlayerSprite

    private var tapToLaunchSprite: TapToLaunchSprite? = null
    private var bgSprite: BgSprite? = null
    private var floorSprite: FloorSprite? = null
    private var cloudInterval: Float = Constants.UNDEFINED
    private var countClouds: Int = 0

    private var freeFall: Float? = null

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private val gameState: GameStates = GameStates()
    private val gameMap: GameMap = GameMap(resourceManager, gameState)

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var rocketTimerRunnable: Runnable? = null
    private var rocketTimer: Int = 0

    init {
        playerSprite = PlayerSprite(resourceManager, gameState)
    }

    fun process() {
        updateSprites()
        if(gameState.currentStatus == Sprite.Status.STATUS_PLAY) {
            checkCollisions()
            catchFreeFall()
            updateRocketTimer()
        }
        updateStates()
    }

    fun pause() {
        stopRocketTimer()
    }

    fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width.toFloat()
        screenHeight = canvas.height.toFloat()
        gameState.setScreenSize(screenWidth)
        gameMap.setScreenSize(screenWidth, screenHeight)
        cloudInterval = screenWidth * CLOUD_INTERVAL
        drawSprites(canvas, globalPaint)
    }

    fun startGame() {
        gameState.currentStatus = Sprite.Status.STATUS_PLAY
        gameState.playerState = ResourceManager.PlayerState.JUMP
        gameState.jump()
    }

    fun gameOver() {
        gameState.gameOver()
    }

    fun moveX(xAcceleration: Float) {
        gameState.moveX(xAcceleration)
    }

    fun getGameStatus(): Sprite.Status = gameState.currentStatus

    fun getCandiesCollected(): Int = gameState.candiesCollected

    fun getPowerUps(): Int = gameState.powerUp

    fun setFrameRateAdjustFactor(frameRateAdjustFactor: Float) {
        gameState.frameRateAdjustFactor = frameRateAdjustFactor
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
        gameState.update(playerSprite.y, playerSprite.lowestY, playerSprite.highestY)
    }

    private fun catchFreeFall() {
        freeFall = if (gameState.direction == GameStates.Direction.DOWN) {
            if (freeFall != null) {
                freeFall!! - gameState.globalSpeedY
            } else {
                0f
            }
        } else {
            null
        }

        val freeFallMax = screenWidth * FREE_FALL_MAX
        if (freeFall != null && freeFall!! > freeFallMax) {
            gameOver()
        }
    }

    private fun addPowerUp(powerUp: Int) {
        gameState.addPowerUp(powerUp)
        if (powerUp == GameStates.POWER_UP_ROCKET) {
            rocketTimer = ROCKET_TIMER
        }
    }

    private fun addBackgroundLayers() {
        if (cloudInterval == Constants.UNDEFINED) {
            return
        }

        if (bgSprite == null) {
            bgSprite = BgSprite(resourceManager, gameState)
            backgroundSprites.add(bgSprite!!)
        }

        var nextCloudY = -(screenWidth * FIRST_CLOUD_Y)
        if(backgroundSprites.size > 1) {
            val lastCloudSprite = backgroundSprites.last()
            nextCloudY = lastCloudSprite.y - cloudInterval
        }
        while(countClouds < MIN_CLOUDS) {
            backgroundSprites.add(CloudSprite(resourceManager, gameState, nextCloudY))
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
                        gameState.collectCandies(sprite.getScore())
                        sprite.isCollected = true
                    }
                    is PowerUpSprite -> {
                        gameState.collectCandies(sprite.getScore())
                        addPowerUp(sprite.powerUp)
                        sprite.isConsumed = true
                    }
                    is JumpingPlatformSprite -> {
                        gameState.jump()
                        sprite.bounce()
                    }
                    is BatSprite, is SpikeSprite -> {
                        if (gameState.powerUp.hasFlag(GameStates.POWER_UP_ARMORED)) {
                            // Shield will be lost if damage is taken.
                            gameState.removeAllPowerUps()
                        } else {
                            gameOver()
                        }
                    }
                    is FloorSprite -> {
                        gameState.jump()
                    }
                }
            } else if (gameState.powerUp.hasFlag(GameStates.POWER_UP_MAGNET)
                && sprite is CandySprite) {
                val minX = playerSprite.x - (screenWidth * MAGNET_RANGE_X)
                val maxX = playerSprite.x + (screenWidth * MAGNET_RANGE_X)
                val minY = playerSprite.y - (screenWidth * MAGNET_RANGE_Y)
                val maxY = playerSprite.y + (screenWidth * MAGNET_RANGE_Y)
                 if (sprite.x in minX..maxX && sprite.y in minY..maxY && !sprite.isCollected) {
                     gameState.collectCandies(sprite.getScore())
                     sprite.isCollected = true
                 }
            }
        }
    }

    private fun setFloor() {
        if (floorSprite == null) {
            floorSprite = FloorSprite(gameState)
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
        playerSprite.onDraw(canvas, globalPaint, gameState.currentStatus)
    }

    private fun drawSprites(canvas: Canvas, globalPaint: Paint, sprites: MutableList<Sprite>) {
        val iterator: MutableListIterator<Sprite> = sprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isAlive()) {
                sprite.onDraw(canvas, globalPaint, gameState.currentStatus)
            } else {
                when (sprite) {
                    is CloudSprite -> countClouds--
                }
                iterator.remove()
                sprite.onDispose()
            }
        }
    }

    private fun updateRocketTimer() {
        if (gameState.powerUp.hasFlag(GameStates.POWER_UP_ROCKET)) {
            startRocketTimer()
        } else {
            stopRocketTimer()
        }
    }

    private fun startRocketTimer() {
        if (rocketTimerRunnable == null) {
            rocketTimerRunnable = Runnable {
                if (rocketTimer == 0) {
                    gameState.removePowerUp(GameStates.POWER_UP_ROCKET)
                    stopRocketTimer()
                } else {
                    rocketTimer--
                    handler.postDelayed(rocketTimerRunnable!!, 1000)
                }
            }
            handler.postDelayed(rocketTimerRunnable!!, 1000)
        }
    }

    private fun stopRocketTimer() {
        if (rocketTimerRunnable != null) {
            handler.removeCallbacks(rocketTimerRunnable!!)
            rocketTimerRunnable = null
        }
    }

}
package com.matthiaslapierre.jumper.core.impl

import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.core.JumperGameStates
import com.matthiaslapierre.jumper.core.JumperGameStates.CameraMovement
import com.matthiaslapierre.jumper.core.JumperGameStates.Companion.POWER_UP_COPTER
import com.matthiaslapierre.jumper.core.JumperGameStates.Companion.POWER_UP_ROCKET
import com.matthiaslapierre.jumper.core.JumperGameStates.Direction
import com.matthiaslapierre.jumper.utils.hasFlag
import com.matthiaslapierre.jumper.utils.minusFlag
import com.matthiaslapierre.jumper.utils.withFlag

internal class JumperGameStatesImpl: JumperGameStates  {

    /**
     * Current status of the game.
     */
    override var currentStatus: Sprite.Status = Sprite.Status.STATUS_NOT_STARTED

    /**
     * Current player state.
     */
    override var playerState: ResourceManager.PlayerState = ResourceManager.PlayerState.IDLE

    /**
     * Current player direction.
     */
    override var direction: Direction = Direction.IDLE

    override var cameraMovement: CameraMovement = CameraMovement.NONE

    /**
     * Score.
     */
    override var candiesCollected: Int = 0

    override var powerUp: Int = 0

    /**
     * When the framerate is not steady, compensate every moving element by a factor.
     */
    override var frameRateAdjustFactor: Float = 1f

    override val speedY: Float
        get() = if (cameraMovement != CameraMovement.NONE) {
            globalSpeedY
        } else {
            0f
        }

    override val playerSpeedX: Float
        get() = normalizedSpeedX(_speedX)
    override val playerSpeedY: Float
        get() = globalSpeedY
    override val backgroundSpeedY: Float
        get() = speedY * JumperConstants.BACKGROUND_SPEED_DECELERATION
    override val cloudSpeedY: Float
        get() = speedY * JumperConstants.CLOUD_SPEED_DECELERATION

    override val globalSpeedY: Float
        get() = normalizedSpeedY(_speedY)

    /**
     * Current speed on the x-axis.
     */
    private var _speedX: Float = 0f
    /**
     * Current speed on the y-axis.
     */
    private var _speedY: Float = 0f
    private val gravity: Float
        get() = screenWidth * JumperConstants.GRAVITY * frameRateAdjustFactor
    private var maxSpeedY: Float = 0f
    private var screenWidth: Float = Constants.UNDEFINED
    private var hasReachedTheTop: Boolean = false
    private var hasReachedTheBottom: Boolean = false

    override fun update(playerY: Float, playerLowestY: Float, playerHighestY: Float) {
        updateSpeed()
        updateDirection()
        updatePlayerState()
        updateCameraMovement(playerY, playerLowestY, playerHighestY)
    }

    override fun moveX(xAcceleration: Float) {
        _speedX = xAcceleration
    }

    override fun jump() {
        _speedY = getJumpAcceleration()
    }

    override fun collectCandies(candies: Int) {
        candiesCollected += candies
        if (_speedY < getCandyAcceleration()) {
            _speedY = getCandyAcceleration()
        }
    }

    override fun setScreenSize(screenWidth: Float) {
        this.screenWidth = screenWidth
        this.maxSpeedY = screenWidth * JumperConstants.MAX_FALL_SPEED
    }

    override fun addPowerUp(powerUpFlag: Int) {
        powerUp = powerUp.withFlag(powerUpFlag)
        if (powerUpFlag == POWER_UP_COPTER) {
            playerState = ResourceManager.PlayerState.COPTER
        }
    }

    override fun removePowerUp(powerUpFlag: Int) {
        powerUp = powerUp.minusFlag(powerUpFlag)
        if (powerUpFlag == POWER_UP_COPTER) {
            playerState = ResourceManager.PlayerState.JUMP
        }
    }

    override fun removeAllPowerUps() {
        powerUp = 0
        playerState = ResourceManager.PlayerState.JUMP
    }

    override fun hasPowerUps(): Boolean = powerUp > 0

    override fun gameOver() {
        _speedY = 0f
        playerState = ResourceManager.PlayerState.DEAD
        currentStatus = Sprite.Status.STATUS_GAME_OVER
    }

    private fun updateCameraMovement(playerY: Float, playerLowestY: Float, playerHighestY: Float) {
        if (currentStatus == Sprite.Status.STATUS_PLAY) {
            if (!hasReachedTheTop) {
                hasReachedTheTop = playerY <= playerHighestY
            }
            if (!hasReachedTheBottom) {
                hasReachedTheBottom = playerY >= playerLowestY
            }
            cameraMovement = when {
                hasReachedTheTop -> {
                    if (globalSpeedY > 0) {
                        CameraMovement.UP
                    } else {
                        hasReachedTheTop = false
                        CameraMovement.NONE
                    }
                }
                hasReachedTheBottom -> {
                    if (globalSpeedY < 0) {
                        CameraMovement.DOWN
                    } else {
                        hasReachedTheBottom = false
                        CameraMovement.NONE
                    }
                }
                else -> {
                    CameraMovement.NONE
                }
            }
        } else {
            hasReachedTheTop = false
            hasReachedTheBottom = false
            cameraMovement = CameraMovement.NONE
        }
    }

    private fun updateSpeed() {
        if (currentStatus == Sprite.Status.STATUS_PLAY) {
            when {
                powerUp.hasFlag(POWER_UP_ROCKET) -> {
                    _speedY = getRocketSpeed()
                }
                powerUp.hasFlag(POWER_UP_COPTER) -> {
                    val minSpeed = getCopterSpeed()
                    if (_speedY < minSpeed) {
                        _speedY = minSpeed
                    } else if (_speedY > minSpeed) {
                        _speedY -= gravity
                    }
                }
                else -> {
                    _speedY -= gravity
                }
            }
        } else if(currentStatus == Sprite.Status.STATUS_GAME_OVER) {
            _speedY -= gravity
        }
    }

    private fun updateDirection() {
        direction = when {
            globalSpeedY > 0f -> Direction.UP
            globalSpeedY < 0f -> Direction.DOWN
            else -> Direction.IDLE
        }
    }

    private fun updatePlayerState() {
        playerState = when(playerState) {
            ResourceManager.PlayerState.JUMP -> {
                if (direction == Direction.DOWN) {
                    ResourceManager.PlayerState.FALL
                } else {
                    ResourceManager.PlayerState.JUMP
                }
            }
            ResourceManager.PlayerState.FALL -> {
                if (direction == Direction.UP) {
                    ResourceManager.PlayerState.JUMP
                } else {
                    ResourceManager.PlayerState.FALL
                }
            }
            else -> playerState
        }
    }

    private fun normalizedSpeedX(speedX: Float): Float {
        return speedX * frameRateAdjustFactor
    }

    private fun normalizedSpeedY(speedY: Float): Float {
        val newSpeedY = when {
            speedY < -maxSpeedY -> {
                -maxSpeedY
            }
            else -> {
                speedY
            }
        }
        return newSpeedY * frameRateAdjustFactor
    }

    private fun getRocketSpeed() = screenWidth * JumperConstants.ROCKET_SPEED

    private fun getCopterSpeed() = screenWidth * JumperConstants.COPTER_SPEED

    private fun getCandyAcceleration() = screenWidth * JumperConstants.CANDIES_ACCELERATION

    private fun getJumpAcceleration() = screenWidth * JumperConstants.JUMP_ACCELERATION

}
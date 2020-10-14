package com.matthiaslapierre.jumper.core

import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.framework.ui.Sprite.Status
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.JumperConstants.BACKGROUND_SPEED_DECELERATION
import com.matthiaslapierre.jumper.JumperConstants.CLOUD_SPEED_DECELERATION
import com.matthiaslapierre.jumper.JumperConstants.MAX_FALL_SPEED
import com.matthiaslapierre.jumper.utils.hasFlag
import com.matthiaslapierre.jumper.utils.minusFlag
import com.matthiaslapierre.jumper.utils.withFlag

internal class GameStates  {

    companion object {
        const val POWER_UP_COPTER = 0x00000001
        const val POWER_UP_MAGNET = 0x00000010
        const val POWER_UP_ROCKET = 0x00000100
        const val POWER_UP_ARMORED = 0x00001000
    }

    enum class Direction {
        UP,
        DOWN,
        IDLE
    }

    enum class CameraMovement {
        UP,
        DOWN,
        NONE
    }

    /**
     * Current status of the game.
     */
    var currentStatus: Status = Status.STATUS_NOT_STARTED

    /**
     * Current player state.
     */
    var playerState: PlayerState = PlayerState.IDLE

    /**
     * Current player direction.
     */
    var direction: Direction = Direction.IDLE

    var cameraMovement: CameraMovement = CameraMovement.NONE

    /**
     * Score.
     */
    var candiesCollected: Int = 0

    var powerUp: Int = 0

    /**
     * When the framerate is not steady, compensate every moving element by a factor.
     */
    var frameRateAdjustFactor: Float = 1f

    /**
     * Current speed on the x-axis.
     */
    private var _speedX: Float = 0f

    /**
     * Current speed on the y-axis.
     */
    private var _speedY: Float = 0f

    val speedY: Float
        get() = if (cameraMovement != CameraMovement.NONE) {
            globalSpeedY
        } else {
            0f
        }

    val playerSpeedX: Float
        get() = normalizedSpeedX(_speedX)
    val playerSpeedY: Float
        get() = globalSpeedY
    val backgroundSpeedY: Float
        get() = speedY * BACKGROUND_SPEED_DECELERATION
    val cloudSpeedY: Float
        get() = speedY * CLOUD_SPEED_DECELERATION

    val globalSpeedY: Float
        get() = normalizedSpeedY(_speedY)

    private val gravity: Float
        get() = screenWidth * JumperConstants.GRAVITY * frameRateAdjustFactor
    private var maxSpeedY: Float = 0f
    private var screenWidth: Float = UNDEFINED
    private var hasReachedTheTop: Boolean = false
    private var hasReachedTheBottom: Boolean = false

    fun update(playerY: Float, playerLowestY: Float, playerHighestY: Float) {
        updateSpeed()
        updateDirection()
        updatePlayerState()
        updateCameraMovement(playerY, playerLowestY, playerHighestY)
    }

    private fun updateCameraMovement(playerY: Float, playerLowestY: Float, playerHighestY: Float) {
        if (currentStatus == Status.STATUS_PLAY) {
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

    fun moveX(xAcceleration: Float) {
        _speedX = xAcceleration
    }

    fun jump() {
        _speedY = getJumpAcceleration()
    }

    fun collectCandies(candies: Int) {
        candiesCollected += candies
        if (_speedY < getCandyAcceleration()) {
            _speedY = getCandyAcceleration()
        }
    }

    fun setScreenSize(screenWidth: Float) {
        this.screenWidth = screenWidth
        this.maxSpeedY = screenWidth * MAX_FALL_SPEED
    }

    fun addPowerUp(powerUpFlag: Int) {
        powerUp = powerUp.withFlag(powerUpFlag)
        if (powerUpFlag == POWER_UP_COPTER) {
            playerState = PlayerState.COPTER
        }
    }

    fun removePowerUp(powerUpFlag: Int) {
        powerUp = powerUp.minusFlag(powerUpFlag)
        if (powerUpFlag == POWER_UP_COPTER) {
            playerState = PlayerState.JUMP
        }
    }

    fun gameOver() {
        _speedY = 0f
        playerState = PlayerState.DEAD
        currentStatus = Status.STATUS_GAME_OVER
    }

    private fun updateSpeed() {
        if (currentStatus == Status.STATUS_PLAY) {
            when {
                powerUp.hasFlag(POWER_UP_ROCKET) -> {
                    _speedY = getRocketSpeed()
                }
                powerUp.hasFlag(POWER_UP_COPTER) -> {
                    _speedY = getCopterSpeed()
                }
                else -> {
                    _speedY -= gravity
                }
            }
        } else if(currentStatus == Status.STATUS_GAME_OVER) {
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
            PlayerState.JUMP -> {
                if (direction == Direction.DOWN) {
                    PlayerState.FALL
                } else {
                    PlayerState.JUMP
                }
            }
            PlayerState.FALL -> {
                if (direction == Direction.UP) {
                    PlayerState.JUMP
                } else {
                    PlayerState.FALL
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
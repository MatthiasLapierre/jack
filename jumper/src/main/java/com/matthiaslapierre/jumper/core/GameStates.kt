package com.matthiaslapierre.jumper.core

import android.util.Log
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.framework.ui.Sprite.Status
import com.matthiaslapierre.jumper.JumperConstants.BACKGROUND_SPEED_DECELERATION
import com.matthiaslapierre.jumper.JumperConstants.CANDIES_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.CLOUD_SPEED_DECELERATION
import com.matthiaslapierre.jumper.JumperConstants.GRAVITY
import com.matthiaslapierre.jumper.JumperConstants.JUMP_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.MAX_SPEED

internal class GameStates  {

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

    var elevation: Float = 0f

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

    private var maxSpeedY: Float = 0f

    /**
     * When the framerate is not steady, compensate every moving element by a factor.
     */
    var frameRateAdjustFactor: Float = 0f

    private var screenWidth: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED

    fun moveX(xAcceleration: Float) {
        _speedX = xAcceleration
    }

    fun update() {
        updateSpeed()
        updateElevation()
        updateDirection()
        updatePlayerState()
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
        this.maxSpeedY = screenWidth * MAX_SPEED
    }

    private fun updateSpeed() {
        if (currentStatus == Status.STATUS_PLAY || currentStatus == Status.STATUS_GAME_OVER) {
            _speedY -= getGravity() * frameRateAdjustFactor
        }
    }

    private fun updateElevation() {
        elevation += speedY
        Log.d(">>>>>>>> ", ">>>>>>> elevation: $elevation / $speedY")
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

    fun collectCandies(candies: Int) {
        candiesCollected += candies
        if (_speedY < getCandyAcceleration()) {
            _speedY = getCandyAcceleration()
        }
    }

    fun jump() {
        _speedY = getJumpAcceleration()
    }

    fun gameOver() {
        _speedY = 0f
        playerState = PlayerState.DEAD
        currentStatus = Status.STATUS_GAME_OVER
    }

    private fun getCandyAcceleration() = screenWidth * CANDIES_ACCELERATION

    private fun getJumpAcceleration() = screenWidth * JUMP_ACCELERATION

    private fun getGravity() = screenWidth * GRAVITY

    private fun normalizedSpeedX(speedX: Float): Float {
        return speedX * frameRateAdjustFactor
    }

    private fun normalizedSpeedY(speedY: Float): Float {
        val newSpeedY = when {
            speedY > maxSpeedY -> {
                maxSpeedY
            }
            speedY < -maxSpeedY -> {
                -maxSpeedY
            }
            else -> {
                speedY
            }
        }
        return newSpeedY * frameRateAdjustFactor
    }

}
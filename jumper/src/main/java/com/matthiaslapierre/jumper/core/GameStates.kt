package com.matthiaslapierre.jumper.core

import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.framework.ui.Sprite.Status
import com.matthiaslapierre.jumper.JumperConstants.JUMP_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.CANDIES_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.GRAVITY
import com.matthiaslapierre.jumper.JumperConstants.MAX_ACCELERATION

class GameStates  {

    enum class Direction {
        UP,
        DOWN,
        IDLE
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

    /**
     * Score.
     */
    var candiesCollected: Int = 0

    /**
     * Current speed on the x-axis.
     */
    private var _speedX: Float = 0f
    val speedX: Float
        get() = _speedX * frameRateAdjustFactor

    /**
     * Current speed on the y-axis.
     */
    private var _speedY: Float = 0f
    val speedY: Float
        get() {
            val speed = when {
                _speedY > maxSpeedY -> {
                    maxSpeedY
                }
                _speedY < -maxSpeedY -> {
                    -maxSpeedY
                }
                else -> {
                    _speedY
                }
            }
            return speed * frameRateAdjustFactor
        }

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
        updateDirection()
        updatePlayerState()
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
        this.maxSpeedY = screenHeight * MAX_ACCELERATION
    }

    private fun updateSpeed() {
        if (currentStatus == Status.STATUS_PLAY) {
            _speedY -= getGravity() * frameRateAdjustFactor
        }
    }

    private fun updateDirection() {
        direction = when {
            speedY > 0f -> Direction.UP
            speedY < 0f -> Direction.DOWN
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
        _speedY = getBounceAcceleration()
    }

    fun kill() {
        _speedY = 0f
        currentStatus = Status.STATUS_GAME_OVER
    }

    private fun getCandyAcceleration() = screenHeight * CANDIES_ACCELERATION

    private fun getBounceAcceleration() = screenHeight * JUMP_ACCELERATION

    private fun getGravity() = screenHeight * GRAVITY

}
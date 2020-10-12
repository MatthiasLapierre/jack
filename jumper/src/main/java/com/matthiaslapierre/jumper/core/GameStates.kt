package com.matthiaslapierre.jumper.core

import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.framework.ui.Sprite.Status
import com.matthiaslapierre.jumper.JumperConstants.BOUNCE_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.CANDIES_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.CANNON_ACCELERATION
import com.matthiaslapierre.jumper.JumperConstants.GRAVITY

class GameStates  {

    enum class State {
        READY_TO_LAUNCH,
        LAUNCHED
    }

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
     * Game state.
     */
    var state: State = State.READY_TO_LAUNCH

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
        get() = _speedY * frameRateAdjustFactor

    /**
     * Elevation in pixel.
     */
    var elevation: Float = 0f

    /**
     * When the framerate is not steady, compensate every moving element by a factor.
     */
    var frameRateAdjustFactor: Float = 0f

    private var screenWidth: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED

    fun launch() {
        state = State.LAUNCHED
        _speedY = getCannonAcceleration()
    }

    fun moveX(xAcceleration: Float) {
        _speedX = xAcceleration
    }

    fun update() {
        updateSpeed()
        updateDirection()
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
    }

    private fun updateSpeed() {
        if (currentStatus == Status.STATUS_PLAY && state == State.LAUNCHED) {
            elevation += _speedY * frameRateAdjustFactor
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

    fun collectCandies(candies: Int) {
        candiesCollected += candies
        if (_speedY < getCandyAcceleration()) {
            _speedY = getCandyAcceleration()
        }
    }

    fun bounce() {
        _speedY = getBounceAcceleration()
    }

    fun kill() {
        _speedY = 0f
        currentStatus = Status.STATUS_GAME_OVER
    }

    private fun getCannonAcceleration() = screenHeight * CANNON_ACCELERATION

    private fun getCandyAcceleration() = screenHeight * CANDIES_ACCELERATION

    private fun getBounceAcceleration() = screenHeight * BOUNCE_ACCELERATION

    private fun getGravity() = screenHeight * GRAVITY

}
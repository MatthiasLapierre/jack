package com.matthiaslapierre.jack.ui.screens.jumper.game

import com.matthiaslapierre.framework.ui.Sprite.Status
import com.matthiaslapierre.jack.Constants.UNDEFINED

class GameStates  {

    companion object {
        private const val CANNON_ACCELERATION = 0.1f
        private const val GRAVITY = 0.002f
    }

    enum class PlayerState {
        READY_TO_LAUNCH,
        LAUNCHED
    }

    /**
     * Current status of the game.
     */
    var currentStatus: Status = Status.STATUS_NOT_STARTED

    /**
     * Player state.
     */
    var playerState: PlayerState = PlayerState.READY_TO_LAUNCH

    /**
     * Score.
     */
    var candiesCollected: Int = 0

    /**
     * Current speed.
     */
    var speed: Float = 0f
    /**
     * Elevation in pixel.
     */
    var elevation: Float = 0f

    private var screenWidth: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED

    fun launch() {
        playerState = PlayerState.LAUNCHED
        speed = getCannonAcceleration()
    }

    fun update() {
        updateSpeed()
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
    }

    private fun updateSpeed() {
        if (currentStatus == Status.STATUS_PLAY
            && playerState == PlayerState.LAUNCHED) {
            elevation += speed
            speed -= getGravity()
        }
    }

    private fun getCannonAcceleration() = screenHeight * CANNON_ACCELERATION

    private fun getGravity() = screenHeight * GRAVITY

}
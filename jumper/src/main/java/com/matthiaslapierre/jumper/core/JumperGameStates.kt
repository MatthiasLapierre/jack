package com.matthiaslapierre.jumper.core

import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.framework.ui.Sprite.Status

internal interface JumperGameStates  {

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
    var currentStatus: Status

    /**
     * Current player state.
     */
    var playerState: PlayerState

    /**
     * Current player direction.
     */
    var direction: Direction

    var cameraMovement: CameraMovement

    /**
     * Score.
     */
    var candiesCollected: Int

    var powerUp: Int

    /**
     * When the framerate is not steady, compensate every moving element by a factor.
     */
    var frameRateAdjustFactor: Float

    val speedY: Float
        get() = if (cameraMovement != CameraMovement.NONE) {
            globalSpeedY
        } else {
            0f
        }

    val playerSpeedX: Float
    val playerSpeedY: Float
    val backgroundSpeedY: Float
    val cloudSpeedY: Float
    val globalSpeedY: Float

    fun update(playerY: Float, playerLowestY: Float, playerHighestY: Float)

    fun moveX(xAcceleration: Float)

    fun jump()

    fun collectCandies(candies: Int)

    fun setScreenSize(screenWidth: Float)

    fun addPowerUp(powerUpFlag: Int)

    fun removePowerUp(powerUpFlag: Int)

    fun removeAllPowerUps()

    fun hasPowerUps(): Boolean

    fun gameOver()
}
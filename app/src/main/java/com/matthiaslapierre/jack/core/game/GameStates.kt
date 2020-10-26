package com.matthiaslapierre.jack.core.game

import com.matthiaslapierre.framework.ui.Sprite.Status
import com.matthiaslapierre.jack.core.PlayerState

/**
 * Stores the state of the game.
 */
interface GameStates  {

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

    /**
     * Movement of the camera.
     */
    var cameraMovement: CameraMovement

    /**
     * Score.
     */
    var candiesCollected: Int

    /**
     * Power-ups enabled.
     */
    var powerUp: Int

    /**
     * When the frame rate is not steady, compensate every moving element by a factor.
     */
    var frameRateAdjustFactor: Float

    /**
     * Speed of sprites.
     */
    val speedY: Float
        get() = if (cameraMovement != CameraMovement.NONE) {
            globalSpeedY
        } else {
            0f
        }

    /**
     * Speed of the Jack sprite on the x-coordinates.
     */
    val playerSpeedX: Float

    /**
     * Speed of the Jack sprite on the y-coordinates.
     */
    val playerSpeedY: Float

    /**
     * Speed of the background.
     */
    val backgroundSpeedY: Float

    /**
     * Speed of the bat on the x-coordinates.
     */
    val batSpeedX: Float

    /**
     * Speed of clouds.
     */
    val cloudSpeedY: Float

    /**
     * Speed of the Jack's sprite or speed of the other sprites depending on the camera movements.
     */
    val globalSpeedY: Float

    /**
     * Updates the Jack sprite position.
     */
    fun update(playerY: Float, playerLowestY: Float, playerHighestY: Float)

    /**
     * Moves the Jack sprite on the x-coordinates.
     */
    fun moveX(xAcceleration: Float)

    /**
     * Jumps. Updates the speed on the y-coordinates and the camera movement.
     */
    fun jump()

    /**
     * Increments the score.
     */
    fun collectCandies(candies: Int)

    /**
     * Updates the screen size properties.
     */
    fun setScreenSize(screenWidth: Float)

    /**
     * Enables a power-up.
     */
    fun addPowerUp(powerUpFlag: Int)

    /**
     * Disables a power-up.
     */
    fun removePowerUp(powerUpFlag: Int)

    /**
     * Disables all the power-ups.
     */
    fun removeAllPowerUps()

    /**
     * Checks if the player has a power-up.
     */
    fun hasPowerUps(): Boolean

    /**
     * Kills the Jack character.
     */
    fun gameOver()
}
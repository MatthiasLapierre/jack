package com.matthiaslapierre.jack.core.game.impl

import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.PlayerState
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.GameStates.CameraMovement
import com.matthiaslapierre.jack.core.game.GameStates.Companion.POWER_UP_COPTER
import com.matthiaslapierre.jack.core.game.GameStates.Companion.POWER_UP_ROCKET
import com.matthiaslapierre.jack.core.game.GameStates.Direction
import com.matthiaslapierre.jack.utils.hasFlag
import com.matthiaslapierre.jack.utils.minusFlag
import com.matthiaslapierre.jack.utils.withFlag

internal class JackGameStates : GameStates {

    /**
     * Current status of the game.
     */
    override var currentStatus: Sprite.Status = Sprite.Status.STATUS_NOT_STARTED

    /**
     * Current player state.
     */
    override var playerState: PlayerState = PlayerState.IDLE

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
        get() = speedY * Constants.BACKGROUND_SPEED_DECELERATION
    override val batSpeedX: Float
        get() = screenWidth * Constants.BAT_SPEED * frameRateAdjustFactor
    override val cloudSpeedY: Float
        get() = speedY * Constants.CLOUD_SPEED_DECELERATION

    override val globalSpeedY: Float
        get() = normalizedSpeedY(_speedY)

    /**
     * Current speed on the x-axis (player).
     */
    private var _speedX: Float = 0f
    /**
     * Current speed on the y-axis (player or others sprites depending on the camera movement).
     */
    private var _speedY: Float = 0f
    private val gravity: Float
        get() = screenWidth * Constants.GRAVITY * frameRateAdjustFactor
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
        this.maxSpeedY = screenWidth * Constants.MAX_FALL_SPEED
    }

    override fun addPowerUp(powerUpFlag: Int) {
        powerUp = powerUp.withFlag(powerUpFlag)
        if (powerUpFlag == POWER_UP_COPTER) {
            playerState = PlayerState.COPTER
        }
    }

    override fun removePowerUp(powerUpFlag: Int) {
        powerUp = powerUp.minusFlag(powerUpFlag)
        if (powerUpFlag == POWER_UP_COPTER) {
            playerState = PlayerState.JUMP
        }
    }

    override fun removeAllPowerUps() {
        powerUp = 0
        playerState = PlayerState.JUMP
    }

    override fun hasPowerUps(): Boolean = powerUp > 0

    override fun gameOver() {
        _speedY = 0f
        playerState = PlayerState.DEAD
        currentStatus = Sprite.Status.STATUS_GAME_OVER
    }

    /**
     * Updates the camera movement depending on the player position.
     */
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
                        // The player has reached the top position and the speed is positive,
                        // moves the camera up.
                        CameraMovement.UP
                    } else {
                        hasReachedTheTop = false
                        CameraMovement.NONE
                    }
                }
                hasReachedTheBottom -> {
                    if (globalSpeedY < 0) {
                        // The player has reached the bottom position and the speed is negative,
                        // moves the camera down.
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

    /**
     * Updates speed properties depending on the power-ups enabled.
     */
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

    /**
     * Updates the player direction.
     */
    private fun updateDirection() {
        direction = when {
            globalSpeedY > 0f -> Direction.UP
            globalSpeedY < 0f -> Direction.DOWN
            else -> Direction.IDLE
        }
    }

    /**
     * Updates the player state.
     */
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

    /**
     * Applies the frame rate factor to the speed.
     */
    private fun normalizedSpeedX(speedX: Float): Float {
        return speedX * frameRateAdjustFactor
    }

    /**
     * Applies the frame rate factor to the speed.
     */
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

    /**
     * Gets the speed with the rocket enabled.
     */
    private fun getRocketSpeed() = screenWidth * Constants.ROCKET_SPEED

    /**
     * Gets the speed with copter enabled.
     */
    private fun getCopterSpeed() = screenWidth * Constants.COPTER_SPEED

    /**
     * Gets the speed after collecting a candy.
     */
    private fun getCandyAcceleration() = screenWidth * Constants.CANDIES_ACCELERATION

    /**
     * Gets the speed after jumping.
     */
    private fun getJumpAcceleration() = screenWidth * Constants.JUMP_ACCELERATION

}
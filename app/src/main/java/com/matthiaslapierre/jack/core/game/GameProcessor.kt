package com.matthiaslapierre.jack.core.game

import android.graphics.Canvas
import android.graphics.Paint
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.resources.ResourceManager

/**
 * Game processor. Manages the game.
 */
interface GameProcessor{

    val resourceManager: ResourceManager
    val gameStates: GameStates
    val gameMap: GameMap
    var gameListener: GameListener?

    /**
     * Processes the game logic.
     */
    fun process()

    /**
     * Resumes the game processor.
     */
    fun resume()

    /**
     * Pauses the game processor.
     */
    fun pause()

    /**
     * Disposes resources.
     */
    fun dispose()

    /**
     * Draws sprites.
     */
    fun paint(canvas: Canvas, globalPaint: Paint)

    /**
     * Starts the game.
     */
    fun startGame()

    /**
     * Stops the game and shows the "Game Over" screen.
     */
    fun gameOver()

    /**
     * Moves the character on the x-coordinate.
     */
    fun moveX(xAcceleration: Float)

    /**
     * Gets the current status.
     */
    fun getGameStatus(): Sprite.Status

    /**
     * Gets candies collected.
     */
    fun getCandiesCollected(): Int

    /**
     * Gets the power-ups enabled.
     */
    fun getPowerUps(): Int

    /**
     * Updates the frame rate factor.
     */
    fun setFrameRateAdjustFactor(frameRateAdjustFactor: Float)

}
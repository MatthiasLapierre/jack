package com.matthiaslapierre.framework.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

/**
 * Game screen (menu, game, game-over...).
 */
abstract class Screen(
    protected val game: Game
) {
    /**
     * Time between the last frame and the previous one.
     */
    var frameTime: Long = 0
    /**
     * https://stackoverflow.com/questions/12053509/android-game-sprite-speed-seems-to-be-different-on-different-phones
     */
    var frameRateAdjustFactor: Float = 0f

    /**
     * Updates the screen.
     */
    abstract fun update()

    /**
     * Draws the screen.
     */
    abstract fun paint(canvas: Canvas, globalPaint: Paint)

    /**
     * Notifies the screen that the game is paused.
     */
    abstract fun pause()

    /**
     * Notifies the screen that the game is resumed.
     */
    abstract fun resume()

    /**
     * Releases the screen resources.
     */
    abstract fun dispose()

    /**
     * Receives a touch event.
     */
    abstract fun onTouch(event: MotionEvent)

    /**
     * Receives a back press event.
     */
    abstract fun onBackPressed()
}
package com.matthiaslapierre.framework.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * Sprites have in common a certain number of behaviors, we will create an interface to model
 * these behaviors.
 */
interface Sprite {
    enum class Status {
        STATUS_NOT_STARTED,
        STATUS_PLAY,
        STATUS_PAUSE,
        STATUS_GAME_OVER
    }

    /**
     * to request the drawing on the Sprite’s Canvas
     */
    fun onDraw(canvas: Canvas, globalPaint: Paint, status: Status)

    /**
     * to know if a Sprite is still alive or not
     */
    fun isAlive(): Boolean

    /**
     * to manage the collision between a Sprite and another Sprite
     */
    fun isHit(sprite: Sprite): Boolean

    /**
     * Returns the number of points associated with a Sprite instance
     */
    fun getScore(): Int

    /**
     * Bounding rectangle for drawing the view.
     */
    fun getRect(): Rect

    /**
     * This method is no longer used and will be destroyed. Destroys resources.
     */
    fun onDispose()

}
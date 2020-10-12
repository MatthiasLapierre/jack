package com.matthiaslapierre.framework.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

abstract class Screen(
    protected val game: Game
) {
    var frameTime: Long = 0
    /**
     * https://stackoverflow.com/questions/12053509/android-game-sprite-speed-seems-to-be-different-on-different-phones
     */
    var frameRateAdjustFactor: Float = 0f
    abstract fun update()
    abstract fun paint(canvas: Canvas, globalPaint: Paint)
    abstract fun pause()
    abstract fun resume()
    abstract fun dispose()
    abstract fun onTouch(event: MotionEvent)
    abstract fun onBackPressed()
}
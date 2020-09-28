package com.matthiaslapierre.framework.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

abstract class Screen(
    protected val game: Game
) {
    abstract fun update()
    abstract fun paint(canvas: Canvas, globalPaint: Paint)
    abstract fun pause()
    abstract fun resume()
    abstract fun dispose()
    abstract fun onTouch(event: MotionEvent)
    abstract fun onBackPressed()
}
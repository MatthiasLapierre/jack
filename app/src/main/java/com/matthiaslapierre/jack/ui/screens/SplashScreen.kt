package com.matthiaslapierre.jack.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.R
import com.matthiaslapierre.jumper.ui.MenuScreen

class SplashScreen(
    game: Game
) : Screen(game) {

    private var drawnOnce = false

    override fun update() {
        if(!drawnOnce) return
        game.getAudio().load()
        game.getTypefaces().load()
        game.getGameResources().load()
        game.setScreen(MenuScreen(game))
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        val context = game as Context
        drawBackground(context, canvas)
        //drawLoadingText(context, graphics)
        drawnOnce = true
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onTouch(event: MotionEvent) {

    }

    override fun onBackPressed() {

    }

    private fun drawBackground(context: Context, canvas: Canvas) {
        val bgLoadingDrawable = ContextCompat.getDrawable(context, R.drawable.bg_jump)!!
        val screenWidth = canvas.width
        val screenHeight = canvas.height
        val originalHeight = bgLoadingDrawable.intrinsicHeight
        val scale = screenWidth / bgLoadingDrawable.intrinsicWidth.toFloat()
        val finalHeight = (originalHeight * scale).toInt()
        bgLoadingDrawable.bounds = Rect(
            0,
            screenHeight - finalHeight,
            screenWidth,
            screenHeight
        )
        bgLoadingDrawable.draw(canvas)
    }

}
package com.matthiaslapierre.jack.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.JackApp
import com.matthiaslapierre.jack.R
import com.matthiaslapierre.jack.core.settings.Settings
import com.matthiaslapierre.jack.core.resources.SoundManager

/**
 * Splash Screen. Resources loading.
 */
class SplashScreen(
    game: Game
) : Screen(game) {

    private var settings: Settings = ((game as Context).applicationContext as JackApp)
        .appContainer.settings

    private var drawnOnce = false

    override fun update() {
        if(!drawnOnce) return
        // Load audio files
        game.getAudio().load()
        // Load typefaces
        game.getTypefaces().load()
        // Load images
        game.getGameResources().load()
        // Initialize the SoundManager.
        val soundManager = game.getAudio() as SoundManager
        soundManager.enableMusic(settings.musicEnabled)
        soundManager.enableSounds(settings.soundEnabled)
        // Play the menu theme music.
        soundManager.playMenuMusic()
        // Show the menu.
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

    /**
     * Draw the loading background image.
     */
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
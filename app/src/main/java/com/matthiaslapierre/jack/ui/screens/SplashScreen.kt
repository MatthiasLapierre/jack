package com.matthiaslapierre.jack.ui.screens

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Graphics
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.R
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.MenuScene
import com.matthiaslapierre.jack.utils.Utils

class SplashScreen(
    game: Game
) : Screen(game) {

    private var mDrawnOnce = false

    private val loadingTextPaint: Paint by lazy {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = Utils.getDimenInPx(game as Context, R.dimen.loadingTextSize)
        paint.color = Color.WHITE
        paint
    }

    override fun update() {
        if(!mDrawnOnce) return
        game.getAudio().load()
        game.getTypefaces().load()
        game.getGameResources().load()
        game.setScreen(MenuScene(game))
    }

    override fun paint() {
        val graphics = game.getGraphics()
        val context = game as Context
        drawBackground(context, graphics)
        //drawLoadingText(context, graphics)
        mDrawnOnce = true
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onBackPressed() {

    }

    private fun drawBackground(context: Context, graphics: Graphics) {
        val bgLoadingDrawable = ContextCompat.getDrawable(context, R.drawable.bg_jump)!!
        val screenWidth = graphics.getWidth()
        val screenHeight = graphics.getHeight()
        val originalHeight = bgLoadingDrawable.intrinsicHeight
        val scale = screenWidth / bgLoadingDrawable.intrinsicWidth.toFloat()
        val finalHeight = (originalHeight * scale).toInt()
        bgLoadingDrawable.bounds = Rect(0, screenHeight - finalHeight, graphics.getWidth(), graphics.getHeight())
        bgLoadingDrawable.draw(graphics.getCanvas())
    }

    private fun drawLoadingText(context: Context, graphics: Graphics) {
        val strLoadingText = context.resources.getString(R.string.loading_game)
        val textBounds = Rect()
        loadingTextPaint.getTextBounds(strLoadingText, 0, strLoadingText.length, textBounds)
        val x = (graphics.getWidth() / 2f).toInt()
        val y = (graphics.getHeight() / 2f - textBounds.height() / 2f).toInt()
        graphics.drawString(strLoadingText, x, y, loadingTextPaint)
    }

}
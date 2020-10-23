package com.matthiaslapierre.framework.ui.android

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.matthiaslapierre.framework.BuildConfig
import com.matthiaslapierre.framework.FrameworkConstants
import com.matthiaslapierre.framework.ui.Game

/**
 * [android.view.SurfaceView] showing the game screens.
 */
class GameView(
    context: Context,
    private var game: Game
) : SurfaceView(context), Runnable, SurfaceHolder.Callback, View.OnTouchListener {

    companion object {
        private const val TAG = "GameView"
    }

    private val globalPaint: Paint by lazy {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint
    }
    /**
     * The game loop.
     */
    private var renderThread: Thread? = null

    /**
     * false if the game is paused.
     */
    private var isRunning: Boolean = true

    init {
        keepScreenOn = true
        setZOrderOnTop(true)
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setOnTouchListener(this)
        isFocusable = true // make sure we get key events
    }

    override fun run() {
        while (!Thread.interrupted()) {
            if(!isRunning) continue

            val startTime = System.currentTimeMillis()

            game.getCurrentScreen().update()

            val canvas = holder.lockCanvas()
            try {
                cleanCanvas(canvas)

                game.getCurrentScreen().paint(canvas, globalPaint)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }

            /*
           The rendering time is measured before comparing this time to a constant called GAP.
           This constant allows us to add, if necessary, a delay to avoid that the rendering
           phase of the Game Loop be too fast.
            */
            val frameDuration = System.currentTimeMillis() - startTime
            game.getCurrentScreen().frameRateAdjustFactor =
                frameDuration.toFloat() / FrameworkConstants.MS_PER_FRAME
            game.getCurrentScreen().frameTime = frameDuration
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "frame duration: $frameDuration")
            }
            val gap = FrameworkConstants.MS_PER_FRAME - frameDuration
            if (gap > 0) {
                try {
                    Thread.sleep(gap)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startGameThread()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGameThread()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
       isRunning = hasWindowFocus
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            game.getCurrentScreen().onTouch(event)
        }
        return true
    }

    /**
     * Resumes the game.
     */
    fun resume() {
        isRunning = true
    }

    /**
     * Pauses the game.
     */
    fun pause() {
        isRunning = false
    }

    /**
     * Takes a screenshot of the current scene.
     */
    fun capture(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val frameCanvas = Canvas(bitmap)
        game.getCurrentScreen().paint(frameCanvas, globalPaint)
        return bitmap

    }

    /**
     * Starts the game loop.
     */
    private fun startGameThread() {
        stopGameThread()
        renderThread = Thread(this)
        renderThread!!.start()
    }

    /**
     * Stops the game loop.
     */
    private fun stopGameThread() {
        renderThread?.interrupt()
        try {
            renderThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Failed to interrupt the drawing thread")
        }
        renderThread = null
    }

    /**
     * Cleans the canvas. Pixels are cleared to 0.
     */
    private fun cleanCanvas(canvas: Canvas) {
        canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR)
    }

}
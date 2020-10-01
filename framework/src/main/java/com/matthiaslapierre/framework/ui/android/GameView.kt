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

class GameView(
    context: Context,
    private var game: Game
) : SurfaceView(context), Runnable, SurfaceHolder.Callback, View.OnTouchListener {

    companion object {
        private const val TAG = "GameView"
    }

    private val mGlobalPaint: Paint by lazy {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint
    }
    private var mRenderThread: Thread? = null
    private var mIsRunning: Boolean = true

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
            if(!mIsRunning) continue

            val startTime = System.currentTimeMillis()

            game.getCurrentScreen().update()

            val canvas = holder.lockCanvas()
            try {
                cleanCanvas(canvas)

                game.getCurrentScreen().paint(canvas, mGlobalPaint)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }

            /*
           The rendering time is measured before comparing this time to a constant called GAP.
           This constant allows us to add, if necessary, a delay to avoid that the rendering
           phase of the Game Loop be too fast.
            */
            val frameDuration = System.currentTimeMillis() - startTime
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
       mIsRunning = hasWindowFocus
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            game.getCurrentScreen().onTouch(event)
        }
        return true
    }

    fun resume() {
        mIsRunning = true
    }

    fun pause() {
        mIsRunning = false
    }

    private fun startGameThread() {
        stopGameThread()
        mRenderThread = Thread(this)
        mRenderThread!!.start()
    }

    private fun stopGameThread() {
        mRenderThread?.interrupt()
        try {
            mRenderThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Failed to interrupt the drawing thread")
        }
        mRenderThread = null
    }

    /**
     * Cleans the canvas. Pixels are cleared to 0.
     */
    private fun cleanCanvas(canvas: Canvas) {
        canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR)
    }

}
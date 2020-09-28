package com.matthiaslapierre.framework.ui.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.matthiaslapierre.framework.FrameworkConstants
import com.matthiaslapierre.framework.ui.Game

class GameView(
    context: Context,
    private var game: Game,
    private var frameBuffer: Bitmap
) : SurfaceView(context), Runnable, SurfaceHolder.Callback {

    companion object {
        private const val TAG = "GameView"
    }

    private var mRenderThread: Thread? = null
    private var mIsRunning: Boolean = true

    init {
        keepScreenOn = true
        setZOrderOnTop(true)
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        isFocusable = true // make sure we get key events
    }

    override fun run() {
        while (!Thread.interrupted()) {
            if(!mIsRunning) continue

            val startTime = System.currentTimeMillis()

            game.getCurrentScreen().update()
            game.getCurrentScreen().paint()

            val canvas = holder.lockCanvas()
            try {
                val dstRect = Rect()
                canvas.getClipBounds(dstRect)
                canvas.drawBitmap(frameBuffer, null, dstRect, null)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }

            /*
           The rendering time is measured before comparing this time to a constant called GAP.
           This constant allows us to add, if necessary, a delay to avoid that the rendering
           phase of the Game Loop be too fast.
            */
            val frameDuration = System.currentTimeMillis() - startTime
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

}
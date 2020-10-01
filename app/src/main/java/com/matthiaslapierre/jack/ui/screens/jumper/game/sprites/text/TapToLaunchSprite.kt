package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.jack.core.ResourceManager

class TapToLaunchSprite(
    private val resourceManager: ResourceManager
) : Sprite {

    companion object {
        private const val Y_RATIO = .25f
        private const val WIDTH_RATIO = .9f
    }

    private var mX: Float = Constants.UNDEFINED
    private var mY: Float = Constants.UNDEFINED
    private var mWidth: Float = Constants.UNDEFINED
    private var mHeight: Float = Constants.UNDEFINED
    private var mIsAlive = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        mIsAlive = status == Sprite.Status.STATUS_NOT_STARTED

        val image = resourceManager.textTapToLaunch!!

        val screenWidth = canvas.width
        val screenHeight = canvas.height
        if (mX == Constants.UNDEFINED) {
            val originalWidth = image.width
            val originalHeight = image.height
            mWidth = screenWidth * WIDTH_RATIO
            mHeight = mWidth * originalHeight / originalWidth
            mX = (screenWidth - mWidth) / 2f
            mY = screenHeight * Y_RATIO
        }

        val srcRect = Rect(
            0,
            0,
            image.bitmap.width,
            image.bitmap.height
        )
        val dstRect = getRectF()

        canvas.drawBitmap(
            image.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )
    }

    override fun isAlive(): Boolean = mIsAlive

    override fun isHit(sprite: Sprite): Boolean = false

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = RectF(
        mX,
        mY,
        mX + mWidth,
        mY + mHeight
    )

    override fun onDispose() {

    }

}
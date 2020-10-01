package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.game.GameStates

class BgSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
) : Sprite {

    private var mMaxY: Float = 0f
    private var mX: Float = UNDEFINED
    private var mY: Float = UNDEFINED
    private var mMinY: Float = UNDEFINED
    private var mWidth: Float = UNDEFINED
    private var mHeight: Float = UNDEFINED
    private var mScreenHeight: Float = UNDEFINED
    private var mDistanceTravelled: Float = UNDEFINED
    private val mSpeed: Float
        get() = gameStates.speed * getAcceleration()

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val bitmap = resourceManager.bgJump!!.bitmap

        if(mY == UNDEFINED) {
            val screenWidth = canvas.width.toFloat()
            mScreenHeight = canvas.height.toFloat()

            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            mWidth = screenWidth
            mHeight = mWidth * originalHeight / originalWidth
            mX = (screenWidth - mWidth) / 2f
            mY = mScreenHeight - mHeight
            mMinY = mY
        }

        if (status == Sprite.Status.STATUS_PLAY) {
            mDistanceTravelled += mSpeed
            if (mDistanceTravelled <= mHeight) {
                mY += mSpeed
                if (mY < mMinY) {
                    mY = mMinY
                } else if (mY > mMaxY) {
                    mY = mMaxY
                }
            }
        }

        val srcRect = Rect(
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        val dstRect = getRectF()

        canvas.drawBitmap(
            bitmap,
            srcRect,
            dstRect,
            globalPaint
        )
    }

    override fun isAlive(): Boolean = true

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

    private fun getAcceleration(): Float {
        val multiplier = 1f - ((1f / (mHeight + mScreenHeight)) * gameStates.elevation)
        return when {
            multiplier > 1f -> {
                1f
            }
            multiplier < 0f -> {
                0f
            }
            else -> {
                multiplier
            }
        }
    }

}
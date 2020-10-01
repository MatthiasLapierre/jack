package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.player

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState.IDLE
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState.LAUNCH
import com.matthiaslapierre.jack.ui.screens.jumper.game.GameStates

class CannonSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    private var cannonInterface: CannonInterface?
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .5f
        private const val BOTTOM_RATIO = .30f
        private const val IDLE_FRAME_PER_MS = 120
        private const val LAUNCH_FRAME_PER_MS = 20
    }

    private var mState: PlayerState = IDLE
    private var mFrame: Int = 0
    private var mX: Float = UNDEFINED
    private var mY: Float = UNDEFINED
    private var mHighestY: Float = UNDEFINED
    private var mWidth: Float = UNDEFINED
    private var mHeight: Float = UNDEFINED
    private var mLastFrameTimestamp: Long = 0L
    private var mIsAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val newState = if(status == Sprite.Status.STATUS_NOT_STARTED) {
            IDLE
        } else {
            LAUNCH
        }
        if(mState != newState) {
            mState = newState
            mFrame = 0
        }
        val images = resourceManager.player!![mState]!!
        val image = images[mFrame]!!

        val screenWidth = canvas.width
        val screenHeight = canvas.height
        if (mX == UNDEFINED) {
            mWidth = screenWidth * WIDTH_RATIO
            mHeight = mWidth * image.height / image.width
            mX = (screenWidth - mWidth) / 2f
            mY = screenHeight - (screenHeight * BOTTOM_RATIO) - (mHeight / 2f)
            mHighestY = mY
        }

        mIsAlive = mY < screenHeight

        if (status == Sprite.Status.STATUS_PLAY) {
            mY += gameStates.speed
            if (mY < mHighestY) {
                mY = mHighestY
            }
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

        if (mState == LAUNCH && mFrame == 4) {
            cannonInterface?.onFire()
        }

        val frameDuration = if (mState == IDLE) {
            IDLE_FRAME_PER_MS
        } else {
            LAUNCH_FRAME_PER_MS
        }

        if(System.currentTimeMillis() - mLastFrameTimestamp > frameDuration) {
            mFrame++
            if (mFrame >= images.size) {
                mFrame = if(mState == LAUNCH) {
                    images.size - 1
                } else {
                    0
                }
            }
            mLastFrameTimestamp = System.currentTimeMillis()
        }
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
        cannonInterface = null
    }

    interface CannonInterface {
        fun onFire()
    }

}
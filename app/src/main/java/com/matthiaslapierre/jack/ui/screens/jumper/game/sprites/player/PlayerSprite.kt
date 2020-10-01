package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.player

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState
import com.matthiaslapierre.jack.ui.screens.jumper.game.GameStates

class PlayerSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .27f
        private const val BOTTOM_RATIO = .35f
        private const val HIGHEST_Y_RATIO = 0.6f
        private const val LOWEST_Y_RATIO = 0.9f
        private const val FRAME_PER_MS = 120
    }

    private var mState: PlayerState = PlayerState.JUMP
    private var mFrame: Int = 0
    private var mX: Float = UNDEFINED
    private var mY: Float = UNDEFINED
    private var mHighestY: Float = UNDEFINED
    private var mLowestY: Float = UNDEFINED
    private var mWidth: Float = UNDEFINED
    private var mHeight: Float = UNDEFINED
    private var mScreenHeight: Float = UNDEFINED
    private var mLastFrameTimestamp: Long = 0L

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val images = resourceManager.player!![mState]!!
        val image = images[mFrame]!!

        val screenWidth = canvas.width.toFloat()
        mScreenHeight = canvas.height.toFloat()
        if (mX == UNDEFINED) {
            mWidth = screenWidth * WIDTH_RATIO
            mHeight = mWidth * image.height / image.width.toFloat()
            mX = (screenWidth - mWidth) / 2f
            mY = mScreenHeight - (mScreenHeight * BOTTOM_RATIO) - (mHeight / 2f)
            mHighestY = (mScreenHeight - mHeight) * HIGHEST_Y_RATIO
            mLowestY = (mScreenHeight - mHeight) * LOWEST_Y_RATIO
        }

        if ((status == Sprite.Status.STATUS_PLAY
                    && gameStates.playerState == GameStates.PlayerState.LAUNCHED)
            || status == Sprite.Status.STATUS_GAME_OVER && mY > 0) {
            mY -= gameStates.speed
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

        if(System.currentTimeMillis() - mLastFrameTimestamp > FRAME_PER_MS) {
            mFrame++
            if (mFrame >= images.size) {
                mFrame = 0
            }
            mLastFrameTimestamp = System.currentTimeMillis()
        }
    }

    override fun isAlive(): Boolean = true

    override fun isHit(sprite: Sprite): Boolean = false

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = if (gameStates.playerState == GameStates.PlayerState.READY_TO_LAUNCH) {
        RectF(0f,0f,0f,0f)
    } else {
        RectF(
            mX,
            mY,
            mX + mWidth,
            mY + mHeight
        )
    }

    override fun onDispose() {

    }

    fun isDead() = mY > mScreenHeight

}
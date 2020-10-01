package com.matthiaslapierre.jack.ui.screens.jumper.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.MenuScreen
import com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.player.CannonSprite
import com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg.*
import com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.player.PlayerSprite
import com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.text.TapToLaunchSprite
import com.matthiaslapierre.jack.utils.Utils

class GameScreen(
    game: Game
): Screen(game), CannonSprite.CannonInterface {

    companion object {
        private const val INDICATOR_RATIO = 0.40f
        private const val TERNARY_BTN_RATIO = 0.14f
        private const val TOP_BAR_INSET_X = .13f
        private const val TOP_BAR_INSET_Y = .18f
    }

    private var mTopBgImage: Image? = null
    private var mCandyIndicatorImage: Image? = null
    private var mPauseBtnImage: Image? = null

    private var mWorkSprites: MutableList<Sprite> = mutableListOf()
    private var mPlayerSprite: PlayerSprite? = null

    private var mScreenWidth: Float = 0f
    private var mScreenHeight: Float = 0f

    private val mGameState: GameStates = GameStates()

    private var mPauseBtnIsPressed: Boolean = false

    override fun update() {
        val resourceManager = getResourceManager()
        mTopBgImage = resourceManager.bgTop
        mCandyIndicatorImage = resourceManager.candyIndicator
        mPauseBtnImage = if(mPauseBtnIsPressed) resourceManager.btnPausePressed else
            resourceManager.btnPause
        updateSprites()
        if(mGameState.currentStatus != Sprite.Status.STATUS_GAME_OVER) {
            checkCollisions()
        }
        mGameState.update()
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        mScreenWidth = canvas.width.toFloat()
        mScreenHeight = canvas.height.toFloat()
        mGameState.setScreenSize(mScreenWidth, mScreenHeight)
        drawSprites(canvas, globalPaint)
        drawTopBar(canvas, globalPaint)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }

    override fun onTouch(event: MotionEvent) {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when(event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                mPauseBtnIsPressed = btnIsPressed(getPauseBtnRect(), touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                mPauseBtnIsPressed = false
                if (touchY > getPauseBtnRect().bottom
                    && mGameState.currentStatus == Sprite.Status.STATUS_NOT_STARTED) {
                    startGame()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(mGameState.currentStatus != Sprite.Status.STATUS_PLAY) {
            game.setScreen(MenuScreen(game))
        }
    }

    override fun onFire() {
        mGameState.launch()
    }

    private fun startGame() {
        mGameState.currentStatus = Sprite.Status.STATUS_PLAY
    }

    private fun setGameOver() {
        mGameState.currentStatus = Sprite.Status.STATUS_GAME_OVER
    }

    private fun updateSprites() {
        if(mWorkSprites.size == 0) {
            val resourceManager = getResourceManager()
            mPlayerSprite = PlayerSprite(resourceManager, mGameState)
            setBackground()
            mWorkSprites.add(mPlayerSprite!!)
            mWorkSprites.add(CannonSprite(resourceManager, mGameState, this@GameScreen))
            setTapToLaunch()
        }
    }

    private fun checkCollisions() {
        if (mPlayerSprite!!.isDead()) {
            setGameOver()
        }
    }

    private fun setTapToLaunch() {
        mWorkSprites.add(TapToLaunchSprite(getResourceManager()))
    }

    private fun setBackground() {
        mWorkSprites.add(BgSprite(getResourceManager(), mGameState))
    }

    private fun drawTopBar(canvas: Canvas, globalPaint: Paint) {
        drawTopBarBackground(canvas, globalPaint)
        drawCandyIndicator(canvas, globalPaint)
        drawPauseBtn(canvas, globalPaint)
    }

    private fun drawSprites(canvas: Canvas, globalPaint: Paint) {
        val iterator: MutableListIterator<Sprite> = mWorkSprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isAlive()) {
                sprite.onDraw(canvas, globalPaint, mGameState.currentStatus)
            } else {
                iterator.remove()
                sprite.onDispose()
            }
        }
    }

    private fun drawTopBarBackground(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            mTopBgImage!!.bitmap,
            Rect(
                0,
                0,
                mTopBgImage!!.width,
                mTopBgImage!!.height
            ),
            getTopBarBackgroundRect(),
            globalPaint
        )
    }

    private fun drawCandyIndicator(canvas: Canvas, globalPaint: Paint) {
        val candyIndicatorRect = getCandyIndicatorRect()

        canvas.drawBitmap(
            mCandyIndicatorImage!!.bitmap,
            Rect(
                0,
                0,
                mCandyIndicatorImage!!.width,
                mCandyIndicatorImage!!.height
            ),
            candyIndicatorRect,
            globalPaint
        )

        val scoreBitmap = Utils.generateScoreBitmap(mGameState.candiesCollected, getResourceManager())
        val originalScoreWidth = scoreBitmap.width
        val originalScoreHeight = scoreBitmap.height
        val targetScoreHeight = (candyIndicatorRect.height() * .4f).toInt()
        val targetScoreWidth = (targetScoreHeight * originalScoreWidth / originalScoreHeight.toFloat()).toInt()
        val scoreX = (candyIndicatorRect.left + (candyIndicatorRect.width() * .5f)).toInt()
        val scoreY = (candyIndicatorRect.top + ((candyIndicatorRect.height() - targetScoreHeight) / 2f)
                + (candyIndicatorRect.height() * .05f)).toInt()
        canvas.drawBitmap(
            scoreBitmap,
            Rect(0, 0, originalScoreWidth, originalScoreHeight),
            Rect(scoreX, scoreY, scoreX + targetScoreWidth, scoreY + targetScoreHeight),
            globalPaint
        )
        scoreBitmap.recycle()
    }

    private fun drawPauseBtn(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            mPauseBtnImage!!.bitmap,
            Rect(
                0,
                0,
                mPauseBtnImage!!.width,
                mPauseBtnImage!!.height
            ),
            getPauseBtnRect(),
            globalPaint
        )
    }

    private fun getTopBarBackgroundRect(): Rect {
        val originalWidth = mTopBgImage!!.width
        val originalHeight = mTopBgImage!!.height
        val barHeight = mScreenWidth * originalHeight / originalWidth
        return Rect(
            0,
            0,
            mScreenWidth.toInt(),
            barHeight.toInt()
        )
    }

    private fun getCandyIndicatorRect(): Rect {
        val fromWidth = mCandyIndicatorImage!!.width
        val fromHeight = mCandyIndicatorImage!!.height
        val toWidth = (mScreenWidth * INDICATOR_RATIO).toInt()
        val toHeight = (toWidth * fromHeight / fromWidth.toFloat()).toInt()
        val xOutset = (toHeight * TOP_BAR_INSET_X).toInt()
        val yOutset = (toHeight * TOP_BAR_INSET_Y).toInt()
        return Rect(
            xOutset,
            yOutset,
            toWidth + xOutset,
            toHeight + yOutset
        )
    }

    private fun getPauseBtnRect(): Rect {
        val fromWidth = mPauseBtnImage!!.width
        val fromHeight = mPauseBtnImage!!.height
        val toWidth =  (mScreenWidth * TERNARY_BTN_RATIO).toInt()
        val toHeight = (toWidth * fromHeight / fromWidth.toFloat()).toInt()
        val xOutset = (toHeight * TOP_BAR_INSET_X).toInt()
        val yOutset = (toHeight * TOP_BAR_INSET_Y).toInt()
        return Rect(
            (mScreenWidth - toWidth - xOutset).toInt(),
            yOutset,
            (mScreenWidth - xOutset).toInt(),
            toHeight + yOutset
        )
    }

    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

    private fun getResourceManager(): ResourceManager = game.getGameResources() as ResourceManager

}
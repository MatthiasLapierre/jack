package com.matthiaslapierre.jumper.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.jumper.core.sprites.bg.BgSprite
import com.matthiaslapierre.jumper.core.sprites.bg.CloudSprite
import com.matthiaslapierre.jumper.core.sprites.player.CannonSprite
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite
import com.matthiaslapierre.jumper.core.sprites.text.TapToLaunchSprite
import com.matthiaslapierre.jumper.core.GameMap
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.utils.JumperUtils

class GameScreen(
    game: Game
): Screen(game), CannonSprite.CannonInterface {

    companion object {
        private const val INDICATOR_RATIO = 0.40f
        private const val TERNARY_BTN_RATIO = 0.14f
        private const val TOP_BAR_INSET_X = .13f
        private const val TOP_BAR_INSET_Y = .18f

        private const val MIN_CLOUDS = 20
        private const val CLOUD_INTERVAL_RATIO = 0.6f
    }

    private var topBgImage: Image? = null
    private var candyIndicatorImage: Image? = null
    private var pauseBtnImage: Image? = null

    private var workSprites: MutableList<Sprite> = mutableListOf()
    private var playerSprite: PlayerSprite? = null
    private var lastCloudSprite: CloudSprite? = null
    private var cloudInterval: Float = UNDEFINED
    private var countClouds: Int = 0
    private var countJumpingPlatforms: Int = 0

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private val gameState: GameStates =
        GameStates()
    private val gameMap: GameMap =
        GameMap(
            game.getGameResources() as ResourceManager,
            gameState
        )

    private var pauseBtnIsPressed: Boolean = false

    override fun update() {
        val resourceManager = getResourceManager()
        topBgImage = resourceManager.bgTop
        candyIndicatorImage = resourceManager.candyIndicator
        pauseBtnImage = if(pauseBtnIsPressed) resourceManager.btnPausePressed else
            resourceManager.btnPause
        updateSprites()
        if(gameState.currentStatus != Sprite.Status.STATUS_GAME_OVER) {
            checkCollisions()
        }
        gameState.update()
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        screenWidth = canvas.width.toFloat()
        screenHeight = canvas.height.toFloat()
        gameState.setScreenSize(screenWidth, screenHeight)
        gameMap.setScreenSize(screenWidth, screenHeight)
        cloudInterval = screenWidth * CLOUD_INTERVAL_RATIO
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
                pauseBtnIsPressed = btnIsPressed(getPauseBtnRect(), touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                pauseBtnIsPressed = false
                if (touchY > getPauseBtnRect().bottom
                    && gameState.currentStatus == Sprite.Status.STATUS_NOT_STARTED) {
                    startGame()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(gameState.currentStatus != Sprite.Status.STATUS_PLAY) {
            game.setScreen(MenuScreen(game))
        }
    }

    override fun onFire() {
        gameState.launch()
    }

    private fun startGame() {
        gameState.currentStatus = Sprite.Status.STATUS_PLAY
    }

    private fun setGameOver() {
        gameState.currentStatus = Sprite.Status.STATUS_GAME_OVER
    }

    private fun updateSprites() {
        if(workSprites.size == 0) {
            val resourceManager = getResourceManager()
            playerSprite = PlayerSprite(resourceManager, gameState)
            setBackground()
            workSprites.add(CannonSprite(resourceManager, gameState, this@GameScreen))
            setTapToLaunch()
        }
        addClouds()
        workSprites.addAll(gameMap.generate())
    }

    private fun addClouds() {
        if (cloudInterval == UNDEFINED) {
            return
        }

        var nextCloudY = -(screenHeight * 0.2f)
        if(lastCloudSprite != null) {
            nextCloudY = lastCloudSprite!!.y - cloudInterval
        }
        while(countClouds < MIN_CLOUDS) {
            lastCloudSprite = CloudSprite(getResourceManager(), gameState, nextCloudY)
            workSprites.add(1, lastCloudSprite!!)
            nextCloudY -= cloudInterval
            countClouds++
        }
    }

    private fun checkCollisions() {
        if (playerSprite!!.isDead()) {
            setGameOver()
        }
    }

    private fun setTapToLaunch() {
        workSprites.add(TapToLaunchSprite(getResourceManager()))
    }

    private fun setBackground() {
        workSprites.add(BgSprite(getResourceManager(), gameState))
    }

    private fun drawTopBar(canvas: Canvas, globalPaint: Paint) {
        drawTopBarBackground(canvas, globalPaint)
        drawCandyIndicator(canvas, globalPaint)
        drawPauseBtn(canvas, globalPaint)
    }

    private fun drawSprites(canvas: Canvas, globalPaint: Paint) {
        val iterator: MutableListIterator<Sprite> = workSprites.listIterator()
        while (iterator.hasNext()) {
            val sprite = iterator.next()
            if (sprite.isAlive()) {
                sprite.onDraw(canvas, globalPaint, gameState.currentStatus)
            } else {
                when (sprite) {
                    is CloudSprite -> countClouds--
                    is JumpingPlatformSprite -> countJumpingPlatforms--
                }
                iterator.remove()
                sprite.onDispose()
            }
        }
        playerSprite?.onDraw(canvas, globalPaint, gameState.currentStatus)
    }

    private fun drawTopBarBackground(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            topBgImage!!.bitmap,
            Rect(
                0,
                0,
                topBgImage!!.width,
                topBgImage!!.height
            ),
            getTopBarBackgroundRect(),
            globalPaint
        )
    }

    private fun drawCandyIndicator(canvas: Canvas, globalPaint: Paint) {
        val candyIndicatorRect = getCandyIndicatorRect()

        canvas.drawBitmap(
            candyIndicatorImage!!.bitmap,
            Rect(
                0,
                0,
                candyIndicatorImage!!.width,
                candyIndicatorImage!!.height
            ),
            candyIndicatorRect,
            globalPaint
        )

        val scoreBitmap = JumperUtils.generateScoreBitmap(gameState.candiesCollected, getResourceManager())
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
            pauseBtnImage!!.bitmap,
            Rect(
                0,
                0,
                pauseBtnImage!!.width,
                pauseBtnImage!!.height
            ),
            getPauseBtnRect(),
            globalPaint
        )
    }

    private fun getTopBarBackgroundRect(): Rect {
        val originalWidth = topBgImage!!.width
        val originalHeight = topBgImage!!.height
        val barHeight = screenWidth * originalHeight / originalWidth
        return Rect(
            0,
            0,
            screenWidth.toInt(),
            barHeight.toInt()
        )
    }

    private fun getCandyIndicatorRect(): Rect {
        val fromWidth = candyIndicatorImage!!.width
        val fromHeight = candyIndicatorImage!!.height
        val toWidth = (screenWidth * INDICATOR_RATIO).toInt()
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
        val fromWidth = pauseBtnImage!!.width
        val fromHeight = pauseBtnImage!!.height
        val toWidth =  (screenWidth * TERNARY_BTN_RATIO).toInt()
        val toHeight = (toWidth * fromHeight / fromWidth.toFloat()).toInt()
        val xOutset = (toHeight * TOP_BAR_INSET_X).toInt()
        val yOutset = (toHeight * TOP_BAR_INSET_Y).toInt()
        return Rect(
            (screenWidth - toWidth - xOutset).toInt(),
            yOutset,
            (screenWidth - xOutset).toInt(),
            toHeight + yOutset
        )
    }

    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

    private fun getResourceManager(): ResourceManager = game.getGameResources() as ResourceManager

}
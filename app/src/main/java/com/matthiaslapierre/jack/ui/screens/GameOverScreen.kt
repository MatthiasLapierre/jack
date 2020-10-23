package com.matthiaslapierre.jack.ui.screens

import android.app.Activity
import android.graphics.*
import android.view.MotionEvent
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.jack.core.resources.SoundManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.R
import com.matthiaslapierre.jack.utils.Utils

class GameOverScreen(
    game: Game,
    private val lastScore: Int,
    private val highScore: Int,
    private var screenShotBitmap: Bitmap
): Screen(game) {

    companion object {
        private const val WINDOW_MAX_WIDTH = 0.90f
        private const val PRIMARY_BTN_WIDTH = 0.5f
        private const val PRIMARY_BTN_MARGIN_TOP = 0.04f
        private const val SECONDARY_BTN_WIDTH = 0.20f
        private const val SECONDARY_BTN_SPACE = 0.02f
        private const val SECONDARY_BTN_BOTTOM = 0.05f
        private const val SCORE_DIGITS_HEIGHT = 0.1f
        private const val LAST_SCORE_Y = .55f
        private const val HIGH_SCORE_Y = .775f
    }

    private var windowImage: Image? = null
    private var replayBtnImage: Image? = null
    private var moreGamesBtnImage: Image? = null
    private var aboutMeBtnImage: Image? = null
    private var facebookBtnImage: Image? = null
    private var twitterBtnImage: Image? = null
    private var lastScoreBitmap: Bitmap? = null
    private var highScoreBitmap: Bitmap? = null

    private var facebookBtnIsPressed: Boolean = false
    private var twitterBtnIsPressed: Boolean = false
    private var replayBtnIsPressed: Boolean = false
    private var moreGamesBtnIsPressed: Boolean = false
    private var aboutMeBtnIsPressed: Boolean = false

    private var windowDstRect: Rect? = null
    private var replayBtnDstRect: Rect? = null
    private var facebookBtnDstRect: Rect? = null
    private var twitterBtnDstRect: Rect? = null
    private var moreGamesBtnDstRect: Rect? = null
    private var aboutMeBtnDstRect: Rect? = null
    private var lastScoreDstRect: Rect? = null
    private var highScoreDstRect: Rect? = null

    override fun update() {
        val resourceManager = (game.getGameResources() as ResourceManager)
        windowImage = resourceManager.windowGameOver
        facebookBtnImage = if(facebookBtnIsPressed) resourceManager.btnFacebookPressed else
            resourceManager.btnFacebook
        twitterBtnImage = if(twitterBtnIsPressed) resourceManager.btnTwitterPressed else
            resourceManager.btnTwitter
        replayBtnImage = if(replayBtnIsPressed) resourceManager.btnReplayPressed else
            resourceManager.btnReplay
        moreGamesBtnImage = if(moreGamesBtnIsPressed) resourceManager.btnMoreGamesPressed else
            resourceManager.btnMoreGames
        aboutMeBtnImage = if(aboutMeBtnIsPressed) resourceManager.btnAboutMePressed else
            resourceManager.btnAboutMe
        lastScoreBitmap = Utils.generateScoreBitmap(resourceManager, lastScore)
        highScoreBitmap = Utils.generateScoreBitmap(resourceManager, highScore)
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        computeDrawingRects(canvas.width, canvas.height)
        drawBackground(canvas, globalPaint)
        drawWindow(canvas, globalPaint)
        drawReplayBtn(canvas, globalPaint)
        drawFooterBtns(canvas, globalPaint)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {
        lastScoreBitmap?.recycle()
        lastScoreBitmap = null
        highScoreBitmap?.recycle()
        highScoreBitmap = null
        screenShotBitmap.recycle()
    }

    override fun onTouch(event: MotionEvent) {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()
        when(event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                replayBtnIsPressed = btnIsPressed(replayBtnDstRect, touchX, touchY)
                facebookBtnIsPressed = btnIsPressed(facebookBtnDstRect, touchX, touchY)
                twitterBtnIsPressed = btnIsPressed(twitterBtnDstRect, touchX, touchY)
                moreGamesBtnIsPressed = btnIsPressed(moreGamesBtnDstRect, touchX, touchY)
                aboutMeBtnIsPressed = btnIsPressed(aboutMeBtnDstRect, touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                when {
                    replayBtnIsPressed -> replay()
                    facebookBtnIsPressed -> shareFacebook()
                    twitterBtnIsPressed -> shareTwitter()
                    moreGamesBtnIsPressed -> showMoreGames()
                    aboutMeBtnIsPressed -> showMyLinkedInProfile()
                }
                if (replayBtnIsPressed
                    || facebookBtnIsPressed
                    || twitterBtnIsPressed
                    || moreGamesBtnIsPressed
                    || aboutMeBtnIsPressed) {
                    (game.getAudio() as SoundManager).playButtonPressedSound()
                }
                replayBtnIsPressed = false
                facebookBtnIsPressed = false
                twitterBtnIsPressed = false
                moreGamesBtnIsPressed = false
                aboutMeBtnIsPressed = false
            }
        }
    }

    override fun onBackPressed() {
        replay()
    }

    /**
     * Returns to the game scene.
     */
    private fun replay() {
        (game.getAudio() as SoundManager).playMenuMusic()
        game.setScreen(GameScreen(game))
    }

    /**
     * Shares on Facebook.
     */
    private fun shareFacebook() {
        val activity = (game as Activity)
        val text = activity.getString(R.string.share_text)
        val url = activity.getString(R.string.share_url)
        Utils.shareFacebook(activity, text, url)
    }

    /**
     * Shares on Twitter.
     */
    private fun shareTwitter() {
        val activity = (game as Activity)
        val text = activity.getString(R.string.share_text)
        val url = activity.getString(R.string.share_url)
        val hashTags = activity.getString(R.string.share_hash_tags)
        Utils.shareTwitter(activity, text, url, null, hashTags)
    }

    /**
     * Shows more games from the author.
     */
    private fun showMoreGames() {
        val activity = (game as Activity)
        val url = activity.getString(R.string.url_more_games)
        Utils.openUrl(activity, url)
    }

    /**
     * Shows my LinkedIn profile.
     */
    private fun showMyLinkedInProfile() {
        val activity = (game as Activity)
        val url = activity.getString(R.string.url_about_me)
        Utils.openUrl(activity, url)
    }

    /**
     * Gets locations of the subviews.
     */
    private fun computeDrawingRects(screenWidth: Int, screenHeight: Int) {
        var windowWidth = (screenWidth * WINDOW_MAX_WIDTH).toInt()
        var windowHeight = (windowWidth * windowImage!!.height / windowImage!!.width.toFloat()).toInt()

        val primaryBtnWidth = (screenWidth * PRIMARY_BTN_WIDTH).toInt()
        val primaryBtnHeight = (primaryBtnWidth * replayBtnImage!!.height / replayBtnImage!!.width.toFloat()).toInt()
        val primaryBtnMarginTop = (screenWidth * PRIMARY_BTN_MARGIN_TOP).toInt()

        val secondaryBtnWidth = (screenWidth * SECONDARY_BTN_WIDTH).toInt()
        val secondaryBtnHeight = (secondaryBtnWidth * facebookBtnImage!!.height / facebookBtnImage!!.width.toFloat()).toInt()
        val secondaryBtnSpace = (screenWidth * SECONDARY_BTN_SPACE).toInt()
        val secondaryBtnBottom = (screenWidth * SECONDARY_BTN_BOTTOM).toInt()

        val maxWindowHeight = screenHeight - (secondaryBtnBottom + secondaryBtnHeight + primaryBtnMarginTop + primaryBtnHeight)
        if (windowHeight > maxWindowHeight) {
            windowHeight = (maxWindowHeight * .9f).toInt()
            windowWidth = (windowHeight * windowImage!!.width / windowImage!!.height.toFloat()).toInt()
        }

        val footerX = ((screenWidth - ((secondaryBtnWidth * 4) + (secondaryBtnSpace * 4))) / 2f).toInt()
        val footerY = screenHeight - secondaryBtnBottom - secondaryBtnHeight
        var secondaryBtnX = footerX
        moreGamesBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )
        secondaryBtnX += secondaryBtnWidth + secondaryBtnSpace
        aboutMeBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )
        secondaryBtnX += secondaryBtnWidth + secondaryBtnSpace
        facebookBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )
        secondaryBtnX += secondaryBtnWidth + secondaryBtnSpace
        twitterBtnDstRect = Rect(
            secondaryBtnX,
            footerY,
            secondaryBtnX + secondaryBtnWidth,
            footerY + secondaryBtnHeight
        )

        val primaryContentSpace = ((screenHeight - secondaryBtnBottom - secondaryBtnHeight - primaryBtnHeight - primaryBtnMarginTop - windowHeight) / 2f).toInt()

        val windowX = ((screenWidth - windowWidth) / 2f).toInt()
        val windowY = primaryContentSpace
        windowDstRect = Rect(
            windowX,
            windowY,
            windowX + windowWidth,
            windowY + windowHeight
        )


        if (lastScoreBitmap != null && highScoreBitmap != null) {
            val scoreHeight = (windowHeight * SCORE_DIGITS_HEIGHT).toInt()
            val lastScoreWidth =
                (scoreHeight * lastScoreBitmap!!.width / lastScoreBitmap!!.height.toFloat()).toInt()
            val lastScoreX = ((screenWidth - lastScoreWidth) / 2f).toInt()
            val lastScoreY = (windowY + windowHeight * LAST_SCORE_Y).toInt()
            lastScoreDstRect = Rect(
                lastScoreX,
                lastScoreY,
                lastScoreX + lastScoreWidth,
                lastScoreY + scoreHeight
            )

            val highScoreWidth =
                (scoreHeight * highScoreBitmap!!.width / highScoreBitmap!!.height.toFloat()).toInt()
            val highScoreX = ((screenWidth - highScoreWidth) / 2f).toInt()
            val highScoreY = (windowY + windowHeight * HIGH_SCORE_Y).toInt()
            highScoreDstRect = Rect(
                highScoreX,
                highScoreY,
                highScoreX + highScoreWidth,
                highScoreY + scoreHeight
            )
        }

        val replayBtnX = ((screenWidth - primaryBtnWidth) / 2f).toInt()
        val replayBtnY = windowDstRect!!.bottom + secondaryBtnSpace
        replayBtnDstRect = Rect(
            replayBtnX,
            replayBtnY,
            replayBtnX + primaryBtnWidth,
            replayBtnY + primaryBtnHeight
        )

    }

    /**
     * Draws the background image (a screenshot of the game scene).
     */
    private fun drawBackground(canvas: Canvas, globalPaint: Paint) {
        if (!screenShotBitmap.isRecycled) {
            canvas.drawBitmap(screenShotBitmap, 0f, 0f, globalPaint)
        }
        canvas.drawARGB(150,0,0,0)
    }

    /**
     * Draws the window background with the last score and the high score.
     */
    private fun drawWindow(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            windowImage!!.bitmap,
            windowImage!!.rect,
            windowDstRect!!,
            globalPaint
        )

        if (lastScoreBitmap != null) {
            canvas.drawBitmap(
                lastScoreBitmap!!,
                Rect(
                    0,
                    0,
                    lastScoreBitmap!!.width,
                    lastScoreBitmap!!.height
                ),
                lastScoreDstRect!!,
                globalPaint
            )
        }
        if (highScoreBitmap != null) {
            canvas.drawBitmap(
                highScoreBitmap!!,
                Rect(
                    0,
                    0,
                    highScoreBitmap!!.width,
                    highScoreBitmap!!.height
                ),
                highScoreDstRect!!,
                globalPaint
            )
        }
    }

    /**
     * Draws the replay button.
     */
    private fun drawReplayBtn(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            replayBtnImage!!.bitmap,
            replayBtnImage!!.rect,
            replayBtnDstRect!!,
            globalPaint
        )
    }

    /**
     * Draws footer buttons.
     */
    private fun drawFooterBtns(canvas: Canvas, globalPaint: Paint) {
        canvas.drawBitmap(
            moreGamesBtnImage!!.bitmap,
            moreGamesBtnImage!!.rect,
            moreGamesBtnDstRect!!,
            globalPaint
        )

        canvas.drawBitmap(
            aboutMeBtnImage!!.bitmap,
            aboutMeBtnImage!!.rect,
            aboutMeBtnDstRect!!,
            globalPaint
        )

        canvas.drawBitmap(
            facebookBtnImage!!.bitmap,
            facebookBtnImage!!.rect,
            facebookBtnDstRect!!,
            globalPaint
        )

        canvas.drawBitmap(
            twitterBtnImage!!.bitmap,
            twitterBtnImage!!.rect,
            twitterBtnDstRect!!,
            globalPaint
        )
    }

    /**
     * Checks if a specified location is pressed.
     */
    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
        rect != null && rect.contains(touchX, touchY)

}
package com.matthiaslapierre.jack.ui.screens

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.jack.JackApp
import com.matthiaslapierre.jack.R
import com.matthiaslapierre.jack.core.resources.SoundManager
import com.matthiaslapierre.jack.core.settings.Settings
import com.matthiaslapierre.jack.utils.Utils

/**
 * Shows the menu.
 */
class MenuScreen(
    game: Game
): Screen(game) {

    companion object {
        private const val LOGO_WIDTH = 0.95f
        private const val PRIMARY_BTN_WIDTH = 0.33f
        private const val SECONDARY_BTN_WIDTH = 0.25f
        private const val TERNARY_BTN_WIDTH = 0.15f
    }

    private var settings: Settings = ((game as Context).applicationContext as JackApp)
        .appContainer.settings

    // Coordinates of the views.
    private var topDstRect: Rect? = null
    private var soundBtnDstRect: Rect? = null
    private var musicBtnDstRect: Rect? = null
    private var facebookBtnDstRect: Rect? = null
    private var twitterBtnDstRect: Rect? = null
    private var logoDstRect: Rect? = null
    private var playBtnDstRect: Rect? = null
    private var moreGamesBtnDstRect: Rect? = null
    private var scoreBtnDstRect: Rect? = null

    // Images to display.
    private var soundBtnImage: Image? = null
    private var musicBtnImage: Image? = null
    private var facebookBtnImage: Image? = null
    private var twitterBtnImage: Image? = null
    private var bgImage: Image? = null
    private var logoImage: Image? = null
    private var playBtnImage: Image? = null
    private var moreGamesBtnImage: Image? = null
    private var scoreBtnImage: Image? = null

    // Determines which button has been pressed.
    private var soundBtnIsPressed: Boolean = false
    private var musicBtnIsPressed: Boolean = false
    private var facebookBtnIsPressed: Boolean = false
    private var twitterBtnIsPressed: Boolean = false
    private var playBtnIsPressed: Boolean = false
    private var moreGamesBtnIsPressed: Boolean = false
    private var scoreGamesBtnIsPressed: Boolean = false

    override fun update() {
        val resourceManager = (game.getGameResources() as ResourceManager)
        bgImage = resourceManager.bgJump
        logoImage = resourceManager.logoJumperJack
        soundBtnImage = getSoundBtnImage()
        musicBtnImage = getMusicBtnImage()
        facebookBtnImage = if(facebookBtnIsPressed) resourceManager.btnFacebookPressed else
            resourceManager.btnFacebook
        twitterBtnImage = if(twitterBtnIsPressed) resourceManager.btnTwitterPressed else
            resourceManager.btnTwitter
        playBtnImage = if(playBtnIsPressed) resourceManager.btnPlayPressed else
            resourceManager.btnPlay
        moreGamesBtnImage = if(moreGamesBtnIsPressed) resourceManager.btnMoreGamesPressed else
            resourceManager.btnMoreGames
        scoreBtnImage = if(scoreGamesBtnIsPressed) resourceManager.btnScoresPressed else
            resourceManager.btnScores
    }

    override fun paint(canvas: Canvas, globalPaint: Paint) {
        computeDrawingRects(canvas.width, canvas.height)
        drawBackground(canvas, globalPaint)
        drawLogo(canvas, globalPaint)
        drawTopBar(canvas, globalPaint)
        drawBtns(canvas, globalPaint)
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
        // Determines which button has been pressed.
        when(event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                soundBtnIsPressed = btnIsPressed(soundBtnDstRect, touchX, touchY)
                musicBtnIsPressed = btnIsPressed(musicBtnDstRect, touchX, touchY)
                facebookBtnIsPressed = btnIsPressed(facebookBtnDstRect, touchX, touchY)
                twitterBtnIsPressed = btnIsPressed(twitterBtnDstRect, touchX, touchY)
                playBtnIsPressed = btnIsPressed(playBtnDstRect, touchX, touchY)
                moreGamesBtnIsPressed = btnIsPressed(moreGamesBtnDstRect, touchX, touchY)
                scoreGamesBtnIsPressed = btnIsPressed(scoreBtnDstRect, touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                when {
                    playBtnIsPressed -> playGame()
                    musicBtnIsPressed -> toggleMusic()
                    soundBtnIsPressed -> toggleSound()
                    facebookBtnIsPressed -> shareFacebook()
                    twitterBtnIsPressed -> shareTwitter()
                    moreGamesBtnIsPressed -> showMoreGames()
                }
                if (playBtnIsPressed
                    || musicBtnIsPressed
                    || soundBtnIsPressed
                    || moreGamesBtnIsPressed
                    || scoreGamesBtnIsPressed
                    || facebookBtnIsPressed
                    || twitterBtnIsPressed) {
                    (game.getAudio() as SoundManager).playButtonPressedSound()
                }
                soundBtnIsPressed = false
                musicBtnIsPressed = false
                facebookBtnIsPressed = false
                twitterBtnIsPressed = false
                playBtnIsPressed = false
                moreGamesBtnIsPressed = false
                scoreGamesBtnIsPressed = false
            }
        }
    }

    override fun onBackPressed() {
        (game as Activity).finish()
    }

    /**
     * Shows the game screen.
     */
    private fun playGame() {
        game.setScreen(GameScreen(game))
    }

    /**
     * Enables / Disables music.
     */
    private fun toggleMusic() {
        settings.musicEnabled = !settings.musicEnabled
        (game.getAudio() as SoundManager).enableMusic(settings.musicEnabled)
    }

    /**
     * Enables / Disables sounds.
     */
    private fun toggleSound() {
        settings.soundEnabled = !settings.soundEnabled
        (game.getAudio() as SoundManager).enableSounds(settings.soundEnabled)
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
     * Gets coordinates of the views.
     */
    private fun computeDrawingRects(screenWidth: Int, screenHeight: Int) {
        var x = 0
        var y = 0

        val titleWidth = (screenWidth * LOGO_WIDTH).toInt()
        val titleHeight = (titleWidth * logoImage!!.height / logoImage!!.width.toFloat()).toInt()

        val primaryBtnWidth = (screenWidth * PRIMARY_BTN_WIDTH).toInt()
        val primaryBtnHeight = (primaryBtnWidth * playBtnImage!!.height / playBtnImage!!.width.toFloat()).toInt()
        val secondaryBtnWidth = (screenWidth * SECONDARY_BTN_WIDTH).toInt()
        val secondaryBtnHeight = (screenWidth * SECONDARY_BTN_WIDTH * scoreBtnImage!!.height / scoreBtnImage!!.width.toFloat()).toInt()
        val ternaryBtnWidth = (screenWidth * TERNARY_BTN_WIDTH).toInt()
        val ternaryBtnHeight = ternaryBtnWidth * soundBtnImage!!.height / soundBtnImage!!.width
        val ternaryBtnSpace = (ternaryBtnWidth * .1f).toInt()
        val btnContainerHeight = primaryBtnHeight + secondaryBtnHeight
        val space = ((screenHeight - ternaryBtnHeight - titleHeight - btnContainerHeight) / 3f).toInt()

        soundBtnDstRect = Rect(
            x + ternaryBtnSpace,
            y + ternaryBtnSpace,
            x + ternaryBtnWidth - ternaryBtnSpace,
            ternaryBtnHeight - ternaryBtnSpace
        )

        x += ternaryBtnWidth
        musicBtnDstRect = Rect(
            x + ternaryBtnSpace,
            y + ternaryBtnSpace,
            x + ternaryBtnWidth - ternaryBtnSpace,
            ternaryBtnHeight - ternaryBtnSpace
        )

        x = screenWidth - (ternaryBtnWidth * 2)
        facebookBtnDstRect = Rect(
            x + ternaryBtnSpace,
            y + ternaryBtnSpace,
            x + ternaryBtnWidth - ternaryBtnSpace,
            ternaryBtnHeight - ternaryBtnSpace
        )

        x += ternaryBtnWidth
        twitterBtnDstRect = Rect(
            x + ternaryBtnSpace,
            y + ternaryBtnSpace,
            x + ternaryBtnWidth - ternaryBtnSpace,
            ternaryBtnHeight - ternaryBtnSpace
        )

        y = ternaryBtnHeight + space

        topDstRect = Rect(0, 0, screenWidth, y)

        x = ((screenWidth - titleWidth) / 2f).toInt()
        logoDstRect = Rect(
            x,
            y,
            x + titleWidth,
            y + titleHeight
        )

        x = ((screenWidth - primaryBtnWidth) / 2f).toInt()
        y += titleHeight + space
        playBtnDstRect = Rect(
            x,
            y,
            x + primaryBtnWidth,
            y + primaryBtnHeight
        )

        y += primaryBtnHeight

        x = ((screenWidth - ( 3 * secondaryBtnWidth)) / 2f).toInt()
        moreGamesBtnDstRect = Rect(
            x,
            y,
            x + secondaryBtnWidth,
            y + secondaryBtnHeight
        )

        x = ((screenWidth + (secondaryBtnWidth)) / 2f).toInt()
        scoreBtnDstRect = Rect(
            x,
            y,
            x + secondaryBtnWidth,
            y + secondaryBtnHeight
        )
    }

    /**
     * Draws the background images.
     */
    private fun drawBackground(canvas: Canvas, globalPaint: Paint) {
        val screenWidth = canvas.width
        val screenHeight = canvas.height
        val originalWidth = bgImage!!.width
        val originalHeight = bgImage!!.height
        val scale = screenWidth / bgImage!!.width.toFloat()
        val finalHeight = (originalHeight * scale).toInt()

        canvas.drawBitmap(
            bgImage!!.bitmap,
            Rect(
                0,
                0,
                originalWidth,
                originalHeight
            ),
            Rect(
                0,
                screenHeight - finalHeight,
                screenWidth,
                screenHeight
            ),
            globalPaint
        )
    }

    /**
     * Draws the top bar with social media buttons.
     */
    private fun drawTopBar(canvas: Canvas, globalPaint: Paint) {
        drawImage(
            canvas,
            globalPaint,
            soundBtnImage!!,
            soundBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            musicBtnImage!!,
            musicBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            facebookBtnImage!!,
            facebookBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            twitterBtnImage!!,
            twitterBtnDstRect!!
        )
    }

    /**
     * Draws the game logo.
     */
    private fun drawLogo(canvas: Canvas, globalPaint: Paint) {
        drawImage(
            canvas,
            globalPaint,
            logoImage!!,
            logoDstRect!!
        )
    }

    /**
     * Draws the "play" button, the "score" button and the "more game" button.
     */
    private fun drawBtns(canvas: Canvas, globalPaint: Paint) {
        drawImage(
            canvas,
            globalPaint,
            playBtnImage!!,
            playBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            moreGamesBtnImage!!,
            moreGamesBtnDstRect!!
        )
        drawImage(
            canvas,
            globalPaint,
            scoreBtnImage!!,
            scoreBtnDstRect!!
        )
    }

    /**
     * Utility method to draw an image on the canvas.
     */
    private fun drawImage(
        canvas: Canvas,
        globalPaint: Paint,
        image: Image,
        dst: Rect
    ) {
        canvas.drawBitmap(
            image.bitmap,
            image.rect,
            dst,
            globalPaint
        )
    }

    /**
     * Checks if the location specified is pressed.
     */
    private fun btnIsPressed(rect: Rect?, touchX: Int, touchY: Int): Boolean =
            rect != null && rect.contains(touchX, touchY)

    /**
     * Gets the image to display in the sound button.
     */
    private fun getSoundBtnImage(): Image {
        val resourceManager = (game.getGameResources() as ResourceManager)
        return when {
            soundBtnIsPressed -> {
                resourceManager.btnSoundPressed!!
            }
            settings.soundEnabled -> {
                resourceManager.btnSound!!
            }
            else -> {
                resourceManager.btnSoundDisabled!!
            }
        }
    }

    /**
     * Gets the image to display in the music button.
     */
    private fun getMusicBtnImage(): Image {
        val resourceManager = (game.getGameResources() as ResourceManager)
        return when {
            musicBtnIsPressed -> {
                resourceManager.btnMusicPressed!!
            }
            settings.musicEnabled -> {
                resourceManager.btnMusic!!
            }
            else -> {
                resourceManager.btnMusicDisabled!!
            }
        }
    }

}
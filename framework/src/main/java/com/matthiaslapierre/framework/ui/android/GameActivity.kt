package com.matthiaslapierre.framework.ui.android

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.matthiaslapierre.framework.files.FileIO
import com.matthiaslapierre.framework.files.android.AndroidFileIO
import com.matthiaslapierre.framework.input.Input
import com.matthiaslapierre.framework.input.android.AndroidInput
import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Typefaces
import com.matthiaslapierre.framework.resources.impl.AndroidGameResources
import com.matthiaslapierre.framework.resources.impl.AndroidTypefaces
import com.matthiaslapierre.framework.sounds.Audio
import com.matthiaslapierre.framework.sounds.android.AndroidAudio
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Graphics
import com.matthiaslapierre.framework.ui.Screen

abstract class GameActivity : AppCompatActivity(), Game {

    private lateinit var mGameView: GameView
    private lateinit var mGraphics: Graphics
    private lateinit var mAudio: Audio
    private lateinit var mGameResources: GameResources
    private lateinit var mTypefaces: Typefaces
    private lateinit var mInput: Input
    private lateinit var mFileIO: FileIO
    private lateinit var mScreen: Screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        volumeControlStream = AudioManager.STREAM_MUSIC

        val frameBufferWidth = Resources.getSystem().displayMetrics.widthPixels
        val frameBufferHeight = Resources.getSystem().displayMetrics.heightPixels
        val frameBuffer = Bitmap.createBitmap(
            frameBufferWidth,
            frameBufferHeight, Bitmap.Config.RGB_565
        )

        mScreen = getInitScreen()
        mGameView = GameView(applicationContext, this, frameBuffer)
        mGraphics = AndroidGraphics(assets, frameBuffer)
        mFileIO = AndroidFileIO(this)
        mAudio = AndroidAudio(this)
        mGameResources = AndroidGameResources()
        mTypefaces = AndroidTypefaces()
        mInput = AndroidInput(mGameView)
        setContentView(mGameView)
    }

    override fun onResume() {
        super.onResume()
        mScreen.resume()
        mGameView.resume()
    }

    override fun onPause() {
        mGameView.pause()
        mScreen.pause()
        super.onPause()
    }

    override fun onDestroy() {
        mScreen.dispose()
        super.onDestroy()
    }

    override fun getAudio(): Audio = mAudio

    override fun getGameResources(): GameResources = mGameResources

    override fun getTypefaces(): Typefaces = mTypefaces

    override fun getInput(): Input = mInput

    override fun getFileIO(): FileIO = mFileIO

    override fun getGraphics(): Graphics = mGraphics

    override fun getCurrentScreen(): Screen = mScreen

    override fun getInitScreen(): Screen = mScreen

    override fun setScreen(screen: Screen) {
        this.mScreen.pause()
        this.mScreen.dispose()
        mGraphics.clearScreen(Color.BLACK)
        screen.resume()
        screen.update()
        this.mScreen = screen
    }

}
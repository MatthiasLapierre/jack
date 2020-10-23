package com.matthiaslapierre.framework.ui.android

import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.matthiaslapierre.framework.files.FileIO
import com.matthiaslapierre.framework.files.android.AndroidFileIO
import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Typefaces
import com.matthiaslapierre.framework.resources.impl.AndroidGameResources
import com.matthiaslapierre.framework.resources.impl.AndroidTypefaces
import com.matthiaslapierre.framework.sounds.Audio
import com.matthiaslapierre.framework.sounds.android.AndroidAudio
import com.matthiaslapierre.framework.ui.Game
import com.matthiaslapierre.framework.ui.Screen

abstract class GameActivity : AppCompatActivity(), Game {

    private lateinit var mGameView: GameView
    private lateinit var mAudio: Audio
    private lateinit var mGameResources: GameResources
    private lateinit var mTypefaces: Typefaces
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

        mScreen = getInitScreen()
        mGameView = GameView(applicationContext, this)
        mFileIO = AndroidFileIO(this)
        mAudio = AndroidAudio(this)
        mGameResources = AndroidGameResources()
        mTypefaces = AndroidTypefaces()
        setContentView(mGameView)
    }

    override fun onResume() {
        super.onResume()
        getAudio().resume()
        mScreen.resume()
        mGameView.resume()
    }

    override fun onPause() {
        mGameView.pause()
        mScreen.pause()
        getAudio().pause()
        super.onPause()
    }

    override fun onDestroy() {
        mScreen.dispose()
        mAudio.dispose()
        mGameResources.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        getCurrentScreen().onBackPressed()
    }

    override fun getAudio(): Audio = mAudio

    override fun getGameResources(): GameResources = mGameResources

    override fun getTypefaces(): Typefaces = mTypefaces

    override fun getFileIO(): FileIO = mFileIO

    override fun getCurrentScreen(): Screen = mScreen

    override fun getInitScreen(): Screen = mScreen

    override fun setScreen(screen: Screen) {
        val previousScreen = mScreen
        previousScreen.pause()
        screen.resume()
        screen.update()
        this.mScreen = screen
        previousScreen.dispose()
    }

    override fun takeScreenShot(): Bitmap = mGameView.capture()

}
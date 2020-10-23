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

/**
 * Game parent Activity.
 */
abstract class GameActivity : AppCompatActivity(), Game {

    private lateinit var gameView: GameView
    private lateinit var audio: Audio
    private lateinit var gameResources: GameResources
    private lateinit var typefaces: Typefaces
    private lateinit var fileIO: FileIO
    private lateinit var currentScreen: Screen

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

        currentScreen = getInitScreen()
        gameView = GameView(applicationContext, this)
        fileIO = AndroidFileIO(this)
        audio = AndroidAudio(this)
        gameResources = AndroidGameResources()
        typefaces = AndroidTypefaces()
        setContentView(gameView)
    }

    override fun onResume() {
        super.onResume()
        getAudio().resume()
        currentScreen.resume()
        gameView.resume()
    }

    override fun onPause() {
        gameView.pause()
        currentScreen.pause()
        getAudio().pause()
        super.onPause()
    }

    override fun onDestroy() {
        currentScreen.dispose()
        audio.dispose()
        gameResources.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        getCurrentScreen().onBackPressed()
    }

    override fun getAudio(): Audio = audio

    override fun getGameResources(): GameResources = gameResources

    override fun getTypefaces(): Typefaces = typefaces

    override fun getFileIO(): FileIO = fileIO

    override fun getCurrentScreen(): Screen = currentScreen

    override fun getInitScreen(): Screen = currentScreen

    override fun setScreen(screen: Screen) {
        val previousScreen = currentScreen
        previousScreen.pause()
        screen.resume()
        screen.update()
        this.currentScreen = screen
        previousScreen.dispose()
    }

    override fun takeScreenShot(): Bitmap = gameView.capture()

}
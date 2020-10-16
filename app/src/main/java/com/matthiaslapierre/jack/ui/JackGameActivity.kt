package com.matthiaslapierre.jack.ui

import com.matthiaslapierre.core.SoundManager
import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Typefaces
import com.matthiaslapierre.framework.sounds.Audio
import com.matthiaslapierre.framework.ui.Screen
import com.matthiaslapierre.framework.ui.android.GameActivity
import com.matthiaslapierre.jack.JackApp
import com.matthiaslapierre.jack.ui.screens.SplashScreen

class JackGameActivity : GameActivity() {

    override fun onResume() {
        super.onResume()
        (getAudio() as SoundManager).resumeMusic()
    }

    override fun onPause() {
        (getAudio() as SoundManager).pauseMusic()
        super.onPause()
    }

    override fun getInitScreen(): Screen = SplashScreen(this)

    override fun getAudio(): Audio = (application as JackApp).appContainer.soundManager

    override fun getGameResources(): GameResources = (application as JackApp).appContainer.resourceManager

    override fun getTypefaces(): Typefaces = (application as JackApp).appContainer.typefaceManager

}
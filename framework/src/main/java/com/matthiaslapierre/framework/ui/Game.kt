package com.matthiaslapierre.framework.ui

import com.matthiaslapierre.framework.files.FileIO
import com.matthiaslapierre.framework.input.Input
import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Typefaces
import com.matthiaslapierre.framework.sounds.Audio

interface Game {
    fun getAudio(): Audio
    fun getGameResources(): GameResources
    fun getTypefaces(): Typefaces
    fun getInput(): Input
    fun getFileIO(): FileIO
    fun getGraphics(): Graphics
    fun getCurrentScreen(): Screen
    fun getInitScreen(): Screen
    fun setScreen(screen: Screen)
}
package com.matthiaslapierre.framework.ui

import android.graphics.Bitmap
import com.matthiaslapierre.framework.files.FileIO
import com.matthiaslapierre.framework.resources.GameResources
import com.matthiaslapierre.framework.resources.Typefaces
import com.matthiaslapierre.framework.sounds.Audio

/**
 * Handles transitions between screens. keeps a reference to the main components of the game.
 */
interface Game {
    /**
     * Handles a set of audio streams
     */
    fun getAudio(): Audio

    /**
     * Manages game resources (images).
     */
    fun getGameResources(): GameResources

    /**
     * Typeface handler.
     */
    fun getTypefaces(): Typefaces

    /**
     * Manages files.
     */
    fun getFileIO(): FileIO

    /**
     * Gets the current screen.
     */
    fun getCurrentScreen(): Screen

    /**
     * Gets the first screen to display.
     */
    fun getInitScreen(): Screen

    /**
     * Changes the screen to display.
     */
    fun setScreen(screen: Screen)

    /**
     * Takes a screenshot of the current screen.
     */
    fun takeScreenShot(): Bitmap
}
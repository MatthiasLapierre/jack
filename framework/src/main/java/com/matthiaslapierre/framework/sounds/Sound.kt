package com.matthiaslapierre.framework.sounds

/**
 * Handles an audio stream
 */
interface Sound {
    /**
     * Plays the sound effect.
     * @param volume audio volume
     */
    fun play(volume: Float)

    /**
     * Releases the audio stream.
     */
    fun dispose()
}
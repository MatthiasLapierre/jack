package com.matthiaslapierre.framework.sounds

/**
 * Handles a set of audio streams
 */
interface Audio {
    /**
     * Loads resources.
     */
    fun load()

    /**
     * Resumes musics and sound effects.
     */
    fun resume()

    /**
     * Pauses musics and sound effects.
     */
    fun pause()

    /**
     * Stops musics and sound effects.
     */
    fun stop()

    /**
     * Creates a new music stream.
     */
    fun createMusic(file: String): Music

    /**
     * Creates a new sound stream.
     */
    fun createSound(file: String): Sound

    /**
     * Releases musics and sounds.
     */
    fun dispose()
}
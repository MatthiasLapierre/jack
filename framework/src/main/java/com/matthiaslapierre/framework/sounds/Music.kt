package com.matthiaslapierre.framework.sounds

/**
 * Handles a music stream
 */
interface Music {
    /**
     * The music is playing.
     */
    val isPlaying: Boolean

    /**
     * The music is stopped.
     */
    val isStopped: Boolean

    /**
     * The music is looping.
     */
    var isLooping: Boolean

    /**
     * Plays the music.
     */
    fun play()

    /**
     * Stops the music.
     */
    fun stop()

    /**
     * Pauses the music.
     */
    fun pause()

    /**
     * Updates the audio volume.
     */
    fun setVolume(volume: Float)

    /**
     * Releases the music stream.
     */
    fun dispose()
}
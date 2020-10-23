package com.matthiaslapierre.framework.sounds

interface Audio {
    fun load()
    fun resume()
    fun pause()
    /**
     * Stops musics and sound effects.
     */
    fun stop()
    fun createMusic(file: String): Music
    fun createSound(file: String): Sound
    fun dispose()
}
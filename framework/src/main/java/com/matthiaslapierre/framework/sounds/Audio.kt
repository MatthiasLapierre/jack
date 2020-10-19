package com.matthiaslapierre.framework.sounds

interface Audio {
    fun load()
    fun resume()
    fun pause()
    fun createMusic(file: String): Music
    fun createSound(file: String): Sound
}
package com.matthiaslapierre.framework.sounds

interface Audio {
    fun load()
    fun resumeMusic()
    fun pauseMusic()
    fun createMusic(file: String): Music
    fun createSound(file: String): Sound
}
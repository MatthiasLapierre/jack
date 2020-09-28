package com.matthiaslapierre.framework.sounds

interface Sound {
    fun play(volume: Float)
    fun dispose()
}
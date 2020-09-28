package com.matthiaslapierre.framework.ui

abstract class Screen(
    protected val game: Game
) {
    abstract fun update()
    abstract fun paint()
    abstract fun pause()
    abstract fun resume()
    abstract fun dispose()
    abstract fun onBackPressed()
}
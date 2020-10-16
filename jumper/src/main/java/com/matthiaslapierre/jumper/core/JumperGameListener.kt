package com.matthiaslapierre.jumper.core

interface JumperGameListener {
    fun onGameOver(candiesCollected: Int)
    fun onDie()
    fun onCollectCandies()
    fun onRocketSpeed()
}
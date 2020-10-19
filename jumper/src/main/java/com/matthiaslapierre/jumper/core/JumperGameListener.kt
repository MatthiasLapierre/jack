package com.matthiaslapierre.jumper.core

interface JumperGameListener {
    fun onJump()
    fun onGameOver(candiesCollected: Int)
    fun onDie()
    fun onCollectCandies()
    fun onHit()
    fun onDestroyEnemy()
}
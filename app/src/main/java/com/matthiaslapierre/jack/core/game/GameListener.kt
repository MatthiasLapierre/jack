package com.matthiaslapierre.jack.core.game

interface GameListener {
    /**
     * The main character jumps
     */
    fun onJump()

    /**
     * The main character is dead and its sprite is out of the screen. The "Game Over"
     * screen is ready to display.
     */
    fun onGameOver(candiesCollected: Int)

    /**
     * The main character is dead.
     */
    fun onDie()

    /**
     * Candies has just been collected.
     */
    fun onCollectCandies()

    /**
     * The main character has just hit an obstacle.
     */
    fun onHit()

    /**
     * The main character has just destroyed an obstacle.
     */
    fun onDestroyEnemy()

    /**
     * The main character is flying with a rocket.
     */
    fun onRocketFlight()

    /**
     * The main character is flying with a copter.
     */
    fun onCopterFlight()

    /**
     * The main character has just lost his rocket or copter.
     */
    fun onNoFlight()
}
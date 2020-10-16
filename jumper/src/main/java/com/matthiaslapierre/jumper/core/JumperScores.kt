package com.matthiaslapierre.jumper.core

/**
 * Stores scores locally by using the Preferences API.
 */
internal interface JumperScores {

    /**
     * Gets the best score achieved.
     */
    fun highScore(): Int

    /**
     * Checks if it's the new best score.
     */
    fun isNewBestScore(score: Int): Boolean

    /**
     * Records the new best score.
     */
    fun storeHighScore(score: Int)

}
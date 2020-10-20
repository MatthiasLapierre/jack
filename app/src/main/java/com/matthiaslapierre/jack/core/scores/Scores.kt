package com.matthiaslapierre.jack.core.scores

/**
 * Stores scores locally by using the Preferences API.
 */
interface Scores {

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
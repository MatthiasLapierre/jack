package com.matthiaslapierre.jumper.core.impl

import android.content.Context
import android.content.SharedPreferences
import com.matthiaslapierre.jumper.core.JumperScores

/**
 * Stores scores locally by using the Preferences API.
 */
internal class JumperScoresImpl(
    private val context: Context
): JumperScores {

    companion object {
        private const val PREF_DEFAULT = "com.matthiaslapierre.jack.jumper.PREF_DEFAULT"
        private const val HIGH_SCORE = "high_score"
    }

    override fun highScore(): Int {
        val p: SharedPreferences = context.getSharedPreferences(
            PREF_DEFAULT,
            Context.MODE_PRIVATE
        )
        return p.getInt(HIGH_SCORE, 0)
    }

    override fun isNewBestScore(score: Int): Boolean = score > highScore()

    override fun storeHighScore(score: Int) {
        val p: SharedPreferences = context.getSharedPreferences(
            PREF_DEFAULT,
            Context.MODE_PRIVATE
        )
        p.edit().putInt(HIGH_SCORE, score).apply()
    }

}
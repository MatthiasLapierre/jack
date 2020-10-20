package com.matthiaslapierre.jack.core.scores

import android.content.Context
import android.content.SharedPreferences

class JackScores(
    private val context: Context
): Scores {

    companion object {
        private const val PREF_DEFAULT = "com.matthiaslapierre.jack.PREF_DEFAULT"
        private const val HIGH_SCORE = "high_score"
    }

    override fun highScore(): Int {
        return getPreferences().getInt(HIGH_SCORE, 0)
    }

    override fun isNewBestScore(score: Int): Boolean = score > highScore()

    override fun storeHighScore(score: Int) {
        getPreferences().edit().putInt(HIGH_SCORE, score).apply()
    }

    private fun getPreferences(): SharedPreferences =
        context.getSharedPreferences(
            PREF_DEFAULT,
            Context.MODE_PRIVATE
        )

}
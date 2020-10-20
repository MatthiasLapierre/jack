package com.matthiaslapierre.jack.core.settings

import android.content.Context
import android.content.SharedPreferences

class JackSettings(
    private val context: Context
): Settings {

    companion object {
        private const val PREF_DEFAULT = "com.matthiaslapierre.jack.PREF_DEFAULT"
        private const val SOUND_ENABLED = "sound_enabled"
        private const val MUSIC_ENABLED = "music_enabled"
    }

    override var soundEnabled: Boolean
        get() = getPreferences().getBoolean(SOUND_ENABLED, true)
        set(value) {
            getPreferences().edit().putBoolean(SOUND_ENABLED, value).apply()
        }
    override var musicEnabled: Boolean
        get() = getPreferences().getBoolean(MUSIC_ENABLED, true)
        set(value) {
            getPreferences().edit().putBoolean(MUSIC_ENABLED, value).apply()
        }

    private fun getPreferences(): SharedPreferences =
        context.getSharedPreferences(
            PREF_DEFAULT,
            Context.MODE_PRIVATE
        )

}
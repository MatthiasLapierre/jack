package com.matthiaslapierre.jack.core.settings

/**
 * Stores settings by using the Preferences API.
 */
interface Settings {
    /**
     * If sounds are enabled.
     */
    var soundEnabled: Boolean

    /**
     * If musics are enabled.
     */
    var musicEnabled: Boolean
}
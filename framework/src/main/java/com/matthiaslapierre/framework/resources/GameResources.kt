package com.matthiaslapierre.framework.resources

/**
 * Manages game resources (images).
 */
interface GameResources {
    /**
     * Loads resources.
     */
    fun load()

    /**
     * Releases resources.
     */
    fun dispose()
}
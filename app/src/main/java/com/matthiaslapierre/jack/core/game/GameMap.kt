package com.matthiaslapierre.jack.core.game

import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.resources.ResourceManager

/**
 * Map generation.
 */
interface GameMap {

    val resourceManager: ResourceManager
    val gameStates: GameStates

    /**
     * Generates the map (add and position the next sprites).
     */
    fun generate(): List<Sprite>

    /**
     * Updates the screen size properties.
     */
    fun setScreenSize(screenWidth: Float, screenHeight: Float)

}
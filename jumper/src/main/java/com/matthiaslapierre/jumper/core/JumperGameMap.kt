package com.matthiaslapierre.jumper.core

import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite

internal interface JumperGameMap {

    val resourceManager: ResourceManager
    val gameStates: JumperGameStates

    fun generate(): List<Sprite>

    fun setScreenSize(screenWidth: Float, screenHeight: Float)

}
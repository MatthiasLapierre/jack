package com.matthiaslapierre.jumper.core.impl

import com.matthiaslapierre.core.Factory
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.jumper.core.JumperGameListener
import com.matthiaslapierre.jumper.core.JumperGameLogic

internal class JumperGameLogicFactory(
    private val resourceManager: ResourceManager,
    private val gameListener: JumperGameListener
): Factory<JumperGameLogic> {
    override fun create(): JumperGameLogic {
        val states = JumperGameStatesImpl()
        val map = JumperGameMapImpl(resourceManager, states)
        val processor = JumperGameProcessorImpl(resourceManager, states, map, gameListener)
        return JumperGameLogicImpl(processor, map, states)
    }
}
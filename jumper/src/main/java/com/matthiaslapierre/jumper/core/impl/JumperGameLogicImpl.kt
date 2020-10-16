package com.matthiaslapierre.jumper.core.impl

import com.matthiaslapierre.jumper.core.JumperGameLogic
import com.matthiaslapierre.jumper.core.JumperGameMap
import com.matthiaslapierre.jumper.core.JumperGameProcessor
import com.matthiaslapierre.jumper.core.JumperGameStates

internal class JumperGameLogicImpl(
    override val gameProcessor: JumperGameProcessor,
    override val gameMap: JumperGameMap,
    override val gameStates: JumperGameStates
) : JumperGameLogic
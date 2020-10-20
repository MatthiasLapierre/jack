package com.matthiaslapierre.jack.core.game.impl

import com.matthiaslapierre.jack.core.game.GameLogic
import com.matthiaslapierre.jack.core.game.GameMap
import com.matthiaslapierre.jack.core.game.GameProcessor
import com.matthiaslapierre.jack.core.game.GameStates

internal class JackGameLogic(
    override val gameProcessor: GameProcessor,
    override val gameMap: GameMap,
    override val gameStates: GameStates
) : GameLogic
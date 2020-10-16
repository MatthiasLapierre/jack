package com.matthiaslapierre.jumper.core

internal interface JumperGameLogic {
    val gameProcessor: JumperGameProcessor
    val gameMap: JumperGameMap
    val gameStates: JumperGameStates
}
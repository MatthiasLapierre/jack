package com.matthiaslapierre.jack.core.game

/**
 * Manages the game's logic (map generation, game state).
 */
interface GameLogic {
    val gameProcessor: GameProcessor
    val gameMap: GameMap
    val gameStates: GameStates
}
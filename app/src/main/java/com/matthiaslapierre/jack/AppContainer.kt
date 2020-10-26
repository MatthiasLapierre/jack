package com.matthiaslapierre.jack

import android.content.Context
import com.matthiaslapierre.jack.core.resources.impl.JackResourceManager
import com.matthiaslapierre.jack.core.game.GameLogic
import com.matthiaslapierre.jack.core.game.GameListener
import com.matthiaslapierre.jack.core.game.impl.JackGameLogic
import com.matthiaslapierre.jack.core.game.impl.JackGameMap
import com.matthiaslapierre.jack.core.game.impl.JackGameProcessor
import com.matthiaslapierre.jack.core.game.impl.JackGameStates
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.jack.core.resources.SoundManager
import com.matthiaslapierre.jack.core.resources.TypefaceManager
import com.matthiaslapierre.jack.core.resources.impl.JackSoundManager
import com.matthiaslapierre.jack.core.resources.impl.JackTypefaceManager
import com.matthiaslapierre.jack.core.scores.JackScores
import com.matthiaslapierre.jack.core.scores.Scores
import com.matthiaslapierre.jack.core.settings.JackSettings
import com.matthiaslapierre.jack.core.settings.Settings
import com.matthiaslapierre.jack.utils.Factory

/**
 * To solve the issue of reusing objects, you can create your own dependencies container class
 * that you use to get dependencies. All instances provided by this container can be public.
 * Because these dependencies are used across the whole application, they need to be placed in
 * a common place all activities can use: the application class.
 * @see [Doc](https://developer.android.com/training/dependency-injection/manual)
 */
class AppContainer(
    context: Context
) {
    val soundManager: SoundManager =
        JackSoundManager(context)
    val resourceManager: ResourceManager = JackResourceManager(context)
    val typefaceManager: TypefaceManager =
        JackTypefaceManager(context)
    val gameLogicFactory: GameLogicFactory = GameLogicFactory(resourceManager)
    val scores: Scores =
        JackScores(context)
    val settings: Settings =
        JackSettings(context)
}

class GameLogicFactory(
    private val resourceManager: ResourceManager
): Factory<GameLogic> {

    private var gameListener: GameListener? = null

    fun gameListener(gameListener: GameListener?): GameLogicFactory {
        this.gameListener = gameListener
        return this
    }

    override fun create(): GameLogic {
        val states = JackGameStates()
        val map = JackGameMap(
            resourceManager,
            states
        )
        val processor =
            JackGameProcessor(
                resourceManager,
                states,
                map,
                gameListener
            )
        return JackGameLogic(
            processor,
            map,
            states
        )
    }
}
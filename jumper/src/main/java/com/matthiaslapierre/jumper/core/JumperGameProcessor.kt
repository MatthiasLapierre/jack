package com.matthiaslapierre.jumper.core

import android.graphics.Canvas
import android.graphics.Paint
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite

internal interface JumperGameProcessor{

    val resourceManager: ResourceManager
    val gameStates: JumperGameStates
    val gameMap: JumperGameMap
    var gameListener: JumperGameListener?

    fun process()

    fun pause()

    fun dispose()

    fun paint(canvas: Canvas, globalPaint: Paint)

    fun startGame()

    fun gameOver()

    fun moveX(xAcceleration: Float)

    fun getGameStatus(): Sprite.Status

    fun getCandiesCollected(): Int

    fun getPowerUps(): Int

    fun setFrameRateAdjustFactor(frameRateAdjustFactor: Float)

}
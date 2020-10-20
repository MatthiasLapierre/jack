package com.matthiaslapierre.jack.core.game.impl.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.FLOOR_HEIGHT
import com.matthiaslapierre.jack.Constants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.impl.sprites.player.PlayerSprite

/**
 * Transparent sprite to locate the floor.
 */
class FloorSprite(
    private val gameStates: GameStates
) : Sprite {

    override var x: Float = UNDEFINED
    override var y: Float = UNDEFINED

    private var highestY: Float = UNDEFINED
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWith = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        if (x == UNDEFINED) {
            width = screenWith
            height = screenWith * FLOOR_HEIGHT
            x = 0f
            y = screenHeight - height
            highestY = y
        }

        isAlive = y <= (screenWith * SPRITE_LIFE_LOWEST_Y)

        if (status == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
            if (y < highestY) {
                y = highestY
            }
        }
    }

    override fun isAlive(): Boolean = true

    override fun isHit(sprite: Sprite): Boolean = sprite is PlayerSprite
            && sprite.getFeetRectF().top >= y

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = RectF(
        x,
        y,
        x + width,
        y + height
    )

    override fun onDispose() {

    }
}
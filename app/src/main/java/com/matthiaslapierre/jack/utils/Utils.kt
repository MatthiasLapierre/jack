package com.matthiaslapierre.jack.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.DimenRes
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.jack.core.game.GameStates
import kotlin.random.Random

object Utils {

    /**
     * Retrieves a dimensional for a particular resource ID for use as a size in raw pixels.
     */
    fun getDimenInPx(context: Context, @DimenRes id: Int): Float =
        context.resources.getDimensionPixelSize(id).toFloat()

    /**
     * Gets a random int.
     */
    fun getRandomInt(minValue: Int, maxValue: Int): Int = Random.nextInt(minValue, maxValue)

    /**
     * Gets a random float.
     */
    fun getRandomFloat(minValue: Float, maxValue: Float): Float =
        Random.nextFloat() * (maxValue - minValue) + minValue

    /**
     * Draws the score in a new bitmap.
     */
    fun generateScoreBitmap(resourceManager: ResourceManager, score: Int): Bitmap {
        val digits = score.toDigits()

        var width = 0
        var height = 0
        val digitBitmaps: List<Bitmap> = digits.reversedArray().map { digit ->
            val bitmap = resourceManager.digits!![digit]!!.bitmap
            width += bitmap.width
            if(bitmap.height > height) {
                height = bitmap.height
            }
            bitmap
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var x = 0f
        digitBitmaps.forEach { digitBitmap ->
            canvas.drawBitmap(digitBitmap, x, (height - digitBitmap.height) / 2f, paint)
            x += digitBitmap.width
        }

        return bitmap
    }

    /**
     * Draws power-ups enabled in a bitmap.
     */
    fun generateBadgesBitmap(resourceManager: ResourceManager, powerUpFlag: Int): Bitmap {
        val firstBadge = resourceManager.badges!![ResourceManager.PowerUpResId.ARMORED]!!
        val badgeWidth = firstBadge.width
        val badgeHeight = firstBadge.height
        val badgeSpace = badgeWidth * .1f

        val height = ((badgeHeight * 4) + (badgeSpace * 3)).toInt()

        val bitmap = Bitmap.createBitmap(badgeWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var y = 0f

        val powerUpFlags = arrayOf(
            GameStates.POWER_UP_ROCKET,
            GameStates.POWER_UP_MAGNET,
            GameStates.POWER_UP_ARMORED,
            GameStates.POWER_UP_COPTER
        )

        for (flag in powerUpFlags) {
            if (powerUpFlag.hasFlag(flag)) {
                canvas.drawBitmap(
                    resourceManager.badges!![getFlagToPowerUpResId(flag)]!!.bitmap,
                    0f,
                    y,
                    paint
                )
                y += badgeHeight + badgeSpace
            }
        }

        return bitmap
    }

    /**
     * Converts a flag to a resource identifier.
     */
    fun getFlagToPowerUpResId(flag: Int): ResourceManager.PowerUpResId =
        when (flag) {
            GameStates.POWER_UP_MAGNET -> ResourceManager.PowerUpResId.MAGNET
            GameStates.POWER_UP_ROCKET -> ResourceManager.PowerUpResId.ROCKET
            GameStates.POWER_UP_COPTER -> ResourceManager.PowerUpResId.COPTER
            GameStates.POWER_UP_ARMORED -> ResourceManager.PowerUpResId.ARMORED
            else -> ResourceManager.PowerUpResId.ARMORED
        }

}

/**
 * Parses number to digits.
 */
fun Int.toDigits(): Array<Int> {
    val digits = mutableListOf<Int>()
    var i = this
    if(i == 0) {
        digits.add(0)
    } else {
        while (i > 0) {
            digits.add(i % 10)
            i /= 10
        }
    }
    return digits.toTypedArray()
}

/**
 * Get the bitmap’s location and size in its own coordinate system.
 */
fun Bitmap.getRect(): Rect = Rect(0, 0, width, height)

// Simple bit flags operation on int values
fun Int.hasFlag(flag: Int) = flag and this == flag
fun Int.withFlag(flag: Int) = this or flag
fun Int.minusFlag(flag: Int) = this and flag.inv()
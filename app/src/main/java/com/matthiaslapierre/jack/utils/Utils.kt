package com.matthiaslapierre.jack.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.DimenRes
import com.matthiaslapierre.jack.core.ResourceManager
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
    fun generateScoreBitmap(score: Int, resourceManager: ResourceManager): Bitmap {
        val digits = score.toDigits()

        var width = 0
        var height = 0
        val digitBitmaps = digits.reversedArray().map { digit ->
            val bitmap = resourceManager.digits!![digit].bitmap
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
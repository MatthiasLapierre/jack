package com.matthiaslapierre.jumper.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.matthiaslapierre.utils.toDigits
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

object JumperUtils  {

    /**
     * Draws the score in a new bitmap.
     */
    fun generateScoreBitmap(score: Int, resourceManager: com.matthiaslapierre.core.ResourceManager): Bitmap {
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

}

fun Int.hasFlag(flag: Int) = flag and this == flag
fun Int.withFlag(flag: Int) = this or flag
fun Int.minusFlag(flag: Int) = this and flag.inv()
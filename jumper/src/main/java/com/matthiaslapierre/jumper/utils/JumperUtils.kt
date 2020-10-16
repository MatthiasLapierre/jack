package com.matthiaslapierre.jumper.utils

import android.graphics.*
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.jumper.core.JumperGameStates
import com.matthiaslapierre.utils.toDigits

object JumperUtils  {

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

    fun generateBadgesBitmap(resourceManager: ResourceManager, powerUpFlag: Int): Bitmap {
        val firstBadge = resourceManager.badges!![ResourceManager.PowerUp.ARMORED]!!
        val badgeWidth = firstBadge.width
        val badgeHeight = firstBadge.height
        val badgeSpace = badgeWidth * .1f

        val height = ((badgeHeight * 4) + (badgeSpace * 3)).toInt()

        val bitmap = Bitmap.createBitmap(badgeWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var y = 0f

        val powerUpFlags = arrayOf(
            JumperGameStates.POWER_UP_ROCKET,
            JumperGameStates.POWER_UP_MAGNET,
            JumperGameStates.POWER_UP_ARMORED,
            JumperGameStates.POWER_UP_COPTER
        )

        for (flag in powerUpFlags) {
            if (powerUpFlag.hasFlag(flag)) {
                canvas.drawBitmap(
                    resourceManager.badges!![getFlagToPowerUp(flag)]!!.bitmap,
                    0f,
                    y,
                    paint
                )
                y += badgeHeight + badgeSpace
            }
        }

        return bitmap
    }

    fun getFlagToPowerUp(flag: Int): ResourceManager.PowerUp =
        when (flag) {
            JumperGameStates.POWER_UP_MAGNET -> ResourceManager.PowerUp.MAGNET
            JumperGameStates.POWER_UP_ROCKET -> ResourceManager.PowerUp.ROCKET
            JumperGameStates.POWER_UP_COPTER -> ResourceManager.PowerUp.COPTER
            JumperGameStates.POWER_UP_ARMORED -> ResourceManager.PowerUp.ARMORED
            else -> ResourceManager.PowerUp.ARMORED
        }

}

fun Int.hasFlag(flag: Int) = flag and this == flag
fun Int.withFlag(flag: Int) = this or flag
fun Int.minusFlag(flag: Int) = this and flag.inv()
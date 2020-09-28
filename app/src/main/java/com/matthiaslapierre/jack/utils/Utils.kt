package com.matthiaslapierre.jack.utils

import android.content.Context
import androidx.annotation.DimenRes
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

}
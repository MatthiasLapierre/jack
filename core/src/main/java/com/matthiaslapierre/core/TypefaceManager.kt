package com.matthiaslapierre.core

import android.graphics.Typeface
import com.matthiaslapierre.framework.resources.Typefaces

/**
 * Caches typefaces.
 */
interface TypefaceManager: Typefaces {

    /**
     * Gets the main typeface.
     */
    fun getFestOfFleshTypeface(): Typeface

}
package com.matthiaslapierre.jack.core.impl

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Typeface
import com.matthiaslapierre.jack.core.TypefaceManager
import java.util.*

class JackTypefaceManager(
    context: Context
): TypefaceManager {

    companion object {
        private const val FONT_FEST_OF_FLESH = "fest_of_flesh"
    }

    private val assets: AssetManager = context.assets
    private val cache: Hashtable<String, Typeface> = Hashtable()

    override fun load() {
        Thread(Runnable {
            get(FONT_FEST_OF_FLESH)
        }).start()
    }

    override fun getFestOfFleshTypeface(): Typeface = get(FONT_FEST_OF_FLESH)!!

    private fun get(name: String): Typeface? {
        synchronized(cache) {
            if (!cache.containsKey(name)) {
                val t = Typeface.createFromAsset(
                    assets,
                    String.format("fonts/%s.TTF", name)
                )
                cache[name] = t
            }
            return cache[name]
        }
    }

}
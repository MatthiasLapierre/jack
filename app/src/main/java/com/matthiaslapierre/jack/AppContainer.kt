package com.matthiaslapierre.jack

import android.content.Context
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.SoundManager
import com.matthiaslapierre.core.TypefaceManager
import com.matthiaslapierre.core.impl.JackResourceManager
import com.matthiaslapierre.core.impl.JackSoundManager
import com.matthiaslapierre.core.impl.JackTypefaceManager

/**
 * To solve the issue of reusing objects, you can create your own dependencies container class
 * that you use to get dependencies. All instances provided by this container can be public.
 * Because these dependencies are used across the whole application, they need to be placed in
 * a common place all activities can use: the application class.
 * @see https://developer.android.com/training/dependency-injection/manual
 */
class AppContainer(
    context: Context
) {

    val soundManager: SoundManager = JackSoundManager(context)
    val resourceManager: ResourceManager = JackResourceManager(context)
    val typefaceManager: TypefaceManager = JackTypefaceManager(context)

}
package com.matthiaslapierre.jack

import android.app.Application

/**
 * Jumper Jack game.
 */
class JackApp: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Load resources once to optimize performances.
        appContainer = AppContainer(applicationContext)
    }

}
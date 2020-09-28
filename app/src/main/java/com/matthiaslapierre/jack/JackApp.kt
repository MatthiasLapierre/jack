package com.matthiaslapierre.jack

import android.app.Application

class JackApp: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Load resources once to optimize performances.
        appContainer = AppContainer(applicationContext)
    }

}
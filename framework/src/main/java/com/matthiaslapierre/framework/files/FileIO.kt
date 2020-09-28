package com.matthiaslapierre.framework.files

import android.content.SharedPreferences
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

interface FileIO {
    @Throws(IOException::class)
    fun readFile(file: String): InputStream

    @Throws(IOException::class)
    fun writeFile(file: String): OutputStream

    @Throws(IOException::class)
    fun readAsset(file: String): InputStream

    val preferences: SharedPreferences
}

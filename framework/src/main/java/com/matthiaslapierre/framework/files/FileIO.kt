package com.matthiaslapierre.framework.files

import android.content.SharedPreferences
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Handles files.
 */
interface FileIO {

    /**
     * Handles preferences.
     */
    val preferences: SharedPreferences

    /**
     * Reads the file content.
     */
    @Throws(IOException::class)
    fun readFile(file: String): InputStream

    /**
     * Writes a file.
     */
    @Throws(IOException::class)
    fun writeFile(file: String): OutputStream

    /**
     * Reads an asset file.
     */
    @Throws(IOException::class)
    fun readAsset(file: String): InputStream

}

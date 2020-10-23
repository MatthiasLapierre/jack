package com.matthiaslapierre.framework.files.android

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import com.matthiaslapierre.framework.files.FileIO
import java.io.*

class AndroidFileIO(
    private val mContext: Context
) : FileIO {

    private val assets: AssetManager = mContext.assets

    override val preferences: SharedPreferences
        get() = mContext.getSharedPreferences(
            mContext.packageName+ "_preferences",
            Context.MODE_PRIVATE
        )

    @Throws(IOException::class)
    override fun readAsset(fileName: String): InputStream {
        return assets.open(fileName)
    }

    @Throws(IOException::class)
    override fun readFile(fileName: String): InputStream {
        return FileInputStream(File(mContext.filesDir, fileName))
    }

    @Throws(IOException::class)
    override fun writeFile(fileName: String): OutputStream {
        return FileOutputStream(File(mContext.filesDir, fileName))
    }

}
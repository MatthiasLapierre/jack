package com.matthiaslapierre.jack.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.resources.ResourceManager
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*
import kotlin.random.Random


object Utils {

    /**
     * Gets a random int.
     */
    fun getRandomInt(minValue: Int, maxValue: Int): Int = Random.nextInt(minValue, maxValue)

    /**
     * Gets a random float.
     */
    fun getRandomFloat(minValue: Float, maxValue: Float): Float =
        Random.nextFloat() * (maxValue - minValue) + minValue

    /**
     * Draws the score in a new bitmap.
     */
    fun generateScoreBitmap(resourceManager: ResourceManager, score: Int): Bitmap {
        val digits = score.toDigits()

        var width = 0
        var height = 0
        val digitBitmaps: List<Bitmap> = digits.reversedArray().map { digit ->
            val bitmap = resourceManager.digits!![digit].bitmap
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

    /**
     * Draws power-ups enabled in a bitmap.
     */
    fun generateBadgesBitmap(resourceManager: ResourceManager, powerUpFlag: Int): Bitmap {
        val firstBadge = resourceManager.badges!![ResourceManager.PowerUpResId.ARMORED]!!
        val badgeWidth = firstBadge.width
        val badgeHeight = firstBadge.height
        val badgeSpace = badgeWidth * .1f

        val height = ((badgeHeight * 4) + (badgeSpace * 3)).toInt()

        val bitmap = Bitmap.createBitmap(badgeWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var y = 0f

        val powerUpFlags = arrayOf(
            GameStates.POWER_UP_ROCKET,
            GameStates.POWER_UP_MAGNET,
            GameStates.POWER_UP_ARMORED,
            GameStates.POWER_UP_COPTER
        )

        for (flag in powerUpFlags) {
            if (powerUpFlag.hasFlag(flag)) {
                canvas.drawBitmap(
                    resourceManager.badges!![getFlagToPowerUpResId(flag)]!!.bitmap,
                    0f,
                    y,
                    paint
                )
                y += badgeHeight + badgeSpace
            }
        }

        return bitmap
    }

    /**
     * Converts a flag to a resource identifier.
     */
    fun getFlagToPowerUpResId(flag: Int): ResourceManager.PowerUpResId =
        when (flag) {
            GameStates.POWER_UP_MAGNET -> ResourceManager.PowerUpResId.MAGNET
            GameStates.POWER_UP_ROCKET -> ResourceManager.PowerUpResId.ROCKET
            GameStates.POWER_UP_COPTER -> ResourceManager.PowerUpResId.COPTER
            GameStates.POWER_UP_ARMORED -> ResourceManager.PowerUpResId.ARMORED
            else -> ResourceManager.PowerUpResId.ARMORED
        }

    /**
     * Shares on Facebook. Using Facebook app if installed or web link otherwise.
     *
     * @param activity activity which launches the intent
     * @param text     not used/allowed on Facebook
     * @param url      url to share
     */
    fun shareFacebook(activity: Activity, text: String, url: String) {
        var facebookAppFound = false
        var shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url))

        val pm = activity.packageManager
        val activityList =
            pm.queryIntentActivities(shareIntent, 0)
        for (app in activityList) {
            if (app.activityInfo.packageName.contains("com.facebook.katana")) {
                val activityInfo = app.activityInfo
                val name =
                    ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                shareIntent.component = name
                facebookAppFound = true
                break
            }
        }
        if (!facebookAppFound) {
            val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=$url"
            shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
        }
        activity.startActivity(shareIntent)
    }

    /**
     * Shares on Twitter. Using Twitter app if installed or web link otherwise.
     *
     * @param activity activity which launches the intent
     * @param text     text to share
     * @param url      url to share
     * @param via      twitter username without '@' who shares
     * @param hashTags hashTags for tweet without '#' and separated by ','
     */
    fun shareTwitter(activity: Activity, text: String?, url: String?, via: String?, hashTags: String?) {
        val tweetUrl =
            StringBuilder("https://twitter.com/intent/tweet?text=")
        tweetUrl.append(if (TextUtils.isEmpty(text)) urlEncode(" ") else urlEncode(text!!))
        if (!TextUtils.isEmpty(url)) {
            tweetUrl.append("&url=")
            tweetUrl.append(urlEncode(url!!))
        }
        if (!TextUtils.isEmpty(via)) {
            tweetUrl.append("&via=")
            tweetUrl.append(urlEncode(via!!))
        }
        if (!TextUtils.isEmpty(hashTags)) {
            tweetUrl.append("&hastags=")
            tweetUrl.append(urlEncode(hashTags!!))
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl.toString()))
        val matches =
            activity.packageManager.queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.toLowerCase(Locale.getDefault())
                    .startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName)
            }
        }
        activity.startActivity(intent)
    }

    /**
     * Opens an url on a web browser.
     */
    fun openUrl(activity: Activity, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }

    /**
     * Convert to UTF-8 text to put it on url format
     *
     * @param s text to be converted
     * @return text on UTF-8 format
     */
    private fun urlEncode(s: String): String? {
        return try {
            URLEncoder.encode(s, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.wtf("wtf", "UTF-8 should always be supported", e)
            throw RuntimeException("URLEncoder.encode() failed for $s")
        }
    }

}

/**
 * Parses number to digits.
 */
fun Int.toDigits(): Array<Int> {
    val digits = mutableListOf<Int>()
    var i = this
    if(i == 0) {
        digits.add(0)
    } else {
        while (i > 0) {
            digits.add(i % 10)
            i /= 10
        }
    }
    return digits.toTypedArray()
}

/**
 * Get the bitmap’s location and size in its own coordinate system.
 */
fun Bitmap.getRect(): Rect = Rect(0, 0, width, height)

// Simple bit flags operation on int values
fun Int.hasFlag(flag: Int) = flag and this == flag
fun Int.withFlag(flag: Int) = this or flag
fun Int.minusFlag(flag: Int) = this and flag.inv()
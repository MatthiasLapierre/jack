package com.matthiaslapierre.framework.input

import android.view.View

interface TouchHandler : View.OnTouchListener {
    val touchEvents: MutableList<Input.TouchEvent>
    fun isTouchDown(pointer: Int): Boolean
    fun getTouchX(pointer: Int): Int
    fun getTouchY(pointer: Int): Int
}
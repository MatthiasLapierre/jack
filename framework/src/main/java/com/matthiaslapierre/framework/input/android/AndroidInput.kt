package com.matthiaslapierre.framework.input.android

import android.view.View
import com.matthiaslapierre.framework.input.Input
import com.matthiaslapierre.framework.input.TouchHandler

class AndroidInput(
    view: View
): Input {

    private val mTouchHandler: TouchHandler = MultiTouchHandler(view)

    override val touchEvents: List<Input.TouchEvent>
        get() = mTouchHandler.touchEvents

    override fun isTouchDown(pointer: Int): Boolean {
        return mTouchHandler.isTouchDown(pointer)
    }

    override fun getTouchX(pointer: Int): Int {
        return mTouchHandler.getTouchX(pointer)
    }

    override fun getTouchY(pointer: Int): Int {
        return mTouchHandler.getTouchY(pointer)
    }

}
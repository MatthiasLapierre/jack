package com.matthiaslapierre.framework.input

interface Input {
    class TouchEvent(
        var type: Type = Type.TOUCH_DOWN,
        var x: Int = 0,
        var y: Int = 0,
        var pointer: Int = 0
    ) {
        enum class Type {
            TOUCH_DOWN,
            TOUCH_UP,
            TOUCH_DRAGGED,
            TOUCH_HOLD
        }
    }

    val touchEvents: List<TouchEvent>
    fun isTouchDown(pointer: Int): Boolean
    fun getTouchX(pointer: Int): Int
    fun getTouchY(pointer: Int): Int
}

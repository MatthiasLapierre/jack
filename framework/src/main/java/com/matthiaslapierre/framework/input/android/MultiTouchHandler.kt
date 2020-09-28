package com.matthiaslapierre.framework.input.android

import android.view.MotionEvent
import android.view.View
import com.matthiaslapierre.framework.input.Input
import com.matthiaslapierre.framework.input.TouchHandler
import com.matthiaslapierre.framework.utils.Pool

class MultiTouchHandler(
    view: View
) : TouchHandler {
    private var mIsTouched = BooleanArray(MAX_TOUCH_POINTS)
    private var mTouchX = IntArray(MAX_TOUCH_POINTS)
    private var mTouchY = IntArray(MAX_TOUCH_POINTS)
    private var mId = IntArray(MAX_TOUCH_POINTS)
    private var mTouchEventPool: Pool<Input.TouchEvent>
    private var mTouchEventsBuffer: MutableList<Input.TouchEvent> = ArrayList()

    companion object {
        private const val MAX_TOUCH_POINTS = 10
    }

    init {
        val factory: Pool.PoolObjectFactory<Input.TouchEvent> = object : Pool.PoolObjectFactory<Input.TouchEvent> {
            override fun createObject(): Input.TouchEvent {
                return Input.TouchEvent()
            }
        }
        mTouchEventPool = Pool(factory, 100)
        view.setOnTouchListener(this)
    }

    override val touchEvents: MutableList<Input.TouchEvent> = ArrayList()
        get() {
            synchronized(this) {
                val len = field.size
                for (i in 0 until len) mTouchEventPool.free(touchEvents[i])
                field.clear()
                field.addAll(mTouchEventsBuffer)
                mTouchEventsBuffer.clear()
                return field
            }
        }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        synchronized(this) {
            val action = event.action and MotionEvent.ACTION_MASK
            val pointerIndex =
                event.action and MotionEvent.ACTION_POINTER_ID_MASK shr MotionEvent.ACTION_POINTER_ID_SHIFT
            val pointerCount = event.pointerCount
            var touchEvent: Input.TouchEvent
            for (i in 0 until MAX_TOUCH_POINTS) {
                if (i >= pointerCount) {
                    mIsTouched[i] = false
                    mId[i] = -1
                    continue
                }
                val pointerId = event.getPointerId(i)
                if (event.action != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                    // if it's an up/down/cancel/out event, mask the id to see if we should process it for this touch
                    // point
                    continue
                }
                when (action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                        touchEvent = mTouchEventPool.newObject()
                        touchEvent.type = Input.TouchEvent.Type.TOUCH_DOWN
                        touchEvent.pointer = pointerId
                        run {
                            mTouchX[i] = event.getX(i).toInt()
                            touchEvent.x = mTouchX[i]
                        }
                        run {
                            mTouchY[i] = event.getY(i).toInt()
                            touchEvent.y = mTouchY[i]
                        }
                        mIsTouched[i] = true
                        mId[i] = pointerId
                        mTouchEventsBuffer.add(touchEvent)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                        touchEvent = mTouchEventPool.newObject()
                        touchEvent.type = Input.TouchEvent.Type.TOUCH_UP
                        touchEvent.pointer = pointerId
                        run {
                            mTouchX[i] = event.getX(i).toInt()
                            touchEvent.x = mTouchX[i]
                        }
                        run {
                            mTouchY[i] = event.getY(i).toInt()
                            touchEvent.y = mTouchY[i]
                        }
                        mIsTouched[i] = false
                        mId[i] = -1
                        mTouchEventsBuffer.add(touchEvent)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        touchEvent = mTouchEventPool.newObject()
                        touchEvent.type = Input.TouchEvent.Type.TOUCH_DRAGGED
                        touchEvent.pointer = pointerId
                        run {
                            mTouchX[i] = event.getX(i).toInt()
                            touchEvent.x = mTouchX[i]
                        }
                        run {
                            mTouchY[i] = event.getY(i).toInt()
                            touchEvent.y = mTouchY[i]
                        }
                        mIsTouched[i] = true
                        mId[i] = pointerId
                        mTouchEventsBuffer.add(touchEvent)
                    }
                }
            }
            return true
        }
    }

    override fun isTouchDown(pointer: Int): Boolean {
        synchronized(this) {
            val index = getIndex(pointer)
            return if (index < 0 || index >= MAX_TOUCH_POINTS) false else mIsTouched[index]
        }
    }

    override fun getTouchX(pointer: Int): Int {
        synchronized(this) {
            val index = getIndex(pointer)
            return if (index < 0 || index >= MAX_TOUCH_POINTS) 0 else mTouchX[index]
        }
    }

    override fun getTouchY(pointer: Int): Int {
        synchronized(this) {
            val index = getIndex(pointer)
            return if (index < 0 || index >= MAX_TOUCH_POINTS) 0 else mTouchY[index]
        }
    }

    // returns the index for a given pointerId or -1 if no index.
    private fun getIndex(pointerId: Int): Int {
        for (i in 0 until MAX_TOUCH_POINTS) {
            if (mId[i] == pointerId) {
                return i
            }
        }
        return -1
    }

}

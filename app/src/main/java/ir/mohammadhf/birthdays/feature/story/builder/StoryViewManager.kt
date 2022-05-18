package ir.mohammadhf.birthdays.feature.story.builder

import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import ir.mohammadhf.birthdays.feature.story.behaviors.IRotateListener
import ir.mohammadhf.birthdays.feature.story.behaviors.ITouchListener
import ir.mohammadhf.birthdays.feature.story.behaviors.IZoomListener
import ir.mohammadhf.birthdays.feature.story.layouts.Layout
import ir.mohammadhf.birthdays.feature.story.layouts.TextLayout
import javax.inject.Inject

class StoryViewManager @Inject constructor() {
    val mLayouts = arrayListOf<Layout>()
    private var mTouchedLay: Layout? = null

    private var mFirstPointerID = INVALID_POINTER_ID
    private var mSecondPointerID = INVALID_POINTER_ID

    var onLayoutTouchedListener: ((layout: Layout?) -> Unit)? = null

    fun onDraw(canvas: Canvas) {
        Log.i("OnDrawMethod", "view manager onDraw is called")
        mLayouts.forEach { it.onDraw(canvas) }
    }

    fun onTouchDown(event: MotionEvent) {
        mFirstPointerID = event.getPointerId(0)
        val (x, y) = getXY(event, mFirstPointerID)
        mTouchedLay?.isSelected = false
        mTouchedLay = selectTouchedLayout(x, y)
        mTouchedLay?.apply {
            isSelected = true
            if (this is ITouchListener) onTouchDown(x, y)
        }

        onLayoutTouchedListener?.invoke(mTouchedLay)
    }

    fun onPointerTouchDown(event: MotionEvent) {
        if (mSecondPointerID == INVALID_POINTER_ID) {
            mSecondPointerID = event.getPointerId(event.actionIndex)
            val (x, y) = getXY(event, mFirstPointerID)
            val (x2, y2) = getXY(event, mSecondPointerID)
            mTouchedLay?.apply {
                if (this is ITouchListener) onPointerTouchDown(x2, y2)
                if (this is IZoomListener) startZoom(x, y, x2, y2)
                if (this is IRotateListener) startRotate(x, y, x2, y2)
            }
        }
    }

    fun onTouchMove(event: MotionEvent) {
        Log.i(
            "GiftViewManager",
            "onTouchMove: firstPointerID = $mFirstPointerID and secondPointerID = $mSecondPointerID"
        )
        val (x, y) = getXY(event, mFirstPointerID)
        mSecondPointerID
            .takeIf { it != INVALID_POINTER_ID }
            ?.apply {
                val (x2, y2) = getXY(event, mSecondPointerID)
                mTouchedLay?.apply {
                    if (this is ITouchListener) onTouchMove(x, y, x2, y2)
                    if (this is IZoomListener) onZoom(x, y, x2, y2)
                    if (this is IRotateListener) onRotate(x, y, x2, y2)
                }
            } ?: run {
            mTouchedLay?.apply {
                if (this is ITouchListener) onTouchMove(x, y, null, null)
            }
        }
    }

    fun onPointerTouchUp(event: MotionEvent) {
        event.actionIndex.let { pointerIndex ->
            event.getPointerId(pointerIndex)
                .takeIf { it == mFirstPointerID }
                ?.apply {
                    val (x, y) = getXY(event, mFirstPointerID)
                    mTouchedLay?.apply { if (this is ITouchListener) onPointerTouchUp(x, y) }
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mFirstPointerID = event.getPointerId(newPointerIndex)
                    mSecondPointerID = INVALID_POINTER_ID
                } ?: run {
                val (x, y) = getXY(event, mSecondPointerID)
                mTouchedLay?.apply { if (this is ITouchListener) onPointerTouchUp(x, y) }
                mSecondPointerID = INVALID_POINTER_ID
            }
        }
//        event.actionIndex.let { pointerIndex ->
//            val eventId = event.getPointerId(pointerIndex)
//            if (eventId == mFirstPointerID) {
//                val newPointerIndex = if (pointerIndex == 0) 1 else 0
//                mFirstPointerID = event.getPointerId(newPointerIndex)
//
//                val (x, y) = getXY(event, mFirstPointerID)
//                mTouchedLay?.apply { if (this is ITouchListener) onPointerTouchUp(x, y) }
//                mSecondPointerID = INVALID_POINTER_ID
//            } else if (eventId == mSecondPointerID) {
//                mSecondPointerID = INVALID_POINTER_ID
//            }
//        }
    }

    fun onTouchUp(event: MotionEvent) {
        val (x, y) = getXY(event, mFirstPointerID)
        mTouchedLay?.apply { if (this is ITouchListener) onTouchUp(x, y) }
        mFirstPointerID = INVALID_POINTER_ID
    }

    private fun getXY(event: MotionEvent, pointerID: Int): Pair<Float, Float> {
        return event.findPointerIndex(pointerID).let { pointerIndex ->
            event.getX(pointerIndex) to event.getY(pointerIndex)
        }
    }

    private fun selectTouchedLayout(x: Float, y: Float): Layout? {
        for (i in mLayouts.indices.reversed()) {
            mLayouts[i].let { if (it.isTouchInside(x, y)) return it }
        }
        return null
    }

    fun changeSelectedLayoutDrawType() {
        mTouchedLay?.apply { if (this is TextLayout) nextDrawType() }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}
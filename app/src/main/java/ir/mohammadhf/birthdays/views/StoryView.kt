package ir.mohammadhf.birthdays.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.feature.story.builder.StoryViewManager
import ir.mohammadhf.birthdays.feature.story.layouts.FrameLayout
import ir.mohammadhf.birthdays.feature.story.layouts.Layout
import ir.mohammadhf.birthdays.feature.story.layouts.PicLayout
import javax.inject.Inject

@AndroidEntryPoint
class StoryView @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet,
    defStyleAttr: Int = -1
) : View(context, attributeSet, defStyleAttr) {

    @Inject
    lateinit var storyViewManager: StoryViewManager

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        storyViewManager.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        storyViewManager.mLayouts.apply {
            add(PicLayout(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)))
            add(FrameLayout(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                storyViewManager.onTouchDown(event)
                invalidate()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                storyViewManager.onPointerTouchDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                storyViewManager.onTouchMove(event)
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                storyViewManager.onPointerTouchUp(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                storyViewManager.onTouchUp(event)
            }
        }

        return true
    }

    fun changeFrame(frameBitmap: Bitmap) {
        val newFrameBitmap = Bitmap.createScaledBitmap(frameBitmap, width, height, true)
        storyViewManager.mLayouts[1] = FrameLayout(newFrameBitmap)
            .apply { replaceBitmapCenter(width / 2f, height / 2f) }
        invalidate()
    }

    fun changePic(picBitmap: Bitmap) {
        storyViewManager.mLayouts[0] = PicLayout(picBitmap)
            .apply { replaceBitmapCenter(width / 2f, height / 2f) }
        invalidate()
    }

    fun addLayout(layout: Layout) {
        storyViewManager.mLayouts.add(
            layout.apply { replaceBitmapCenter(width / 2f, height / 2f) }
        )
        invalidate()
    }

    fun getResultBitmap(): Bitmap {
        val resBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val resCanvas = Canvas(resBitmap)
        storyViewManager.mLayouts.forEach { it.onDraw(resCanvas) }
        return resBitmap
    }

    fun setOnLayoutTouchedListener(listener: ((layout: Layout?) -> Unit)?) {
        storyViewManager.onLayoutTouchedListener = listener
    }
}
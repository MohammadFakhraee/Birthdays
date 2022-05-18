package ir.mohammadhf.birthdays.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import ir.mohammadhf.birthdays.feature.story.builder.StoryViewManager
import ir.mohammadhf.birthdays.feature.story.layouts.*
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
        storyViewManager.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        storyViewManager.mLayouts.let {
            if (it.isEmpty()) {
                it.add(PicLayout(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)))
                it.add(FrameLayout(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)))
            }
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
//        val xScale = width.toFloat() / frameBitmap.width
//        val yScale = height.toFloat() / frameBitmap.height
//        val scale = min(xScale, yScale)
//        val dstWidth = (frameBitmap.width * scale).toInt()
//        val dstHeight = (frameBitmap.height * scale).toInt()
//        val newFrameBitmap = Bitmap.createScaledBitmap(frameBitmap, dstWidth, dstHeight, true)
//        storyViewManager.mLayouts[1] = FrameLayout(newFrameBitmap)
//            .apply { replaceBitmapCenter(dstWidth / 2f, dstHeight / 2f) }
//        layoutParams = layoutParams.apply {
//            width = dstWidth
//            height = dstHeight
//        }
        Log.i("BitmapConfigS", "changeFrame: loaded bitmap config is ${frameBitmap.config}")
        val newFrameBitmap = Bitmap.createScaledBitmap(frameBitmap, width, height, true)
        Log.i("BitmapConfigS", "changeFrame: scaled bitmap config is ${newFrameBitmap.config}")
        storyViewManager.mLayouts[1] = FrameLayout(newFrameBitmap)
            .apply { replaceBitmapCenter(width / 2f, height / 2f) }
        invalidate()
    }

    fun changePic(picBitmap: Bitmap) {
        var bitmap = picBitmap
        // checking if the config of incoming bitmap is HARDWARE or not. In case it is HARDWARE,
        // we should take a copy. Because the HARDWARE bitmap won't get rendered in our result canvas
        // which is a software canvas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && bitmap.config == Bitmap.Config.HARDWARE)
            bitmap = picBitmap.copy(Bitmap.Config.ARGB_8888, false)

        storyViewManager.mLayouts[0] = PicLayout(bitmap)
            .apply { replaceBitmapCenter(width / 2f, height / 2f) }
        invalidate()
    }

    fun addLayout(layout: Layout) {
        storyViewManager.mLayouts.add(
            layout.apply { replaceBitmapCenter(width / 2f, height / 2f) }
        )
        invalidate()
    }

    fun removeLayout(layout: Layout?) {
        layout?.let {
            when (it) {
                is TextLayout -> {
                    storyViewManager.mLayouts.remove(it)
                    invalidate()
                }
                is StickerLayout -> {
                    storyViewManager.mLayouts.remove(it)
                    invalidate()
                }
                is PicLayout -> {
                    storyViewManager.mLayouts[0] =
                        PicLayout(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
                    invalidate()
                }
            }
        }
    }

    fun getResultBitmap(): Bitmap {
        val resBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val resCanvas = Canvas(resBitmap)
        storyViewManager.onDraw(resCanvas)
        return resBitmap
    }

    fun nextDrawTypeOfSelectedLayout() {
        storyViewManager.changeSelectedLayoutDrawType()
        invalidate()
    }

    fun setOnLayoutTouchedListener(listener: ((layout: Layout?) -> Unit)?) {
        storyViewManager.onLayoutTouchedListener = listener
    }
}
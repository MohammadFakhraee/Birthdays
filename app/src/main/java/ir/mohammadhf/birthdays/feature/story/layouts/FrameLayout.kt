package ir.mohammadhf.birthdays.feature.story.layouts

import android.graphics.Bitmap
import android.graphics.Canvas

class FrameLayout(bitmap: Bitmap) : Layout(bitmap) {
    override fun isTouchInside(x: Float, y: Float): Boolean {
        val pixel =
            bitmap.getPixel(x.toInt(), y.toInt())
        return pixel != 0
//        return x >= cornerPoints[0].x
//                && x <= cornerPoints[1].x
//                && y >= cornerPoints[0].y
//                && y <= cornerPoints[2].y
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }
}

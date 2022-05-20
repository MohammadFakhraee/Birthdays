package ir.mohammadhf.birthdays.feature.story.layouts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log

class FrameLayout(bitmap: Bitmap) : Layout(bitmap) {

    override fun isTouchInside(x: Float, y: Float): Boolean {
        val replaceX = (x - cornerPoints[0].x).toInt()
        val replaceY = (y - cornerPoints[0].y).toInt()
        if (replaceX > bitmap.width || replaceY > bitmap.height) return false
        val pixel =
            bitmap.getPixel(replaceX, replaceY)
        return pixel != 0
    }

    override fun onDraw(canvas: Canvas) {
        Log.i("OnDrawMethod", "Frame layout onDraw is called")
        canvas.drawBitmap(bitmap, cornerPoints[0].x, cornerPoints[0].y, null)
    }
}

package ir.mohammadhf.birthdays.feature.story.layouts

import android.graphics.Bitmap
import android.graphics.PointF
import ir.mohammadhf.birthdays.feature.story.behaviors.IDrawListener

abstract class Layout(var bitmap: Bitmap) : IDrawListener {
    val cornerPoints: List<PointF> = arrayListOf(PointF(), PointF(), PointF(), PointF())
    val centerPoint = PointF()

    var isSelected: Boolean = false

    fun replaceBitmapCenter(centerX: Float, centerY: Float) {
        val halfWidth = bitmap.width / 2
        val halfHeight = bitmap.height / 2
        centerPoint.set(centerX, centerY)
        cornerPoints[0].set(centerX - halfWidth, centerY - halfHeight)
        cornerPoints[1].set(centerX + halfWidth, centerY - halfHeight)
        cornerPoints[2].set(centerX + halfWidth, centerY + halfHeight)
        cornerPoints[3].set(centerX - halfWidth, centerY + halfHeight)
    }

    abstract fun isTouchInside(x: Float, y: Float): Boolean
}

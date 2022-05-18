package ir.mohammadhf.birthdays.feature.story.layouts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import ir.mohammadhf.birthdays.feature.story.behaviors.IRotateListener
import ir.mohammadhf.birthdays.feature.story.behaviors.ITouchListener
import ir.mohammadhf.birthdays.feature.story.behaviors.IZoomListener
import kotlin.math.*

open class PicLayout(bitmap: Bitmap) : Layout(bitmap), ITouchListener, IZoomListener,
    IRotateListener {
    private val lastTouchedP = PointF()

    override val maxZoom: Float = 4.0F
    override val minZoom: Float = 0.5F

    private var scaleFactor = 1f
    private var lastLineLength = 0f

    private val matrix = Matrix()
    private var currentDeg = 0F
    private var lastDeg = 0F

    override fun onDraw(canvas: Canvas) {
        matrix.apply {
            reset()
            postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
            postRotate(currentDeg)
            postScale(scaleFactor, scaleFactor)
            postTranslate(centerPoint.x, centerPoint.y)
        }
        canvas.drawBitmap(bitmap, matrix, null)
        Log.i("OnDrawMethod", "Pic layout onDraw is called")
    }

    override fun onTouchDown(x: Float, y: Float) {
        lastTouchedP.set(x, y)
    }

    override fun onPointerTouchDown(x: Float, y: Float) {
        lastTouchedP.set((x + lastTouchedP.x) / 2, (y + lastTouchedP.y) / 2)
    }

    override fun onTouchMove(firstX: Float, firstY: Float, secondX: Float?, secondY: Float?) {
        secondX?.let {
            val centerX = (firstX + secondX) / 2
            val centerY = (firstY + secondY!!) / 2
            relocateCenterAndCorners(centerX - lastTouchedP.x, centerY - lastTouchedP.y)
            lastTouchedP.set(centerX, centerY)
        } ?: let {
            relocateCenterAndCorners(firstX - lastTouchedP.x, firstY - lastTouchedP.y)
            lastTouchedP.set(firstX, firstY)
        }
    }

    override fun onPointerTouchUp(x: Float, y: Float) {
        lastTouchedP.set(2 * lastTouchedP.x - x, 2 * lastTouchedP.y - y)
    }

    override fun onTouchUp(x: Float, y: Float) {
        lastTouchedP.set(0f, 0f)
    }

    override fun startZoom(firstX: Float, firstY: Float, secondX: Float, secondY: Float) {
        lastLineLength = getLineLength(firstX, firstY, secondX, secondY)
    }

    override fun onZoom(firstX: Float, firstY: Float, secondX: Float, secondY: Float) {
        val prevScale = scaleFactor

        val newLineLength = getLineLength(firstX, firstY, secondX, secondY)
        val zoomScale = newLineLength / lastLineLength
        scaleFactor *= zoomScale
        scaleFactor = minZoom.coerceAtLeast(maxZoom.coerceAtMost(scaleFactor))
        lastLineLength = newLineLength

        scaleCornerPoints(scaleFactor / prevScale)
    }

    private fun getLineLength(x1: Float, y1: Float, x2: Float, y2: Float): Float =
        sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))

    private fun scaleCornerPoints(zoomScale: Float) {
        cornerPoints.forEach {
            val xScale = zoomScale * (it.x - centerPoint.x)
            val yScale = zoomScale * (it.y - centerPoint.y)
            it.set(centerPoint.x + xScale, centerPoint.y + yScale)
        }
    }

    override fun startRotate(firstX: Float, firstY: Float, secondX: Float, secondY: Float) {
        lastDeg = getLineDegree(firstX, firstY, secondX, secondY)
    }

    override fun onRotate(firstX: Float, firstY: Float, secondX: Float, secondY: Float) {
        val newDeg = getLineDegree(firstX, firstY, secondX, secondY)
        val difDeg = newDeg - lastDeg
        currentDeg += difDeg
        lastDeg = newDeg
        rotateCornerPoints(difDeg)
    }

    private fun rotateCornerPoints(difDeg: Float) {
        val difRad = (difDeg * PI / 180).toFloat()
        cornerPoints.forEach { pointF ->
            pointF.apply {
                set(x - centerPoint.x, y - centerPoint.y)
                set(x * cos(difRad) - y * sin(difRad), x * sin(difRad) + y * cos(difRad))
                set(x + centerPoint.x, y + centerPoint.y)
            }
        }
    }

    /**
     * calculates angle of the line between two points (named 1 and 2) and transforms it to degree
     * @param x1 of point 1
     * @param y1 of point 1
     * @param x2 of point 2
     * @param y2 of point 2
     *
     * @return line angle in degree
     */
    private fun getLineDegree(x1: Float, y1: Float, x2: Float, y2: Float): Float =
        (atan2((y2 - y1), (x2 - x1)) * 180 / PI).toFloat()

    /**
     * checks if the touched point is inside layout edges or not.
     * @param x of user touched point
     * @param y of user touched point
     *
     * @return true if the point is on the line or on the right side of line
     * between each two corners.
     *         false if the point is on the left side of at least one line.
     */
    override fun isTouchInside(x: Float, y: Float): Boolean {
        for (index in cornerPoints.indices) {
            val point1 = if (index == 0) cornerPoints.last() else cornerPoints[index - 1]
            val point2 = if (index == 0) cornerPoints.first() else cornerPoints[index]
            if (!rightOfLine(x, y, point1.x, point1.y, point2.x, point2.y)) return false
        }
        return true
    }

    /**
     * Checks whether the given point is on the right side of a line drawn between (x1, y1) and
     * (x2, y2) or not.
     * @param x of user touched point
     * @param y of user touched point
     * @param x1 of first point of line
     * @param y1 of first point of line
     * @param x2 of second point of line
     * @param y2 of second point of line
     *
     * @return true if the given point is on the right side of the line or on the line.
     *         false if the given point is on the left side of the line.
     */
    private fun rightOfLine(
        x: Float,
        y: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Boolean =
        (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1) >= 0

    private fun relocateCenterAndCorners(difX: Float, difY: Float) {
        centerPoint.set(centerPoint.x + difX, centerPoint.y + difY)
        cornerPoints.forEach { it.set(it.x + difX, it.y + difY) }
    }
}

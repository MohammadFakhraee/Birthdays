package ir.mohammadhf.birthdays.feature.story.layouts

import android.graphics.*

open class StickerLayout(bitmap: Bitmap) : PicLayout(bitmap) {
    private val path = Path()
    private val paint = Paint()

    init {
        paint.apply {
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = DEF_STROKE_SIZE
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isSelected) {
            makeLines()
            canvas.drawPath(path, paint)
        }
    }

    private fun makeLines() {
        path.reset()
        path.moveTo(cornerPoints[0].x, cornerPoints[0].y)
        path.lineTo(cornerPoints[1].x, cornerPoints[1].y)
        path.lineTo(cornerPoints[2].x, cornerPoints[2].y)
        path.lineTo(cornerPoints[3].x, cornerPoints[3].y)
        path.lineTo(cornerPoints[0].x, cornerPoints[0].y)
    }

    companion object {
        const val DEF_STROKE_SIZE = 3F
    }
}
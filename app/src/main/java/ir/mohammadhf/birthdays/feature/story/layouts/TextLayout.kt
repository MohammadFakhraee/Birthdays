package ir.mohammadhf.birthdays.feature.story.layouts

import android.graphics.*
import androidx.core.graphics.toRectF

class TextLayout(
    text: String,
    color: Int,
    size: Float,
    font: Typeface,
    bitmap: Bitmap
) : PicLayout(bitmap) {

    private val textOffset: Int = 30
    private val roundCorner: Float = 15F

    var font = font
        set(value) {
            field = value
            _paint?.typeface = value
            changeBitmap()
        }

    var size = size
        set(value) {
            field = value
            _paint?.textSize = value
            changeBitmap()
        }

    var color = color
        set(value) {
            field = value
            _paint?.color = value
            changeBitmap()
        }

    var text = text
        set(value) {
            field = value
            changeBitmap()
        }

    private var _paint: Paint? = null

    // Used to draw text on bitmap and change the default text layout
    private val preDrawCanvas = Canvas(bitmap)

    // An Enum value to use for drawing style type. It has 3 styles explained in TextLayout.DrawType
    var drawType: DrawType = DrawType.EMPTY_OUTSIDE
        private set

    // used to determine text bounds and for drawing rounded background on canvas
    private val bounds = Rect()

    init {
        _paint = Paint(Paint.ANTI_ALIAS_FLAG)
        _paint?.let {
            it.textAlign = Paint.Align.CENTER
            it.isDither = true
            it.color = color
            it.textSize = size
            it.typeface = font
            it.style = Paint.Style.FILL
        }
        changeBitmap()
    }

    fun nextDrawType() {
        DrawType.values().let { arrayOfValues ->
            drawType = arrayOfValues.indexOf(drawType).let { index ->
                arrayOfValues[if (index + 1 < arrayOfValues.size) index + 1 else 0]
            }
        }
        changeBitmap()
    }

    private fun changeBitmap() {
        _paint?.let {
            it.getTextBounds(text, 0, text.length, bounds)

            bitmap = Bitmap.createBitmap(
                bounds.width() + 2 * textOffset,
                bounds.height() + 2 * textOffset, Bitmap.Config.ARGB_8888
            ).apply { eraseColor(Color.TRANSPARENT) }

            preDrawCanvas.setBitmap(bitmap)

            when (drawType) {
                DrawType.FILLED_COLORED -> drawText(Color.WHITE, color)
                DrawType.FILLED_WHITE -> drawText(color, Color.WHITE)
                else -> drawText(Color.TRANSPARENT, color)
            }
        }
    }

    private fun drawText(bgCol: Int, textCol: Int) {
        _paint?.let {
            bounds.left = 0
            bounds.top = 0
            bounds.right = bitmap.width
            bounds.bottom = bitmap.height
            it.color = bgCol
            preDrawCanvas.drawRoundRect(bounds.toRectF(), roundCorner, roundCorner, it)
            it.color = textCol
            preDrawCanvas.drawText(
                text,
                bitmap.width / 2F,
                bitmap.height / 2F + 1.2F * textOffset,
                it
            )
        }
    }

    // Defines 3 types of text drawing styles
    enum class DrawType {
        // transparent background & colored text
        EMPTY_OUTSIDE,

        // white background & colored text
        FILLED_COLORED,

        // colored background & white text
        FILLED_WHITE
    }
}
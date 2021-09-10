package ir.mohammadhf.birthdays.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import ir.mohammadhf.birthdays.R

class GroupView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = -1
) : AppCompatTextView(context, attributeSet, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private val secondRectF = RectF()

    private var isFirstLoad = true

    var isGroupSelected: Boolean = false
        set(value) {
            field = value
            if (!isFirstLoad) postInvalidate()
        }

    var groupColor: Int = Color.RED
        set(value) {
            field = value
            if (!isFirstLoad) postInvalidate()
        }

    var isGroupModeEnabled: Boolean = true
        set(value) {
            field = value
            if (!isFirstLoad) postInvalidate()
        }

    var cornerRadius: Float = DEF_CORNER_RADIUS.toFloat()
        set(value) {
            field = value
            if (!isFirstLoad) postInvalidate()
        }

    var strokeWidth: Float = DEF_STROKE_WIDTH
        set(value) {
            field = value
            if (!isFirstLoad) {
                makeSecondRect()
                postInvalidate()
            }
        }

    var showBadge: Boolean = false
    private var badgeColor: Int = Color.BLUE

    init {
        paint.isAntiAlias = true

        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.GroupView)
            cornerRadius =
                typedArray.getDimensionPixelSize(
                    R.styleable.GroupView_gv_radius, DEF_CORNER_RADIUS
                ).toFloat()
            isGroupSelected =
                typedArray.getBoolean(R.styleable.GroupView_gv_isSelected, false)
            groupColor = typedArray.getColor(R.styleable.GroupView_gv_defColor, Color.RED)
            isGroupModeEnabled =
                typedArray.getBoolean(R.styleable.GroupView_gv_isGroupModeEnable, true)
            strokeWidth =
                typedArray.getDimension(
                    R.styleable.GroupView_gv_strokeWidth,
                    DEF_STROKE_WIDTH
                )

            typedArray.recycle()
        }

        isFirstLoad = false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        makeFirstRect()
        makeSecondRect()
    }

    override fun onDraw(canvas: Canvas) {
        if (isGroupModeEnabled) {
            // drawing outer rect with desired color and radius
            paint.color = groupColor
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
            // draw another rect if the view is unselected
            if (!isGroupSelected) {
                paint.color = Color.WHITE
                canvas.drawRoundRect(secondRectF, cornerRadius, cornerRadius, paint)
            }
        }
        // draw a bottom blue badge
        if (showBadge) {
            paint.color = badgeColor
            canvas.drawCircle(
                (width / 2).toFloat(),
                height - BADGE_WIDTH / 2,
                BADGE_WIDTH / 2,
                paint
            )
        }

        super.onDraw(canvas)
    }

    private fun drawBadge(canvas: Canvas) {

    }

    fun addBadge(color: Int) {
        badgeColor = color
        showBadge = true
        postInvalidate()
    }

    fun removeBadge() {
        showBadge = false
        postInvalidate()
    }

    private fun makeFirstRect() {
        rectF.left = 0f
        rectF.top = 0f
        rectF.right = width.toFloat()
        rectF.bottom = height.toFloat()
    }

    private fun makeSecondRect() {
        secondRectF.left = strokeWidth
        secondRectF.top = strokeWidth
        secondRectF.right = width - strokeWidth
        secondRectF.bottom = height - strokeWidth
    }

    companion object {
        private const val DEF_STROKE_WIDTH = 4f
        private const val DEF_CORNER_RADIUS = 0
        private const val BADGE_WIDTH = 12f
    }
}
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

    private var isGroupSelected: Boolean = false
    private var groupColor = Color.RED
    private var cornerRadius: Float = 0f
    private var strokeWidth = 4f

    init {
        paint.isAntiAlias = true

        attributeSet?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.GroupView)
            cornerRadius =
                typedArray.getDimensionPixelSize(
                    R.styleable.GroupView_gv_radius, 0
                ).toFloat()
            isGroupSelected =
                typedArray.getBoolean(R.styleable.GroupView_gv_isSelected, false)
            groupColor = typedArray.getColor(R.styleable.GroupView_gv_defColor, Color.RED)
            typedArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rectF.left = 0f
        rectF.top = 0f
        rectF.right = width.toFloat()
        rectF.bottom = height.toFloat()

        secondRectF.left = strokeWidth
        secondRectF.top = strokeWidth
        secondRectF.right = width - strokeWidth
        secondRectF.bottom = height - strokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = groupColor
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

        if (!isGroupSelected) {
            setTextColor(Color.BLACK)
            paint.color = Color.WHITE
            canvas.drawRoundRect(secondRectF, cornerRadius, cornerRadius, paint)
        } else
            setTextColor(Color.WHITE)

        super.onDraw(canvas)
    }

    fun selectGroup(isSelected: Boolean) {
        isGroupSelected = isSelected
        postInvalidate()
    }

    fun isGroupSelected(): Boolean =
        isGroupSelected

    fun setColor(color: Int) {
        groupColor = color
        postInvalidate()
    }
}
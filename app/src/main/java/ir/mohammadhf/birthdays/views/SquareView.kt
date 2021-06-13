package ir.mohammadhf.birthdays.views

import android.content.Context
import android.util.AttributeSet
import android.view.View

class SquareView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = -1
) : View(context, attributeSet, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
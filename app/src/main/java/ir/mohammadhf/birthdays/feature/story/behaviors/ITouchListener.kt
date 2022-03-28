package ir.mohammadhf.birthdays.feature.story.behaviors


interface ITouchListener {
    fun onTouchDown(x: Float, y: Float)

    fun onTouchMove(firstX: Float, firstY: Float, secondX: Float?, secondY: Float?)

    fun onTouchUp(x: Float, y: Float)

    fun onPointerTouchDown(x: Float, y: Float)

    fun onPointerTouchUp(x: Float, y: Float)
}
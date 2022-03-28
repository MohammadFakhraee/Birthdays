package ir.mohammadhf.birthdays.feature.story.behaviors

interface IRotateListener {

    fun startRotate(firstX: Float, firstY: Float, secondX: Float, secondY: Float)

    fun onRotate(firstX: Float, firstY: Float, secondX: Float, secondY: Float)
}
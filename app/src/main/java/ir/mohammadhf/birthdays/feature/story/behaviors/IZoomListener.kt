package ir.mohammadhf.birthdays.feature.story.behaviors

interface IZoomListener {
    val maxZoom: Float
    val minZoom: Float

    fun startZoom(firstX: Float, firstY: Float, secondX: Float, secondY: Float)

    fun onZoom(firstX: Float, firstY: Float, secondX: Float, secondY: Float)
}
package pl.patrykgoworowski.liftchart_common.path

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import pl.patrykgoworowski.liftchart_common.extension.setBounds
import pl.patrykgoworowski.liftchart_common.extension.updateBounds


fun rectShape(): Shape = object : Shape {

    override fun drawShape(
        canvas: Canvas,
        paint: Paint,
        path: Path,
        bounds: RectF
    ) {
        path.moveTo(bounds.left, bounds.top)
        path.lineTo(bounds.right, bounds.top)
        path.lineTo(bounds.right, bounds.bottom)
        path.lineTo(bounds.left, bounds.bottom)
        path.close()
        canvas.drawPath(path, paint)
    }

}

fun roundedCornersShape(all: Float): Shape = roundedCornersShape(all, all, all, all)

fun roundedCornersShape(
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomRight: Float = 0f,
    bottomLeft: Float = 0f,
): Shape = object : CornerShape(topLeft, topRight, bottomRight, bottomLeft) {

    private val radii = FloatArray(8)

    override fun drawBarPathWithCorners(
        canvas: Canvas,
        paint: Paint,
        barPath: Path,
        barBounds: RectF,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ) {
        if (barBounds.height() == 0f) return
        radii[0] = topLeft
        radii[1] = topLeft
        radii[2] = topRight
        radii[3] = topRight
        radii[4] = bottomRight
        radii[5] = bottomRight
        radii[6] = bottomLeft
        radii[7] = bottomLeft
        overrideBoundsWithMinSize(barBounds, topLeft, topRight, bottomRight, bottomLeft)
        barPath.addRoundRect(barBounds, radii, Path.Direction.CCW)
        canvas.drawPath(barPath, paint)
    }

}

fun cutCornerShape(all: Float): Shape = cutCornerShape(all, all, all, all)

fun cutCornerShape(
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomRight: Float = 0f,
    bottomLeft: Float = 0f
): Shape = object : CornerShape(topLeft, topRight, bottomRight, bottomLeft) {

    override fun drawBarPathWithCorners(
        canvas: Canvas,
        paint: Paint,
        barPath: Path,
        barBounds: RectF,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ) {
        barPath.moveTo(barBounds.left, barBounds.top + topLeft)
        barPath.lineTo(barBounds.left + topLeft, barBounds.top)
        barPath.lineTo(barBounds.right - topRight, barBounds.top)
        barPath.lineTo(barBounds.right, barBounds.top + topRight)
        barPath.lineTo(barBounds.right, barBounds.bottom - bottomRight)
        barPath.lineTo(barBounds.right - bottomRight, barBounds.bottom)
        barPath.lineTo(barBounds.left + bottomLeft, barBounds.bottom)
        barPath.lineTo(barBounds.left, barBounds.bottom - bottomLeft)
        barPath.close()
        canvas.drawPath(barPath, paint)
    }

}

fun drawableShape(
    drawable: Drawable,
    keepAspectRatio: Boolean = false,
    otherCreator: Shape? = rectShape()
): Shape = object : Shape {

    private val ratio: Float = drawable.intrinsicWidth.coerceAtLeast(1) /
            drawable.intrinsicHeight.coerceAtLeast(1).toFloat()

    override fun drawShape(
        canvas: Canvas,
        paint: Paint,
        path: Path,
        bounds: RectF
    ) {
        if (bounds.height() == 0f) return
        val drawableHeight = if (keepAspectRatio) bounds.width() * ratio else bounds.height()
        val top = minOf(bounds.top, bounds.bottom - drawableHeight)
        drawable.setBounds(bounds.left, top, bounds.right, top + drawableHeight)
        drawable.draw(canvas)
        otherCreator ?: return

        bounds.updateBounds(top = drawable.bounds.bottom.toFloat())
        if (bounds.height() > 0) {
            otherCreator.drawShape(
                canvas,
                paint,
                path,
                bounds
            )
        }
    }

}
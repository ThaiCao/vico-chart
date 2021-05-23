package pl.patrykgoworowski.liftchart_common.component

import android.graphics.*
import android.graphics.Color.DKGRAY
import android.graphics.Paint.Align.*
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import pl.patrykgoworowski.liftchart_common.component.dimension.DefaultPadding
import pl.patrykgoworowski.liftchart_common.component.dimension.Padding
import pl.patrykgoworowski.liftchart_common.extension.half
import pl.patrykgoworowski.liftchart_common.extension.sp
import pl.patrykgoworowski.liftchart_common.path.Shape
import pl.patrykgoworowski.liftchart_common.path.rectShape
import pl.patrykgoworowski.liftchart_common.text.staticLayout

public open class TextComponent(
    shape: Shape = rectShape(),
    color: Int = Color.GRAY,
    textColor: Int = DKGRAY,
    textSize: Float = 12f.sp,
    val ellipsize: TextUtils.TruncateAt = TextUtils.TruncateAt.END,
) : Component(shape, color), Padding by DefaultPadding() {

    public val textPaint = TextPaint()

    public var isLTR: Boolean = true
    public var textColor: Int by textPaint::color
    public var textSize: Float by textPaint::textSize
    public var textAlign: Paint.Align by textPaint::textAlign
    public var typeface: Typeface by textPaint::typeface
    public var rotationDegrees: Float = 0f
    private var layout: StaticLayout = staticLayout("", textPaint, 0)

    private val measurementBounds = Rect()
    private val layoutCache = HashMap<Int, StaticLayout>()

    init {
        textPaint.color = textColor
        textPaint.textSize = textSize
    }

    public fun drawTextVertically(
        canvas: Canvas,
        text: String,
        textX: Float,
        textY: Float,
        verticalPosition: VerticalPosition,
        width: Int = Int.MAX_VALUE,
    ) {

        val layoutWidth = minOf(textPaint.measureText(text).toInt(), width)
        layout = getLayout(text, width)
        val layoutHeight = layout.height

        val adjustedX = getAdjustedX(textX)
        val adjustedY = getAdjustedY(textY, layoutHeight, verticalPosition)
        val baseLeft = getBaseLeft(adjustedX, layoutWidth)

        draw(
            canvas = canvas,
            left = baseLeft - padding.getLeft(isLTR),
            top = adjustedY - ((layoutHeight / 2) + padding.top),
            right = baseLeft + layoutWidth + padding.getRight(isLTR),
            bottom = adjustedY + ((layoutHeight / 2) + padding.bottom)
        )

        val centeredY = adjustedY - layoutHeight.half

        canvas.save()
        canvas.translate(adjustedX, centeredY)
        if (rotationDegrees != 0f) {
            canvas.rotate(rotationDegrees, adjustedX, centeredY)
        }

        layout.draw(canvas)

        canvas.restore()
    }

    private fun getAdjustedX(textX: Float) = when (textAlign) {
        LEFT -> textX + padding.getLeft(isLTR)
        CENTER -> textX
        RIGHT -> textX - padding.getRight(isLTR)
    }

    private fun getAdjustedY(
        textY: Float,
        layoutHeight: Int,
        verticalPosition: VerticalPosition,
    ) = when (verticalPosition) {
        VerticalPosition.Top -> textY + layoutHeight.half
        VerticalPosition.Center -> textY
        VerticalPosition.Bottom -> textY - layoutHeight.half
    }

    private fun getBaseLeft(adjustedX: Float, layoutWidth: Int) = when (textAlign) {
        LEFT -> adjustedX
        CENTER -> adjustedX - (layoutWidth / 2)
        RIGHT -> adjustedX - layoutWidth
    }

    public fun getTextBounds(text: String, outBounds: Rect = measurementBounds): Rect {
        textPaint.getTextBounds(text, 0, text.length, outBounds)
        return outBounds
    }

    public fun getWidth(text: String, outBounds: Rect = measurementBounds): Float {
        getTextBounds(text, outBounds)
        return outBounds.width() + padding.start + padding.end
    }

    public fun getHeight(
        text: String = TEXT_MEASUREMENT_CHAR,
        width: Int = Int.MAX_VALUE,
    ): Float {
        return getLayout(text, width).height + padding.top + padding.bottom
    }

    public fun clearLayoutCache() {
        layoutCache.clear()
    }

    private fun getLayout(text: String, width: Int): StaticLayout =
        layoutCache.getOrPut(text.hashCode() + 31 * width) {
            staticLayout(text, textPaint, width, ellipsize = ellipsize)
        }

    enum class VerticalPosition {
        Top,
        Center,
        Bottom
    }

    companion object {
        const val TEXT_MEASUREMENT_CHAR = "1"
    }

}
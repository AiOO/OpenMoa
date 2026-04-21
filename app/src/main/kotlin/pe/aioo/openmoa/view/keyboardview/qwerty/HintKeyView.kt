package pe.aioo.openmoa.view.keyboardview.qwerty

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView

class HintKeyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var keyHint: String = ""
        set(value) {
            field = value
            invalidate()
        }

    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9f, resources.displayMetrics)
        color = Color.argb(153, 0, 0, 0)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        hintPaint.color = Color.argb(
            (Color.alpha(color) * 0.6f).toInt(),
            Color.red(color),
            Color.green(color),
            Color.blue(color),
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (keyHint.isEmpty()) return
        val x = width - paddingRight - hintPaint.measureText(keyHint) - 4f
        val y = paddingTop + hintPaint.textSize + 3f
        canvas.drawText(keyHint, x, y, hintPaint)
    }
}

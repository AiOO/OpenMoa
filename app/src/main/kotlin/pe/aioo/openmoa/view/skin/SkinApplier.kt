package pe.aioo.openmoa.view.skin

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import pe.aioo.openmoa.config.KeyboardSkin

object SkinApplier {

    fun apply(root: ViewGroup, skin: KeyboardSkin) {
        val ctx = root.context
        val density = ctx.resources.displayMetrics.density
        val keyBgColor = ContextCompat.getColor(ctx, skin.keyBgColorRes)
        val keyBgPressedColor = ContextCompat.getColor(ctx, skin.keyBgPressedColorRes)
        val keyboardBgColor = ContextCompat.getColor(ctx, skin.keyboardBgColorRes)
        val fgColor = ContextCompat.getColor(ctx, skin.keyFgColorRes)

        root.setBackgroundColor(keyboardBgColor)
        applyRecursive(root, keyBgColor, keyBgPressedColor, fgColor, density)
    }

    fun buildKeyDrawable(ctx: Context, skin: KeyboardSkin, pressed: Boolean): Drawable {
        val density = ctx.resources.displayMetrics.density
        val color = ContextCompat.getColor(
            ctx,
            if (pressed) skin.keyBgPressedColorRes else skin.keyBgColorRes,
        )
        return InsetDrawable(roundedDrawable(color, 4 * density), (2 * density).toInt())
    }

    fun fgColor(ctx: Context, skin: KeyboardSkin): Int =
        ContextCompat.getColor(ctx, skin.keyFgColorRes)

    fun fgAccentColor(ctx: Context, skin: KeyboardSkin): Int =
        ContextCompat.getColor(ctx, skin.keyFgAccentColorRes)

    fun keyboardBgColor(ctx: Context, skin: KeyboardSkin): Int =
        ContextCompat.getColor(ctx, skin.keyboardBgColorRes)

    fun buildPreviewBackground(ctx: Context, skin: KeyboardSkin): GradientDrawable {
        val density = ctx.resources.displayMetrics.density
        val color = ContextCompat.getColor(ctx, skin.keyBgColorRes)
        val strokeColor = ContextCompat.getColor(ctx, skin.keyboardBgColorRes)
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8 * density
            setColor(color)
            setStroke((1 * density).toInt(), strokeColor)
        }
    }

    private fun applyRecursive(
        view: View,
        keyBgColor: Int,
        keyBgPressedColor: Int,
        fgColor: Int,
        density: Float,
    ) {
        val resName = if (view.id == View.NO_ID) "" else {
            try {
                view.resources.getResourceEntryName(view.id)
            } catch (_: Exception) {
                ""
            }
        }

        if (resName.endsWith("Key")) {
            applyKeyBackground(view, keyBgColor, keyBgPressedColor, density)
        }

        if (view is TextView) {
            view.setTextColor(fgColor)
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                applyRecursive(view.getChildAt(i), keyBgColor, keyBgPressedColor, fgColor, density)
            }
        }
    }

    private fun applyKeyBackground(view: View, bgColor: Int, pressedColor: Int, density: Float) {
        val insetPx = (2 * density).toInt()
        val cornerPx = 4 * density
        val selector = StateListDrawable().apply {
            addState(
                intArrayOf(android.R.attr.state_pressed),
                InsetDrawable(roundedDrawable(pressedColor, cornerPx), insetPx),
            )
            addState(
                android.util.StateSet.WILD_CARD,
                InsetDrawable(roundedDrawable(bgColor, cornerPx), insetPx),
            )
        }
        view.background = selector
    }

    private fun roundedDrawable(color: Int, cornerPx: Float): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerPx
            setColor(color)
        }
}

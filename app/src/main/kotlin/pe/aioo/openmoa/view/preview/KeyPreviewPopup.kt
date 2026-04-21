package pe.aioo.openmoa.view.preview

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.KeyboardSkin
import pe.aioo.openmoa.view.skin.SkinApplier

class KeyPreviewPopup(context: Context, skin: KeyboardSkin) {

    private val popupView: View = LayoutInflater.from(context).inflate(
        R.layout.key_preview_popup, null
    )
    private val previewText: TextView = popupView.findViewById<TextView>(R.id.previewText).apply {
        setTextColor(SkinApplier.fgColor(context, skin))
        background = SkinApplier.buildPreviewBackground(context, skin)
    }
    private val popup = PopupWindow(
        popupView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        false
    ).apply {
        isTouchable = false
    }

    fun show(anchor: View, text: String) {
        if (!anchor.isAttachedToWindow) return
        previewText.text = text
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xoff = anchor.width / 2 - popupView.measuredWidth / 2
        val yoff = -(anchor.height + popupView.measuredHeight)
        if (popup.isShowing) {
            popup.update(anchor, xoff, yoff, -1, -1)
        } else {
            popup.showAsDropDown(anchor, xoff, yoff, Gravity.NO_GRAVITY)
        }
    }

    fun update(anchor: View, text: String) {
        if (!popup.isShowing) return
        previewText.text = text
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xoff = anchor.width / 2 - popupView.measuredWidth / 2
        val yoff = -(anchor.height + popupView.measuredHeight)
        popup.update(anchor, xoff, yoff, -1, -1)
    }

    fun hide() {
        if (popup.isShowing) popup.dismiss()
    }

}

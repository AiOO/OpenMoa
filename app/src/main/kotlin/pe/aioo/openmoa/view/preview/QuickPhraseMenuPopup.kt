package pe.aioo.openmoa.view.preview

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import pe.aioo.openmoa.R

class QuickPhraseMenuPopup(context: Context) {

    enum class MenuItem { PHRASE_PREVIEW, EDIT, CANCEL }

    private val popupView: View = LayoutInflater.from(context).inflate(
        R.layout.quick_phrase_menu_popup, null
    )
    private val phrasePreviewItem: TextView = popupView.findViewById(R.id.phrasePreviewItem)
    private val editItem: TextView = popupView.findViewById(R.id.editItem)
    private val cancelItem: TextView = popupView.findViewById(R.id.cancelItem)
    private val popup = PopupWindow(
        popupView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        false
    ).apply {
        isTouchable = false
    }
    private var selectedItem = MenuItem.PHRASE_PREVIEW

    val isShowing: Boolean get() = popup.isShowing

    fun show(anchor: View, phrase: String) {
        if (!anchor.isAttachedToWindow) return
        phrasePreviewItem.text = if (phrase.length > 5) phrase.substring(0, 5) + "…" else phrase
        selectedItem = MenuItem.PHRASE_PREVIEW
        updateHighlight()
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xoff = anchor.width / 2 - popupView.measuredWidth / 2
        val yoff = -(anchor.height + popupView.measuredHeight)
        if (popup.isShowing) {
            popup.update(anchor, xoff, yoff, -1, -1)
        } else {
            popup.showAsDropDown(anchor, xoff, yoff, Gravity.NO_GRAVITY)
        }
    }

    fun updateSelectionByDelta(dx: Float, anchorWidth: Int) {
        val editThreshold = anchorWidth * 0.25f
        val cancelThreshold = anchorWidth * 0.75f
        selectedItem = when {
            dx < editThreshold -> MenuItem.PHRASE_PREVIEW
            dx < cancelThreshold -> MenuItem.EDIT
            else -> MenuItem.CANCEL
        }
        updateHighlight()
    }

    fun confirmAndDismiss(): MenuItem {
        val item = selectedItem
        dismiss()
        return item
    }

    fun dismiss() {
        if (popup.isShowing) popup.dismiss()
    }

    private fun updateHighlight() {
        phrasePreviewItem.alpha = if (selectedItem == MenuItem.PHRASE_PREVIEW) 1.0f else 0.4f
        editItem.alpha = if (selectedItem == MenuItem.EDIT) 1.0f else 0.4f
        cancelItem.alpha = if (selectedItem == MenuItem.CANCEL) 1.0f else 0.4f
    }
}

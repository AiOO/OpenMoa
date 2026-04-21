package pe.aioo.openmoa.view.preview

import android.os.Handler
import android.os.Looper
import android.view.View
import pe.aioo.openmoa.config.KeyboardSkin

class KeyPreviewController(private val enabled: () -> Boolean, private val skin: KeyboardSkin = KeyboardSkin.DEFAULT) {

    private var popup: KeyPreviewPopup? = null
    private val handler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { popup?.hide() }

    fun show(anchor: View, text: String) {
        if (!enabled()) return
        handler.removeCallbacks(hideRunnable)
        val p = popup ?: KeyPreviewPopup(anchor.context, skin).also { popup = it }
        p.show(anchor, text)
    }

    fun update(anchor: View, text: String) {
        if (!enabled()) return
        popup?.update(anchor, text)
    }

    fun hide() {
        handler.removeCallbacks(hideRunnable)
        handler.postDelayed(hideRunnable, 200L)
    }

    fun cancel() {
        handler.removeCallbacks(hideRunnable)
        popup?.hide()
    }

}

package pe.aioo.openmoa

import android.inputmethodservice.InputMethodService
import android.view.View

class OpenMoaIME : InputMethodService() {

    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.open_moa_view, null)
    }

}
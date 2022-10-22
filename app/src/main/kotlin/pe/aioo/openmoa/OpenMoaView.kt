package pe.aioo.openmoa

import android.content.Context
import android.util.AttributeSet
import android.view.View

class OpenMoaView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )
}
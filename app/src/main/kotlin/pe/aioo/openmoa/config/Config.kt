package pe.aioo.openmoa.config

data class Config (
    val longPressRepeatTime: Long = 50L,
    val longPressThresholdTime: Long = 500L,
    val gestureThreshold: Float = 50f,
)
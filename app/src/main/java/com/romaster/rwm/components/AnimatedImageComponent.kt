package com.romaster.rwm.components

import android.os.Parcelable
import com.romaster.rwm.PLAY_BOUNCE
import com.romaster.rwm.PLAY_LOOP
import com.romaster.rwm.PLAY_ONCE
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AnimatedImageComponent(
    override val id: String,
    override val position: ParcelablePosition,
    override val size: ParcelableSize,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas
    val gifUri: String = "",
    val playMode: String = PLAY_ONCE,
    val speed: Float = 1.0f,
    val loopCount: Int = 1,
    val autoStart: Boolean = false
) : WidgetComponent() {
    override val type: String = "ANIMATED_IMAGE"
}

// Esta clase solo para uso interno (no Parcelable)
enum class PlayMode {
    ONCE, LOOP, BOUNCE
}
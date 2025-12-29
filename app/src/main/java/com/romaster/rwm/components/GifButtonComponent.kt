package com.romaster.rwm.components

import android.os.Parcelable
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GifButtonComponent(
    override val id: String,
    override val position: ParcelablePosition,
    override val size: ParcelableSize,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades de GIF
    val gifUri: String = "",
    val playOnce: Boolean = true,
    val speed: Float = 1.0f,
    val autoStart: Boolean = false,
    
    // Acción post-animación
    val postAnimationAction: ParcelableGifAction? = null,
    
    // Estado interno (no serializado)
    @Transient val isPlaying: Boolean = false,
    @Transient val hasPlayed: Boolean = false
    
) : WidgetComponent() {
    override val type: String = "GIF_BUTTON"
}

@Serializable
@Parcelize
sealed class ParcelableGifAction : Parcelable {
    @Parcelize
    @Serializable
    data class LaunchApp(val packageName: String) : ParcelableGifAction()
    
    @Parcelize
    @Serializable
    data class OpenUrl(val url: String) : ParcelableGifAction()
    
    @Parcelize
    @Serializable
    data class RunScript(val script: String) : ParcelableGifAction()
    
    @Parcelize
    @Serializable
    data class StartAnimation(val targetGifId: String) : ParcelableGifAction()
    
    @Parcelize
    @Serializable
    data class ShowNotification(val message: String) : ParcelableGifAction()
    
    @Parcelize
    @Serializable
    data class OpenActivity(val activityClass: String) : ParcelableGifAction()
}
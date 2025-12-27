package com.romaster.rwm.components

import android.os.Parcelable
import com.romaster.rwm.ParcelableButtonState
import com.romaster.rwm.ParcelableButtonStates
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ButtonComponent(
    override val id: String,
    override val position: ParcelablePosition,
    override val size: ParcelableSize,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de botón
    val text: String = "",
    val textColor: String = "#FFFFFFFF",
    val backgroundColor: String = "#FF6750A4",
    val cornerRadius: Float = 8f,
    val onClickAction: ParcelableButtonAction? = null,
    val states: ParcelableButtonStates = ParcelableButtonStates()
) : WidgetComponent() {
    override val type: String = "BUTTON"
}

@Serializable
@Parcelize
sealed class ParcelableButtonAction : Parcelable {
    @Parcelize
    @Serializable
    data class LaunchApp(val packageName: String) : ParcelableButtonAction()
    
    @Parcelize
    @Serializable
    data class OpenUrl(val url: String) : ParcelableButtonAction()
    
    @Parcelize
    @Serializable
    data class RunScript(val script: String) : ParcelableButtonAction()
    
    @Parcelize
    @Serializable
    data class ToggleAnimation(val animationId: String) : ParcelableButtonAction()
}
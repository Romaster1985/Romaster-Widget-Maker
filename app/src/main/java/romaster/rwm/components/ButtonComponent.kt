package com.romaster.rwm.components

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ButtonComponent(
    override val id: String,
    override val position: Position,
    override val size: Size,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de botón
    val text: String = "",
    val textColor: String = "#FFFFFFFF",
    val backgroundColor: String = "#FF6750A4",
    val cornerRadius: Float = 8f,
    val onClickAction: ButtonAction? = null,
    val states: ButtonStates = ButtonStates()
) : WidgetComponent() {
    override val type: ComponentType = ComponentType.BUTTON
}

@Serializable
data class ButtonStates(
    val normal: ButtonState = ButtonState(),
    val pressed: ButtonState? = null,
    val disabled: ButtonState? = null
)

@Serializable
data class ButtonState(
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val scale: Float = 1.0f
)

@Serializable
sealed class ButtonAction {
    data class LaunchApp(val packageName: String) : ButtonAction()
    data class OpenUrl(val url: String) : ButtonAction()
    data class RunScript(val script: String) : ButtonAction()
    data class ToggleAnimation(val animationId: String) : ButtonAction()
}
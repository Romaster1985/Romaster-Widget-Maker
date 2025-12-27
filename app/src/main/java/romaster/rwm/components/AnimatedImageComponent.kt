package com.romaster.rwm.components

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AnimatedImageComponent(
    override val id: String,
    override val position: Position,
    override val size: Size,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de GIF
    val gifUri: String = "",
    val playMode: PlayMode = PlayMode.ONCE,
    val speed: Float = 1.0f,
    val loopCount: Int = 1,
    val autoStart: Boolean = false,
    val triggerEvents: List<AnimationTrigger> = emptyList(),
    val frameControl: FrameControl? = null
) : WidgetComponent() {
    override val type: ComponentType = ComponentType.ANIMATED_IMAGE
}

@Serializable
enum class PlayMode {
    ONCE,      // Reproducir una vez
    LOOP,      // Repetir infinitamente
    BOUNCE,    // Ida y vuelta
    REVERSE,   // Al revés
    PING_PONG  // Alternar dirección
}

@Serializable
data class AnimationTrigger(
    val triggerType: TriggerType,
    val targetComponentId: String? = null,
    val action: TriggerAction,
    val condition: String? = null
)

@Serializable
enum class TriggerType {
    ON_CLICK,
    ON_LONG_PRESS,
    ON_DATA_CHANGE,
    ON_TIME,
    ON_SYSTEM_EVENT
}

@Serializable
sealed class TriggerAction {
    data class PlayAnimation(val animationId: String) : TriggerAction()
    data class PauseAnimation(val animationId: String) : TriggerAction()
    data class StopAnimation(val animationId: String) : TriggerAction()
    data class SeekToFrame(val animationId: String, val frame: Int) : TriggerAction()
}

@Serializable
data class FrameControl(
    val startFrame: Int = 0,
    val endFrame: Int = -1, // -1 = hasta el final
    val currentFrame: Int = 0
)
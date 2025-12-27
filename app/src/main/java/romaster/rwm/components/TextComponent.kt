package com.romaster.rwm.components

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TextComponent(
    override val id: String,
    override val position: Position,
    override val size: Size,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de texto
    val text: String = "",
    val fontSize: Float = 16f,
    val color: String = "#FFFFFFFF",
    val fontFamily: String? = null,
    val textAlignment: TextAlignment = TextAlignment.START,
    val maxLines: Int = 1,
    val isDynamic: Boolean = false,
    val dynamicFormula: String? = null
) : WidgetComponent() {
    override val type: ComponentType = ComponentType.TEXT
}

@Serializable
enum class TextAlignment {
    START, CENTER, END
}
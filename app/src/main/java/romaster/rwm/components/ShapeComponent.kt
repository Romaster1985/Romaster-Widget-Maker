package com.romaster.rwm.components

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ShapeComponent(
    override val id: String,
    override val position: Position,
    override val size: Size,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de forma
    val shapeType: ShapeType = ShapeType.RECTANGLE,
    val color: String = "#FF6750A4",
    val cornerRadius: Float = 0f,
    val strokeWidth: Float = 0f,
    val strokeColor: String? = null,
    val gradient: Gradient? = null
) : WidgetComponent() {
    override val type: ComponentType = ComponentType.SHAPE
}

@Serializable
enum class ShapeType {
    RECTANGLE, CIRCLE, TRIANGLE, OVAL
}

@Serializable
data class Gradient(
    val type: GradientType = GradientType.LINEAR,
    val colors: List<String> = emptyList(),
    val angle: Float = 0f
)

@Serializable
enum class GradientType {
    LINEAR, RADIAL, SWEEP
}
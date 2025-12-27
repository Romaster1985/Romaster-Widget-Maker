package com.romaster.rwm.components

import android.os.Parcelable
import com.romaster.rwm.*
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ShapeComponent(
    override val id: String,
    override val position: ParcelablePosition,
    override val size: ParcelableSize,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de forma
    val shapeType: String = SHAPE_RECTANGLE,
    val color: String = "#FF6750A4",
    val cornerRadius: Float = 0f,
    val strokeWidth: Float = 0f,
    val strokeColor: String? = null,
    val gradient: ParcelableGradient? = null
) : WidgetComponent() {
    override val type: String = "SHAPE"
}

// Esta clase solo para uso interno (no Parcelable)
enum class ShapeType {
    RECTANGLE, CIRCLE, TRIANGLE, OVAL
}
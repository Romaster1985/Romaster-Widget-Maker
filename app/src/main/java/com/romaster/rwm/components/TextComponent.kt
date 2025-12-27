package com.romaster.rwm.components

import android.os.Parcelable
import com.romaster.rwm.*
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TextComponent(
    override val id: String,
    override val position: ParcelablePosition,
    override val size: ParcelableSize,
    override val zIndex: Int,
    override val visible: Boolean = true,
    override val rotation: Float = 0f,
    override val alpha: Float = 1f,
    
    // Propiedades específicas de texto
    val text: String = "",
    val fontSize: Float = 16f,
    val color: String = "#FFFFFFFF",
    val fontFamily: String? = null,
    val textAlignment: String = TEXT_START,
    val maxLines: Int = 1,
    val isDynamic: Boolean = false,
    val dynamicFormula: String? = null
) : WidgetComponent() {
    override val type: String = "TEXT"
}

// Esta clase solo para uso interno (no Parcelable)
enum class TextAlignment {
    START, CENTER, END
}
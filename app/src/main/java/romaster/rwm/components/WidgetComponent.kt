package com.romaster.rwm.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed class WidgetComponent : Parcelable {
    abstract val id: String
    abstract val type: ComponentType
    abstract val position: Position
    abstract val size: Size
    abstract val zIndex: Int
    abstract val visible: Boolean
    abstract val rotation: Float
    abstract val alpha: Float
}

@Serializable
@Parcelize
data class Position(
    val x: Float = 0f,
    val y: Float = 0f
) : Parcelable

@Serializable
@Parcelize
data class Size(
    val width: Float = 100f,
    val height: Float = 100f
) : Parcelable

@Serializable
enum class ComponentType {
    ANIMATED_IMAGE,
    TEXT,
    SHAPE,
    BUTTON,
    IMAGE,
    PROGRESS
}
package com.romaster.rwm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// Clases base Parcelables para reutilizar
@Parcelize
@Serializable
data class ParcelablePosition(
    val x: Float = 0f,
    val y: Float = 0f
) : Parcelable

@Parcelize
@Serializable
data class ParcelableSize(
    val width: Float = 100f,
    val height: Float = 100f
) : Parcelable

@Parcelize
@Serializable
data class ParcelableButtonState(
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val scale: Float = 1.0f
) : Parcelable

@Parcelize
@Serializable
data class ParcelableButtonStates(
    val normal: ParcelableButtonState = ParcelableButtonState(),
    val pressed: ParcelableButtonState? = null,
    val disabled: ParcelableButtonState? = null
) : Parcelable

@Parcelize
@Serializable
data class ParcelableGradient(
    val type: String = "LINEAR",
    val colors: List<String> = emptyList(),
    val angle: Float = 0f
) : Parcelable

// Enums como strings para Parcelable
const val SHAPE_RECTANGLE = "RECTANGLE"
const val SHAPE_CIRCLE = "CIRCLE"
const val SHAPE_TRIANGLE = "TRIANGLE"
const val SHAPE_OVAL = "OVAL"

const val GRADIENT_LINEAR = "LINEAR"
const val GRADIENT_RADIAL = "RADIAL"
const val GRADIENT_SWEEP = "SWEEP"

const val PLAY_ONCE = "ONCE"
const val PLAY_LOOP = "LOOP"
const val PLAY_BOUNCE = "BOUNCE"

const val TEXT_START = "START"
const val TEXT_CENTER = "CENTER"
const val TEXT_END = "END"
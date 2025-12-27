package com.romaster.rwm.components

import android.os.Parcelable
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
sealed class WidgetComponent : Parcelable {
    abstract val id: String
    abstract val type: String
    abstract val position: ParcelablePosition
    abstract val size: ParcelableSize
    abstract val zIndex: Int
    abstract val visible: Boolean
    abstract val rotation: Float
    abstract val alpha: Float
}
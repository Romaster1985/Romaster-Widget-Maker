package com.romaster.rwm.editor

import com.romaster.rwm.components.WidgetComponent
import com.romaster.rwm.projects.Project

data class EditorUiState(
    val project: Project? = null,
    val components: List<WidgetComponent> = emptyList(),
    val selectedComponent: WidgetComponent? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
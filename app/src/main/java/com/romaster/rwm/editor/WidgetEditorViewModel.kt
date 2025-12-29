package com.romaster.rwm.editor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.romaster.rwm.PLAY_ONCE
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import com.romaster.rwm.SHAPE_RECTANGLE
import com.romaster.rwm.TEXT_START
import com.romaster.rwm.components.*
import com.romaster.rwm.projects.Project
import com.romaster.rwm.projects.ProjectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class WidgetEditorViewModel(
    application: Application,
    private val projectId: String,
    private val projectName: String
) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()
    
    private val projectManager = ProjectManager(application)
    
    init {
        if (projectId.isNotEmpty()) {
            loadProject()
        } else {
            createNewProject()
        }
    }
    
    fun loadProject() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val project = projectManager.loadProject(projectId)
                _uiState.update {
                    it.copy(
                        project = project,
                        components = project.blueprint.components,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar el proyecto: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun createNewProject() {
        val project = Project(
            id = UUID.randomUUID().toString(),
            name = projectName,
            path = "",
            blueprint = Project.Blueprint(
                components = emptyList(),
                size = "4x2"
            ),
            createdAt = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis()
        )
        
        _uiState.update {
            it.copy(project = project, components = emptyList())
        }
        
        saveProject()
    }
    
    fun importGifAnimation(uri: Uri) {
        val componentId = UUID.randomUUID().toString()
        val gifComponent = GifButtonComponent(
            id = componentId,
            position = ParcelablePosition(100f, 100f),
            size = ParcelableSize(200f, 200f),
            zIndex = _uiState.value.components.size,
            gifUri = uri.toString(),
            playOnce = true,
            speed = 1.0f,
            autoStart = false
        )
        
        addComponent(gifComponent)
    }
    
    fun addTextComponent() {
        val componentId = UUID.randomUUID().toString()
        val textComponent = TextComponent(
            id = componentId,
            position = ParcelablePosition(100f, 100f),
            size = ParcelableSize(200f, 60f),
            zIndex = _uiState.value.components.size,
            text = "Texto editable",
            fontSize = 24f,
            color = "#FFFFFFFF"
        )
        
        addComponent(textComponent)
    }
    
    fun addShapeComponent() {
        val componentId = UUID.randomUUID().toString()
        val shapeComponent = ShapeComponent(
            id = componentId,
            position = ParcelablePosition(150f, 150f),
            size = ParcelableSize(150f, 150f),
            zIndex = _uiState.value.components.size,
            shapeType = SHAPE_RECTANGLE,
            color = "#FF6750A4",
            cornerRadius = 16f
        )
        
        addComponent(shapeComponent)
    }
    
    fun addButtonComponent() {
        val componentId = UUID.randomUUID().toString()
        val buttonComponent = ButtonComponent(
            id = componentId,
            position = ParcelablePosition(200f, 200f),
            size = ParcelableSize(150f, 60f),
            zIndex = _uiState.value.components.size,
            text = "Botón",
            backgroundColor = "#FF6750A4",
            textColor = "#FFFFFFFF",
            cornerRadius = 12f
        )
        
        addComponent(buttonComponent)
    }
    
    private fun addComponent(component: WidgetComponent) {
        _uiState.update { state ->
            state.copy(
                components = state.components + component,
                selectedComponent = component
            )
        }
        saveProject()
    }
    
    fun selectComponent(componentId: String) {
        val component = _uiState.value.components.find { it.id == componentId }
        _uiState.update { it.copy(selectedComponent = component) }
    }
    
    fun moveComponent(componentId: String, deltaX: Float, deltaY: Float) {
        updateComponent(componentId) { component ->
            val newPosition = ParcelablePosition(
                x = component.position.x + deltaX,
                y = component.position.y + deltaY
            )
            
            when (component) {
                is AnimatedImageComponent -> component.copy(position = newPosition)
                is TextComponent -> component.copy(position = newPosition)
                is ShapeComponent -> component.copy(position = newPosition)
                is ButtonComponent -> component.copy(position = newPosition)
                is GifButtonComponent -> component.copy(position = newPosition)
                else -> component
            }
        }
    }
    
    fun updateComponentProperty(componentId: String, property: String, value: Any) {
        updateComponent(componentId) { component ->
            when (component) {
                is TextComponent -> updateTextProperty(component, property, value)
                is ShapeComponent -> updateShapeProperty(component, property, value)
                is ButtonComponent -> updateButtonProperty(component, property, value)
                is AnimatedImageComponent -> updateAnimatedImageProperty(component, property, value)
                is GifButtonComponent -> updateGifButtonProperty(component, property, value)
                else -> component
            }
        }
    }
    
    private fun updateTextProperty(
        component: TextComponent,
        property: String,
        value: Any
    ): TextComponent {
        return when (property) {
            "text" -> component.copy(text = value as String)
            "fontSize" -> component.copy(fontSize = value as Float)
            "color" -> component.copy(color = value as String)
            else -> component
        }
    }
    
    private fun updateShapeProperty(
        component: ShapeComponent,
        property: String,
        value: Any
    ): ShapeComponent {
        return when (property) {
            "color" -> component.copy(color = value as String)
            "cornerRadius" -> component.copy(cornerRadius = value as Float)
            else -> component
        }
    }
    
    private fun updateButtonProperty(
        component: ButtonComponent,
        property: String,
        value: Any
    ): ButtonComponent {
        return when (property) {
            "text" -> component.copy(text = value as String)
            "backgroundColor" -> component.copy(backgroundColor = value as String)
            "textColor" -> component.copy(textColor = value as String)
            "cornerRadius" -> component.copy(cornerRadius = value as Float)
            else -> component
        }
    }
    
    private fun updateAnimatedImageProperty(
        component: AnimatedImageComponent,
        property: String,
        value: Any
    ): AnimatedImageComponent {
        return when (property) {
            "speed" -> component.copy(speed = value as Float)
            else -> component
        }
    }
    
    private fun updateGifButtonProperty(
        component: GifButtonComponent,
        property: String,
        value: Any
    ): GifButtonComponent {
        return when (property) {
            "speed" -> component.copy(speed = value as Float)
            "playOnce" -> component.copy(playOnce = value as Boolean)
            "autoStart" -> component.copy(autoStart = value as Boolean)
            else -> component
        }
    }
    
    private fun updateComponent(
        componentId: String,
        transform: (WidgetComponent) -> WidgetComponent
    ) {
        _uiState.update { state ->
            val updatedComponents = state.components.map { component ->
                if (component.id == componentId) {
                    transform(component)
                } else {
                    component
                }
            }
            
            val updatedSelectedComponent = if (state.selectedComponent?.id == componentId) {
                transform(state.selectedComponent)
            } else {
                state.selectedComponent
            }
            
            state.copy(
                components = updatedComponents,
                selectedComponent = updatedSelectedComponent
            )
        }
        
        saveProject()
    }
    
    fun saveProject() {
        viewModelScope.launch {
            _uiState.value.project?.let { project ->
                val updatedProject = project.copy(
                    blueprint = project.blueprint.copy(
                        components = _uiState.value.components
                    ),
                    lastModified = System.currentTimeMillis()
                )
                
                try {
                    projectManager.saveProject(updatedProject)
                    _uiState.update { it.copy(project = updatedProject) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Error al guardar: ${e.message}") }
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    // ¡¡¡ESTE ES EL FACTORY QUE FALTA!!!
    companion object {
        class Factory(
            private val application: Application,
            private val projectId: String,
            private val projectName: String
        ) : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return WidgetEditorViewModel(application, projectId, projectName) as T
            }
        }
    }
}
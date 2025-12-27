package com.romaster.rwm.editor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.romaster.rwm.components.*
import com.romaster.rwm.projects.Project
import com.romaster.rwm.projects.ProjectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val project: Project? = null,
    val components: List<WidgetComponent> = emptyList(),
    val selectedComponent: WidgetComponent? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WidgetEditorViewModel(
    application: Application,
    private val projectId: String,
    private val projectName: String
) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()
    
    private val projectManager = ProjectManager(application)
    private val componentHistory = mutableListOf<List<WidgetComponent>>()
    private var historyIndex = -1
    
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
                saveHistory()
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
            blueprint = Project.Blueprint(
                components = emptyList(),
                size = "4x2"
            )
        )
        
        _uiState.update {
            it.copy(project = project, components = emptyList())
        }
        
        saveHistory()
    }
    
    fun importGifAnimation(uri: Uri) {
        val componentId = UUID.randomUUID().toString()
        val gifComponent = AnimatedImageComponent(
            id = componentId,
            position = Position(100f, 100f),
            size = Size(200f, 200f),
            zIndex = _uiState.value.components.size,
            gifUri = uri.toString()
        )
        
        addComponent(gifComponent)
    }
    
    fun addTextComponent() {
        val componentId = UUID.randomUUID().toString()
        val textComponent = TextComponent(
            id = componentId,
            position = Position(100f, 100f),
            size = Size(200f, 60f),
            zIndex = _uiState.value.components.size,
            text = "Texto",
            fontSize = 24f,
            color = "#FFFFFFFF"
        )
        
        addComponent(textComponent)
    }
    
    fun addShapeComponent() {
        val componentId = UUID.randomUUID().toString()
        val shapeComponent = ShapeComponent(
            id = componentId,
            position = Position(100f, 100f),
            size = Size(200f, 200f),
            zIndex = _uiState.value.components.size,
            shapeType = ShapeType.RECTANGLE,
            color = "#FF6750A4",
            cornerRadius = 16f
        )
        
        addComponent(shapeComponent)
    }
    
    fun addButtonComponent() {
        val componentId = UUID.randomUUID().toString()
        val buttonComponent = ButtonComponent(
            id = componentId,
            position = Position(100f, 100f),
            size = Size(150f, 60f),
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
        saveHistory()
        saveProject()
    }
    
    fun selectComponent(componentId: String) {
        val component = _uiState.value.components.find { it.id == componentId }
        _uiState.update { it.copy(selectedComponent = component) }
    }
    
    fun moveComponent(componentId: String, newPosition: Position) {
        updateComponent(componentId) { component ->
            when (component) {
                is AnimatedImageComponent -> component.copy(position = newPosition)
                is TextComponent -> component.copy(position = newPosition)
                is ShapeComponent -> component.copy(position = newPosition)
                is ButtonComponent -> component.copy(position = newPosition)
                else -> component
            }
        }
    }
    
    fun resizeComponent(componentId: String, newSize: Size) {
        updateComponent(componentId) { component ->
            when (component) {
                is AnimatedImageComponent -> component.copy(size = newSize)
                is TextComponent -> component.copy(size = newSize)
                is ShapeComponent -> component.copy(size = newSize)
                is ButtonComponent -> component.copy(size = newSize)
                else -> component
            }
        }
    }
    
    fun updateComponentProperty(componentId: String, property: String, value: Any) {
        updateComponent(componentId) { component ->
            when (component) {
                is AnimatedImageComponent -> updateAnimatedImageProperty(component, property, value)
                is TextComponent -> updateTextProperty(component, property, value)
                is ShapeComponent -> updateShapeProperty(component, property, value)
                is ButtonComponent -> updateButtonProperty(component, property, value)
                else -> component
            }
        }
    }
    
    private fun updateAnimatedImageProperty(
        component: AnimatedImageComponent,
        property: String,
        value: Any
    ): AnimatedImageComponent {
        return when (property) {
            "gifUri" -> component.copy(gifUri = value as String)
            "playMode" -> component.copy(playMode = value as PlayMode)
            "speed" -> component.copy(speed = value as Float)
            "loopCount" -> component.copy(loopCount = value as Int)
            "autoStart" -> component.copy(autoStart = value as Boolean)
            else -> component
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
        
        saveHistory()
        saveProject()
    }
    
    private fun saveHistory() {
        val currentComponents = _uiState.value.components
        
        // Si estamos en medio del historial, eliminamos el futuro
        if (historyIndex < componentHistory.size - 1) {
            componentHistory.subList(historyIndex + 1, componentHistory.size).clear()
        }
        
        componentHistory.add(currentComponents)
        historyIndex++
        
        // Limitar historial a 50 estados
        if (componentHistory.size > 50) {
            componentHistory.removeAt(0)
            historyIndex--
        }
    }
    
    fun undo() {
        if (historyIndex > 0) {
            historyIndex--
            val previousComponents = componentHistory[historyIndex]
            _uiState.update { it.copy(components = previousComponents) }
            saveProject()
        }
    }
    
    fun redo() {
        if (historyIndex < componentHistory.size - 1) {
            historyIndex++
            val nextComponents = componentHistory[historyIndex]
            _uiState.update { it.copy(components = nextComponents) }
            saveProject()
        }
    }
    
    private fun saveProject() {
        viewModelScope.launch {
            _uiState.value.project?.let { project ->
                val updatedProject = project.copy(
                    blueprint = project.blueprint.copy(
                        components = _uiState.value.components
                    )
                )
                
                try {
                    projectManager.saveProject(updatedProject)
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Error al guardar: ${e.message}") }
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
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
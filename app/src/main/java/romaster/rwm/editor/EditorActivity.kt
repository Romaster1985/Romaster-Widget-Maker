package com.romaster.rwm.editor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romaster.rwm.editor.canvas.WidgetCanvas
import com.romaster.rwm.editor.inspector.PropertyInspector
import com.romaster.rwm.editor.palette.ComponentPalette
import com.romaster.rwm.ui.theme.RWMTheme

class EditorActivity : ComponentActivity() {
    
    private lateinit var viewModel: WidgetEditorViewModel
    
    private val pickGifLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importGifAnimation(it)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID) ?: ""
        val projectName = intent.getStringExtra(EXTRA_PROJECT_NAME) ?: "Nuevo Widget"
        
        setContent {
            RWMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditorScreen(
                        projectId = projectId,
                        projectName = projectName,
                        onImportGif = { pickGifLauncher.launch("image/gif") },
                        onSave = { project ->
                            // Guardar proyecto
                            saveProject(project)
                        },
                        onExport = { project ->
                            exportProject(project)
                        }
                    )
                }
            }
        }
    }
    
    @Composable
    fun EditorScreen(
        projectId: String,
        projectName: String,
        onImportGif: () -> Unit,
        onSave: (com.romaster.rwm.projects.Project) -> Unit,
        onExport: (com.romaster.rwm.projects.Project) -> Unit
    ) {
        viewModel = viewModel(
            factory = WidgetEditorViewModel.Factory(
                application = application,
                projectId = projectId,
                projectName = projectName
            )
        )
        
        val uiState by viewModel.uiState.collectAsState()
        
        LaunchedEffect(Unit) {
            viewModel.loadProject()
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            // Canvas principal
            WidgetCanvas(
                modifier = Modifier.fillMaxSize(),
                components = uiState.components,
                selectedComponent = uiState.selectedComponent,
                onComponentSelected = { componentId ->
                    viewModel.selectComponent(componentId)
                },
                onComponentMoved = { componentId, newPosition ->
                    viewModel.moveComponent(componentId, newPosition)
                },
                onComponentResized = { componentId, newSize ->
                    viewModel.resizeComponent(componentId, newSize)
                }
            )
            
            // Paleta de componentes (izquierda)
            ComponentPalette(
                onGifSelected = onImportGif,
                onTextSelected = { viewModel.addTextComponent() },
                onShapeSelected = { viewModel.addShapeComponent() },
                onButtonSelected = { viewModel.addButtonComponent() }
            )
            
            // Inspector de propiedades (derecha)
            uiState.selectedComponent?.let { component ->
                PropertyInspector(
                    component = component,
                    onPropertyChanged = { property, value ->
                        viewModel.updateComponentProperty(component.id, property, value)
                    }
                )
            }
        }
    }
    
    private fun saveProject(project: com.romaster.rwm.projects.Project) {
        // Implementar guardado
    }
    
    private fun exportProject(project: com.romaster.rwm.projects.Project) {
        // Implementar exportación
    }
    
    companion object {
        const val EXTRA_PROJECT_ID = "project_id"
        const val EXTRA_PROJECT_NAME = "project_name"
    }
}
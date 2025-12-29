package com.romaster.rwm.editor

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romaster.rwm.editor.layout.EditorLayout
import com.romaster.rwm.ui.theme.RWMTheme
import com.romaster.rwm.utils.Logger
import com.romaster.rwm.widgets.RWMWidgetProvider

class EditorActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "EditorActivity"
    }
    
    private lateinit var projectId: String
    private var widgetId: Int = -1
    
    // Para seleccionar GIFs
    private val pickGifLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel?.importGifAnimation(it)
            Toast.makeText(this, "GIF importado", Toast.LENGTH_SHORT).show()
        }
    }
    
    private var viewModel: WidgetEditorViewModel? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Logger.info(TAG, "=== EDITOR ACTIVITY INICIANDO ===")
        
        try {
            projectId = intent.getStringExtra("extra_project_id") ?: ""
            widgetId = intent.getIntExtra("extra_widget_id", -1)
            val projectName = intent.getStringExtra("extra_project_name") ?: "Sin nombre"
            
            if (projectId.isEmpty()) {
                Logger.error(TAG, "ERROR: Project ID está vacío")
                Toast.makeText(this, "Error: ID del proyecto no recibido", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            
            Logger.debug(TAG, "Editando widget $widgetId, proyecto: $projectId")
            
            setContent {
                RWMTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val vm: WidgetEditorViewModel = viewModel(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                    return WidgetEditorViewModel(application, projectId, projectName) as T
                                }
                            }
                        )
                        viewModel = vm
                        
                        // Recoger el estado del ViewModel
                        val uiState = vm.uiState
                        
                        EditorLayout(
                            projectName = projectName,
                            uiState = uiState,
                            onSave = { saveWidget(vm) },
                            onPreview = { previewWidget() },
                            onExit = { finish() },
                            onAddText = { vm.addTextComponent() },
                            onAddShape = { vm.addShapeComponent() },
                            onAddButton = { vm.addButtonComponent() },
                            onAddGif = { 
                                Toast.makeText(this, "Selecciona un GIF", Toast.LENGTH_SHORT).show()
                                pickGifLauncher.launch("image/gif") 
                            },
                            onComponentSelected = { componentId -> 
                                vm.selectComponent(componentId) 
                            },
                            onComponentMoved = { componentId, deltaX, deltaY -> 
                                vm.moveComponent(componentId, deltaX, deltaY) 
                            },
                            onPropertyChanged = { componentId, property, value ->
                                vm.updateComponentProperty(componentId, property, value)
                            }
                        )
                    }
                }
            }
            
            Logger.info(TAG, "EditorActivity creado exitosamente")
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "onCreate - Error crítico")
            Toast.makeText(this, "Error en el editor: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun saveWidget(viewModel: WidgetEditorViewModel) {
        try {
            // 1. Guardar proyecto
            viewModel.saveProject()
            
            // 2. Actualizar widget en home screen
            if (widgetId != -1) {
                // FORZAR actualización
                RWMWidgetProvider.forceUpdateWidget(this, widgetId, projectId)
                
                Toast.makeText(this, "✅ Widget actualizado en pantalla principal", Toast.LENGTH_SHORT).show()
                Logger.info(TAG, "Widget $widgetId guardado y actualizado")
            } else {
                Toast.makeText(this, "✅ Proyecto guardado", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "saveWidget")
            Toast.makeText(this, "❌ Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun previewWidget() {
        Toast.makeText(this, "🔍 Vista previa (próximamente)", Toast.LENGTH_SHORT).show()
    }
    
    override fun onResume() {
        super.onResume()
        Logger.debug(TAG, "onResume")
    }
    
    override fun onPause() {
        super.onPause()
        Logger.debug(TAG, "onPause")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Logger.info(TAG, "onDestroy")
    }
}
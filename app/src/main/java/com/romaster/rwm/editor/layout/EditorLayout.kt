package com.romaster.rwm.editor.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import kotlinx.coroutines.flow.collectAsState
import com.romaster.rwm.editor.EditorUiState
import com.romaster.rwm.editor.canvas.EditorCanvas
import com.romaster.rwm.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorLayout(
    projectName: String,
    uiState: StateFlow<EditorUiState>,
    onSave: () -> Unit,
    onPreview: () -> Unit,
    onExit: () -> Unit,
    onAddText: () -> Unit,
    onAddShape: () -> Unit,
    onAddButton: () -> Unit,
    onAddGif: () -> Unit,
    onComponentSelected: (String) -> Unit,
    onComponentMoved: (String, Float, Float) -> Unit,
    onPropertyChanged: (String, String, Any) -> Unit
) {
    val state by uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        EditorTopBar(
            projectName = projectName,
            onSave = onSave,
            onPreview = onPreview,
            onExit = onExit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        
        Row(modifier = Modifier.fillMaxSize()) {
            ComponentPalettePanel(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp),
                onAddText = onAddText,
                onAddShape = onAddShape,
                onAddButton = onAddButton,
                onAddGif = onAddGif
            )
            
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.7f)
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.65f)
                        .background(Color(0xFF1C1B1F), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF6750A4), RoundedCornerShape(12.dp))
                ) {
                    EditorCanvas(
                        components = state.components,
                        selectedComponentId = state.selectedComponent?.id,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SimpleTabPanel(
                    components = state.components,
                    selectedComponentId = state.selectedComponent?.id,
                    selectedComponent = state.selectedComponent,
                    onComponentSelected = onComponentSelected,
                    onPropertyChanged = onPropertyChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f)
                )
            }
        }
    }
}

@Composable
fun SimpleTabPanel(
    components: List<WidgetComponent>,
    selectedComponentId: String?,
    selectedComponent: WidgetComponent?,
    onComponentSelected: (String) -> Unit,
    onPropertyChanged: (String, String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) }
    
    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color(0xFF1C1B1F),
            contentColor = Color.White
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("CAPAS") }
            )
            
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("PROPIEDADES") }
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF25232A))
        ) {
            when (activeTab) {
                0 -> LayersPanel(
                    components = components,
                    selectedComponentId = selectedComponentId,
                    onComponentSelected = onComponentSelected
                )
                1 -> PropertiesPanel(
                    selectedComponent = selectedComponent,
                    onPropertyChanged = onPropertyChanged
                )
            }
        }
    }
}

@Composable
fun PropertiesPanel(
    selectedComponent: WidgetComponent?,
    onPropertyChanged: (String, String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(12.dp)) {
        if (selectedComponent == null) {
            Text(
                text = "Selecciona un componente",
                color = Color(0xFF958DA5),
                fontSize = 12.sp
            )
        } else {
            Text(
                text = "Propiedades de ${selectedComponent.type}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            when (selectedComponent) {
                is TextComponent -> TextPropertiesPanel(
                    component = selectedComponent,
                    onPropertyChanged = { property, value ->
                        onPropertyChanged(selectedComponent.id, property, value)
                    }
                )
                is ShapeComponent -> ShapePropertiesPanel(
                    component = selectedComponent,
                    onPropertyChanged = { property, value ->
                        onPropertyChanged(selectedComponent.id, property, value)
                    }
                )
                is ButtonComponent -> ButtonPropertiesPanel(
                    component = selectedComponent,
                    onPropertyChanged = { property, value ->
                        onPropertyChanged(selectedComponent.id, property, value)
                    }
                )
                is AnimatedImageComponent -> GifPropertiesPanel(
                    component = selectedComponent,
                    onPropertyChanged = { property, value ->
                        onPropertyChanged(selectedComponent.id, property, value)
                    }
                )
                is GifButtonComponent -> GifButtonPropertiesPanel(
                    component = selectedComponent,
                    onPropertyChanged = { property, value ->
                        onPropertyChanged(selectedComponent.id, property, value)
                    }
                )
            }
        }
    }
}

@Composable
fun ComponentPalettePanel(
    modifier: Modifier = Modifier,
    onAddText: () -> Unit,
    onAddShape: () -> Unit,
    onAddButton: () -> Unit,
    onAddGif: () -> Unit
) {
    Column(
        modifier = modifier
            .background(Color(0xFF1C1B1F))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        PaletteIconButton(
            icon = Icons.Default.TextFields,
            label = "Texto",
            color = Color(0xFF4CAF50),
            onClick = onAddText
        )
        
        PaletteIconButton(
            icon = Icons.Default.Circle,
            label = "Forma",
            color = Color(0xFFFF9800),
            onClick = onAddShape
        )
        
        PaletteIconButton(
            icon = Icons.Default.TouchApp,
            label = "Botón",
            color = Color(0xFFE91E63),
            onClick = onAddButton
        )
        
        PaletteIconButton(
            icon = Icons.Default.Animation,
            label = "GIF",
            color = Color(0xFF00BCD4),
            onClick = onAddGif
        )
    }
}

@Composable
fun PaletteIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(52.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun LayersPanel(
    components: List<WidgetComponent>,
    selectedComponentId: String?,
    onComponentSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Capas (${components.size})",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (components.isEmpty()) {
            Text(
                text = "No hay componentes",
                color = Color(0xFF958DA5),
                fontSize = 12.sp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(components.reversed()) { component ->
                    LayerItem(
                        component = component,
                        isSelected = component.id == selectedComponentId,
                        onClick = { onComponentSelected(component.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LayerItem(
    component: WidgetComponent,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFF6750A4) else Color(0xFF302D38)
    val textColor = if (isSelected) Color.White else Color(0xFFE6E1E5)
    
    Box(
        modifier = modifier
            .height(48.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = when (component.type) {
                    "TEXT" -> Icons.Default.TextFields
                    "SHAPE" -> Icons.Default.Circle
                    "BUTTON" -> Icons.Default.TouchApp
                    "ANIMATED_IMAGE", "GIF_BUTTON" -> Icons.Default.Animation
                    else -> Icons.Default.Widgets
                },
                contentDescription = component.type,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = when (component.type) {
                    "TEXT" -> "Texto: ${(component as TextComponent).text.take(15)}..."
                    "SHAPE" -> "Forma: ${(component as ShapeComponent).shapeType}"
                    "BUTTON" -> "Botón: ${(component as ButtonComponent).text.take(15)}..."
                    "ANIMATED_IMAGE" -> "GIF Animado"
                    "GIF_BUTTON" -> "Botón GIF"
                    else -> "Componente ${component.type}"
                },
                color = textColor,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun EditorTopBar(
    projectName: String,
    onSave: () -> Unit,
    onPreview: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF1C1B1F),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Salir",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = projectName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onPreview) {
                    Text("VISTA PREVIA")
                }
                
                Button(onClick = onSave) {
                    Text("GUARDAR")
                }
            }
        }
    }
}
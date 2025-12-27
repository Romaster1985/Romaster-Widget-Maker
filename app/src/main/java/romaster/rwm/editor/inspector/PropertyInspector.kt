package com.romaster.rwm.editor.inspector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.romaster.rwm.components.*

@Composable
fun PropertyInspector(
    component: WidgetComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Propiedades",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            when (component) {
                is AnimatedImageComponent -> GifProperties(component, onPropertyChanged)
                is TextComponent -> TextProperties(component, onPropertyChanged)
                is ShapeComponent -> ShapeProperties(component, onPropertyChanged)
                is ButtonComponent -> ButtonProperties(component, onPropertyChanged)
            }
            
            // Propiedades comunes a todos los componentes
            CommonProperties(component, onPropertyChanged)
        }
    }
}

@Composable
fun GifProperties(
    component: AnimatedImageComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var speed by remember { mutableStateOf(component.speed.toString()) }
    var loopCount by remember { mutableStateOf(component.loopCount.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Animación GIF",
            style = MaterialTheme.typography.titleMedium
        )
        
        OutlinedTextField(
            value = speed,
            onValueChange = {
                speed = it
                it.toFloatOrNull()?.let { value ->
                    onPropertyChanged("speed", value)
                }
            },
            label = { Text("Velocidad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = loopCount,
            onValueChange = {
                loopCount = it
                it.toIntOrNull()?.let { value ->
                    onPropertyChanged("loopCount", value)
                }
            },
            label = { Text("Repeticiones") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        var autoStart by remember { mutableStateOf(component.autoStart) }
        Switch(
            checked = autoStart,
            onCheckedChange = {
                autoStart = it
                onPropertyChanged("autoStart", it)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Inicio automático")
    }
}

@Composable
fun CommonProperties(
    component: WidgetComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Posición y Tamaño",
            style = MaterialTheme.typography.titleMedium
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = component.position.x.toString(),
                onValueChange = {
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("positionX", value)
                    }
                },
                label = { Text("X") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = component.position.y.toString(),
                onValueChange = {
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("positionY", value)
                    }
                },
                label = { Text("Y") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = component.size.width.toString(),
                onValueChange = {
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("width", value)
                    }
                },
                label = { Text("Ancho") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = component.size.height.toString(),
                onValueChange = {
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("height", value)
                    }
                },
                label = { Text("Alto") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        OutlinedTextField(
            value = component.zIndex.toString(),
            onValueChange = {
                it.toIntOrNull()?.let { value ->
                    onPropertyChanged("zIndex", value)
                }
            },
            label = { Text("Orden Z") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        var visible by remember { mutableStateOf(component.visible) }
        Switch(
            checked = visible,
            onCheckedChange = {
                visible = it
                onPropertyChanged("visible", it)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Visible")
    }
}
package com.romaster.rwm.editor.inspector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.romaster.rwm.*
import com.romaster.rwm.components.*

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GifProperties(
    component: AnimatedImageComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var speed by remember { mutableStateOf(component.speed.toString()) }
    var loopCount by remember { mutableStateOf(component.loopCount.toString()) }
    var autoStart by remember { mutableStateOf(component.autoStart) }
    var playMode by remember { mutableStateOf(component.playMode) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Animación GIF",
            style = MaterialTheme.typography.titleMedium
        )
        
        // Selector de modo de reproducción
        var playModeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = playModeExpanded,
            onExpandedChange = { playModeExpanded = !playModeExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = when (playMode) {
                    PLAY_ONCE -> "Una vez"
                    PLAY_LOOP -> "Repetir"
                    PLAY_BOUNCE -> "Rebotar"
                    else -> "Una vez"
                },
                onValueChange = {},
                label = { Text("Modo de reproducción") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playModeExpanded) }
            )
            ExposedDropdownMenu(
                expanded = playModeExpanded,
                onDismissRequest = { playModeExpanded = false }
            ) {
                listOf(
                    PLAY_ONCE to "Una vez",
                    PLAY_LOOP to "Repetir",
                    PLAY_BOUNCE to "Rebotar"
                ).forEach { (mode, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            playMode = mode
                            onPropertyChanged("playMode", mode)
                            playModeExpanded = false
                        }
                    )
                }
            }
        }
        
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
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Inicio automático")
            Switch(
                checked = autoStart,
                onCheckedChange = {
                    autoStart = it
                    onPropertyChanged("autoStart", it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextProperties(
    component: TextComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var text by remember { mutableStateOf(component.text) }
    var fontSize by remember { mutableStateOf(component.fontSize.toString()) }
    var color by remember { mutableStateOf(component.color) }
    var textAlignment by remember { mutableStateOf(component.textAlignment) }
    var maxLines by remember { mutableStateOf(component.maxLines.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Propiedades de Texto",
            style = MaterialTheme.typography.titleMedium
        )
        
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onPropertyChanged("text", it)
            },
            label = { Text("Texto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fontSize,
                onValueChange = {
                    fontSize = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("fontSize", value)
                    }
                },
                label = { Text("Tamaño") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = maxLines,
                onValueChange = {
                    maxLines = it
                    it.toIntOrNull()?.let { value ->
                        onPropertyChanged("maxLines", value)
                    }
                },
                label = { Text("Líneas máx") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Selector de alineación
        var alignmentExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = alignmentExpanded,
            onExpandedChange = { alignmentExpanded = !alignmentExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = when (textAlignment) {
                    TEXT_START -> "Izquierda"
                    TEXT_CENTER -> "Centro"
                    TEXT_END -> "Derecha"
                    else -> "Izquierda"
                },
                onValueChange = {},
                label = { Text("Alineación") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = alignmentExpanded) }
            )
            ExposedDropdownMenu(
                expanded = alignmentExpanded,
                onDismissRequest = { alignmentExpanded = false }
            ) {
                listOf(
                    TEXT_START to "Izquierda",
                    TEXT_CENTER to "Centro",
                    TEXT_END to "Derecha"
                ).forEach { (alignment, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            textAlignment = alignment
                            onPropertyChanged("textAlignment", alignment)
                            alignmentExpanded = false
                        }
                    )
                }
            }
        }
        
        OutlinedTextField(
            value = color,
            onValueChange = {
                color = it
                onPropertyChanged("color", it)
            },
            label = { Text("Color (hex)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapeProperties(
    component: ShapeComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var color by remember { mutableStateOf(component.color) }
    var cornerRadius by remember { mutableStateOf(component.cornerRadius.toString()) }
    var shapeType by remember { mutableStateOf(component.shapeType) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Propiedades de Forma",
            style = MaterialTheme.typography.titleMedium
        )
        
        // Selector de tipo de forma
        var shapeTypeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = shapeTypeExpanded,
            onExpandedChange = { shapeTypeExpanded = !shapeTypeExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = when (shapeType) {
                    SHAPE_RECTANGLE -> "Rectángulo"
                    SHAPE_CIRCLE -> "Círculo"
                    SHAPE_TRIANGLE -> "Triángulo"
                    SHAPE_OVAL -> "Óvalo"
                    else -> "Rectángulo"
                },
                onValueChange = {},
                label = { Text("Tipo de forma") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shapeTypeExpanded) }
            )
            ExposedDropdownMenu(
                expanded = shapeTypeExpanded,
                onDismissRequest = { shapeTypeExpanded = false }
            ) {
                listOf(
                    SHAPE_RECTANGLE to "Rectángulo",
                    SHAPE_CIRCLE to "Círculo",
                    SHAPE_TRIANGLE to "Triángulo",
                    SHAPE_OVAL to "Óvalo"
                ).forEach { (type, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            shapeType = type
                            onPropertyChanged("shapeType", type)
                            shapeTypeExpanded = false
                        }
                    )
                }
            }
        }
        
        OutlinedTextField(
            value = color,
            onValueChange = {
                color = it
                onPropertyChanged("color", it)
            },
            label = { Text("Color (hex)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = cornerRadius,
            onValueChange = {
                cornerRadius = it
                it.toFloatOrNull()?.let { value ->
                    onPropertyChanged("cornerRadius", value)
                }
            },
            label = { Text("Radio de esquina") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonProperties(
    component: ButtonComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var text by remember { mutableStateOf(component.text) }
    var backgroundColor by remember { mutableStateOf(component.backgroundColor) }
    var textColor by remember { mutableStateOf(component.textColor) }
    var cornerRadius by remember { mutableStateOf(component.cornerRadius.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Propiedades de Botón",
            style = MaterialTheme.typography.titleMedium
        )
        
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onPropertyChanged("text", it)
            },
            label = { Text("Texto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = backgroundColor,
            onValueChange = {
                backgroundColor = it
                onPropertyChanged("backgroundColor", it)
            },
            label = { Text("Color de fondo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = textColor,
            onValueChange = {
                textColor = it
                onPropertyChanged("textColor", it)
            },
            label = { Text("Color de texto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = cornerRadius,
            onValueChange = {
                cornerRadius = it
                it.toFloatOrNull()?.let { value ->
                    onPropertyChanged("cornerRadius", value)
                }
            },
            label = { Text("Radio de esquina") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonProperties(
    component: WidgetComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var positionX by remember { mutableStateOf(component.position.x.toString()) }
    var positionY by remember { mutableStateOf(component.position.y.toString()) }
    var width by remember { mutableStateOf(component.size.width.toString()) }
    var height by remember { mutableStateOf(component.size.height.toString()) }
    var zIndex by remember { mutableStateOf(component.zIndex.toString()) }
    var visible by remember { mutableStateOf(component.visible) }
    var rotation by remember { mutableStateOf(component.rotation.toString()) }
    var alpha by remember { mutableStateOf(component.alpha.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Posición y Tamaño",
            style = MaterialTheme.typography.titleMedium
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = positionX,
                onValueChange = {
                    positionX = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("positionX", value)
                    }
                },
                label = { Text("X") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = positionY,
                onValueChange = {
                    positionY = it
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
                value = width,
                onValueChange = {
                    width = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("width", value)
                    }
                },
                label = { Text("Ancho") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = height,
                onValueChange = {
                    height = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("height", value)
                    }
                },
                label = { Text("Alto") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = zIndex,
                onValueChange = {
                    zIndex = it
                    it.toIntOrNull()?.let { value ->
                        onPropertyChanged("zIndex", value)
                    }
                },
                label = { Text("Orden Z") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = rotation,
                onValueChange = {
                    rotation = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("rotation", value)
                    }
                },
                label = { Text("Rotación") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        OutlinedTextField(
            value = alpha,
            onValueChange = {
                alpha = it
                it.toFloatOrNull()?.let { value ->
                    onPropertyChanged("alpha", value)
                }
            },
            label = { Text("Transparencia") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Visible")
            Switch(
                checked = visible,
                onCheckedChange = {
                    visible = it
                    onPropertyChanged("visible", it)
                }
            )
        }
    }
}
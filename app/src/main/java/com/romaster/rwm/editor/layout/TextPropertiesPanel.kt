package com.romaster.rwm.editor.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextPropertiesPanel(
    component: com.romaster.rwm.components.TextComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var textValue by remember { mutableStateOf(component.text) }
    var fontSizeValue by remember { mutableStateOf(component.fontSize.toString()) }
    var colorValue by remember { mutableStateOf(component.color) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Texto
        Column {
            Text(
                text = "Texto",
                color = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
                fontSize = 12.sp
            )
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    onPropertyChanged("text", it)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Tamaño de fuente
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tamaño",
                    color = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = fontSizeValue,
                    onValueChange = {
                        fontSizeValue = it
                        it.toFloatOrNull()?.let { value ->
                            onPropertyChanged("fontSize", value)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Color
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Color",
                    color = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = colorValue,
                    onValueChange = {
                        colorValue = it
                        onPropertyChanged("color", it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
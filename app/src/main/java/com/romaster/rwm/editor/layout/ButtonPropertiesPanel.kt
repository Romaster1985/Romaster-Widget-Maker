package com.romaster.rwm.editor.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp  // ¡¡IMPORTANTE!!

@Composable
fun ButtonPropertiesPanel(
    component: com.romaster.rwm.components.ButtonComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var textValue by remember { mutableStateOf(component.text) }
    var bgColorValue by remember { mutableStateOf(component.backgroundColor) }
    var textColorValue by remember { mutableStateOf(component.textColor) }
    var cornerRadiusValue by remember { mutableStateOf(component.cornerRadius.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Campo de texto
        Column {
            Text(
                text = "Texto",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp  // <-- USAR .sp
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
            // Color de fondo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Color fondo",
                    color = Color(0xFFE6E1E5),
                    fontSize = 12.sp  // <-- USAR .sp
                )
                OutlinedTextField(
                    value = bgColorValue,
                    onValueChange = {
                        bgColorValue = it
                        onPropertyChanged("backgroundColor", it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Color de texto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Color texto",
                    color = Color(0xFFE6E1E5),
                    fontSize = 12.sp  // <-- USAR .sp
                )
                OutlinedTextField(
                    value = textColorValue,
                    onValueChange = {
                        textColorValue = it
                        onPropertyChanged("textColor", it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Radio de esquina
        Column {
            Text(
                text = "Radio de esquina",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp  // <-- USAR .sp
            )
            OutlinedTextField(
                value = cornerRadiusValue,
                onValueChange = {
                    cornerRadiusValue = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("cornerRadius", value)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
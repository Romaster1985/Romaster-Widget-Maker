package com.romaster.rwm.editor.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GifPropertiesPanel(
    component: com.romaster.rwm.components.AnimatedImageComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var speedValue by remember { mutableStateOf(component.speed.toString()) }
    var loopCountValue by remember { mutableStateOf(component.loopCount.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Configuración de GIF",
            color = Color(0xFF00BCD4),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Velocidad
        Column {
            Text(
                text = "Velocidad",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp
            )
            OutlinedTextField(
                value = speedValue,
                onValueChange = {
                    speedValue = it
                    it.toFloatOrNull()?.let { value ->
                        onPropertyChanged("speed", value)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Repeticiones
        Column {
            Text(
                text = "Repeticiones",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp
            )
            OutlinedTextField(
                value = loopCountValue,
                onValueChange = {
                    loopCountValue = it
                    it.toIntOrNull()?.let { value ->
                        onPropertyChanged("loopCount", value)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Inicio automático
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inicio automático",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp
            )
            
            Switch(
                checked = component.autoStart,
                onCheckedChange = {
                    onPropertyChanged("autoStart", it)
                }
            )
        }
    }
}
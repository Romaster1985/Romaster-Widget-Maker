package com.romaster.rwm.editor.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShapePropertiesPanel(
    component: com.romaster.rwm.components.ShapeComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var colorValue by remember { mutableStateOf(component.color) }
    var cornerRadiusValue by remember { mutableStateOf(component.cornerRadius.toString()) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Color
        Column {
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
        
        // Radio de esquina
        Column {
            Text(
                text = "Radio de esquina",
                color = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
                fontSize = 12.sp
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
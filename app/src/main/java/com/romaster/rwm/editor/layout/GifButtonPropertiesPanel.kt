package com.romaster.rwm.editor.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.romaster.rwm.components.GifButtonComponent

@Composable
fun GifButtonPropertiesPanel(
    component: GifButtonComponent,
    onPropertyChanged: (String, Any) -> Unit
) {
    var speedValue by remember { mutableStateOf(component.speed.toString()) }
    var playOnceValue by remember { mutableStateOf(component.playOnce) }
    var autoStartValue by remember { mutableStateOf(component.autoStart) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Botón GIF Interactivo",
            color = Color(0xFF00BCD4),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Campo de velocidad
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Velocidad de reproducción",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            OutlinedTextField(
                value = speedValue,
                onValueChange = { newValue ->
                    speedValue = newValue
                    newValue.toFloatOrNull()?.let { value ->
                        if (value > 0) {
                            onPropertyChanged("speed", value)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "1.0 (normal)",
                        color = Color(0xFF958DA5),
                        fontSize = 12.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color(0xFF49454F),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF00BCD4)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 13.sp
                )
            )
            
            Text(
                text = "1.0 = velocidad normal, 2.0 = doble velocidad",
                color = Color(0xFF958DA5),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        // Switches para opciones
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Switch: Reproducir solo una vez
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reproducir solo una vez",
                        color = Color(0xFFE6E1E5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "El GIF se detiene después de completar la animación",
                        color = Color(0xFF958DA5),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Switch(
                    checked = playOnceValue,
                    onCheckedChange = { newValue ->
                        playOnceValue = newValue
                        onPropertyChanged("playOnce", newValue)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00BCD4),
                        checkedTrackColor = Color(0xFF00BCD4).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color(0xFF958DA5),
                        uncheckedTrackColor = Color(0xFF49454F)
                    )
                )
            }
            
            // Switch: Inicio automático
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Inicio automático",
                        color = Color(0xFFE6E1E5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "El GIF comienza a reproducirse automáticamente",
                        color = Color(0xFF958DA5),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Switch(
                    checked = autoStartValue,
                    onCheckedChange = { newValue ->
                        autoStartValue = newValue
                        onPropertyChanged("autoStart", newValue)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00BCD4),
                        checkedTrackColor = Color(0xFF00BCD4).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color(0xFF958DA5),
                        uncheckedTrackColor = Color(0xFF49454F)
                    )
                )
            }
        }
        
        // Sección de acciones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Acción al completar",
                color = Color(0xFFE6E1E5),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            OutlinedTextField(
                value = component.postAnimationAction?.let { 
                    when (it) {
                        is com.romaster.rwm.components.ParcelableGifAction.LaunchApp -> 
                            "Abrir app: ${it.packageName}"
                        is com.romaster.rwm.components.ParcelableGifAction.OpenUrl -> 
                            "Abrir URL: ${it.url}"
                        is com.romaster.rwm.components.ParcelableGifAction.ShowNotification -> 
                            "Notificación: ${it.message}"
                        else -> "Sin acción configurada"
                    }
                } ?: "Sin acción configurada",
                onValueChange = { /* No editable directamente */ },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text(
                        text = "Configurar acción post-animación",
                        color = Color(0xFF958DA5),
                        fontSize = 12.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFF49454F),
                    disabledTextColor = Color(0xFF958DA5),
                    disabledPlaceholderColor = Color(0xFF958DA5)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { 
                            // TODO: Abrir diálogo para configurar acción
                        },
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Configurar acción",
                            tint = Color(0xFF00BCD4)
                        )
                    }
                }
            )
            
            Text(
                text = "Las acciones estarán disponibles en una futura actualización",
                color = Color(0xFFFF9800),
                fontSize = 10.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
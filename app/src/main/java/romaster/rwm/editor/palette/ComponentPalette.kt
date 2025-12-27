package com.romaster.rwm.editor.palette

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ComponentPalette(
    onGifSelected: () -> Unit,
    onTextSelected: () -> Unit,
    onShapeSelected: () -> Unit,
    onButtonSelected: () -> Unit,
    onImageSelected: () -> Unit,
    onProgressSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        PaletteItem(
            icon = Icons.Default.Animation,
            label = "GIF",
            color = Color(0xFF00BCD4),
            onClick = onGifSelected
        )
        
        PaletteItem(
            icon = Icons.Default.TextFields,
            label = "Texto",
            color = Color(0xFF4CAF50),
            onClick = onTextSelected
        )
        
        PaletteItem(
            icon = Icons.Default.Circle,
            label = "Forma",
            color = Color(0xFFFF9800),
            onClick = onShapeSelected
        )
        
        PaletteItem(
            icon = Icons.Default.TouchApp,
            label = "Botón",
            color = Color(0xFFE91E63),
            onClick = onButtonSelected
        )
        
        PaletteItem(
            icon = Icons.Default.Image,
            label = "Imagen",
            color = Color(0xFF9C27B0),
            onClick = onImageSelected
        )
        
        PaletteItem(
            icon = Icons.Default.TrendingUp,
            label = "Progreso",
            color = Color(0xFF3F51B5),
            onClick = onProgressSelected
        )
    }
}

@Composable
fun PaletteItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .height(64.dp)
            .clickable(onClick = onClick)
    ) {
        Card(
            modifier = Modifier
                .size(48.dp)
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.2f),
                contentColor = color
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
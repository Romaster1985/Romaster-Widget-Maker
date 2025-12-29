package com.romaster.rwm.editor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import com.romaster.rwm.components.*

@Composable
fun EditorCanvas(
    components: List<WidgetComponent> = emptyList(),
    selectedComponentId: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0F0D13))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dibujar grid
            drawGrid()
            
            // Dibujar borde del widget (tamaño real)
            drawWidgetBorder()
            
            // Dibujar componentes
            components.forEach { component ->
                drawComponent(component, selectedComponentId == component.id)
            }
        }
    }
}

private fun DrawScope.drawGrid() {
    val gridSize = 20f
    val gridColor = Color(0x33FFFFFF)
    
    // Líneas verticales
    for (x in 0..size.width.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridColor,
            start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f),
            end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height),
            strokeWidth = 1f
        )
    }
    
    // Líneas horizontales
    for (y in 0..size.height.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridColor,
            start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()),
            end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat()),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawWidgetBorder() {
    val borderColor = Color(0x4D6750A4)
    val borderWidth = 2f
    
    drawRect(
        color = borderColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth),
        topLeft = androidx.compose.ui.geometry.Offset(50f, 50f),
        size = androidx.compose.ui.geometry.Size(300f, 150f)
    )
}

private fun DrawScope.drawComponent(
    component: WidgetComponent,
    isSelected: Boolean = false
) {
    when (component) {
        is TextComponent -> drawTextComponent(component, isSelected)
        is ShapeComponent -> drawShapeComponent(component, isSelected)
        is ButtonComponent -> drawButtonComponent(component, isSelected)
        is AnimatedImageComponent -> drawAnimatedImageComponent(component, isSelected)
        is GifButtonComponent -> drawGifButtonComponent(component, isSelected)
    }
}

private fun DrawScope.drawTextComponent(
    component: TextComponent,
    isSelected: Boolean
) {
    val bgColor = if (isSelected) Color(0x666750A4) else Color(0x336750A4)
    
    // Fondo del componente
    drawRect(
        color = bgColor,
        topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
        size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height)
    )
    
    // Borde si está seleccionado
    if (isSelected) {
        drawRect(
            color = Color(0xFF6750A4),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
            topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
            size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height)
        )
    }
    
    // Simulación de texto (líneas)
    drawRect(
        color = Color.White,
        topLeft = androidx.compose.ui.geometry.Offset(component.position.x + 10f, component.position.y + 25f),
        size = androidx.compose.ui.geometry.Size(component.size.width - 20f, 2f)
    )
    
    drawRect(
        color = Color.White,
        topLeft = androidx.compose.ui.geometry.Offset(component.position.x + 10f, component.position.y + 35f),
        size = androidx.compose.ui.geometry.Size(component.size.width - 40f, 2f)
    )
}

private fun DrawScope.drawShapeComponent(
    component: ShapeComponent,
    isSelected: Boolean
) {
    val color = Color(android.graphics.Color.parseColor(component.color))
    val bgColor = if (isSelected) color.copy(alpha = 0.7f) else color
    
    when (component.shapeType) {
        "RECTANGLE" -> {
            drawRoundRect(
                color = bgColor,
                topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
                size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(component.cornerRadius, component.cornerRadius)
            )
            
            if (isSelected) {
                drawRoundRect(
                    color = Color(0xFF6750A4),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
                    topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
                    size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(component.cornerRadius, component.cornerRadius)
                )
            }
        }
        "CIRCLE" -> {
            drawCircle(
                color = bgColor,
                center = androidx.compose.ui.geometry.Offset(
                    component.position.x + component.size.width / 2,
                    component.position.y + component.size.height / 2
                ),
                radius = kotlin.math.min(component.size.width, component.size.height) / 2
            )
        }
        // Otras formas...
    }
}

private fun DrawScope.drawButtonComponent(
    component: ButtonComponent,
    isSelected: Boolean
) {
    val bgColor = Color(android.graphics.Color.parseColor(component.backgroundColor))
    val borderColor = if (isSelected) Color(0xFF6750A4) else Color.Transparent
    
    // Fondo del botón
    drawRoundRect(
        color = bgColor,
        topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
        size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(component.cornerRadius, component.cornerRadius)
    )
    
    // Borde si está seleccionado
    if (isSelected) {
        drawRoundRect(
            color = borderColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
            topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
            size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(component.cornerRadius, component.cornerRadius)
        )
    }
    
    // Simulación de texto del botón
    val textStartX = component.position.x + component.size.width / 4
    val textStartY = component.position.y + component.size.height / 2
    
    drawRect(
        color = Color(android.graphics.Color.parseColor(component.textColor)),
        topLeft = androidx.compose.ui.geometry.Offset(textStartX, textStartY - 1f),
        size = androidx.compose.ui.geometry.Size(component.size.width / 2, 2f)
    )
}

private fun DrawScope.drawAnimatedImageComponent(
    component: AnimatedImageComponent,
    isSelected: Boolean
) {
    val borderColor = if (isSelected) Color(0xFF00BCD4) else Color(0x3300BCD4)
    
    // Marco para GIF
    drawRect(
        color = Color(0x3300BCD4),
        topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
        size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height)
    )
    
    // Borde si está seleccionado
    if (isSelected) {
        drawRect(
            color = borderColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
            topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
            size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height)
        )
    }
    
    // Indicador de animación
    drawCircle(
        color = Color(0xFF00BCD4),
        center = androidx.compose.ui.geometry.Offset(
            component.position.x + 20f,
            component.position.y + 20f
        ),
        radius = 8f
    )
}

private fun DrawScope.drawGifButtonComponent(
    component: GifButtonComponent,
    isSelected: Boolean
) {
    val borderColor = if (isSelected) Color(0xFF00FF00) else Color(0x3300FF00)
    
    // Marco para GIF Button
    drawRect(
        color = Color(0x3300BCD4),
        topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
        size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height)
    )
    
    // Borde si está seleccionado
    if (isSelected) {
        drawRect(
            color = borderColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
            topLeft = androidx.compose.ui.geometry.Offset(component.position.x, component.position.y),
            size = androidx.compose.ui.geometry.Size(component.size.width, component.size.height)
        )
    }
    
    // Indicador de botón GIF
    drawCircle(
        color = Color(0xFF00FF00),
        center = androidx.compose.ui.geometry.Offset(
            component.position.x + component.size.width / 2,
            component.position.y + component.size.height / 2
        ),
        radius = 16f
    )
}
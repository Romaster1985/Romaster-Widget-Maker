package com.romaster.rwm.editor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.romaster.rwm.ParcelablePosition
import com.romaster.rwm.ParcelableSize
import com.romaster.rwm.components.*

@Composable
fun InteractiveCanvas(
    components: List<WidgetComponent> = emptyList(),
    selectedComponentId: String? = null,
    onComponentSelected: (String) -> Unit = {},
    onComponentMoved: (String, ParcelablePosition) -> Unit = { _, _ -> },
    onComponentResized: (String, ParcelableSize) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var dragState by remember { mutableStateOf<DragState?>(null) }
    var resizeState by remember { mutableStateOf<ResizeState?>(null) }
    
    Box(
        modifier = modifier
            .background(Color(0xFF0F0D13))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Buscar componente en la posición del tap
                    val tappedComponent = findComponentAtPosition(offset, components)
                    tappedComponent?.let { component ->
                        onComponentSelected(component.id)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val component = findComponentAtPosition(offset, components)
                        if (component != null) {
                            // Verificar si el clic fue en el borde (para redimensionar)
                            val isOnEdge = isOnComponentEdge(offset, component)
                            if (isOnEdge && selectedComponentId == component.id) {
                                // Iniciar redimensionamiento
                                resizeState = ResizeState(
                                    componentId = component.id,
                                    startOffset = offset,
                                    startSize = component.size
                                )
                            } else {
                                // Iniciar arrastre
                                dragState = DragState(
                                    componentId = component.id,
                                    startOffset = offset,
                                    startPosition = component.position
                                )
                                onComponentSelected(component.id)
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        
                        dragState?.let { state ->
                            val newPosition = ParcelablePosition(
                                x = state.startPosition.x + dragAmount.x,
                                y = state.startPosition.y + dragAmount.y
                            )
                            onComponentMoved(state.componentId, newPosition)
                        }
                        
                        resizeState?.let { state ->
                            val newSize = ParcelableSize(
                                width = maxOf(50f, state.startSize.width + dragAmount.x),
                                height = maxOf(50f, state.startSize.height + dragAmount.y)
                            )
                            onComponentResized(state.componentId, newSize)
                        }
                    },
                    onDragEnd = {
                        dragState = null
                        resizeState = null
                    },
                    onDragCancel = {
                        dragState = null
                        resizeState = null
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dibujar grid
            drawGrid()
            
            // Dibujar borde del widget (área de trabajo)
            drawWidgetBorder()
            
            // Dibujar componentes
            components.sortedBy { it.zIndex }.forEach { component ->
                drawInteractiveComponent(
                    component = component,
                    isSelected = component.id == selectedComponentId,
                    isDragging = component.id == dragState?.componentId,
                    isResizing = component.id == resizeState?.componentId
                )
            }
        }
    }
}

private data class DragState(
    val componentId: String,
    val startOffset: Offset,
    val startPosition: ParcelablePosition
)

private data class ResizeState(
    val componentId: String,
    val startOffset: Offset,
    val startSize: ParcelableSize
)

private fun findComponentAtPosition(
    position: Offset,
    components: List<WidgetComponent>
): WidgetComponent? {
    return components.findLast { component -> // findLast para obtener el de mayor zIndex
        position.x >= component.position.x &&
        position.x <= component.position.x + component.size.width &&
        position.y >= component.position.y &&
        position.y <= component.position.y + component.size.height
    }
}

private fun isOnComponentEdge(position: Offset, component: WidgetComponent): Boolean {
    val edgeThreshold = 16f // Pixeles desde el borde
    
    val isNearRightEdge = position.x >= component.position.x + component.size.width - edgeThreshold &&
                         position.x <= component.position.x + component.size.width + edgeThreshold
    
    val isNearBottomEdge = position.y >= component.position.y + component.size.height - edgeThreshold &&
                          position.y <= component.position.y + component.size.height + edgeThreshold
    
    val isNearLeftEdge = position.x >= component.position.x - edgeThreshold &&
                        position.x <= component.position.x + edgeThreshold
    
    val isNearTopEdge = position.y >= component.position.y - edgeThreshold &&
                       position.y <= component.position.y + edgeThreshold
    
    return isNearRightEdge || isNearBottomEdge || isNearLeftEdge || isNearTopEdge
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrid() {
    val gridSize = 20f
    val gridColor = Color(0x33FFFFFF)
    
    // Líneas verticales
    for (x in 0..size.width.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridColor,
            start = Offset(x.toFloat(), 0f),
            end = Offset(x.toFloat(), size.height),
            strokeWidth = 1f
        )
    }
    
    // Líneas horizontales
    for (y in 0..size.height.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y.toFloat()),
            end = Offset(size.width, y.toFloat()),
            strokeWidth = 1f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWidgetBorder() {
    val borderColor = Color(0x4D6750A4)
    val borderWidth = 2f
    
    // Área de trabajo del widget (simulada)
    val widgetArea = androidx.compose.ui.geometry.Rect(
        left = 50f,
        top = 50f,
        right = 350f,
        bottom = 200f
    )
    
    drawRect(
        color = borderColor,
        style = Stroke(width = borderWidth),
        topLeft = widgetArea.topLeft,
        size = widgetArea.size
    )
    
    // Indicador de tamaño
    drawText(
        text = "Widget Area",
        color = borderColor,
        topLeft = Offset(widgetArea.left + 5f, widgetArea.top + 5f)
    )
}

// Función drawText simplificada
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawText(
    text: String,
    color: Color,
    topLeft: Offset
) {
    // Implementación simplificada - en producción usarías TextLayout
    // Por ahora dibujamos un rectángulo como placeholder
    drawRect(
        color = color,
        topLeft = topLeft,
        size = androidx.compose.ui.geometry.Size(text.length * 6f, 10f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawInteractiveComponent(
    component: WidgetComponent,
    isSelected: Boolean = false,
    isDragging: Boolean = false,
    isResizing: Boolean = false
) {
    val bgColor = when (component.type) {
        "TEXT" -> Color(0x334CAF50)
        "SHAPE" -> Color(0x33FF9800)
        "BUTTON" -> Color(0x33E91E63)
        "ANIMATED_IMAGE", "GIF_BUTTON" -> Color(0x3300BCD4)
        else -> Color(0x336750A4)
    }
    
    val borderColor = when {
        isSelected && isResizing -> Color(0xFFFF9800)
        isSelected && isDragging -> Color(0xFF4CAF50)
        isSelected -> Color(0xFF6750A4)
        else -> Color.Transparent
    }
    
    val componentRect = androidx.compose.ui.geometry.Rect(
        left = component.position.x,
        top = component.position.y,
        right = component.position.x + component.size.width,
        bottom = component.position.y + component.size.height
    )
    
    // Fondo del componente
    drawRect(
        color = bgColor,
        topLeft = componentRect.topLeft,
        size = componentRect.size
    )
    
    // Borde si está seleccionado
    if (isSelected) {
        drawRect(
            color = borderColor,
            style = Stroke(width = 2f),
            topLeft = componentRect.topLeft,
            size = componentRect.size
        )
        
        // Marcadores de redimensionamiento en las esquinas
        if (isResizing) {
            val handleSize = 8f
            val cornerColor = Color(0xFFFF9800)
            
            // Esquina inferior derecha
            drawCircle(
                color = cornerColor,
                center = Offset(componentRect.right, componentRect.bottom),
                radius = handleSize
            )
            
            // Esquina superior derecha
            drawCircle(
                color = cornerColor,
                center = Offset(componentRect.right, componentRect.top),
                radius = handleSize
            )
            
            // Esquina inferior izquierda
            drawCircle(
                color = cornerColor,
                center = Offset(componentRect.left, componentRect.bottom),
                radius = handleSize
            )
            
            // Esquina superior izquierda
            drawCircle(
                color = cornerColor,
                center = componentRect.topLeft,
                radius = handleSize
            )
        }
    }
    
    // Indicador de tipo
    drawText(
        text = component.type,
        color = Color.White.copy(alpha = 0.7f),
        topLeft = Offset(component.position.x + 5f, component.position.y + 5f)
    )
    
    // Indicador de posición/tamaño
    val infoText = "${component.position.x.toInt()},${component.position.y.toInt()}"
    drawText(
        text = infoText,
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(component.position.x + 5f, component.position.y + component.size.height - 15f)
    )
}
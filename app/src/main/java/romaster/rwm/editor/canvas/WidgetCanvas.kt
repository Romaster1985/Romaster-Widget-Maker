package com.romaster.rwm.editor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.romaster.rwm.components.*
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(
    modifier: Modifier = Modifier,
    components: List<WidgetComponent>,
    selectedComponent: WidgetComponent? = null,
    onComponentSelected: (String) -> Unit = {},
    onComponentMoved: (String, Position) -> Unit = { _, _ -> },
    onComponentResized: (String, Size) -> Unit = { _, _ -> }
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var resizingHandle by remember { mutableStateOf<ResizeHandle?>(null) }
    
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                canvasSize = coordinates.size
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val clickedComponent = findComponentAt(offset, components, canvasSize)
                        clickedComponent?.let {
                            onComponentSelected(it.id)
                        }
                    },
                    onLongPress = { offset ->
                        // TODO: Mostrar menú contextual
                    }
                )
            }
    ) {
        // Dibujar grid de fondo
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGrid(canvasSize)
        }
        
        // Dibujar componentes
        components.sortedBy { it.zIndex }.forEach { component ->
            ComponentRenderer(
                component = component,
                isSelected = component.id == selectedComponent?.id,
                canvasSize = canvasSize,
                onDragStart = { offset ->
                    isDragging = true
                    dragStart = offset
                    onComponentSelected(component.id)
                },
                onDrag = { offset ->
                    if (isDragging) {
                        val newX = component.position.x + (offset.x - dragStart.x)
                        val newY = component.position.y + (offset.y - dragStart.y)
                        onComponentMoved(component.id, Position(newX, newY))
                        dragStart = offset
                    }
                },
                onDragEnd = {
                    isDragging = false
                },
                onResizeStart = { handle ->
                    resizingHandle = handle
                    onComponentSelected(component.id)
                },
                onResize = { delta ->
                    resizingHandle?.let { handle ->
                        val newSize = calculateNewSize(component.size, handle, delta)
                        onComponentResized(component.id, newSize)
                    }
                },
                onResizeEnd = {
                    resizingHandle = null
                }
            )
        }
    }
}

@Composable
fun ComponentRenderer(
    component: WidgetComponent,
    isSelected: Boolean,
    canvasSize: IntSize,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onResizeStart: (ResizeHandle) -> Unit,
    onResize: (Offset) -> Unit,
    onResizeEnd: () -> Unit
) {
    val screenX = (component.position.x * canvasSize.width / 1000f).dp
    val screenY = (component.position.y * canvasSize.height / 1000f).dp
    val screenWidth = (component.size.width * canvasSize.width / 1000f).dp
    val screenHeight = (component.size.height * canvasSize.height / 1000f).dp
    
    Box(
        modifier = Modifier
            .offset(screenX, screenY)
            .size(screenWidth, screenHeight)
            .pointerInput(component.id) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (isOnResizeHandle(offset, component.size)) {
                            val handle = getResizeHandleAt(offset, component.size)
                            onResizeStart(handle)
                        } else {
                            onDragStart(offset)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (isOnResizeHandle(change.position, component.size)) {
                            onResize(Offset(dragAmount.x, dragAmount.y))
                        } else {
                            onDrag(change.position)
                        }
                    },
                    onDragEnd = {
                        if (isOnResizeHandle(Offset.Zero, component.size)) {
                            onResizeEnd()
                        } else {
                            onDragEnd()
                        }
                    }
                )
            }
    ) {
        when (component) {
            is AnimatedImageComponent -> {
                GifRenderer(component)
            }
            is TextComponent -> {
                TextRenderer(component)
            }
            is ShapeComponent -> {
                ShapeRenderer(component)
            }
            is ButtonComponent -> {
                ButtonRenderer(component)
            }
        }
        
        // Borde de selección
        if (isSelected) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = Color.Blue,
                    style = Stroke(width = 2f)
                )
                
                // Dibujar handles de redimensionamiento
                drawResizeHandles()
            }
        }
    }
}

@Composable
fun GifRenderer(component: AnimatedImageComponent) {
    // TODO: Implementar renderizado de GIF con Coil
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    )
}

@Composable
fun TextRenderer(component: TextComponent) {
    androidx.compose.material3.Text(
        text = component.text,
        color = androidx.compose.ui.graphics.Color(
            android.graphics.Color.parseColor(component.color)
        ),
        fontSize = androidx.compose.ui.unit.TextUnit(component.fontSize, androidx.compose.ui.unit.TextUnitType.Sp),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ShapeRenderer(component: ShapeComponent) {
    val color = androidx.compose.ui.graphics.Color(
        android.graphics.Color.parseColor(component.color)
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        when (component.shapeType) {
            ShapeType.RECTANGLE -> {
                drawRoundRect(
                    color = color,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        component.cornerRadius,
                        component.cornerRadius
                    )
                )
            }
            ShapeType.CIRCLE -> {
                drawCircle(color = color)
            }
            ShapeType.TRIANGLE -> {
                drawTriangle(color = color)
            }
        }
    }
}

@Composable
fun ButtonRenderer(component: ButtonComponent) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Color(
                    android.graphics.Color.parseColor(component.backgroundColor)
                ),
                androidx.compose.foundation.shape.RoundedCornerShape(component.cornerRadius.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = component.text,
            color = androidx.compose.ui.graphics.Color(
                android.graphics.Color.parseColor(component.textColor)
            )
        )
    }
}

// Funciones de utilidad
private fun findComponentAt(
    offset: Offset,
    components: List<WidgetComponent>,
    canvasSize: IntSize
): WidgetComponent? {
    val normalizedX = offset.x / canvasSize.width * 1000f
    val normalizedY = offset.y / canvasSize.height * 1000f
    
    return components.find { component ->
        normalizedX >= component.position.x &&
        normalizedX <= component.position.x + component.size.width &&
        normalizedY >= component.position.y &&
        normalizedY <= component.position.y + component.size.height
    }
}

private fun drawGrid(canvasSize: IntSize) {
    // Implementación simplificada del grid
}

private fun isOnResizeHandle(offset: Offset, size: Size): Boolean {
    val handleSize = 16f
    return offset.x <= handleSize || offset.x >= size.width - handleSize ||
           offset.y <= handleSize || offset.y >= size.height - handleSize
}

private fun getResizeHandleAt(offset: Offset, size: Size): ResizeHandle {
    val handleSize = 16f
    
    return when {
        offset.x <= handleSize && offset.y <= handleSize -> ResizeHandle.TOP_LEFT
        offset.x >= size.width - handleSize && offset.y <= handleSize -> ResizeHandle.TOP_RIGHT
        offset.x <= handleSize && offset.y >= size.height - handleSize -> ResizeHandle.BOTTOM_LEFT
        offset.x >= size.width - handleSize && offset.y >= size.height - handleSize -> ResizeHandle.BOTTOM_RIGHT
        offset.x <= handleSize -> ResizeHandle.LEFT
        offset.x >= size.width - handleSize -> ResizeHandle.RIGHT
        offset.y <= handleSize -> ResizeHandle.TOP
        else -> ResizeHandle.BOTTOM
    }
}

private fun calculateNewSize(currentSize: Size, handle: ResizeHandle, delta: Offset): Size {
    var newWidth = currentSize.width
    var newHeight = currentSize.height
    
    when (handle) {
        ResizeHandle.LEFT -> newWidth -= delta.x
        ResizeHandle.RIGHT -> newWidth += delta.x
        ResizeHandle.TOP -> newHeight -= delta.y
        ResizeHandle.BOTTOM -> newHeight += delta.y
        ResizeHandle.TOP_LEFT -> {
            newWidth -= delta.x
            newHeight -= delta.y
        }
        ResizeHandle.TOP_RIGHT -> {
            newWidth += delta.x
            newHeight -= delta.y
        }
        ResizeHandle.BOTTOM_LEFT -> {
            newWidth -= delta.x
            newHeight += delta.y
        }
        ResizeHandle.BOTTOM_RIGHT -> {
            newWidth += delta.x
            newHeight += delta.y
        }
    }
    
    // Limitar tamaño mínimo
    newWidth = newWidth.coerceAtLeast(20f)
    newHeight = newHeight.coerceAtLeast(20f)
    
    return Size(newWidth, newHeight)
}

enum class ResizeHandle {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
    LEFT, RIGHT, TOP, BOTTOM
}
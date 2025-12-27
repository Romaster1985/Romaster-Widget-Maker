package com.romaster.rwm.editor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SimpleCanvas(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0D13))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dibujar grid simple
            val gridSize = 50f
            for (x in 0..size.width.toInt() step gridSize.toInt()) {
                drawLine(
                    color = Color(0x33FFFFFF),
                    start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f),
                    end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..size.height.toInt() step gridSize.toInt()) {
                drawLine(
                    color = Color(0x33FFFFFF),
                    start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()),
                    end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat()),
                    strokeWidth = 1f
                )
            }
            
            // Borde del canvas
            drawRect(
                color = Color(0x4D6750A4),
                style = Stroke(width = 2f)
            )
        }
    }
}
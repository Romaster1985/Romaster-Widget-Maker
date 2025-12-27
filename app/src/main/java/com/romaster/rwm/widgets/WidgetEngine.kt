package com.romaster.rwm.widgets

import android.content.Context
import android.widget.RemoteViews
import com.romaster.rwm.R
import com.romaster.rwm.animation.GifAnimationController
import com.romaster.rwm.components.*
import com.romaster.rwm.projects.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WidgetEngine(private val context: Context) {
    
    private val gifController = GifAnimationController(context)
    
    suspend fun renderWidget(project: Project): RemoteViews = withContext(Dispatchers.IO) {
        return@withContext try {
            // Determinar el layout según el tamaño
            val layoutRes = when (project.blueprint.size) {
                "1x1" -> R.layout.widget_placeholder  // Usar placeholder para 1x1
                "2x1" -> R.layout.widget_2x1
                "2x2" -> R.layout.widget_2x2
                "4x1" -> R.layout.widget_placeholder  // Usar placeholder para 4x1
                "4x2" -> R.layout.widget_placeholder  // Usar placeholder para 4x2
                else -> R.layout.widget_placeholder
            }
            
            val remoteViews = RemoteViews(context.packageName, layoutRes)
            
            // Configurar fondo
            try {
                val color = android.graphics.Color.parseColor(project.blueprint.backgroundColor)
                remoteViews.setInt(R.id.widget_container, "setBackgroundColor", color)
            } catch (e: Exception) {
                // Si no hay widget_container, usar el primer View
                remoteViews.setInt(android.R.id.background, "setBackgroundColor", 
                    android.graphics.Color.parseColor(project.blueprint.backgroundColor))
            }
            
            // Renderizar componentes (simplificado por ahora)
            project.blueprint.components.forEach { component ->
                renderComponent(remoteViews, component)
            }
            
            remoteViews
        } catch (e: Exception) {
            // En caso de error, retornar widget de error
            RemoteViews(context.packageName, R.layout.widget_error)
        }
    }
    
    private fun renderComponent(remoteViews: RemoteViews, component: WidgetComponent) {
        when (component) {
            is AnimatedImageComponent -> renderAnimatedImage(remoteViews, component)
            is TextComponent -> renderText(remoteViews, component)
            is ShapeComponent -> renderShape(remoteViews, component)
            is ButtonComponent -> renderButton(remoteViews, component)
            // Añade estos casos para hacer el when exhaustivo
            else -> {
                // No hacer nada para otros tipos de componentes
            }
        }
    }
    
    private fun renderAnimatedImage(remoteViews: RemoteViews, component: AnimatedImageComponent) {
        // TODO: Implementar renderizado de GIF en widget
        // Por ahora solo mostramos placeholder
        try {
            remoteViews.setImageViewResource(R.id.widget_container, R.drawable.ic_gif_placeholder)
        } catch (e: Exception) {
            // Ignorar si no hay widget_container
        }
    }
    
    private fun renderText(remoteViews: RemoteViews, component: TextComponent) {
        try {
            remoteViews.setTextViewText(R.id.widget_container, component.text)
            remoteViews.setTextColor(R.id.widget_container, 
                android.graphics.Color.parseColor(component.color))
        } catch (e: Exception) {
            // Intentar con otro ID común
            remoteViews.setTextViewText(android.R.id.text1, component.text)
            remoteViews.setTextColor(android.R.id.text1, 
                android.graphics.Color.parseColor(component.color))
        }
    }
    
    private fun renderShape(remoteViews: RemoteViews, component: ShapeComponent) {
        // TODO: Implementar renderizado de formas en widget
        // Por ahora no hacer nada
    }
    
    private fun renderButton(remoteViews: RemoteViews, component: ButtonComponent) {
        // TODO: Implementar renderizado de botones en widget
        // Por ahora no hacer nada
    }
    
    fun cleanup() {
        gifController.releaseAll()
    }
}
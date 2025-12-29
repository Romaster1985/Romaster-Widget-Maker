package com.romaster.rwm.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*
import android.util.Log
import android.widget.RemoteViews
import com.romaster.rwm.R
import com.romaster.rwm.editor.EditorActivity
import com.romaster.rwm.projects.ProjectManager
import com.romaster.rwm.utils.Logger
import java.util.*

class RWMWidgetProvider : AppWidgetProvider() {
    
    companion object {
        private const val TAG = "RWMWidgetProvider"
        const val ACTION_CLICK = "com.romaster.rwm.ACTION_CLICK"
        const val EXTRA_WIDGET_ID = "widget_id"
        const val EXTRA_PROJECT_ID = "project_id"
        
        private const val PREFS_NAME = "widget_configs"
        private const val KEY_WIDGET_COUNTER = "widget_counter"
        
        // Actualizar widget
        fun updateWidget(
            context: Context, 
            appWidgetManager: AppWidgetManager, 
            appWidgetId: Int,
            projectId: String? = null
        ) {
            Logger.debug(TAG, "Actualizando widget $appWidgetId")
            
            try {
                // 1. Obtener tamaño del widget
                val widgetSize = getWidgetSize(context, appWidgetId)
                Logger.debug(TAG, "Widget $appWidgetId es de tamaño: $widgetSize")
                
                // 2. Verificar si el widget está configurado
                val isConfigured = isWidgetConfigured(context, appWidgetId)
                val actualProjectId = projectId ?: getProjectIdForWidget(context, appWidgetId)
                
                val views = if (!isConfigured || actualProjectId == null) {
                    // Widget sin configurar - mostrar placeholder
                    createPlaceholderWidget(context, appWidgetId, widgetSize)
                } else {
                    // Widget configurado - intentar cargar proyecto
                    try {
                        val project = ProjectManager(context).loadProject(actualProjectId)
                        renderConfiguredWidget(context, project, appWidgetId, widgetSize)
                    } catch (e: Exception) {
                        Logger.logException(TAG, e, "Error cargando proyecto")
                        createPlaceholderWidget(context, appWidgetId, widgetSize)
                    }
                }
                
                // 3. Actualizar widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
                
                Logger.debug(TAG, "Widget $appWidgetId actualizado (tamaño: $widgetSize, configurado: $isConfigured)")
                
            } catch (e: Exception) {
                Logger.logException(TAG, e, "updateWidget")
                showErrorWidget(context, appWidgetManager, appWidgetId)
            }
        }
        
        // Obtener tamaño del widget basado en el provider
        private fun getWidgetSize(context: Context, appWidgetId: Int): String {
            return try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val providerInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                
                when (providerInfo.provider.className) {
                    "com.romaster.rwm.widgets.RWMWidgetProvider1x1" -> "1x1"
                    "com.romaster.rwm.widgets.RWMWidgetProvider2x1" -> "2x1"
                    "com.romaster.rwm.widgets.RWMWidgetProvider2x2" -> "2x2"
                    "com.romaster.rwm.widgets.RWMWidgetProvider3x3" -> "3x3"
                    "com.romaster.rwm.widgets.RWMWidgetProvider4x1" -> "4x1"
                    "com.romaster.rwm.widgets.RWMWidgetProvider4x2" -> "4x2"
                    else -> "4x2"
                }
            } catch (e: Exception) {
                Logger.logException(TAG, e, "getWidgetSize")
                "4x2"
            }
        }
        
        // Crear widget placeholder
        private fun createPlaceholderWidget(
            context: Context, 
            widgetId: Int,
            widgetSize: String
        ): RemoteViews {
            Logger.debug(TAG, "Creando placeholder para widget $widgetId (tamaño: $widgetSize)")
            
            val placeholderViews = RemoteViews(context.packageName, R.layout.widget_placeholder)
            
            // Configurar intent para abrir configuración
            val configIntent = Intent(context, WidgetConfigActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                putExtra("widget_size", widgetSize)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                widgetId,
                configIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            placeholderViews.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            return placeholderViews
        }
        
        // Renderizar widget configurado
        private fun renderConfiguredWidget(
            context: Context,
            project: com.romaster.rwm.projects.Project,
            widgetId: Int,
            widgetSize: String
        ): RemoteViews {
            Logger.debug(TAG, "Renderizando widget configurado $widgetId (tamaño del proyecto: ${project.blueprint.size})")
            
            // Determinar layout según tamaño del proyecto
            val layoutRes = when (project.blueprint.size) {
                "1x1" -> R.layout.widget_1x1
                "2x1" -> R.layout.widget_2x1
                "2x2" -> R.layout.widget_2x2
                "3x3" -> R.layout.widget_3x3
                "4x1" -> R.layout.widget_4x1
                "4x2" -> R.layout.widget_4x2
                else -> R.layout.widget_placeholder
            }
            
            val views = RemoteViews(context.packageName, layoutRes)
            
            // Mostrar nombre del proyecto
            val displayText = "${project.name}\n(${project.blueprint.size})"
            views.setTextViewText(R.id.widget_container, displayText)
            
            // Configurar para abrir editor al tocar
            val editIntent = Intent(context, EditorActivity::class.java).apply {
                putExtra("extra_project_id", project.id)
                putExtra("extra_widget_id", widgetId)
                putExtra("extra_project_name", project.name)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                widgetId,
                editIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            return views
        }
        
        // Mostrar widget de error
        private fun showErrorWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                val errorViews = RemoteViews(context.packageName, R.layout.widget_error)
                
                // Configurar intent para abrir la app principal
                val intent = Intent(context, com.romaster.rwm.MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                errorViews.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                
                appWidgetManager.updateAppWidget(appWidgetId, errorViews)
            } catch (e: Exception) {
                Log.e(TAG, "Error incluso al mostrar widget de error", e)
            }
        }
        
        // Funciones de gestión de estado
        fun saveWidgetMapping(context: Context, appWidgetId: Int, projectId: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("widget_${appWidgetId}_project", projectId)
                putBoolean("widget_${appWidgetId}_configured", true)
            }.apply()
        }
        
        fun getProjectIdForWidget(context: Context, appWidgetId: Int): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString("widget_${appWidgetId}_project", null)
        }
        
        fun isWidgetConfigured(context: Context, appWidgetId: Int): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean("widget_${appWidgetId}_configured", false)
        }
        
        fun generateUniqueWidgetId(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val counter = prefs.getInt(KEY_WIDGET_COUNTER, 0) + 1
            prefs.edit().putInt(KEY_WIDGET_COUNTER, counter).apply()
            
            val timestamp = System.currentTimeMillis()
            val random = (Math.random() * 10000).toInt()
            
            return "w_${timestamp}_${counter}_${random}"
        }
        fun forceUpdateWidget(context: Context, appWidgetId: Int, projectId: String? = null) {
            Logger.debug(TAG, "FORZANDO actualización del widget $appWidgetId")
            
            val appWidgetManager = AppWidgetManager.getInstance(context)
            
            // Marcar como configurado
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean("widget_${appWidgetId}_configured", true).apply()
            
            if (projectId != null) {
                saveWidgetMapping(context, appWidgetId, projectId)
            }
            
            // Actualizar inmediatamente
            updateWidget(context, appWidgetManager, appWidgetId, projectId)
            
            // También forzar actualización de AppWidgetManager
            val ids = intArrayOf(appWidgetId)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_container)
            
            Logger.debug(TAG, "Widget $appWidgetId forzado a actualizar")
        }
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Logger.debug(TAG, "onUpdate llamado para ${appWidgetIds.size} widgets")
        
        appWidgetIds.forEach { widgetId ->
            updateWidget(context, appWidgetManager, widgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Logger.info(TAG, "Widget habilitado")
    }
    
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Logger.info(TAG, "Widget deshabilitado")
    }
    
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        appWidgetIds.forEach { widgetId ->
            editor.remove("widget_${widgetId}_project")
            editor.remove("widget_${widgetId}_configured")
        }
        
        editor.apply()
        Logger.info(TAG, "${appWidgetIds.size} widgets eliminados")
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        Logger.debug(TAG, "onReceive acción: ${intent.action}")
        
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                // Manejar actualizaciones
            }
            ACTION_CLICK -> {
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
                if (widgetId != -1) {
                    Logger.debug(TAG, "Widget $widgetId clickeado")
                }
            }
        }
    }
}
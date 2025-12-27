package com.romaster.rwm.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.romaster.rwm.MainActivity
import com.romaster.rwm.R
import com.romaster.rwm.projects.ProjectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RWMWidgetProvider : AppWidgetProvider() {
    
    companion object {
        const val ACTION_REFRESH = "com.romaster.rwm.ACTION_REFRESH"
        const val ACTION_CLICK = "com.romaster.rwm.ACTION_CLICK"
        const val EXTRA_WIDGET_ID = "widget_id"
        const val EXTRA_COMPONENT_ID = "component_id"
        
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val widgetEngine = WidgetEngine(context)
                    val projectManager = ProjectManager(context)
                    
                    // Obtener configuración del widget
                    val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
                    val projectId = prefs.getString("widget_${appWidgetId}_project", null)
                    
                    if (projectId != null) {
                        val project = projectManager.loadProject(projectId)
                        val views = widgetEngine.renderWidget(project)
                        
                        // Configurar intent para abrir la app al hacer clic
                        val intent = Intent(context, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        
                        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                        
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    } else {
                        // Widget sin configurar - mostrar placeholder
                        val views = RemoteViews(context.packageName, R.layout.rwm_widget_placeholder)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    // En caso de error, mostrar widget de error
                    val views = RemoteViews(context.packageName, R.layout.rwm_widget_error)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
        
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, RWMWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            appWidgetIds.forEach { widgetId ->
                updateWidget(context, appWidgetManager, widgetId)
            }
        }
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { widgetId ->
            updateWidget(context, appWidgetManager, widgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Cuando se habilita el primer widget
        super.onEnabled(context)
    }
    
    override fun onDisabled(context: Context) {
        // Cuando se deshabilita el último widget
        super.onDisabled(context)
    }
    
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // Eliminar preferencias de widgets eliminados
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        appWidgetIds.forEach { widgetId ->
            editor.remove("widget_${widgetId}_project")
        }
        editor.apply()
        
        super.onDeleted(context, appWidgetIds)
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_REFRESH -> {
                val appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
                if (appWidgetId != -1) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateWidget(context, appWidgetManager, appWidgetId)
                }
            }
            ACTION_CLICK -> {
                // Manejar clics en componentes del widget
                val appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
                val componentId = intent.getStringExtra(EXTRA_COMPONENT_ID)
                
                // TODO: Ejecutar acción del componente
            }
        }
    }
}
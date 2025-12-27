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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RWMWidgetProvider : AppWidgetProvider() {
    
    companion object {
        const val ACTION_REFRESH = "com.romaster.rwm.ACTION_REFRESH"
        const val ACTION_CLICK = "com.romaster.rwm.ACTION_CLICK"
        const val EXTRA_WIDGET_ID = "widget_id"
        
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Por ahora usamos un widget simple
                    val views = RemoteViews(context.packageName, R.layout.widget_placeholder)
                    
                    // Configurar intent para abrir la app
                    val intent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        appWidgetId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    // Usa el ID correcto del layout widget_placeholder.xml
                    views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                    
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    // En caso de error
                    val errorViews = RemoteViews(context.packageName, R.layout.widget_error)
                    appWidgetManager.updateAppWidget(appWidgetId, errorViews)
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
        super.onEnabled(context)
    }
    
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }
    
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
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
        }
    }
}
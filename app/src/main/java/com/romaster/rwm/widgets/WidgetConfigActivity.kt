package com.romaster.rwm.widgets

import android.content.Context
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.romaster.rwm.R
import com.romaster.rwm.editor.EditorActivity
import com.romaster.rwm.projects.ProjectManager
import com.romaster.rwm.utils.Logger

class WidgetConfigActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "WidgetConfigActivity"
    }
    
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Logger.info(TAG, "Configuración de widget iniciada")
        
        // Configurar resultado por defecto (cancelado)
        setResult(RESULT_CANCELED)
        
        // Obtener ID del widget
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        
        // Si el ID es inválido, terminar
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Logger.error(TAG, "ID de widget inválido")
            finish()
            return
        }
        
        Logger.debug(TAG, "Configurando widget ID: $appWidgetId")
        
        // Obtener tamaño del widget
        val widgetSize = intent.getStringExtra("widget_size") ?: determineWidgetSize()
        Logger.debug(TAG, "Tamaño del widget: $widgetSize")
        
        // Mostrar diálogo para configurar nombre
        showWidgetNameDialog(widgetSize)
    }
    
    private fun determineWidgetSize(): String {
        // Intentar determinar el tamaño basado en el provider
        return try {
            val appWidgetManager = AppWidgetManager.getInstance(this)
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
            Logger.logException(TAG, e, "determineWidgetSize")
            "4x2"
        }
    }
    
    private fun showWidgetNameDialog(widgetSize: String) {
        val textInputLayout = TextInputLayout(this).apply {
            hint = "Nombre del widget"
            setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
            
            val editText = TextInputEditText(context).apply {
                id = android.R.id.text1
                setText("Mi Widget $appWidgetId")
            }
            
            addView(editText)
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Configurar Widget")
            .setMessage("Ingresa un nombre para tu widget ($widgetSize):")
            .setView(textInputLayout)
            .setPositiveButton("Crear") { dialog, _ ->
                try {
                    val input = textInputLayout.findViewById<TextInputEditText>(android.R.id.text1)
                    val widgetName = input?.text?.toString()?.trim() ?: "Mi Widget"
                    
                    if (widgetName.isNotEmpty()) {
                        createWidgetProject(widgetName, widgetSize)
                    } else {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Logger.logException(TAG, e, "showWidgetNameDialog")
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }
    
    private fun createWidgetProject(widgetName: String, widgetSize: String) {
        try {
            val projectManager = ProjectManager(this)
            
            Logger.info(TAG, "Creando proyecto: $widgetName, tamaño: $widgetSize")
            val project = projectManager.createProject(widgetName, widgetSize)
            
            // Guardar mapping
            saveWidgetMapping(project.id, widgetSize)
            
            // Actualizar widget inmediatamente
            val appWidgetManager = AppWidgetManager.getInstance(this)
            RWMWidgetProvider.updateWidget(this, appWidgetManager, appWidgetId, project.id)
            
            // Configurar resultado exitoso
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            
            Logger.info(TAG, "Widget $appWidgetId configurado exitosamente")
            
            // Abrir el editor inmediatamente
            openEditor(project.id, project.name)
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "createWidgetProject")
            Toast.makeText(this, "Error al crear widget: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun saveWidgetMapping(projectId: String, widgetSize: String) {
        // Guardar en SharedPreferences local
        val prefs = getSharedPreferences("widgets", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("widget_${appWidgetId}_project", projectId)
            putString("widget_${appWidgetId}_size", widgetSize)
            putBoolean("widget_${appWidgetId}_configured", true)
        }.apply()
        
        // También guardar en el sistema global
        RWMWidgetProvider.saveWidgetMapping(this, appWidgetId, projectId)
    }
    
    private fun openEditor(projectId: String, projectName: String) {
        try {
            Logger.info(TAG, "Abriendo editor para proyecto: $projectName")
            val intent = Intent(this, EditorActivity::class.java).apply {
                putExtra("extra_project_id", projectId)
                putExtra("extra_widget_id", appWidgetId)
                putExtra("extra_project_name", projectName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Logger.logException(TAG, e, "openEditor")
            Toast.makeText(this, "No se pudo abrir el editor", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
    }
}
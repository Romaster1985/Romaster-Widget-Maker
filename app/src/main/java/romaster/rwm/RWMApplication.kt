package com.romaster.rwm

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.romaster.rwm.projects.ProjectManager

class RWMApplication : Application() {
    
    companion object {
        lateinit var instance: RWMApplication
            private set
        
        fun getAppContext(): Context = instance.applicationContext
    }
    
    val projectManager: ProjectManager by lazy {
        ProjectManager(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Forzar modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        
        // Inicializar componentes
        initializeApp()
    }
    
    private fun initializeApp() {
        // Crear directorios necesarios
        val baseDir = getExternalFilesDir(null)
        listOf("RWM/Projects", "RWM/Exports", "RWM/Backups").forEach { dir ->
            File(baseDir, dir).mkdirs()
        }
        
        // TODO: Inicializar más componentes aquí
    }
    
    override fun onTerminate() {
        // Limpiar recursos
        cleanup()
        super.onTerminate()
    }
    
    private fun cleanup() {
        // TODO: Limpiar caché y recursos
    }
}
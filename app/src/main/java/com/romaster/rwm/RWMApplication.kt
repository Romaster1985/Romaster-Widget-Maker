package com.romaster.rwm

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.romaster.rwm.data.DataRepository
import com.romaster.rwm.projects.ProjectManager
import java.io.File

class RWMApplication : Application() {
    
    companion object {
        lateinit var instance: RWMApplication
            private set
        
        fun getAppContext(): Context = instance.applicationContext
    }
    
    lateinit var projectManager: ProjectManager
    lateinit var dataRepository: DataRepository
    
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
        
        // Inicializar managers y repositorios
        projectManager = ProjectManager(this)
        dataRepository = DataRepository(this)
        
        // Registrar datos iniciales
        setupInitialData()
    }
    
    private fun setupInitialData() {
        // Ejemplo: Registrar fuente de datos personalizada para contador
        dataRepository.registerCustomDataSource("counter") {
            // Esto es solo un ejemplo, en realidad se actualizaría
            0
        }
    }
    
    override fun onTerminate() {
        // Limpiar recursos
        cleanup()
        super.onTerminate()
    }
    
    private fun cleanup() {
        dataRepository.cleanup()
    }
}
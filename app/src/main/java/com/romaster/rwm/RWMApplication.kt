package com.romaster.rwm

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.romaster.rwm.data.DataRepository
import com.romaster.rwm.projects.ProjectManager
import com.romaster.rwm.utils.Logger
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
        
        // Configurar handler global de excepciones
        Logger.setGlobalExceptionHandler()
        
        // Inicializar logger
        Logger.initialize(this)
        Logger.info("RWMApplication", "Application onCreate iniciado")
        
        try {
            // Forzar modo oscuro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            
            // Inicializar componentes
            initializeApp()
            
            Logger.info("RWMApplication", "Application onCreate completado exitosamente")
        } catch (e: Exception) {
            Logger.logException("RWMApplication", e, "onCreate")
        }
    }
    
    private fun initializeApp() {
        try {
            Logger.info("RWMApplication", "Inicializando directorios...")
            
            // Crear directorios necesarios
            val baseDir = getExternalFilesDir(null)
            if (baseDir == null) {
                Logger.error("RWMApplication", "No se pudo obtener external files dir")
                return
            }
            
            listOf("RWM/Projects", "RWM/Exports", "RWM/Backups").forEach { dir ->
                val directory = File(baseDir, dir)
                if (!directory.exists()) {
                    val created = directory.mkdirs()
                    Logger.info("RWMApplication", "Directorio $dir creado: $created")
                }
            }
            
            Logger.info("RWMApplication", "Inicializando managers...")
            
            // Inicializar managers y repositorios
            projectManager = ProjectManager(this)
            dataRepository = DataRepository(this)
            
            // Registrar datos iniciales
            setupInitialData()
            
            Logger.info("RWMApplication", "Inicialización completada exitosamente")
        } catch (e: Exception) {
            Logger.logException("RWMApplication", e, "initializeApp")
        }
    }
    
    private fun setupInitialData() {
        try {
            // Ejemplo: Registrar fuente de datos personalizada para contador
            dataRepository.registerCustomDataSource("counter") {
                0
            }
            Logger.debug("RWMApplication", "Fuente de datos 'counter' registrada")
        } catch (e: Exception) {
            Logger.logException("RWMApplication", e, "setupInitialData")
        }
    }
    
    override fun onTerminate() {
        Logger.info("RWMApplication", "Application onTerminate")
        try {
            cleanup()
        } catch (e: Exception) {
            Logger.logException("RWMApplication", e, "onTerminate")
        }
        super.onTerminate()
    }
    
    private fun cleanup() {
        try {
            dataRepository.cleanup()
            Logger.info("RWMApplication", "Recursos limpiados")
        } catch (e: Exception) {
            Logger.logException("RWMApplication", e, "cleanup")
        }
    }
}
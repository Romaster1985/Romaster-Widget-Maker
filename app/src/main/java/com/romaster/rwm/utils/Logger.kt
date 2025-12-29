package com.romaster.rwm.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object Logger {
    
    private const val TAG = "RWMLogger"
    private const val LOG_DIR = "RWM/Logs"
    private const val LOG_FILE = "rwm_log.txt"
    private const val MAX_LOG_SIZE = 1024 * 1024 * 5 // 5MB
    private const val MAX_LOG_FILES = 10
    
    private var isInitialized = false
    private var logFile: File? = null
    private val lock = ReentrantLock()
    
    // Niveles de log
    enum class Level {
        DEBUG, INFO, WARN, ERROR, FATAL
    }
    
    fun initialize(context: Context) {
        lock.withLock {
            if (isInitialized) return
            
            try {
                // Crear directorio de logs
                val logDir = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), LOG_DIR)
                } else {
                    File(context.getExternalFilesDir(null), LOG_DIR)
                }
                
                if (!logDir.exists()) {
                    logDir.mkdirs()
                    logToConsole(Level.INFO, "Directorio de logs creado: ${logDir.absolutePath}")
                }
                
                logFile = File(logDir, LOG_FILE)
                
                // Rotar logs si es necesario
                rotateLogsIfNeeded(logDir)
                
                // Escribir encabezado
                writeToFile("=".repeat(80))
                writeToFile("RWM Log - Iniciado: ${getCurrentDateTime()}")
                writeToFile("App Version: ${getAppVersion(context)}")
                writeToFile("Device: ${android.os.Build.MODEL} (API ${android.os.Build.VERSION.SDK_INT})")
                writeToFile("=".repeat(80))
                
                isInitialized = true
                logToConsole(Level.INFO, "Logger inicializado exitosamente")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error al inicializar Logger: ${e.message}")
            }
        }
    }
    
    private fun rotateLogsIfNeeded(logDir: File) {
        try {
            val currentLog = File(logDir, LOG_FILE)
            
            // Si el archivo actual es muy grande, rotarlo
            if (currentLog.exists() && currentLog.length() > MAX_LOG_SIZE) {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val rotatedFile = File(logDir, "rwm_log_$timestamp.txt")
                currentLog.renameTo(rotatedFile)
                
                // Limitar número de archivos de log
                val logFiles = logDir.listFiles { file -> 
                    file.name.startsWith("rwm_log_") && file.name.endsWith(".txt")
                }?.sortedByDescending { it.lastModified() }
                
                logFiles?.let { files ->
                    if (files.size > MAX_LOG_FILES) {
                        files.subList(MAX_LOG_FILES, files.size).forEach { it.delete() }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al rotar logs: ${e.message}")
        }
    }
    
    fun debug(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.DEBUG, tag, message, throwable)
    }
    
    fun info(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.INFO, tag, message, throwable)
    }
    
    fun warn(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.WARN, tag, message, throwable)
    }
    
    fun error(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.ERROR, tag, message, throwable)
    }
    
    fun fatal(tag: String, message: String, throwable: Throwable? = null) {
        log(Level.FATAL, tag, message, throwable)
        // En FATAL, también escribimos a logcat
        Log.e(tag, "FATAL: $message", throwable)
    }
    
    fun logException(tag: String, throwable: Throwable, context: String = "") {
        error(tag, "Excepción${if (context.isNotEmpty()) " en $context" else ""}: ${throwable.message}", throwable)
    }
    
    private fun log(level: Level, tag: String, message: String, throwable: Throwable? = null) {
        // Siempre a logcat
        logToConsole(level, "$tag: $message", throwable)
        
        // Y a archivo si está inicializado
        if (isInitialized) {
            writeLogToFile(level, tag, message, throwable)
        }
    }
    
    private fun logToConsole(level: Level, message: String, throwable: Throwable? = null) {
        when (level) {
            Level.DEBUG -> Log.d(TAG, message, throwable)
            Level.INFO -> Log.i(TAG, message, throwable)
            Level.WARN -> Log.w(TAG, message, throwable)
            Level.ERROR -> Log.e(TAG, message, throwable)
            Level.FATAL -> Log.e(TAG, "FATAL: $message", throwable)
        }
    }
    
    private fun writeLogToFile(level: Level, tag: String, message: String, throwable: Throwable?) {
        lock.withLock {
            try {
                val logEntry = buildString {
                    append("[${getCurrentDateTime()}]")
                    append(" [${level.name}]")
                    append(" [$tag]")
                    append(" $message")
                    
                    throwable?.let {
                        append("\n")
                        append(getStackTrace(it))
                    }
                }
                
                writeToFile(logEntry)
            } catch (e: Exception) {
                Log.e(TAG, "Error al escribir en archivo de log: ${e.message}")
            }
        }
    }
    
    private fun writeToFile(text: String) {
        try {
            logFile?.let { file ->
                FileOutputStream(file, true).use { fos ->
                    fos.write("$text\n".toByteArray())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error escribiendo en archivo: ${e.message}")
        }
    }
    
    fun getLogFile(): File? = logFile
    
    fun getLogFilePath(): String? = logFile?.absolutePath
    
    fun clearLogs() {
        lock.withLock {
            try {
                logFile?.delete()
                isInitialized = false
            } catch (e: Exception) {
                Log.e(TAG, "Error al limpiar logs: ${e.message}")
            }
        }
    }
    
    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
    }
    
    private fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }
    
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Desconocida"
        }
    }
    
    // Para capturar excepciones no controladas
    fun setGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            fatal("UncaughtException", "Excepción no controlada en hilo: ${thread.name}", throwable)
            
            // Ejecutar el handler por defecto
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
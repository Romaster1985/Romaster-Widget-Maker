package com.romaster.rwm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.romaster.rwm.utils.Logger
import java.io.File

class LogViewerActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)
        
        val textView = findViewById<TextView>(R.id.logTextView)
        
        try {
            val logFile = Logger.getLogFile()
            if (logFile != null && logFile.exists()) {
                val logContent = logFile.readText()
                textView.text = logContent
            } else {
                textView.text = "Archivo de log no encontrado\nRuta: ${Logger.getLogFilePath()}"
            }
        } catch (e: Exception) {
            textView.text = "Error al leer logs: ${e.message}"
        }
    }
}
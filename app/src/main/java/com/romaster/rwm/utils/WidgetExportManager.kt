package com.romaster.rwm.utils

import android.content.Context
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.*

class WidgetExportManager(private val context: Context) {
    
    fun exportWidgetToZip(projectName: String, componentsJson: String): File {
        val exportDir = File(context.getExternalFilesDir(null), "RWM/Exports")
        exportDir.mkdirs()
        
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val zipFile = File(exportDir, "${projectName}_$timestamp.zip")
        
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            // 1. Manifest
            val manifest = """
                {
                    "version": "1.0",
                    "name": "$projectName",
                    "export_date": "$timestamp",
                    "components": "widget_config.json"
                }
            """.trimIndent()
            
            addToZip(zipOut, "manifest.json", manifest)
            
            // 2. Configuración
            addToZip(zipOut, "widget_config.json", componentsJson)
            
            // 3. Recursos (si los hubiera)
            val resourcesDir = File(context.getExternalFilesDir(null), "RWM/Resources")
            if (resourcesDir.exists()) {
                addDirectoryToZip(zipOut, resourcesDir, "resources/")
            }
        }
        
        return zipFile
    }
    
    private fun addToZip(zipOut: ZipOutputStream, fileName: String, content: String) {
        val entry = ZipEntry(fileName)
        zipOut.putNextEntry(entry)
        zipOut.write(content.toByteArray())
        zipOut.closeEntry()
    }
    
    private fun addDirectoryToZip(zipOut: ZipOutputStream, directory: File, basePath: String) {
        directory.listFiles()?.forEach { file ->
            val entryName = "$basePath/${file.name}"
            if (file.isDirectory) {
                addDirectoryToZip(zipOut, file, entryName)
            } else {
                val entry = ZipEntry(entryName)
                zipOut.putNextEntry(entry)
                FileInputStream(file).use { fis ->
                    fis.copyTo(zipOut)
                }
                zipOut.closeEntry()
            }
        }
    }
}
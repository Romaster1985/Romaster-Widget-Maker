package com.romaster.rwm.utils

import android.content.Context
import com.romaster.rwm.projects.Project
import com.romaster.rwm.projects.JsonUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.*

class WidgetExporter(private val context: Context) {
    
    companion object {
        private const val TAG = "WidgetExporter"
        private const val WIDGET_EXTENSION = ".rwmw"
        private const val MANIFEST_FILE = "manifest.json"
        private const val PROJECT_FILE = "project.rwm"
        private const val RESOURCES_DIR = "Resources"
        private const val METADATA_DIR = "Metadata"
    }
    
    // Exportar widget a ZIP
    fun exportWidget(project: Project): File {
        return try {
            val exportDir = File(context.getExternalFilesDir(null), "RWM/Exports").apply {
                mkdirs()
            }
            
            val widgetName = sanitizeFileName(project.name)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val zipFileName = "${widgetName}_${timestamp}$WIDGET_EXTENSION"
            val zipFile = File(exportDir, zipFileName)
            
            ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                // 1. Agregar manifest.json
                val manifest = createManifest(project)
                addStringToZip(zipOut, MANIFEST_FILE, manifest)
                
                // 2. Agregar project.rwm (serializado)
                val projectJson = project.toJson()
                addStringToZip(zipOut, PROJECT_FILE, projectJson)
                
                // 3. Agregar recursos si existen
                val projectDir = File(project.path)
                if (projectDir.exists() && projectDir.isDirectory) {
                    // Agregar carpeta Resources
                    val resourcesDir = File(projectDir, RESOURCES_DIR)
                    if (resourcesDir.exists()) {
                        addDirectoryToZip(zipOut, resourcesDir, RESOURCES_DIR)
                    }
                    
                    // Agregar preview si existe
                    val previewFile = File(projectDir, "preview.png")
                    if (previewFile.exists()) {
                        addFileToZip(zipOut, previewFile, "preview.png")
                    }
                }
                
                // 4. Agregar metadata
                val metadata = createMetadata(project)
                addStringToZip(zipOut, "$METADATA_DIR/metadata.json", metadata)
            }
            
            Logger.info(TAG, "Widget exportado: ${zipFile.absolutePath}")
            zipFile
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "exportWidget")
            throw e
        }
    }
    
    // Importar widget desde ZIP
    fun importWidget(zipFile: File): Project {
        return try {
            val tempDir = File(context.cacheDir, "import_${System.currentTimeMillis()}")
            tempDir.mkdirs()
            
            // Extraer ZIP
            ZipFile(zipFile).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val entryFile = File(tempDir, entry.name)
                    
                    if (entry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        entryFile.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            FileOutputStream(entryFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
            
            // Leer manifest para verificar
            val manifestFile = File(tempDir, MANIFEST_FILE)
            if (!manifestFile.exists()) {
                throw IOException("Archivo manifest no encontrado")
            }
            
            // Leer proyecto
            val projectFile = File(tempDir, PROJECT_FILE)
            if (!projectFile.exists()) {
                throw IOException("Archivo de proyecto no encontrado")
            }
            
            val projectJson = projectFile.readText()
            val project = JsonUtils.decodeFromString(projectJson)
            
            // Crear directorio real del proyecto
            val projectManager = com.romaster.rwm.projects.ProjectManager(context)
            val actualProjectDir = projectManager.createProjectDirectory(project)
            
            // Copiar recursos
            val resourcesDir = File(tempDir, RESOURCES_DIR)
            if (resourcesDir.exists()) {
                copyDirectory(resourcesDir, File(actualProjectDir, RESOURCES_DIR))
            }
            
            // Actualizar path del proyecto
            val updatedProject = project.copy(path = actualProjectDir.absolutePath)
            
            // Guardar proyecto
            val updatedJson = updatedProject.toJson()
            File(actualProjectDir, PROJECT_FILE).writeText(updatedJson)
            
            Logger.info(TAG, "Widget importado: ${updatedProject.name}")
            updatedProject
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "importWidget")
            throw e
        }
    }
    
    private fun createManifest(project: Project): String {
        return """
            {
                "version": "1.0",
                "format": "RWM_WIDGET",
                "name": "${escapeJson(project.name)}",
                "id": "${project.id}",
                "size": "${project.blueprint.size}",
                "created": ${project.createdAt},
                "modified": ${project.lastModified},
                "components": ${project.blueprint.components.size},
                "requires": {
                    "min_version": "1.0.0",
                    "app": "Romaster Widget Maker"
                }
            }
        """.trimIndent()
    }
    
    private fun createMetadata(project: Project): String {
        return """
            {
                "export_date": ${System.currentTimeMillis()},
                "export_version": "1.0",
                "original_path": "${escapeJson(project.path)}",
                "widget_id": "${project.id}",
                "components": [
                    ${project.blueprint.components.joinToString { "\"${it.type}\"" }}
                ]
            }
        """.trimIndent()
    }
    
    private fun addStringToZip(zipOut: ZipOutputStream, fileName: String, content: String) {
        val entry = ZipEntry(fileName)
        zipOut.putNextEntry(entry)
        zipOut.write(content.toByteArray())
        zipOut.closeEntry()
    }
    
    private fun addFileToZip(zipOut: ZipOutputStream, file: File, entryName: String) {
        val entry = ZipEntry(entryName)
        zipOut.putNextEntry(entry)
        FileInputStream(file).use { fis ->
            fis.copyTo(zipOut)
        }
        zipOut.closeEntry()
    }
    
    private fun addDirectoryToZip(zipOut: ZipOutputStream, directory: File, basePath: String) {
        directory.listFiles()?.forEach { file ->
            val entryName = "$basePath/${file.name}"
            if (file.isDirectory) {
                addDirectoryToZip(zipOut, file, entryName)
            } else {
                addFileToZip(zipOut, file, entryName)
            }
        }
    }
    
    private fun copyDirectory(source: File, target: File) {
        if (!target.exists()) {
            target.mkdirs()
        }
        
        source.listFiles()?.forEach { file ->
            val targetFile = File(target, file.name)
            if (file.isDirectory) {
                copyDirectory(file, targetFile)
            } else {
                file.copyTo(targetFile, overwrite = true)
            }
        }
    }
    
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
    
    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
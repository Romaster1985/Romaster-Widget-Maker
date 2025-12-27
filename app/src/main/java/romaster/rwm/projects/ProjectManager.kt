package com.romaster.rwm.projects

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class ProjectManager(private val context: Context) {
    
    companion object {
        private const val TAG = "ProjectManager"
        private const val PROJECTS_DIR = "RWM/Projects"
        private const val PROJECT_FILE = "project.rwm"
        private const val PREVIEW_FILE = "preview.png"
        private const val RESOURCES_DIR = "Resources"
    }
    
    private val baseDir: File by lazy {
        File(context.getExternalFilesDir(null), PROJECTS_DIR).apply {
            if (!exists()) mkdirs()
        }
    }
    
    // Crear nuevo proyecto
    fun createProject(name: String, size: String): Project {
        val project = Project.createNew(name, size)
        val projectDir = createProjectDirectory(project)
        
        project.path = projectDir.absolutePath
        saveProject(project)
        
        Log.d(TAG, "Proyecto creado: ${project.name} en ${project.path}")
        return project
    }
    
    // Guardar proyecto
    fun saveProject(project: Project): Boolean {
        return try {
            val projectFile = File(project.path, PROJECT_FILE)
            project.lastModified = System.currentTimeMillis()
            
            val json = project.toJson()
            projectFile.writeText(json)
            
            Log.d(TAG, "Proyecto guardado: ${project.name}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar proyecto: ${e.message}")
            false
        }
    }
    
    // Cargar proyecto por ID
    fun loadProject(projectId: String): Project {
        val projectDir = File(baseDir, projectId)
        return loadProjectFromDirectory(projectDir)
    }
    
    // Cargar proyecto desde directorio
    private fun loadProjectFromDirectory(projectDir: File): Project {
        val projectFile = File(projectDir, PROJECT_FILE)
        if (!projectFile.exists()) {
            throw IOException("Archivo de proyecto no encontrado")
        }
        
        val json = projectFile.readText()
        val project = Project.fromJson<Project>(json)
        
        // Actualizar path si es necesario
        if (project.path != projectDir.absolutePath) {
            val updatedProject = project.copy(path = projectDir.absolutePath)
            saveProject(updatedProject)
            return updatedProject
        }
        
        return project
    }
    
    // Obtener proyectos recientes
    fun getRecentProjects(limit: Int = 10): List<Project> {
        return try {
            baseDir.listFiles { file ->
                file.isDirectory && File(file, PROJECT_FILE).exists()
            }?.map { dir ->
                try {
                    loadProjectFromDirectory(dir)
                } catch (e: Exception) {
                    null
                }
            }?.filterNotNull()
                ?.sortedByDescending { it.lastModified }
                ?.take(limit) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error al listar proyectos: ${e.message}")
            emptyList()
        }
    }
    
    // Importar proyecto desde URI
    fun importProject(uri: Uri): Project {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("No se pudo abrir el archivo")
            
            // Crear directorio temporal
            val tempDir = File(context.cacheDir, "import_${System.currentTimeMillis()}")
            tempDir.mkdirs()
            
            // Extraer ZIP (simplificado - asumimos que es un .rwmpack)
            // TODO: Implementar extracción ZIP real
            
            val projectJson = inputStream.bufferedReader().use { it.readText() }
            val project = Project.fromJson<Project>(projectJson)
            
            // Crear directorio final
            val projectDir = createProjectDirectory(project)
            project.path = projectDir.absolutePath
            
            // Guardar
            saveProject(project)
            
            Log.d(TAG, "Proyecto importado: ${project.name}")
            project
        } catch (e: Exception) {
            Log.e(TAG, "Error al importar proyecto: ${e.message}")
            throw e
        }
    }
    
    // Exportar proyecto
    fun exportProject(project: Project): File {
        return try {
            val exportDir = File(context.getExternalFilesDir(null), "RWM/Exports").apply {
                mkdirs()
            }
            
            val exportFile = File(exportDir, "${project.name}_${System.currentTimeMillis()}.rwmpack")
            
            // Crear ZIP con proyecto y recursos
            // TODO: Implementar creación ZIP completa
            
            // Por ahora solo guardamos el JSON
            exportFile.writeText(project.toJson())
            
            Log.d(TAG, "Proyecto exportado: ${exportFile.absolutePath}")
            exportFile
        } catch (e: Exception) {
            Log.e(TAG, "Error al exportar proyecto: ${e.message}")
            throw e
        }
    }
    
    // Abrir proyecto desde URI
    fun openProject(uri: Uri): Project {
        return importProject(uri) // Por ahora es lo mismo
    }
    
    private fun createProjectDirectory(project: Project): File {
        val dirName = "${sanitizeFileName(project.name)}_${project.id.take(8)}"
        val projectDir = File(baseDir, dirName)
        
        if (!projectDir.exists()) {
            projectDir.mkdirs()
            
            // Crear subdirectorios de recursos
            listOf("GIFs", "Images", "Fonts", "Sounds").forEach { subDir ->
                File(projectDir, "$RESOURCES_DIR/$subDir").mkdirs()
            }
        }
        
        return projectDir
    }
    
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
    
    // Agregar recurso al proyecto
    fun addResourceToProject(project: Project, uri: Uri, type: ResourceType): ResourceInfo {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("No se pudo abrir el recurso")
            
            val originalName = getFileNameFromUri(uri) ?: "resource_${System.currentTimeMillis()}"
            val resourceId = UUID.randomUUID().toString()
            val safeName = sanitizeFileName(originalName)
            
            val resourceSubDir = when (type) {
                ResourceType.GIF -> "GIFs"
                ResourceType.IMAGE -> "Images"
                ResourceType.FONT -> "Fonts"
                ResourceType.SOUND -> "Sounds"
                else -> "Other"
            }
            
            val destFile = File(project.path, "$RESOURCES_DIR/$resourceSubDir/$safeName")
            
            // Copiar archivo
            inputStream.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            val resourceInfo = ResourceInfo(
                id = resourceId,
                type = type,
                path = "$RESOURCES_DIR/$resourceSubDir/$safeName",
                originalName = originalName,
                size = destFile.length(),
                dimensions = if (type == ResourceType.GIF || type == ResourceType.IMAGE) {
                    getImageDimensions(destFile)
                } else null,
                frameCount = if (type == ResourceType.GIF) {
                    getGifFrameCount(destFile)
                } else null
            )
            
            // Actualizar proyecto
            val updatedProject = project.copy(
                resources = project.resources + resourceInfo,
                lastModified = System.currentTimeMillis()
            )
            
            saveProject(updatedProject)
            resourceInfo
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al agregar recurso: ${e.message}")
            throw e
        }
    }
    
    private fun getFileNameFromUri(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName = cursor.getString(
                    cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                )
                displayName
            } else {
                null
            }
        }
    }
    
    private fun getImageDimensions(file: File): String {
        // Implementación simplificada
        return "200x200"
    }
    
    private fun getGifFrameCount(file: File): Int {
        // Implementación simplificada
        return 30
    }
}
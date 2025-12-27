package com.romaster.rwm.projects

import com.romaster.rwm.components.WidgetComponent
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.util.UUID

@Serializable
data class Project(
    val id: String,
    val name: String,
    var path: String,
    val blueprint: Blueprint,
    val resources: List<ResourceInfo> = emptyList(),
    val createdAt: Long,
    var lastModified: Long,
    val version: String = "1.0.0"
) {
    @Serializable
    data class Blueprint(
        @Contextual
        val components: List<WidgetComponent>,
        val size: String, // "2x1", "4x2", etc.
        val backgroundColor: String = "#FF1C1B1F",
        val canvasWidth: Int = 400,
        val canvasHeight: Int = 200
    )
    
    fun toJson(): String {
        return JsonUtils.encodeToString(this)
    }
    
    companion object {
        fun fromJson(json: String): Project {
            return JsonUtils.decodeFromString(json)
        }
        
        fun createNew(name: String, size: String): Project {
            return Project(
                id = UUID.randomUUID().toString(),
                name = name,
                path = "", // Se asignará al guardar
                blueprint = Blueprint(
                    components = emptyList(),
                    size = size,
                    canvasWidth = when (size) {
                        "1x1" -> 200
                        "2x1" -> 400
                        "2x2" -> 400
                        "4x1" -> 800
                        "4x2" -> 800
                        else -> 400
                    },
                    canvasHeight = when (size) {
                        "1x1" -> 200
                        "2x1" -> 200
                        "2x2" -> 400
                        "4x1" -> 200
                        "4x2" -> 400
                        else -> 200
                    }
                ),
                createdAt = System.currentTimeMillis(),
                lastModified = System.currentTimeMillis()
            )
        }
    }
}

@Serializable
data class ResourceInfo(
    val id: String,
    val type: ResourceType,
    val path: String, // Ruta relativa dentro del proyecto
    val originalName: String,
    val size: Long,
    val dimensions: String? = null, // "300x200"
    val frameCount: Int? = null, // Para GIFs
    val duration: Int? = null // Duración en ms
)

@Serializable
enum class ResourceType {
    GIF, IMAGE, FONT, SOUND, OTHER
}

// Utilidad para JSON
object JsonUtils {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    
    fun encodeToString(project: Project): String {
        return json.encodeToString(project)
    }
    
    fun decodeFromString(jsonString: String): Project {
        return json.decodeFromString(jsonString)
    }
}
package com.romaster.rwm.animation

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.*
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.File

class GifButtonController(private val context: Context) {
    
    data class GifState(
        val isPlaying: Boolean = false,
        val currentFrame: Int = 0,
        val totalFrames: Int = 0,
        val hasCompleted: Boolean = false,
        val progress: Float = 0f
    )
    
    private val gifCache = mutableMapOf<String, GifDrawable>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Cargar GIF con control de una sola reproducción
    fun loadGifButton(
        uri: String,
        imageView: GifImageView,
        playOnce: Boolean = true,
        onAnimationComplete: (() -> Unit)? = null
    ): GifDrawable? {
        return try {
            val gifDrawable = if (uri.startsWith("content://") || uri.startsWith("file://")) {
                GifDrawable(context.contentResolver, Uri.parse(uri))
            } else {
                GifDrawable(File(uri))
            }
            
            gifDrawable.apply {
                if (playOnce) {
                    // Configurar para reproducir solo una vez
                    loopCount = 1
                    
                    // Escuchar cuando termine
                    addAnimationListener(object : pl.droidsonroids.gif.AnimationListener {
                        override fun onAnimationCompleted(loopNumber: Int) {
                            onAnimationComplete?.invoke()
                            removeAnimationListener(this)
                        }
                    })
                } else {
                    loopCount = 0 // Loop infinito
                }
                
                // Pausar inicialmente
                stop()
                
                // Configurar ImageView
                imageView.setImageDrawable(this)
                
                // Cachear
                gifCache[uri] = this
            }
            
            gifDrawable
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Reproducir GIF una vez y ejecutar acción al finalizar
    fun playGifOnceWithAction(
        uri: String, 
        onComplete: () -> Unit,
        onProgress: ((Float) -> Unit)? = null
    ) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.apply {
            if (!isRunning) {
                // Resetear si ya se reprodujo
                seekTo(0)
                loopCount = 1
                
                // Configurar listener para el fin
                addAnimationListener(object : pl.droidsonroids.gif.AnimationListener {
                    override fun onAnimationCompleted(loopNumber: Int) {
                        onComplete()
                        removeAnimationListener(this)
                    }
                })
                
                // Iniciar animación
                start()
                
                // Monitorear progreso si se solicita
                onProgress?.let { progressCallback ->
                    scope.launch {
                        while (isRunning) {
                            val progress = currentPosition.toFloat() / duration.toFloat()
                            progressCallback(progress)
                            delay(16) // ~60 FPS
                        }
                    }
                }
            }
        }
    }
    
    // Pausar GIF
    fun pauseGif(uri: String) {
        gifCache[uri]?.stop()
    }
    
    // Reiniciar GIF
    fun resetGif(uri: String) {
        gifCache[uri]?.apply {
            stop()
            seekTo(0)
        }
    }
    
    // Verificar si un GIF ha completado su animación
    fun hasGifCompleted(uri: String): Boolean {
        val gif = gifCache[uri] ?: return false
        return !gif.isRunning && gif.currentPosition >= gif.duration - 100
    }
    
    // Obtener estado actual del GIF
    fun getGifState(uri: String): GifState? {
        val gif = gifCache[uri] ?: return null
        return GifState(
            isPlaying = gif.isRunning,
            currentFrame = gif.currentFrameIndex,
            totalFrames = gif.numberOfFrames,
            hasCompleted = !gif.isRunning && gif.currentPosition >= gif.duration - 100,
            progress = gif.currentPosition.toFloat() / gif.duration.toFloat()
        )
    }
    
    // Ejecutar acción basada en ParcelableGifAction
    fun executeGifAction(action: com.romaster.rwm.components.ParcelableGifAction) {
        when (action) {
            is com.romaster.rwm.components.ParcelableGifAction.LaunchApp -> {
                try {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(action.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            is com.romaster.rwm.components.ParcelableGifAction.OpenUrl -> {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            is com.romaster.rwm.components.ParcelableGifAction.ShowNotification -> {
                // TODO: Implementar notificación
                android.widget.Toast.makeText(context, action.message, android.widget.Toast.LENGTH_SHORT).show()
            }
            
            else -> {
                // Otras acciones por implementar
            }
        }
    }
    
    fun cleanup() {
        gifCache.values.forEach { it.recycle() }
        gifCache.clear()
        scope.cancel()
    }
}
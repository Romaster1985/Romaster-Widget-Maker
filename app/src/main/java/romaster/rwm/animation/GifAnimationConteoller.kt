package com.romaster.rwm.animation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GifAnimationController(private val context: Context) {
    
    private val gifCache = mutableMapOf<String, GifDrawable>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    data class AnimationState(
        val isPlaying: Boolean = false,
        val currentFrame: Int = 0,
        val totalFrames: Int = 0,
        val speed: Float = 1.0f,
        val loopCount: Int = 0
    )
    
    private val _animationState = MutableStateFlow<AnimationState>(AnimationState())
    val animationState: StateFlow<AnimationState> = _animationState
    
    // Cargar GIF desde URI
    fun loadGif(uri: String, imageView: ImageView): GifDrawable? {
        return try {
            val gifDrawable = if (uri.startsWith("content://") || uri.startsWith("file://")) {
                GifDrawable(context.contentResolver, Uri.parse(uri))
            } else {
                GifDrawable(File(uri))
            }
            
            gifDrawable.apply {
                setLoopCount(0) // Loop infinito por defecto
                start()
                
                // Cachear
                gifCache[uri] = this
                
                // Configurar ImageView
                if (imageView is GifImageView) {
                    imageView.setImageDrawable(this)
                } else {
                    imageView.setImageDrawable(this)
                }
                
                // Actualizar estado
                updateAnimationState(this)
            }
            
            gifDrawable
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Cargar GIF optimizado para widgets
    fun loadGifForWidget(uri: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        return try {
            val gifDrawable = if (uri.startsWith("content://") || uri.startsWith("file://")) {
                GifDrawable(context.contentResolver, Uri.parse(uri))
            } else {
                GifDrawable(File(uri))
            }
            
            // Redimensionar si es necesario
            if (gifDrawable.intrinsicWidth > targetWidth || gifDrawable.intrinsicHeight > targetHeight) {
                val scaleFactor = minOf(
                    targetWidth.toFloat() / gifDrawable.intrinsicWidth,
                    targetHeight.toFloat() / gifDrawable.intrinsicHeight
                ).coerceAtMost(1.0f)
                
                val scaledWidth = (gifDrawable.intrinsicWidth * scaleFactor).toInt()
                val scaledHeight = (gifDrawable.intrinsicHeight * scaleFactor).toInt()
                
                // Obtener primer frame redimensionado
                val firstFrame = gifDrawable.seekToFrameAndGet(0)
                Bitmap.createScaledBitmap(firstFrame, scaledWidth, scaledHeight, true)
            } else {
                gifDrawable.seekToFrameAndGet(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Control de reproducción
    fun playGif(uri: String) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.start()
        gifDrawable?.let { updateAnimationState(it) }
    }
    
    fun pauseGif(uri: String) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.stop()
        gifDrawable?.let { updateAnimationState(it) }
    }
    
    fun stopGif(uri: String) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.stop()
        gifDrawable?.seekTo(0)
        gifDrawable?.let { updateAnimationState(it) }
    }
    
    fun seekToFrame(uri: String, frame: Int) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.seekTo(frame)
        gifDrawable?.let { updateAnimationState(it) }
    }
    
    fun setSpeed(uri: String, speed: Float) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.speed = speed
        gifDrawable?.let { updateAnimationState(it) }
    }
    
    fun setLoopCount(uri: String, loopCount: Int) {
        val gifDrawable = gifCache[uri]
        gifDrawable?.loopCount = loopCount
        gifDrawable?.let { updateAnimationState(it) }
    }
    
    // Obtener información del GIF
    fun getGifInfo(uri: String): GifInfo? {
        return try {
            val gifDrawable = gifCache[uri] ?: run {
                if (uri.startsWith("content://") || uri.startsWith("file://")) {
                    GifDrawable(context.contentResolver, Uri.parse(uri))
                } else {
                    GifDrawable(File(uri))
                }
            }
            
            GifInfo(
                width = gifDrawable.intrinsicWidth,
                height = gifDrawable.intrinsicHeight,
                frameCount = gifDrawable.numberOfFrames,
                duration = gifDrawable.duration,
                isAnimated = gifDrawable.numberOfFrames > 1
            )
        } catch (e: Exception) {
            null
        }
    }
    
    // Liberar recursos
    fun releaseGif(uri: String) {
        val gifDrawable = gifCache.remove(uri)
        gifDrawable?.recycle()
    }
    
    fun releaseAll() {
        gifCache.values.forEach { it.recycle() }
        gifCache.clear()
        scope.cancel()
    }
    
    private fun updateAnimationState(gifDrawable: GifDrawable) {
        _animationState.value = AnimationState(
            isPlaying = gifDrawable.isRunning,
            currentFrame = gifDrawable.currentFrame,
            totalFrames = gifDrawable.numberOfFrames,
            speed = gifDrawable.speed,
            loopCount = gifDrawable.loopCount
        )
    }
    
    data class GifInfo(
        val width: Int,
        val height: Int,
        val frameCount: Int,
        val duration: Int,
        val isAnimated: Boolean
    )
}

// Extensión para ImageView
fun ImageView.loadGif(controller: GifAnimationController, uri: String) {
    controller.loadGif(uri, this)
}
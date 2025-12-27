package com.romaster.rwm.animation

import android.content.Context
import android.widget.ImageView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.droidsonroids.gif.GifDrawable
import java.io.File

class GifAnimationController(private val context: Context) {
    
    private val gifCache = mutableMapOf<String, GifDrawable>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    data class AnimationState(
        val isPlaying: Boolean = false,
        val currentFrame: Int = 0,
        val totalFrames: Int = 0,
        val loopCount: Int = 0
    )
    
    private val _animationState = MutableStateFlow(AnimationState())
    val animationState: StateFlow<AnimationState> = _animationState
    
    // Cargar GIF
    fun loadGif(uri: String, imageView: ImageView): GifDrawable? {
        return try {
            val gifDrawable = if (uri.startsWith("content://") || uri.startsWith("file://")) {
                GifDrawable(context.contentResolver, android.net.Uri.parse(uri))
            } else {
                GifDrawable(File(uri))
            }
            
            gifDrawable.apply {
                loopCount = 0 // Loop infinito
                start()
                
                // Cachear
                gifCache[uri] = this
                
                // Configurar ImageView
                imageView.setImageDrawable(this)
                
                // Actualizar estado
                updateAnimationState(this)
            }
            
            gifDrawable
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
    
    private fun updateAnimationState(gifDrawable: GifDrawable) {
        _animationState.value = AnimationState(
            isPlaying = gifDrawable.isRunning,
            currentFrame = 0,
            totalFrames = gifDrawable.numberOfFrames,
            loopCount = gifDrawable.loopCount
        )
    }
    
    fun releaseAll() {
        gifCache.values.forEach { it.recycle() }
        gifCache.clear()
        scope.cancel()
    }
}
package com.romaster.rwm.editor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.romaster.rwm.ui.theme.RWMTheme

class EditorActivity : ComponentActivity() {
    
    private val pickGifLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // TODO: Manejar GIF seleccionado
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            RWMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditorScreen(
                        onImportGif = { pickGifLauncher.launch("image/gif") },
                        onSave = { /* TODO */ },
                        onExport = { /* TODO */ }
                    )
                }
            }
        }
    }
    
    @Composable
    fun EditorScreen(
        onImportGif: () -> Unit,
        onSave: (Any) -> Unit,
        onExport: (Any) -> Unit
    ) {
        // TODO: Implementar editor simple
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder por ahora
        }
    }
}
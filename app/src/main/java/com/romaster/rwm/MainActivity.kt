package com.romaster.rwm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romaster.rwm.databinding.ActivityMainBinding
import com.romaster.rwm.editor.EditorActivity
import com.romaster.rwm.projects.Project
import com.romaster.rwm.projects.ProjectManager
import com.romaster.rwm.utils.Logger
import java.io.File
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var projectManager: ProjectManager
    private lateinit var recentProjectsAdapter: RecentProjectsAdapter
    
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_OPEN_PROJECT = 1001
        private const val REQUEST_IMPORT_PROJECT = 1002
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Logger.info(TAG, "onCreate iniciado")
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            setupToolbar()
            setupProjectManager()
            setupRecyclerView()
            setupClickListeners()
            loadRecentProjects()
            
            Logger.info(TAG, "onCreate completado exitosamente")
            Logger.info(TAG, "Ruta de logs: ${Logger.getLogFilePath()}")
            
            // Mostrar toast con ubicación de logs
            Toast.makeText(this, "Logs en: Documentos/RWM/Logs", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "onCreate")
            Toast.makeText(this, "Error al iniciar la app. Revisar logs.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupToolbar() {
        try {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(true)
        } catch (e: Exception) {
            Logger.logException(TAG, e, "setupToolbar")
        }
    }
    
    private fun setupProjectManager() {
        try {
            projectManager = ProjectManager(this)
            Logger.info(TAG, "ProjectManager inicializado")
        } catch (e: Exception) {
            Logger.logException(TAG, e, "setupProjectManager")
        }
    }
    
    private fun setupRecyclerView() {
        try {
            recentProjectsAdapter = RecentProjectsAdapter { project ->
                openProject(project)
            }
            
            binding.recyclerRecentProjects.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = recentProjectsAdapter
                setHasFixedSize(true)
            }
            
            Logger.debug(TAG, "RecyclerView configurado")
        } catch (e: Exception) {
            Logger.logException(TAG, e, "setupRecyclerView")
        }
    }
    
    private fun setupClickListeners() {
        try {
            binding.btnCreateNew.setOnClickListener {
                Logger.debug(TAG, "Botón Nuevo Widget presionado")
                showCreateNewWidgetDialog()
            }
            
            binding.btnOpenProject.setOnClickListener {
                Logger.debug(TAG, "Botón Abrir Proyecto presionado")
                Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show()
            }
            
            binding.btnImport.setOnClickListener {
                Logger.debug(TAG, "Botón Importar presionado")
                Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Logger.logException(TAG, e, "setupClickListeners")
        }
    }
    
    private fun showCreateNewWidgetDialog() {
        try {
            Logger.debug(TAG, "=== DIÁLOGO CON BOTONES PERSONALIZADOS ===")
            
            // Crear layout programáticamente
            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(50, 30, 50, 30)
                setBackgroundColor(android.graphics.Color.WHITE)
            }
            
            val sizes = listOf("1x1", "2x1", "2x2", "3x3", "4x1", "4x2")
            val labels = listOf(
                "Pequeño (1x1)", 
                "Horizontal (2x1)", 
                "Cuadrado (2x2)", 
                "Grande (3x3)", 
                "Ancho (4x1)", 
                "Extra Grande (4x2)"
            )
            
            labels.forEachIndexed { index, label ->
                val button = android.widget.Button(this).apply {
                    text = label
                    setTextColor(android.graphics.Color.BLACK)
                    setBackgroundColor(android.graphics.Color.LTGRAY)
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = if (index > 0) 15 else 0
                        bottomMargin = 15
                    }
                    
                    setOnClickListener {
                        Logger.debug(TAG, "Botón personalizado clickeado: ${sizes[index]}")
                        createNewProject(sizes[index])
                    }
                }
                layout.addView(button)
            }
            
            // Crear diálogo
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar tamaño")
                .setView(layout)
                .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
                .create()
            
            // Forzar fondo blanco
            dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE))
            
            dialog.show()
            Logger.debug(TAG, "Diálogo con botones personalizados mostrado. Botones: ${labels.size}")
            
        } catch (e: Exception) {
            Logger.logException(TAG, e, "showCreateNewWidgetDialog - Botones personalizados")
            
            // Prueba final: diálogo del sistema nativo
            showNativeSystemDialog()
        }
    }
    
    private fun showNativeSystemDialog() {
        try {
            Logger.debug(TAG, "=== DIÁLOGO NATIVO DEL SISTEMA ===")
            
            android.app.AlertDialog.Builder(this)
                .setTitle("Diálogo nativo")
                .setMessage("¿Funciona este diálogo nativo?")
                .setPositiveButton("SÍ") { dialog, _ ->
                    Logger.debug(TAG, "Diálogo nativo - Sí presionado")
                    createNewProject("2x2")
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    Logger.debug(TAG, "Diálogo nativo - No presionado")
                    dialog.dismiss()
                }
                .show()
                
        } catch (e: Exception) {
            Logger.logException(TAG, e, "showNativeSystemDialog")
            
            // Error crítico - algo está mal con el sistema de diálogos
            Logger.error(TAG, "ERROR CRÍTICO: Ningún tipo de diálogo funciona")
            
            // Navegar directamente
            createNewProject("2x2")
        }
    }
    
    private fun createNewProject(size: String) {
        try {
            val textInputLayout = com.google.android.material.textfield.TextInputLayout(this).apply {
                hint = "Mi Widget"
                setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                addView(com.google.android.material.textfield.TextInputEditText(context).apply {
                    id = android.R.id.text1
                    setText("Mi Widget ${System.currentTimeMillis() % 1000}")
                })
            }
            
            MaterialAlertDialogBuilder(this)
                .setTitle("Nombre del Widget")
                .setMessage("Ingresa un nombre para tu widget:")
                .setView(textInputLayout)
                .setPositiveButton("Crear") { dialog, _ ->
                    try {
                        val alertDialog = dialog as androidx.appcompat.app.AlertDialog
                        val input = alertDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(android.R.id.text1)
                        val projectName = input?.text?.toString()?.trim() ?: "Mi Widget"
                        
                        if (projectName.isNotEmpty()) {
                            Logger.info(TAG, "Creando proyecto: $projectName, tamaño: $size")
                            val project = projectManager.createProject(projectName, size)
                            openEditor(project)
                        } else {
                            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Logger.logException(TAG, e, "createNewProject.dialog")
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } catch (e: Exception) {
            Logger.logException(TAG, e, "createNewProject")
        }
    }
    
    private fun openProject(project: Project) {
        try {
            Logger.debug(TAG, "Abriendo proyecto: ${project.name}")
            openEditor(project)
        } catch (e: Exception) {
            Logger.logException(TAG, e, "openProject")
        }
    }
    
    private fun openEditor(project: Project) {
        try {
            Logger.info(TAG, "Abriendo editor para proyecto: ${project.name}")
            val intent = Intent(this, EditorActivity::class.java).apply {
                putExtra("extra_project_id", project.id)
                putExtra("extra_project_name", project.name)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Logger.logException(TAG, e, "openEditor")
            Toast.makeText(this, "No se pudo abrir el editor", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadRecentProjects() {
        try {
            val projects = projectManager.getRecentProjects(10)
            recentProjectsAdapter.submitList(projects)
            Logger.info(TAG, "Cargados ${projects.size} proyectos recientes")
        } catch (e: Exception) {
            Logger.logException(TAG, e, "loadRecentProjects")
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == RESULT_OK && data != null) {
            Logger.debug(TAG, "onActivityResult: requestCode=$requestCode")
            // TODO: Implementar cuando esté listo
        }
    }
    
    override fun onResume() {
        super.onResume()
        Logger.debug(TAG, "onResume")
        loadRecentProjects()
    }
    
    override fun onPause() {
        super.onPause()
        Logger.debug(TAG, "onPause")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Logger.info(TAG, "onDestroy")
    }
}

// Adapter para proyectos recientes
class RecentProjectsAdapter(
    private val onProjectClick: (Project) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Project, RecentProjectsAdapter.ViewHolder>(ProjectDiffCallback()) {
    
    companion object {
        private const val TAG = "RecentProjectsAdapter"
    }
    
    class ViewHolder(
        private val binding: com.romaster.rwm.databinding.ItemProjectBinding,
        private val onProjectClick: (Project) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        
        fun bind(project: Project) {
            try {
                binding.textProjectName.text = project.name
                binding.textProjectDate.text = android.text.format.DateFormat
                    .getDateFormat(binding.root.context)
                    .format(project.createdAt)
                
                binding.root.setOnClickListener {
                    onProjectClick(project)
                }
            } catch (e: Exception) {
                Logger.logException(TAG, e, "ViewHolder.bind")
            }
        }
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return try {
            val binding = com.romaster.rwm.databinding.ItemProjectBinding.inflate(
                android.view.LayoutInflater.from(parent.context),
                parent,
                false
            )
            ViewHolder(binding, onProjectClick)
        } catch (e: Exception) {
            Logger.logException(TAG, e, "onCreateViewHolder")
            throw e
        }
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            Logger.logException(TAG, e, "onBindViewHolder")
        }
    }
}

class ProjectDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Project>() {
    override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem == newItem
    }
}
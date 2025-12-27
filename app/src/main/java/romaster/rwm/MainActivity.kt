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
import java.io.File

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var projectManager: ProjectManager
    private lateinit var recentProjectsAdapter: RecentProjectsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupProjectManager()
        setupRecyclerView()
        setupClickListeners()
        loadRecentProjects()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }
    
    private fun setupProjectManager() {
        projectManager = ProjectManager(this)
    }
    
    private fun setupRecyclerView() {
        recentProjectsAdapter = RecentProjectsAdapter { project ->
            openProject(project)
        }
        
        binding.recyclerRecentProjects.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recentProjectsAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupClickListeners() {
        binding.btnCreateNew.setOnClickListener {
            showCreateNewWidgetDialog()
        }
        
        binding.btnOpenProject.setOnClickListener {
            openProjectBrowser()
        }
        
        binding.btnImport.setOnClickListener {
            importProject()
        }
    }
    
    private fun showCreateNewWidgetDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Nuevo Widget")
            .setMessage("Selecciona el tamaño del widget:")
            .setItems(arrayOf("1x1", "2x1", "2x2", "4x1", "4x2")) { _, which ->
                val sizes = arrayOf("1x1", "2x1", "2x2", "4x1", "4x2")
                createNewProject(sizes[which])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun createNewProject(size: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Nombre del Widget")
            .setMessage("Ingresa un nombre para tu widget:")
            .setView(com.google.android.material.textfield.TextInputLayout(this).apply {
                hint = "Mi Widget"
                setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE)
                addView(com.google.android.material.textfield.TextInputEditText(context).apply {
                    id = android.R.id.text1
                })
            })
            .setPositiveButton("Crear") { dialog, _ ->
                val input = (dialog as MaterialAlertDialogBuilder).findViewById<com.google.android.material.textfield.TextInputEditText>(android.R.id.text1)
                val projectName = input?.text?.toString()?.trim() ?: "Mi Widget"
                
                if (projectName.isNotEmpty()) {
                    val project = projectManager.createProject(projectName, size)
                    openEditor(project)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun openProjectBrowser() {
        // Implementar selector de archivos para proyectos .rwmpack
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/zip", "application/x-rwm"))
        }
        
        startActivityForResult(intent, REQUEST_OPEN_PROJECT)
    }
    
    private fun importProject() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/zip", "application/x-rwm"))
        }
        
        startActivityForResult(intent, REQUEST_IMPORT_PROJECT)
    }
    
    private fun openProject(project: Project) {
        openEditor(project)
    }
    
    private fun openEditor(project: Project) {
        val intent = Intent(this, EditorActivity::class.java).apply {
            putExtra(EditorActivity.EXTRA_PROJECT_ID, project.id)
            putExtra(EditorActivity.EXTRA_PROJECT_NAME, project.name)
        }
        startActivity(intent)
    }
    
    private fun loadRecentProjects() {
        val projects = projectManager.getRecentProjects(10)
        recentProjectsAdapter.submitList(projects)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_OPEN_PROJECT -> {
                    data.data?.let { uri ->
                        try {
                            val project = projectManager.openProject(uri)
                            openEditor(project)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error al abrir el proyecto", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                REQUEST_IMPORT_PROJECT -> {
                    data.data?.let { uri ->
                        try {
                            val project = projectManager.importProject(uri)
                            openEditor(project)
                            Toast.makeText(this, "Proyecto importado", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error al importar el proyecto", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadRecentProjects()
    }
    
    companion object {
        private const val REQUEST_OPEN_PROJECT = 1001
        private const val REQUEST_IMPORT_PROJECT = 1002
    }
}

// Adapter para proyectos recientes
class RecentProjectsAdapter(
    private val onProjectClick: (Project) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Project, RecentProjectsAdapter.ViewHolder>(ProjectDiffCallback()) {
    
    class ViewHolder(
        private val binding: com.romaster.rwm.databinding.ItemProjectBinding,
        private val onProjectClick: (Project) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        
        fun bind(project: Project) {
            binding.textProjectName.text = project.name
            binding.textProjectDate.text = android.text.format.DateFormat
                .getDateFormat(binding.root.context)
                .format(project.createdAt)
            
            binding.root.setOnClickListener {
                onProjectClick(project)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val binding = com.romaster.rwm.databinding.ItemProjectBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onProjectClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
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
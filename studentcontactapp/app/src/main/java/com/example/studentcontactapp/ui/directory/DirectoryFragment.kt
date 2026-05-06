package com.example.studentcontactapp.ui.directory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentcontactapp.adapter.StudentAdapter
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.FragmentDirectoryBinding
import com.example.studentcontactapp.ui.form.FormActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DirectoryFragment : Fragment() {
    private var _binding: FragmentDirectoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        setupRecyclerView()
        setupFab()
        setupSearchView()
        loadStudents()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onEditClick = { student ->
                val intent = Intent(requireContext(), FormActivity::class.java)
                intent.putExtra("student_id", student.id)
                startActivity(intent)
            },
            onDeleteClick = { student ->
                showDeleteConfirmation(student)
            }
        )
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), FormActivity::class.java))
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchStudents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) loadStudents()
                else searchStudents(newText)
                return true
            }
        })
    }

    private fun loadStudents() {
        viewLifecycleOwner.lifecycleScope.launch {
            database.studentDao().getAllStudents().collectLatest { students ->
                adapter.submitList(students)
            }
        }
    }

    private fun searchStudents(keyword: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            database.studentDao().searchStudents(keyword).collectLatest { students ->
                adapter.submitList(students)
            }
        }
    }

    private fun showDeleteConfirmation(student: StudentEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"${student.name}\"?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.studentDao().delete(student)
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
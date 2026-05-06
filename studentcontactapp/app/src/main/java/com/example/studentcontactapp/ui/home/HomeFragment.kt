package com.example.studentcontactapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentcontactapp.R
import com.example.studentcontactapp.adapter.StudentAdapter
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.databinding.FragmentHomeBinding
import com.example.studentcontactapp.ui.detail.DetailFragment
import com.example.studentcontactapp.utils.PrefManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentAdapter
    private lateinit var database: AppDatabase
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())
        database = AppDatabase.getDatabase(requireContext())

        setupWelcomeMessage()
        setupRecyclerView()
        loadStudents()
    }

    private fun setupWelcomeMessage() {
        val username = prefManager.getUsername()
        binding.tvWelcome.text = "Welcome, $username!"
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onEditClick = null,
            onDeleteClick = null,
            onItemClick = { student ->
                // Buka DetailFragment dengan menambah ke back stack
                val detailFragment = DetailFragment.newInstance(student.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)   // <-- PENTING agar tombol back berfungsi
                    .commit()
            }
        )
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter
    }

    private fun loadStudents() {
        viewLifecycleOwner.lifecycleScope.launch {
            database.studentDao().getAllStudents().collectLatest { students ->
                adapter.submitList(students)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
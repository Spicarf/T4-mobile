package com.example.studentcontactapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.databinding.FragmentDetailBinding
import com.example.studentcontactapp.utils.FileHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var studentId: Long = 0
    private lateinit var database: AppDatabase
    private lateinit var nim: String

    companion object {
        private const val ARG_STUDENT_ID = "student_id"
        fun newInstance(studentId: Long): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putLong(ARG_STUDENT_ID, studentId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentId = arguments?.getLong(ARG_STUDENT_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        loadStudentData()
        setupListeners()
    }

    private fun loadStudentData() {
        lifecycleScope.launch {
            val student = database.studentDao().getStudentById(studentId)
            student?.let {
                nim = it.nim
                binding.tvName.text = it.name
                binding.tvNimProdi.text = "${it.nim} · ${it.prodi}"
                val initials = it.name.split(" ").take(2).map { s -> s.first() }.joinToString("")
                binding.tvInitials.text = initials
                updateStatus()
            }
        }
    }

    private fun updateStatus() {
        if (FileHelper.isNoteExists(requireContext(), nim)) {
            val count = FileHelper.getNoteFilesForStudent(requireContext(), nim).size
            val totalSize = FileHelper.getTotalNoteSize(requireContext(), nim)
            binding.tvStatus.text = "✓ $count catatan tersimpan ($totalSize bytes)"
        } else {
            binding.tvStatus.text = "Belum ada catatan"
        }
    }

    private fun setupListeners() {
        binding.btnSaveNote.setOnClickListener {
            val note = binding.etNote.text.toString()
            if (note.isNotEmpty()) {
                val savedFileName = FileHelper.saveNote(requireContext(), nim, note)
                if (savedFileName != null) {
                    Toast.makeText(requireContext(), "Catatan baru tersimpan", Toast.LENGTH_SHORT).show()
                    updateStatus()
                    binding.etNote.setText("")
                } else {
                    Toast.makeText(requireContext(), "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLoadNote.setOnClickListener {
            val files = FileHelper.getNoteFilesForStudent(requireContext(), nim)
            if (files.isEmpty()) {
                Toast.makeText(requireContext(), "Belum ada catatan tersimpan", Toast.LENGTH_SHORT).show()
            } else {
                showNoteListDialog(files)
            }
        }
    }

    private fun showNoteListDialog(files: List<java.io.File>) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val items = files.mapIndexed { index, file ->
            val lastModified = dateFormat.format(Date(file.lastModified()))
            val preview = FileHelper.loadNoteByFileName(requireContext(), file.name)
                ?.take(30)
                ?.replace("\n", " ")
                ?: ""
            "[${index + 1}] $lastModified - \"$preview...\" (${file.length()} bytes)"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih catatan")
            .setItems(items) { _, which ->
                val selectedFile = files[which]
                AlertDialog.Builder(requireContext())
                    .setTitle("Aksi")
                    .setMessage("Pilih tindakan untuk catatan ini")
                    .setPositiveButton("Muat") { _, _ ->
                        val content = FileHelper.loadNoteByFileName(requireContext(), selectedFile.name)
                        if (content != null) {
                            binding.etNote.setText(content)
                            Toast.makeText(requireContext(), "Catatan dimuat", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Gagal membaca catatan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Hapus") { _, _ ->
                        AlertDialog.Builder(requireContext())
                            .setTitle("Hapus catatan?")
                            .setMessage("Catatan ini akan dihapus permanen.")
                            .setPositiveButton("Ya, hapus") { _, _ ->
                                if (FileHelper.deleteNote(requireContext(), selectedFile.name)) {
                                    Toast.makeText(requireContext(), "Catatan dihapus", Toast.LENGTH_SHORT).show()
                                    updateStatus()
                                    val newFiles = FileHelper.getNoteFilesForStudent(requireContext(), nim)
                                    if (newFiles.isNotEmpty()) {
                                        showNoteListDialog(newFiles)
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Gagal menghapus", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                    .setNeutralButton("Batal", null)
                    .show()
            }
            .setNegativeButton("Tutup", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
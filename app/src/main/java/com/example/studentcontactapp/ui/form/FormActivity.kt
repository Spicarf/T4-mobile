package com.example.studentcontactapp.ui.form

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.ActivityFormBinding
import com.example.studentcontactapp.utils.SettingsManager
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormBinding
    private lateinit var database: AppDatabase
    private var studentId: Long = 0
    private var isEdit = false

    override fun attachBaseContext(newBase: Context) {
        val settingsManager = SettingsManager(newBase)
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = settingsManager.fontScale
        applyOverrideConfiguration(config)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager(this)
        AppCompatDelegate.setDefaultNightMode(
            if (settingsManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = AppDatabase.getDatabase(this)
        setupSpinner()
        studentId = intent.getLongExtra("student_id", 0)
        isEdit = studentId > 0
        supportActionBar?.title = if (isEdit) "Edit Mahasiswa" else "Tambah Mahasiswa"
        if (isEdit) loadStudentData()
        binding.btnSave.setOnClickListener { if (validateInput()) saveStudent() }
    }

    private fun setupSpinner() {
        val prodiList = arrayOf("Teknik Informatika", "Sistem Informasi", "Teknik Komputer", "Manajemen Informatika")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prodiList)
        binding.etProdi.setAdapter(adapter)
        binding.etProdi.keyListener = null
    }

    private fun loadStudentData() {
        lifecycleScope.launch {
            database.studentDao().getStudentById(studentId)?.let { student ->
                binding.apply {
                    etName.setText(student.name)
                    etNim.setText(student.nim)
                    etProdi.setText(student.prodi, false)
                    etEmail.setText(student.email)
                    etSemester.setText(student.semester.toString())
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.etName.text.isNullOrBlank()) { binding.etName.error = "Nama harus diisi"; return false }
        if (binding.etNim.text.isNullOrBlank()) { binding.etNim.error = "NIM harus diisi"; return false }
        if (binding.etProdi.text.isNullOrBlank()) { binding.etProdi.error = "Prodi harus dipilih"; return false }
        if (binding.etEmail.text.isNullOrBlank()) { binding.etEmail.error = "Email harus diisi"; return false }
        if (binding.etSemester.text.isNullOrBlank()) { binding.etSemester.error = "Semester harus diisi"; return false }
        return true
    }

    private fun saveStudent() {
        val student = StudentEntity(
            id = if (isEdit) studentId else 0,
            name = binding.etName.text.toString(),
            nim = binding.etNim.text.toString(),
            prodi = binding.etProdi.text.toString(),
            email = binding.etEmail.text.toString(),
            semester = binding.etSemester.text.toString().toIntOrNull() ?: 1
        )
        lifecycleScope.launch {
            if (isEdit) database.studentDao().update(student)
            else database.studentDao().insert(student)
            Toast.makeText(this@FormActivity, if (isEdit) "Data berhasil diupdate" else "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
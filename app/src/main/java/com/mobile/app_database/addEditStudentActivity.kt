package com.mobile.app_database

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentdirectory.R
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.entity.StudentEntity
import kotlinx.coroutines.launch

class AddEditStudentActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STUDENT_ID = "extra_student_id"
        private const val NO_ID = -1
    }

    private lateinit var etName: EditText
    private lateinit var etNim: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSemester: EditText
    private lateinit var spinnerProdi: Spinner
    private lateinit var btnSave: Button

    private val studentDao by lazy { AppDatabase.getInstance(this).studentDao() }
    private var studentId: Int = NO_ID
    private var existingStudent: StudentEntity? = null

    private val prodiList = listOf(
        "Pilih Prodi",
        "Informatika",
        "Sistem Informasi",
        "Teknik Elektro",
        "Teknik Sipil",
        "Manajemen",
        "Akuntansi"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_student)

        etName      = findViewById(R.id.etName)
        etNim       = findViewById(R.id.etNim)
        etEmail     = findViewById(R.id.etEmail)
        etSemester  = findViewById(R.id.etSemester)
        spinnerProdi = findViewById(R.id.spinnerProdi)
        btnSave     = findViewById(R.id.btnSave)

        setupSpinner()
        setupToolbar()

        studentId = intent.getIntExtra(EXTRA_STUDENT_ID, NO_ID)
        if (studentId != NO_ID) {
            loadExistingStudent(studentId)
        }

        btnSave.setOnClickListener { saveStudent() }
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, prodiList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerProdi.adapter = spinnerAdapter
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (studentId != NO_ID) "Edit Mahasiswa" else "Tambah Mahasiswa"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadExistingStudent(id: Int) {
        lifecycleScope.launch {
            val student = studentDao.getStudentById(id)
            student?.let {
                existingStudent = it
                etName.setText(it.name)
                etNim.setText(it.nim)
                etEmail.setText(it.email)
                etSemester.setText(it.semester.toString())
                val prodiIndex = prodiList.indexOf(it.prodi)
                if (prodiIndex >= 0) spinnerProdi.setSelection(prodiIndex)
            }
        }
    }

    private fun saveStudent() {
        val name     = etName.getText().toString().trim()
        val nim      = etNim.getText().toString().trim()
        val email    = etEmail.getText().toString().trim()
        val semesterStr = etSemester.getText().toString().trim()
        val prodiIndex  = spinnerProdi.selectedItemPosition

        // ── Validasi ────────────────────────────────────────────────────────
        if (name.isEmpty()) {
            etName.error = "Nama tidak boleh kosong"
            etName.requestFocus()
            return
        }
        if (nim.isEmpty()) {
            etNim.error = "NIM tidak boleh kosong"
            etNim.requestFocus()
            return
        }
        if (prodiIndex == 0) {
            Toast.makeText(this, "Pilih program studi terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email tidak valid"
            etEmail.requestFocus()
            return
        }
        if (semesterStr.isEmpty()) {
            etSemester.error = "Semester tidak boleh kosong"
            etSemester.requestFocus()
            return
        }
        val semester = semesterStr.toIntOrNull()
        if (semester == null || semester < 1 || semester > 14) {
            etSemester.error = "Semester harus angka 1–14"
            etSemester.requestFocus()
            return
        }

        val prodi = prodiList[prodiIndex]

        lifecycleScope.launch {
            if (studentId == NO_ID) {
                // Mode TAMBAH
                val newStudent = StudentEntity(
                    name = name, nim = nim, prodi = prodi,
                    email = email, semester = semester
                )
                studentDao.insert(newStudent)
                Toast.makeText(this@AddEditStudentActivity, "Mahasiswa berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            } else {
                // Mode EDIT — pertahankan id dan createdAt asli
                val updated = existingStudent!!.copy(
                    name = name, nim = nim, prodi = prodi,
                    email = email, semester = semester
                )
                studentDao.update(updated)
                Toast.makeText(this@AddEditStudentActivity, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
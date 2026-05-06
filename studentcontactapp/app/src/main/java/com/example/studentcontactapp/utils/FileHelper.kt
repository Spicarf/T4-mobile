package com.example.studentcontactapp.utils

import android.content.Context
import java.io.File

object FileHelper {

    /** Format nama file catatan: note_[nim]_[timestamp].txt */
    private fun generateNoteFileName(nim: String): String {
        return "note_${nim}_${System.currentTimeMillis()}.txt"
    }

    /** Simpan catatan baru. Mengembalikan nama file jika sukses. */
    fun saveNote(context: Context, studentNim: String, content: String): String? {
        return try {
            val fileName = generateNoteFileName(studentNim)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(content.toByteArray())
            }
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Muat isi catatan berdasarkan nama file lengkap (misal note_nim_123456.txt) */
    fun loadNoteByFileName(context: Context, fileName: String): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                context.openFileInput(fileName).bufferedReader().readText()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /** Hapus file catatan */
    fun deleteNote(context: Context, fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }

    /** Ambil daftar nama file catatan untuk NIM tertentu, diurutkan dari terbaru */
    fun getNoteFilesForStudent(context: Context, studentNim: String): List<File> {
        return context.filesDir.listFiles()
            ?.filter { it.name.startsWith("note_${studentNim}_") && it.name.endsWith(".txt") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    /** Cek apakah ada catatan untuk NIM tertentu */
    fun isNoteExists(context: Context, studentNim: String): Boolean {
        return getNoteFilesForStudent(context, studentNim).isNotEmpty()
    }

    /** Total ukuran semua catatan untuk NIM tertentu */
    fun getTotalNoteSize(context: Context, studentNim: String): Long {
        return getNoteFilesForStudent(context, studentNim).sumOf { it.length() }
    }
}
package com.example.studentcontactapp.database.dao

import androidx.room.*
import com.example.studentcontactapp.database.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insert(student: StudentEntity): Long

    @Insert
    suspend fun insertAll(students: List<StudentEntity>)

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :keyword || '%' OR nim LIKE '%' || :keyword || '%'")
    fun searchStudents(keyword: String): Flow<List<StudentEntity>>

    @Update
    suspend fun update(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}
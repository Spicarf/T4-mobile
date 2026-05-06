package com.example.studentdirectory.database.dao

import androidx.room.*
import com.example.studentdirectory.database.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<StudentEntity>)

    // Flow: otomatis emit ulang saat data berubah
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): StudentEntity?

    @Query("""
        SELECT * FROM students 
        WHERE name LIKE '%' || :keyword || '%' 
           OR nim  LIKE '%' || :keyword || '%' 
        ORDER BY name ASC
    """)
    fun searchStudents(keyword: String): Flow<List<StudentEntity>>

    @Update
    suspend fun update(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}
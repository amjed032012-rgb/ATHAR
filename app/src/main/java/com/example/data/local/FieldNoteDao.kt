package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldNoteDao {
    @Query("SELECT * FROM field_notes ORDER BY visitDate DESC")
    fun getAllNotes(): Flow<List<FieldNoteEntity>>

    @Query("SELECT * FROM field_notes WHERE monumentId = :monumentId ORDER BY visitDate DESC")
    fun getNotesForMonument(monumentId: String): Flow<List<FieldNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: FieldNoteEntity): Long

    @Update
    suspend fun updateNote(note: FieldNoteEntity)

    @Delete
    suspend fun deleteNote(note: FieldNoteEntity)

    @Query("DELETE FROM field_notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}

package com.example.rewire.repository

import com.example.rewire.db.dao.AddictionNoteDao
import com.example.rewire.db.entity.AddictionNoteEntity

class AddictionNoteRepository(private val dao: AddictionNoteDao) {
    suspend fun getAll(): List<AddictionNoteEntity> = dao.getAll()
    suspend fun getById(id: Long): AddictionNoteEntity? = dao.getById(id)
    suspend fun insert(note: AddictionNoteEntity): Long = dao.insert(note)
    suspend fun update(note: AddictionNoteEntity) = dao.update(note)
    suspend fun delete(note: AddictionNoteEntity) = dao.delete(note)
    suspend fun deleteAll() = dao.deleteAll()
}

package com.ruicomp.cmptemplate.core.domain.repository

import com.ruicomp.cmptemplate.core.database.models.Contact
import kotlinx.coroutines.flow.Flow

interface CallerRepository {
    fun getContacts(): Flow<List<Contact>>
    suspend fun insertCaller(name: String, number: String)
    suspend fun deleteCaller(id: Long)
} 
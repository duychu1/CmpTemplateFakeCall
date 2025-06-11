package com.ruicomp.cmptemplate.features.saved_caller.domain.repository

import com.ruicomp.cmptemplate.core.models.Contact
import kotlinx.coroutines.flow.Flow

interface CallerRepository {
    fun getContacts(): Flow<List<Contact>>
    suspend fun insertCaller(name: String, number: String)
    suspend fun deleteCaller(id: Long)
} 
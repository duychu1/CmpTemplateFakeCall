package com.ruicomp.cmptemplate.domain.repository

import com.ruicomp.cmptemplate.domain.models.Caller
import kotlinx.coroutines.flow.Flow

interface CallerRepository {
    fun getCallers(): Flow<List<Caller>>
    suspend fun insertCaller(name: String, number: String)
    suspend fun deleteCaller(id: Long)
} 
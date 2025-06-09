package com.ruicomp.cmptemplate.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ruicomp.cmptemplate.data.mappers.toDomain
import com.ruicomp.cmptemplate.database.AppDatabase
import com.ruicomp.cmptemplate.domain.models.Caller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CallerDataSource(
    private val database: AppDatabase
) {
    fun getCallers(): Flow<List<Caller>> {
        return database.callerQueries
            .getAllCallers()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { callers -> callers.map { it.toDomain() } }
    }

    suspend fun insertCaller(name: String, number: String) {
        database.callerQueries.insertCaller(name, number)
    }

    suspend fun deleteCaller(id: Long) {
        database.callerQueries.deleteCallerById(id)
    }
} 
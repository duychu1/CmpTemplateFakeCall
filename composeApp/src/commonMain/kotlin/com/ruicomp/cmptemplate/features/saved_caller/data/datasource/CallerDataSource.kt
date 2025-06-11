package com.ruicomp.cmptemplate.features.saved_caller.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CallerDataSource(
    private val database: AppDatabase
) {
    fun getContacts(): Flow<List<Contact>> {
        return database.callerQueries
            .getAllCallers()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { callers ->
                callers.map {
                    Contact(
                        id = it.id,
                        name = it.name,
                        number = it.number
                    )
                }
            }
    }

    suspend fun insertCaller(name: String, number: String) {
        database.callerQueries.insertCaller(name, number)
    }

    suspend fun deleteCaller(id: Long) {
        database.callerQueries.deleteCallerById(id)
    }
} 
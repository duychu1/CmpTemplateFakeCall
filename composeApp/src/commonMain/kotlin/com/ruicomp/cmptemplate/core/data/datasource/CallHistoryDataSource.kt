package com.ruicomp.cmptemplate.core.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ruicomp.cmptemplate.database.AppDatabase
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class CallHistoryDataSource(
    private val database: AppDatabase
) {
    fun getCallHistory(): Flow<List<CallHistory>> {
        return database.callHistoryQueries
            .getAllCallHistory()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    CallHistory(
                        id = it.id,
                        name = it.name,
                        number = it.number,
                        timestamp = it.timestamp
                    )
                }
            }
    }

    suspend fun insertCallHistory(name: String, number: String) {
        database.callHistoryQueries.insertCallHistory(
            name = name,
            number = number,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }
} 
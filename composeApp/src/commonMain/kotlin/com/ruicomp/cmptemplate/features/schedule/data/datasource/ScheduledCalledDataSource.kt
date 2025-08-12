package com.ruicomp.cmptemplate.features.schedule.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.ruicomp.cmptemplate.database.AppDatabase
import com.ruicomp.cmptemplate.features.schedule.data.models.ScheduledCalled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduledCalledDataSource(
    private val database: AppDatabase
) {
    fun getScheduledCalls(): Flow<List<ScheduledCalled>> {
        return database.scheduleCalledQueries
            .selectAllScheduledCalls()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    ScheduledCalled(
                        id = it.id,
                        name = it.callerName,
                        number = it.callerNumber,
                        avatarUrl = it.callerAvatarUrl,
                        triggerAtMillis = it.triggerAtMillis
                    )
                }
            }
    }

    suspend fun insertScheduledCall(
        id: Int,
        callerName: String,
        callerNumber: String,
        callerAvatarUrl: String?,
        triggerAtMillis: Long
    ) {
        database.scheduleCalledQueries.insertScheduledCall(
            callerName = callerName,
            callerNumber = callerNumber,
            callerAvatarUrl = callerAvatarUrl,
            triggerAtMillis = triggerAtMillis
        )
    }

    suspend fun insertScheduledCallAndReturnId(
        callerName: String,
        callerNumber: String,
        callerAvatarUrl: String?,
        triggerAtMillis: Long
    ): Long {
        database.scheduleCalledQueries.insertScheduledCall(
            callerName = callerName,
            callerNumber = callerNumber,
            callerAvatarUrl = callerAvatarUrl,
            triggerAtMillis = triggerAtMillis
        )
        return database.scheduleCalledQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun removeScheduledCall(id: Int) {
        database.scheduleCalledQueries.deleteScheduledCallById(id.toLong())
    }
}

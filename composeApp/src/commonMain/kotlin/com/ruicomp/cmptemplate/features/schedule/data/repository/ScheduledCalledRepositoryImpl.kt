package com.ruicomp.cmptemplate.features.schedule.data.repository

import com.ruicomp.cmptemplate.features.schedule.data.datasource.ScheduledCalledDataSource
import com.ruicomp.cmptemplate.features.schedule.data.models.ScheduledCalled
import com.ruicomp.cmptemplate.features.schedule.domain.repository.ScheduledCalledRepository
import kotlinx.coroutines.flow.Flow

class ScheduledCalledRepositoryImpl(
    private val dataSource: ScheduledCalledDataSource
) : ScheduledCalledRepository {
    override fun getScheduledCalls(): Flow<List<ScheduledCalled>> {
        return dataSource.getScheduledCalls()
    }

    override suspend fun addScheduledCall(call: ScheduledCalled) {
        dataSource.insertScheduledCall(
            id = call.id.toInt(),
            callerName = call.name,
            callerNumber = call.number,
            callerAvatarUrl = call.avatarUrl,
            triggerAtMillis = call.triggerAtMillis
        )
    }

    override suspend fun addScheduledCallAndReturnId(call: ScheduledCalled): Long {
        return dataSource.insertScheduledCallAndReturnId(
            callerName = call.name,
            callerNumber = call.number,
            callerAvatarUrl = call.avatarUrl,
            triggerAtMillis = call.triggerAtMillis
        )
    }

    override suspend fun removeScheduledCall(id: Int) {
        dataSource.removeScheduledCall(id)
    }
}

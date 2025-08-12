package com.ruicomp.cmptemplate.features.schedule.domain.repository

import com.ruicomp.cmptemplate.features.schedule.data.models.ScheduledCalled
import kotlinx.coroutines.flow.Flow

interface ScheduledCalledRepository {
    fun getScheduledCalls(): Flow<List<ScheduledCalled>>
    suspend fun addScheduledCall(call: ScheduledCalled)
    suspend fun removeScheduledCall(id: Int)
    suspend fun addScheduledCallAndReturnId(call: ScheduledCalled): Long
}

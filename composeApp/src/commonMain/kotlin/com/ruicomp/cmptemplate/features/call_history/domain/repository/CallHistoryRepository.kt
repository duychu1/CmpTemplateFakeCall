package com.ruicomp.cmptemplate.features.call_history.domain.repository

import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import kotlinx.coroutines.flow.Flow

interface CallHistoryRepository {
    fun getCallHistory(): Flow<List<CallHistory>>
    suspend fun addCallToHistory(contact: Contact)
} 
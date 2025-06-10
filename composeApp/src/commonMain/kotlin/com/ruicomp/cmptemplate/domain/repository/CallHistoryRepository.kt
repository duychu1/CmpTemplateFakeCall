package com.ruicomp.cmptemplate.domain.repository

import com.ruicomp.cmptemplate.domain.models.CallHistory
import com.ruicomp.cmptemplate.domain.models.Contact
import kotlinx.coroutines.flow.Flow

interface CallHistoryRepository {
    fun getCallHistory(): Flow<List<CallHistory>>
    suspend fun addCallToHistory(contact: Contact)
} 
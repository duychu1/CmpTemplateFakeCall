package com.ruicomp.cmptemplate.features.call_history.data.repository

import com.ruicomp.cmptemplate.features.call_history.data.datasource.CallHistoryDataSource
import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.Flow

class CallHistoryRepositoryImpl(
    private val dataSource: CallHistoryDataSource
) : CallHistoryRepository {
    override fun getCallHistory(): Flow<List<CallHistory>> {
        return dataSource.getCallHistory()
    }

    override suspend fun addCallToHistory(contact: Contact) {
        dataSource.insertCallHistory(
            name = contact.name,
            number = contact.number
        )
    }
} 
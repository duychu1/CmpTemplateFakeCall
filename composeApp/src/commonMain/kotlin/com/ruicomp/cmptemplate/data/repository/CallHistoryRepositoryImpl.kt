package com.ruicomp.cmptemplate.data.repository

import com.ruicomp.cmptemplate.data.local.CallHistoryDataSource
import com.ruicomp.cmptemplate.domain.models.CallHistory
import com.ruicomp.cmptemplate.domain.models.Contact
import com.ruicomp.cmptemplate.domain.repository.CallHistoryRepository
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
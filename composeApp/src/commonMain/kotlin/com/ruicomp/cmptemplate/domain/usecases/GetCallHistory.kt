package com.ruicomp.cmptemplate.domain.usecases

import com.ruicomp.cmptemplate.domain.models.CallHistory
import com.ruicomp.cmptemplate.domain.repository.CallHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetCallHistory(private val repository: CallHistoryRepository) {
    operator fun invoke(): Flow<List<CallHistory>> {
        return repository.getCallHistory()
    }
} 
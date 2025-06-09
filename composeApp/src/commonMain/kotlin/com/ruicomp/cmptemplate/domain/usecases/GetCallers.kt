package com.ruicomp.cmptemplate.domain.usecases

import com.ruicomp.cmptemplate.domain.repository.CallerRepository
import kotlinx.coroutines.flow.Flow
import com.ruicomp.cmptemplate.domain.models.Caller

class GetCallers(private val callerRepository: CallerRepository) {
    operator fun invoke(): Flow<List<Caller>> {
        return callerRepository.getCallers()
    }
} 
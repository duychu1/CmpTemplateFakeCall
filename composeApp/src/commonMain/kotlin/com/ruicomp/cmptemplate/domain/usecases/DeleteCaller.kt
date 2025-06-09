package com.ruicomp.cmptemplate.domain.usecases

import com.ruicomp.cmptemplate.domain.repository.CallerRepository

class DeleteCaller(private val callerRepository: CallerRepository) {
    suspend operator fun invoke(id: Long) {
        callerRepository.deleteCaller(id)
    }
} 
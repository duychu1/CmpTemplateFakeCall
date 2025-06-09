package com.ruicomp.cmptemplate.domain.usecases

import com.ruicomp.cmptemplate.domain.repository.CallerRepository

class AddCaller(private val callerRepository: CallerRepository) {
    suspend operator fun invoke(name: String, number: String) {
        callerRepository.insertCaller(name, number)
    }
} 
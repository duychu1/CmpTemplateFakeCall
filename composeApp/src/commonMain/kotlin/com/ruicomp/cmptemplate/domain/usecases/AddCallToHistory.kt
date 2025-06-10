package com.ruicomp.cmptemplate.domain.usecases

import com.ruicomp.cmptemplate.domain.models.Contact
import com.ruicomp.cmptemplate.domain.repository.CallHistoryRepository

class AddCallToHistory(private val repository: CallHistoryRepository) {
    suspend operator fun invoke(contact: Contact) {
        repository.addCallToHistory(contact)
    }
} 
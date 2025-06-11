package com.ruicomp.cmptemplate.features.saved_caller.data.repository

import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.saved_caller.data.datasource.CallerDataSource
import com.ruicomp.cmptemplate.features.saved_caller.domain.repository.CallerRepository
import kotlinx.coroutines.flow.Flow

class CallerRepositoryImpl(
    private val callerDataSource: CallerDataSource
) : CallerRepository {
    override fun getContacts(): Flow<List<Contact>> {
        return callerDataSource.getContacts()
    }

    override suspend fun insertCaller(name: String, number: String) {
        callerDataSource.insertCaller(name, number)
    }

    override suspend fun deleteCaller(id: Long) {
        callerDataSource.deleteCaller(id)
    }
} 
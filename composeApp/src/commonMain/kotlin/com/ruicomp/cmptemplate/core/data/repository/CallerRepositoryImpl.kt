package com.ruicomp.cmptemplate.core.data.repository

import com.ruicomp.cmptemplate.core.data.datasource.CallerDataSource
import com.ruicomp.cmptemplate.core.database.models.Contact
import com.ruicomp.cmptemplate.core.domain.repository.CallerRepository
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
package com.ruicomp.cmptemplate.data.repository

import com.ruicomp.cmptemplate.data.local.CallerDataSource
import com.ruicomp.cmptemplate.domain.models.Caller
import com.ruicomp.cmptemplate.domain.repository.CallerRepository
import kotlinx.coroutines.flow.Flow

class CallerRepositoryImpl(private val dataSource: CallerDataSource) : CallerRepository {
    override fun getCallers(): Flow<List<Caller>> {
        return dataSource.getCallers()
    }

    override suspend fun insertCaller(name: String, number: String) {
        dataSource.insertCaller(name, number)
    }

    override suspend fun deleteCaller(id: Long) {
        dataSource.deleteCaller(id)
    }
} 
package com.ruicomp.cmptemplate.features.call_history.di

import com.ruicomp.cmptemplate.features.call_history.data.datasource.CallHistoryDataSource
import com.ruicomp.cmptemplate.features.call_history.data.repository.CallHistoryRepositoryImpl
import com.ruicomp.cmptemplate.features.call_history.domain.repository.CallHistoryRepository
import com.ruicomp.cmptemplate.features.call_history.presentation.CallHistoryViewModel
import org.koin.dsl.module

val callHistoryModule = module {
    single { CallHistoryDataSource(database = get()) }
    single<CallHistoryRepository> { CallHistoryRepositoryImpl(dataSource = get()) }
    factory { CallHistoryViewModel(callHistoryRepository = get()) }
}

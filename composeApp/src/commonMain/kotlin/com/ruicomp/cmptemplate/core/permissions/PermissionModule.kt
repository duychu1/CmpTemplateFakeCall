package com.ruicomp.cmptemplate.core.permissions

import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import com.ruicomp.cmptemplate.core.permissions.phoneaccount.PhoneAccountPermissionManager
import com.ruicomp.cmptemplate.core.permissions.presentation.BasePermissionManager
import org.koin.dsl.module

val permissionModule = module {
//    factory<PermissionHandler> { PermissionHandlerImpl() }
    single { PhoneAccountPermissionManager(get()) }
    factory { BasePermissionManager() }
}
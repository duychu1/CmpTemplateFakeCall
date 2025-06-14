package com.ruicomp.cmptemplate.core.permissions

import com.ruicomp.cmptemplate.core.datastore.DataStorePreferences
import org.koin.dsl.module

val permissionModule = module {
    factory<PermissionHandler> { PermissionHandlerImpl() }
}
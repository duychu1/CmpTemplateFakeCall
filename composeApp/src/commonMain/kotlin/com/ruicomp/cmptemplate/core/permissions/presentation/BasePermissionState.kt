package com.ruicomp.cmptemplate.core.permissions.presentation

data class BasePermissionState(
    val permissionAwareStates: Map<String, Boolean> = emptyMap(),
    val permissionStatuses: Map<String, PermissionStatus> = emptyMap()
)
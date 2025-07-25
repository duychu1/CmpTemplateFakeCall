package com.ruicomp.cmptemplate.core.permissions.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.set

class BasePermissionManager {

    private val _uiBasePermissionState = MutableStateFlow(BasePermissionState())
    val uiBasePermissionState = _uiBasePermissionState.asStateFlow()

    fun onEvent(event: BasePermissionEvent) {
        when (event) {
            is BasePermissionEvent.ShowPermissionAwareChange ->
                handleShowPermissionAwareChange(event.permission, event.show)
            is BasePermissionEvent.PermissionStatusChecked ->
                handlePermissionStatusChecked(event.permission, event.status)
            is BasePermissionEvent.PermissionResult ->
                handlePermissionResult(event.permission, event.status)
            is BasePermissionEvent.OnInitialed -> {
                _uiBasePermissionState.update { currentState ->
                    currentState.copy(
                        initialized = true,
                        permissionAwareStates = currentState.permissionAwareStates.toMutableMap().apply {
                            this[event.permission] = false
                        }
                    )
                }
            }
        }
    }

    private fun handleShowPermissionAwareChange(permission: String, show: Boolean) {
        _uiBasePermissionState.update { currentState ->
            currentState.copy(
                permissionAwareStates = currentState.permissionAwareStates.toMutableMap().apply {
                    this[permission] = show
                }
            )
        }
    }

    /**
     * Initializes the permission manager for a specific permission.
     * This method remains public as it's a direct action rather than just an event.
     */
    fun initialize(permission: String) {
        _uiBasePermissionState.update { currentState ->
            currentState.copy(
                initialized = false,
            )
        }
        checkAndShowPermissionAwareness(permission)
    }

    /**
     * Checks if the permission awareness UI should be shown for a specific permission
     * and updates the state accordingly.
     * Returns true if the awareness UI was shown (and the caller should potentially return),
     * false otherwise.
     * This method remains public as it's a direct query/action rather than just an event.
     */
     fun checkAndShowPermissionAwareness(permission: String): Boolean {
        val isAwarenessShown = _uiBasePermissionState.value.permissionAwareStates[permission] ?: false
        val isPermissionGranted = _uiBasePermissionState.value.permissionStatuses[permission] == PermissionStatus.Granted

        if (!isAwarenessShown && !isPermissionGranted) {
            // Internally call the handler for ShowPermissionAwareChange
            handleShowPermissionAwareChange(permission, true)
            return true
        }
        return false
    }

    private fun handlePermissionStatusChecked(permission: String, status: PermissionStatus) {
        _uiBasePermissionState.update { currentState ->
            currentState.copy(
                permissionStatuses = currentState.permissionStatuses.toMutableMap().apply {
                    this[permission] = status
                }
            )
        }
        if (status == PermissionStatus.Granted) {
            // When permission is granted, ensure its awareness UI is hidden.
            handleShowPermissionAwareChange(permission, false)
        }
    }

    private fun handlePermissionResult(permission: String, status: PermissionStatus) {
        _uiBasePermissionState.update { currentState ->
            val updatedStatuses = currentState.permissionStatuses.toMutableMap().apply {
                this[permission] = status
            }
            // If granted, also hide the awareness UI for this permission
            val updatedAwareStates = if (status == PermissionStatus.Granted) {
                currentState.permissionAwareStates.toMutableMap().apply {
                    this[permission] = false
                }
            } else {
                currentState.permissionAwareStates
            }
            currentState.copy(
                permissionStatuses = updatedStatuses,
                permissionAwareStates = updatedAwareStates
            )
        }

        if (status == PermissionStatus.Granted) {
            // When permission is granted, ensure its awareness UI is hidden.
            handleShowPermissionAwareChange(permission, false)
        }
    }

    /**
     * Utility function to check if a specific permission is granted.
     * This method remains public for direct state querying.
     */
    fun isPermissionGranted(permission: String): Boolean {
        return _uiBasePermissionState.value.permissionStatuses[permission] == PermissionStatus.Granted
    }
}

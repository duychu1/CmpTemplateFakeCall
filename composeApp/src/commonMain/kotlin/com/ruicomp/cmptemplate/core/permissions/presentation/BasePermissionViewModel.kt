package com.ruicomp.cmptemplate.core.permissions.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BasePermissionViewModel : ViewModel() {

    private val _uiBasePermissionState = MutableStateFlow(BasePermissionState())
    val uiBasePermissionState = _uiBasePermissionState.asStateFlow()

    /**
     * Updates the visibility of the permission awareness UI for a specific permission.
     */
    fun onShowPermissionAwareChange(permission: String, show: Boolean) {
        _uiBasePermissionState.update { currentState ->
            currentState.copy(
                permissionAwareStates = currentState.permissionAwareStates.toMutableMap().apply {
                    this[permission] = show
                }
            )
        }
    }

    /**
     * Checks if the permission awareness UI should be shown for a specific permission
     * and updates the state accordingly.
     * Returns true if the awareness UI was shown (and the caller should potentially return),
     * false otherwise.
     */
    protected fun checkAndShowPermissionAwareness(permission: String): Boolean {
        val isAwarenessShown = _uiBasePermissionState.value.permissionAwareStates[permission] ?: false
        val isPermissionGranted = _uiBasePermissionState.value.permissionStatuses[permission] == PermissionStatus.Granted

        if (!isAwarenessShown && !isPermissionGranted) {
            onShowPermissionAwareChange(permission, true)
            return true
        }
        return false
    }

    /**
     * Event from the UI: The initial check for a specific permission status is complete.
     */
    fun onPermissionStatusChecked(permission: String, status: PermissionStatus) {
        _uiBasePermissionState.update { currentState ->
            currentState.copy(
                permissionStatuses = currentState.permissionStatuses.toMutableMap().apply {
                    this[permission] = status
                }
            )
        }
        if (status == PermissionStatus.Granted) {
            handlePermissionGranted(permission)
        }
        // Potentially add more logic based on other statuses
    }

    /**
     * Event from the UI: The user has responded to the permission request dialog for a specific permission.
     */
    fun onPermissionResult(permission: String, status: PermissionStatus) {
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
            handlePermissionGranted(permission)
        }
        // Potentially add more logic based on other statuses
    }

    private fun handlePermissionGranted(permission: String) {
        // Now, when a permission is granted, we also ensure its awareness UI is hidden.
        onShowPermissionAwareChange(permission, false)
    }

    /**
     * Utility function to check if a specific permission is granted.
     */
    protected fun isPermissionGranted(permission: String): Boolean {
        return _uiBasePermissionState.value.permissionStatuses[permission] == PermissionStatus.Granted
    }
}
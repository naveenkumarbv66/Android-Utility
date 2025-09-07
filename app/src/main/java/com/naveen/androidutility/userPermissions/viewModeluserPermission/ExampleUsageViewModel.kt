package com.naveen.androidutility.userPermissions.viewModeluserPermission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Example ViewModel showing how to integrate the permission library
 * into any existing ViewModel following clean architecture principles
 */
class ExampleUsageViewModel(application: Application) : AndroidViewModel(application) {

    // Get the permission manager instance
    private val permissionManager = PermissionManager.getInstance()
    
    // Get the permission repository for direct access if needed
    private val permissionRepository = PermissionRepository()

    // State flows for your app's specific needs
    private val _cameraFeatureEnabled = MutableStateFlow(false)
    val cameraFeatureEnabled: StateFlow<Boolean> = _cameraFeatureEnabled.asStateFlow()

    private val _storageFeatureEnabled = MutableStateFlow(false)
    val storageFeatureEnabled: StateFlow<Boolean> = _storageFeatureEnabled.asStateFlow()

    private val _locationFeatureEnabled = MutableStateFlow(false)
    val locationFeatureEnabled: StateFlow<Boolean> = _locationFeatureEnabled.asStateFlow()

    private val _permissionStatus = MutableStateFlow<String>("Checking permissions...")
    val permissionStatus: StateFlow<String> = _permissionStatus.asStateFlow()

    init {
        // Check permissions when ViewModel is created
        checkAllRequiredPermissions()
    }

    /**
     * Check all required permissions for your app
     */
    private fun checkAllRequiredPermissions() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                
                // Check camera permission
                val cameraResult = permissionManager.checkPermission(
                    context, 
                    PermissionRepository.Permissions.CAMERA
                )
                _cameraFeatureEnabled.value = permissionManager.isPermissionGranted(cameraResult)
                
                // Check storage permission
                val storagePermission = permissionRepository.getStoragePermission(context)
                val storageResult = permissionManager.checkPermission(context, storagePermission)
                _storageFeatureEnabled.value = permissionManager.isPermissionGranted(storageResult)
                
                // Check location permission
                val locationResult = permissionManager.checkPermission(
                    context, 
                    PermissionRepository.Permissions.ACCESS_FINE_LOCATION
                )
                _locationFeatureEnabled.value = permissionManager.isPermissionGranted(locationResult)
                
                // Update status message
                updatePermissionStatus()
                
            } catch (e: Exception) {
                _permissionStatus.value = "Error checking permissions: ${e.message}"
            }
        }
    }

    /**
     * Update the permission status message
     */
    private fun updatePermissionStatus() {
        val cameraStatus = if (_cameraFeatureEnabled.value) "✅" else "❌"
        val storageStatus = if (_storageFeatureEnabled.value) "✅" else "❌"
        val locationStatus = if (_locationFeatureEnabled.value) "✅" else "❌"
        
        _permissionStatus.value = "Camera: $cameraStatus | Storage: $storageStatus | Location: $locationStatus"
    }

    /**
     * Check a specific permission
     * @param permission The permission to check
     * @return StateFlow with the result
     */
    fun checkSpecificPermission(permission: String): StateFlow<PermissionResult> {
        val stateFlow = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
        
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val result = permissionManager.checkPermission(context, permission)
                stateFlow.value = result
            } catch (e: Exception) {
                stateFlow.value = PermissionResult.Error("Error: ${e.message}", e)
            }
        }
        
        return stateFlow
    }

    /**
     * Check if multiple permissions are granted
     * @param permissions Array of permissions to check
     * @return StateFlow with boolean result
     */
    fun checkMultiplePermissions(permissions: Array<String>): StateFlow<Boolean> {
        val stateFlow = MutableStateFlow(false)
        
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val result = permissionManager.areAllPermissionsGranted(context, permissions)
                stateFlow.value = result
            } catch (e: Exception) {
                stateFlow.value = false
            }
        }
        
        return stateFlow
    }

    /**
     * Refresh permission states
     */
    fun refreshPermissions() {
        checkAllRequiredPermissions()
    }

    /**
     * Get permission status as string
     * @param result The PermissionResult
     * @return String representation
     */
    fun getPermissionStatusString(result: PermissionResult): String {
        return permissionManager.getPermissionStatusString(result)
    }

    /**
     * Check if permission is granted
     * @param result The PermissionResult
     * @return Boolean indicating if granted
     */
    fun isPermissionGranted(result: PermissionResult): Boolean {
        return permissionManager.isPermissionGranted(result)
    }
}

/**
 * Example of how to use the permission library in a simple ViewModel
 */
class SimplePermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val permissionManager = PermissionManager.getInstance()
    
    private val _permissionResult = MutableStateFlow<PermissionResult?>(null)
    val permissionResult: StateFlow<PermissionResult?> = _permissionResult.asStateFlow()

    /**
     * Simple method to check any permission
     * @param permission The permission to check
     */
    fun checkPermission(permission: String) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val result = permissionManager.checkPermission(context, permission)
            _permissionResult.value = result
        }
    }

    /**
     * Check multiple permissions
     * @param permissions Array of permissions to check
     */
    fun checkMultiplePermissions(permissions: Array<String>) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val result = permissionManager.checkMultiplePermissions(context, permissions)
            _permissionResult.value = result
        }
    }
}

package com.naveen.androidutility.userPermissions.viewModeluserPermission

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling permission operations with MVVM pattern
 * This ViewModel provides a clean interface for UI components to interact with permissions
 */
class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PermissionRepository()
    private val context: Context = application.applicationContext

    // State flows for different permission states
    private val _cameraPermissionState = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
    val cameraPermissionState: StateFlow<PermissionResult> = _cameraPermissionState.asStateFlow()

    private val _storagePermissionState = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
    val storagePermissionState: StateFlow<PermissionResult> = _storagePermissionState.asStateFlow()

    private val _locationPermissionState = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
    val locationPermissionState: StateFlow<PermissionResult> = _locationPermissionState.asStateFlow()

    private val _multiplePermissionsState = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
    val multiplePermissionsState: StateFlow<PermissionResult> = _multiplePermissionsState.asStateFlow()

    // General permission result state
    private val _permissionResult = MutableStateFlow<PermissionResult?>(null)
    val permissionResult: StateFlow<PermissionResult?> = _permissionResult.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Check initial permission states
        checkInitialPermissions()
    }

    /**
     * Check initial permission states when ViewModel is created
     */
    private fun checkInitialPermissions() {
        viewModelScope.launch {
            checkCameraPermission()
            checkStoragePermission()
            checkLocationPermission()
        }
    }

    /**
     * Check camera permission status
     */
    fun checkCameraPermission() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.checkPermission(context, PermissionRepository.Permissions.CAMERA)
                _cameraPermissionState.value = result
                _permissionResult.value = result
            } catch (e: Exception) {
                _cameraPermissionState.value = PermissionResult.Error("Error checking camera permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Check storage permission status
     */
    fun checkStoragePermission() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val storagePermission = repository.getStoragePermission(context)
                val result = repository.checkPermission(context, storagePermission)
                _storagePermissionState.value = result
                _permissionResult.value = result
            } catch (e: Exception) {
                _storagePermissionState.value = PermissionResult.Error("Error checking storage permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Check location permission status
     */
    fun checkLocationPermission() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.checkPermission(context, PermissionRepository.Permissions.ACCESS_FINE_LOCATION)
                _locationPermissionState.value = result
                _permissionResult.value = result
            } catch (e: Exception) {
                _locationPermissionState.value = PermissionResult.Error("Error checking location permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Check multiple permissions at once
     */
    fun checkMultiplePermissions(permissions: Array<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.checkMultiplePermissions(context, permissions)
                _multiplePermissionsState.value = result
                _permissionResult.value = result
            } catch (e: Exception) {
                _multiplePermissionsState.value = PermissionResult.Error("Error checking multiple permissions: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Check a custom permission
     * @param permission The permission to check
     * @return StateFlow with the result
     */
    fun checkCustomPermission(permission: String): StateFlow<PermissionResult> {
        val stateFlow = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.checkPermission(context, permission)
                stateFlow.value = result
                _permissionResult.value = result
            } catch (e: Exception) {
                stateFlow.value = PermissionResult.Error("Error checking permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
        
        return stateFlow
    }

    /**
     * Check if all permissions in the array are granted
     * @param permissions Array of permissions to check
     * @return StateFlow with boolean result
     */
    fun areAllPermissionsGranted(permissions: Array<String>): StateFlow<Boolean> {
        val stateFlow = MutableStateFlow(false)
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.areAllPermissionsGranted(context, permissions)
                stateFlow.value = result
            } catch (e: Exception) {
                stateFlow.value = false
            } finally {
                _isLoading.value = false
            }
        }
        
        return stateFlow
    }

    /**
     * Clear the current permission result
     */
    fun clearPermissionResult() {
        _permissionResult.value = null
    }

    /**
     * Refresh all permission states
     */
    fun refreshAllPermissions() {
        checkInitialPermissions()
    }

    /**
     * Request camera permission (this would typically trigger a permission request dialog)
     * Note: In a real implementation, this would need to be called from an Activity context
     * and would require proper permission request handling
     */
    fun getCameraPermission() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First check current status
                val currentResult = repository.checkPermission(context, PermissionRepository.Permissions.CAMERA)
                
                when (currentResult) {
                    is PermissionResult.Granted -> {
                        _cameraPermissionState.value = PermissionResult.Granted
                        _permissionResult.value = PermissionResult.Granted
                    }
                    is PermissionResult.Denied -> {
                        // In a real app, this would trigger the permission request dialog
                        // For now, we'll simulate the request and show that it needs to be handled
                        _cameraPermissionState.value = PermissionResult.Denied
                        _permissionResult.value = PermissionResult.Denied
                        
                        // Log that permission request is needed
                        println("Camera permission request needed - this should trigger permission dialog in real app")
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        _cameraPermissionState.value = PermissionResult.PermanentlyDenied
                        _permissionResult.value = PermissionResult.PermanentlyDenied
                    }
                    is PermissionResult.Error -> {
                        _cameraPermissionState.value = currentResult
                        _permissionResult.value = currentResult
                    }
                    else -> {
                        _cameraPermissionState.value = PermissionResult.Denied
                        _permissionResult.value = PermissionResult.Denied
                    }
                }
            } catch (e: Exception) {
                _cameraPermissionState.value = PermissionResult.Error("Error requesting camera permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Request storage permission
     */
    fun getStoragePermission() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val storagePermission = repository.getStoragePermission(context)
                val currentResult = repository.checkPermission(context, storagePermission)
                
                when (currentResult) {
                    is PermissionResult.Granted -> {
                        _storagePermissionState.value = PermissionResult.Granted
                        _permissionResult.value = PermissionResult.Granted
                    }
                    is PermissionResult.Denied -> {
                        _storagePermissionState.value = PermissionResult.Denied
                        _permissionResult.value = PermissionResult.Denied
                        println("Storage permission request needed - this should trigger permission dialog in real app")
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        _storagePermissionState.value = PermissionResult.PermanentlyDenied
                        _permissionResult.value = PermissionResult.PermanentlyDenied
                    }
                    is PermissionResult.Error -> {
                        _storagePermissionState.value = currentResult
                        _permissionResult.value = currentResult
                    }
                    else -> {
                        _storagePermissionState.value = PermissionResult.Denied
                        _permissionResult.value = PermissionResult.Denied
                    }
                }
            } catch (e: Exception) {
                _storagePermissionState.value = PermissionResult.Error("Error requesting storage permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Request location permission
     */
    fun getLocationPermission() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentResult = repository.checkPermission(context, PermissionRepository.Permissions.ACCESS_FINE_LOCATION)
                
                when (currentResult) {
                    is PermissionResult.Granted -> {
                        _locationPermissionState.value = PermissionResult.Granted
                        _permissionResult.value = PermissionResult.Granted
                    }
                    is PermissionResult.Denied -> {
                        _locationPermissionState.value = PermissionResult.Denied
                        _permissionResult.value = PermissionResult.Denied
                        println("Location permission request needed - this should trigger permission dialog in real app")
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        _locationPermissionState.value = PermissionResult.PermanentlyDenied
                        _permissionResult.value = PermissionResult.PermanentlyDenied
                    }
                    is PermissionResult.Error -> {
                        _locationPermissionState.value = currentResult
                        _permissionResult.value = currentResult
                    }
                    else -> {
                        _locationPermissionState.value = PermissionResult.Denied
                        _permissionResult.value = PermissionResult.Denied
                    }
                }
            } catch (e: Exception) {
                _locationPermissionState.value = PermissionResult.Error("Error requesting location permission: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

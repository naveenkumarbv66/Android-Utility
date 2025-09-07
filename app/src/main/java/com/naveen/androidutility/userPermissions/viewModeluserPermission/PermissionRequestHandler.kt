package com.naveen.androidutility.userPermissions.viewModeluserPermission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PermissionRequestHandler - Handles actual permission requests with proper dialogs
 * This class provides the bridge between ViewModels and Activity permission requests
 */
class PermissionRequestHandler(private val activity: Activity) {

    // Permission request launcher for modern Android versions
    private val requestPermissionLauncher = if (activity is ComponentActivity) {
        try {
            activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                val result = if (isGranted) PermissionResult.Granted else PermissionResult.Denied
                _permissionResult.value = result
            }
        } catch (e: Exception) {
            println("Error registering permission launcher: ${e.message}")
            null
        }
    } else null

    // Multiple permissions request launcher
    private val requestMultiplePermissionsLauncher = if (activity is ComponentActivity) {
        try {
            activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val results = permissions.mapValues { (_, isGranted) ->
                    if (isGranted) PermissionResult.Granted else PermissionResult.Denied
                }
                _permissionResult.value = PermissionResult.MultiplePermissionsResult(results)
            }
        } catch (e: Exception) {
            println("Error registering multiple permissions launcher: ${e.message}")
            null
        }
    } else null

    // State flow for permission results
    private val _permissionResult = MutableStateFlow<PermissionResult?>(null)
    val permissionResult: StateFlow<PermissionResult?> = _permissionResult.asStateFlow()

    /**
     * Request a single permission
     * @param permission The permission to request
     */
    fun requestPermission(permission: String) {
        if (activity is ComponentActivity && requestPermissionLauncher != null) {
            // Use modern permission request for API 23+
            requestPermissionLauncher.launch(permission)
        } else {
            // Fallback for older versions or non-ComponentActivity
            _permissionResult.value = PermissionResult.Error("Permission request not supported on this device")
        }
    }

    /**
     * Request multiple permissions
     * @param permissions Array of permissions to request
     */
    fun requestMultiplePermissions(permissions: Array<String>) {
        if (activity is ComponentActivity && requestMultiplePermissionsLauncher != null) {
            // Use modern permission request for API 23+
            requestMultiplePermissionsLauncher.launch(permissions)
        } else {
            // Fallback for older versions or non-ComponentActivity
            _permissionResult.value = PermissionResult.Error("Multiple permission request not supported on this device")
        }
    }

    /**
     * Open app settings for manual permission grant
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    /**
     * Clear the current permission result
     */
    fun clearPermissionResult() {
        _permissionResult.value = null
    }
}

/**
 * Extension function to create PermissionRequestHandler from Activity
 */
fun Activity.createPermissionRequestHandler(): PermissionRequestHandler {
    return PermissionRequestHandler(this)
}

package com.naveen.androidutility.userPermissions.viewModeluserPermission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.flow.StateFlow

/**
 * PermissionManager - A simple interface for any ViewModel to request permissions
 * This class provides a clean, easy-to-use interface for permission management
 * following MVVM architecture with coroutines
 */
class PermissionManager private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: PermissionManager? = null

        fun getInstance(): PermissionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PermissionManager().also { INSTANCE = it }
            }
        }
    }

    /**
     * Get PermissionViewModel instance
     * @param viewModelStoreOwner The owner of the ViewModel (Activity, Fragment, etc.)
     * @param application The Application instance
     * @return PermissionViewModel instance
     */
    fun getPermissionViewModel(
        viewModelStoreOwner: ViewModelStoreOwner,
        application: android.app.Application
    ): PermissionViewModel {
        return ViewModelProvider(viewModelStoreOwner)[PermissionViewModel::class.java]
    }

    /**
     * Simple permission checker for any ViewModel
     * @param context The context
     * @param permission The permission to check
     * @return PermissionResult
     */
    suspend fun checkPermission(context: Context, permission: String): PermissionResult {
        val repository = PermissionRepository()
        return repository.checkPermission(context, permission)
    }

    /**
     * Check multiple permissions
     * @param context The context
     * @param permissions Array of permissions to check
     * @return PermissionResult with multiple results
     */
    suspend fun checkMultiplePermissions(
        context: Context,
        permissions: Array<String>
    ): PermissionResult {
        val repository = PermissionRepository()
        return repository.checkMultiplePermissions(context, permissions)
    }

    /**
     * Check if all permissions are granted
     * @param context The context
     * @param permissions Array of permissions to check
     * @return Boolean indicating if all permissions are granted
     */
    suspend fun areAllPermissionsGranted(
        context: Context,
        permissions: Array<String>
    ): Boolean {
        val repository = PermissionRepository()
        return repository.areAllPermissionsGranted(context, permissions)
    }

    /**
     * Open app settings for manual permission grant
     * @param activity The activity to start the settings intent
     */
    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    /**
     * Get permission status as a readable string
     * @param result The PermissionResult
     * @return String representation of the permission status
     */
    fun getPermissionStatusString(result: PermissionResult): String {
        return when (result) {
            is PermissionResult.Granted -> "Granted ✅"
            is PermissionResult.Denied -> "Denied ⚠️"
            is PermissionResult.PermanentlyDenied -> "Permanently Denied ❌"
            is PermissionResult.MultiplePermissionsResult -> {
                val grantedCount = result.results.values.count { it is PermissionResult.Granted }
                val totalCount = result.results.size
                "Multiple: $grantedCount/$totalCount granted"
            }
            is PermissionResult.Error -> "Error: ${result.message}"
        }
    }

    /**
     * Check if permission result indicates success
     * @param result The PermissionResult
     * @return Boolean indicating if permission is granted
     */
    fun isPermissionGranted(result: PermissionResult): Boolean {
        return when (result) {
            is PermissionResult.Granted -> true
            is PermissionResult.MultiplePermissionsResult -> {
                result.results.values.all { it is PermissionResult.Granted }
            }
            else -> false
        }
    }
}

/**
 * Extension function for easy permission checking in any ViewModel
 */
suspend fun Context.checkPermission(permission: String): PermissionResult {
    return PermissionManager.getInstance().checkPermission(this, permission)
}

/**
 * Extension function for easy multiple permission checking in any ViewModel
 */
suspend fun Context.checkMultiplePermissions(permissions: Array<String>): PermissionResult {
    return PermissionManager.getInstance().checkMultiplePermissions(this, permissions)
}

/**
 * Extension function for easy permission checking with boolean result
 */
suspend fun Context.isPermissionGranted(permission: String): Boolean {
    val result = PermissionManager.getInstance().checkPermission(this, permission)
    return PermissionManager.getInstance().isPermissionGranted(result)
}

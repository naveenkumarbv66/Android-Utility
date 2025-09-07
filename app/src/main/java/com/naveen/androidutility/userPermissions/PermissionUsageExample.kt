package com.naveen.androidutility.userPermissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity

/**
 * Example usage of UserPermissionHelper
 * This class demonstrates how to use the UserPermissionHelper in your activities
 */
class PermissionUsageExample(private val activity: Activity) {

    private val permissionHelper = UserPermissionHelper(activity)

    /**
     * Example: Check camera permission status
     */
    fun checkCameraPermission(): Boolean {
        return permissionHelper.isPermissionGranted(Manifest.permission.CAMERA)
    }

    /**
     * Example: Request camera permission with callback
     */
    fun requestCameraPermission(onResult: (Boolean) -> Unit) {
        permissionHelper.requestPermission(
            Manifest.permission.CAMERA
        ) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with camera functionality
                onResult(true)
            } else {
                // Permission denied, handle accordingly
                handlePermissionDenied(Manifest.permission.CAMERA)
                onResult(false)
            }
        }
    }

    /**
     * Example: Request multiple storage permissions
     */
    fun requestStoragePermissions(onResult: (Map<String, Boolean>) -> Unit) {
        val permissions = arrayOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_VIDEO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )

        permissionHelper.requestMultiplePermissions(permissions) { results ->
            val allGranted = results.values.all { it }
            if (allGranted) {
                // All permissions granted
                onResult(results)
            } else {
                // Some permissions denied
                handleMultiplePermissionsDenied(results)
                onResult(results)
            }
        }
    }

    /**
     * Example: Check permission status with detailed information
     */
    fun checkDetailedPermissionStatus(permission: String): String {
        return when (permissionHelper.getPermissionStatus(permission)) {
            UserPermissionHelper.PermissionStatus.GRANTED -> "Permission is granted"
            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> "Permission denied, can ask again"
            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> "Permission denied permanently"
        }
    }

    /**
     * Example: One-click permission check and request
     */
    fun oneClickPermissionCheck(permission: String, onResult: (Boolean) -> Unit) {
        if (permissionHelper.isPermissionGranted(permission)) {
            // Permission already granted
            onResult(true)
        } else {
            // Request permission
            permissionHelper.requestPermission(permission) { isGranted ->
                onResult(isGranted)
            }
        }
    }

    /**
     * Example: Handle permission denied scenarios
     */
    private fun handlePermissionDenied(permission: String) {
        when (permissionHelper.getPermissionStatus(permission)) {
            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                // Show rationale and ask again
                showPermissionRationale(permission)
            }
            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                // Open app settings
                openAppSettings()
            }
            else -> {
                // Permission granted (shouldn't reach here)
            }
        }
    }

    /**
     * Example: Handle multiple permissions denied
     */
    private fun handleMultiplePermissionsDenied(results: Map<String, Boolean>) {
        val deniedPermissions = results.filter { !it.value }.keys
        
        if (deniedPermissions.any { permissionHelper.getPermissionStatus(it) == UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY }) {
            // At least one permission is permanently denied
            openAppSettings()
        } else {
            // All denied permissions can be asked again
            showMultiplePermissionsRationale(deniedPermissions.toList())
        }
    }

    /**
     * Show rationale for a single permission
     */
    private fun showPermissionRationale(permission: String) {
        // Implement your rationale dialog here
        // For example, show an AlertDialog explaining why the permission is needed
    }

    /**
     * Show rationale for multiple permissions
     */
    private fun showMultiplePermissionsRationale(permissions: List<String>) {
        // Implement your rationale dialog here
        // For example, show an AlertDialog explaining why the permissions are needed
    }

    /**
     * Open app settings for manual permission grant
     */
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
}

/**
 * Extension functions for easier usage in Activities
 */
fun ComponentActivity.createPermissionHelper(): UserPermissionHelper {
    return UserPermissionHelper(this)
}

/**
 * Quick permission check extension
 */
fun Activity.isPermissionGranted(permission: String): Boolean {
    return UserPermissionHelper(this).isPermissionGranted(permission)
}

/**
 * Quick permission request extension
 */
fun Activity.requestPermission(permission: String, onResult: (Boolean) -> Unit) {
    UserPermissionHelper(this).requestPermission(permission, onResult)
}

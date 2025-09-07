package com.naveen.androidutility.userPermissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * UserPermissionHelper - A comprehensive helper class for handling runtime permissions
 * Supports Android API levels from 24 (Android 7.0) to latest versions
 * 
 * Features:
 * - Check permission status with a single method call
 * - Request permissions with proper result handling
 * - Support for both single and multiple permissions
 * - Automatic handling of permission rationale
 * - Compatible with all Android versions
 */
class UserPermissionHelper(private val activity: Activity) {

    // Permission request launcher for modern Android versions
    private val requestPermissionLauncher = if (activity is ComponentActivity) {
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            println("UserPermissionHelper: Permission launcher result: $isGranted")
            onPermissionResult?.invoke(isGranted)
        }
    } else null

    // Multiple permissions request launcher
    private val requestMultiplePermissionsLauncher = if (activity is ComponentActivity) {
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            onMultiplePermissionsResult?.invoke(permissions)
        }
    } else null

    // Callback for single permission result
    private var onPermissionResult: ((Boolean) -> Unit)? = null

    // Callback for multiple permissions result
    private var onMultiplePermissionsResult: ((Map<String, Boolean>) -> Unit)? = null

    /**
     * Check if a single permission is granted
     * @param permission The permission to check
     * @return true if permission is granted, false otherwise
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if multiple permissions are granted
     * @param permissions Array of permissions to check
     * @return Map of permission to grant status
     */
    fun checkMultiplePermissions(permissions: Array<String>): Map<String, Boolean> {
        return permissions.associateWith { permission ->
            isPermissionGranted(permission)
        }
    }

    /**
     * Check if all permissions in the array are granted
     * @param permissions Array of permissions to check
     * @return true if all permissions are granted, false otherwise
     */
    fun areAllPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }

    /**
     * Request a single permission with result callback
     * @param permission The permission to request
     * @param onResult Callback function that receives the result
     */
    fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        println("UserPermissionHelper: Requesting permission: $permission")
        onPermissionResult = onResult
        
        if (isPermissionGranted(permission)) {
            println("UserPermissionHelper: Permission already granted")
            onResult(true)
            return
        }

        if (activity is ComponentActivity && requestPermissionLauncher != null) {
            // Use modern permission request for API 23+
            println("UserPermissionHelper: Using modern permission launcher")
            requestPermissionLauncher.launch(permission)
        } else {
            // Fallback for older versions or non-ComponentActivity
            println("UserPermissionHelper: Using legacy permission request")
            requestPermissionLegacy(permission, onResult)
        }
    }

    /**
     * Request multiple permissions with result callback
     * @param permissions Array of permissions to request
     * @param onResult Callback function that receives the results map
     */
    fun requestMultiplePermissions(permissions: Array<String>, onResult: (Map<String, Boolean>) -> Unit) {
        onMultiplePermissionsResult = onResult
        
        val permissionsToRequest = permissions.filter { !isPermissionGranted(it) }
        
        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            onResult(permissions.associateWith { true })
            return
        }

        if (activity is ComponentActivity && requestMultiplePermissionsLauncher != null) {
            // Use modern permission request for API 23+
            requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Fallback for older versions or non-ComponentActivity
            requestMultiplePermissionsLegacy(permissions, onResult)
        }
    }

    /**
     * Check if we should show rationale for a permission
     * @param permission The permission to check
     * @return true if rationale should be shown, false otherwise
     */
    fun shouldShowRationale(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.shouldShowRequestPermissionRationale(permission)
        } else {
            false
        }
    }

    /**
     * Get permission status with detailed information
     * @param permission The permission to check
     * @return PermissionStatus enum value
     */
    fun getPermissionStatus(permission: String): PermissionStatus {
        return when {
            isPermissionGranted(permission) -> PermissionStatus.GRANTED
            shouldShowRationale(permission) -> PermissionStatus.DENIED_CAN_ASK_AGAIN
            else -> PermissionStatus.DENIED_PERMANENTLY
        }
    }

    /**
     * Legacy method for requesting single permission (for older Android versions)
     */
    private fun requestPermissionLegacy(permission: String, onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
            // Note: For legacy support, you would need to override onRequestPermissionsResult
            // in your Activity and handle the result there
        } else {
            // For API < 23, permissions are granted at install time
            onResult(true)
        }
    }

    /**
     * Legacy method for requesting multiple permissions (for older Android versions)
     */
    private fun requestMultiplePermissionsLegacy(permissions: Array<String>, onResult: (Map<String, Boolean>) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE)
            // Note: For legacy support, you would need to override onRequestPermissionsResult
            // in your Activity and handle the result there
        } else {
            // For API < 23, permissions are granted at install time
            onResult(permissions.associateWith { true })
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val MULTIPLE_PERMISSIONS_REQUEST_CODE = 1002

        // Common permission constants for easy access
        object Permissions {
            const val CAMERA = Manifest.permission.CAMERA
            const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
            const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
            
            // Android 13+ (API 33+) media permissions
            const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
            const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO
            const val READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO
            
            const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
            const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
            const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
            const val CALL_PHONE = Manifest.permission.CALL_PHONE
            const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
            const val WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS
            const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
            const val SEND_SMS = Manifest.permission.SEND_SMS
            const val READ_SMS = Manifest.permission.READ_SMS
        }
        
        /**
         * Helper function to get the appropriate storage permission based on Android version
         * @param context The context to check Android version
         * @return The appropriate permission string
         */
        fun getStoragePermission(context: Context): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Permissions.READ_MEDIA_IMAGES
            } else {
                Permissions.READ_EXTERNAL_STORAGE
            }
        }
        
        /**
         * Helper function to get the appropriate video permission based on Android version
         * @param context The context to check Android version
         * @return The appropriate permission string
         */
        fun getVideoPermission(context: Context): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Permissions.READ_MEDIA_VIDEO
            } else {
                Permissions.READ_EXTERNAL_STORAGE
            }
        }
        
        /**
         * Helper function to get the appropriate audio permission based on Android version
         * @param context The context to check Android version
         * @return The appropriate permission string
         */
        fun getAudioPermission(context: Context): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Permissions.READ_MEDIA_AUDIO
            } else {
                Permissions.READ_EXTERNAL_STORAGE
            }
        }
    }

    /**
     * Enum representing different permission states
     */
    enum class PermissionStatus {
        GRANTED,                    // Permission is granted
        DENIED_CAN_ASK_AGAIN,       // Permission denied, can ask again
        DENIED_PERMANENTLY          // Permission denied permanently (user selected "Don't ask again")
    }
}

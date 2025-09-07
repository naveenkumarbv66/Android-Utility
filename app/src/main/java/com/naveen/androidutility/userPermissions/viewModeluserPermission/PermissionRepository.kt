package com.naveen.androidutility.userPermissions.viewModeluserPermission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/**
 * Repository for handling permission operations
 * This class provides a clean interface for permission-related operations
 * and handles the complexity of different Android versions
 */
class PermissionRepository {

    /**
     * Check if a single permission is granted
     * @param context The context to check permissions
     * @param permission The permission to check
     * @return PermissionResult indicating the status
     */
    suspend fun checkPermission(
        context: Context,
        permission: String
    ): PermissionResult = withContext(Dispatchers.IO) {
        try {
            val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            
            if (isGranted) {
                PermissionResult.Granted
            } else {
                // Check if we should show rationale
                val shouldShowRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // For API 23+, we need an Activity context to check shouldShowRequestPermissionRationale
                    // Since we only have Context here, we'll return Denied and let the ViewModel handle it
                    false
                } else {
                    false
                }
                
                PermissionResult.Denied
            }
        } catch (e: Exception) {
            PermissionResult.Error("Error checking permission: ${e.message}", e)
        }
    }

    /**
     * Check multiple permissions
     * @param context The context to check permissions
     * @param permissions Array of permissions to check
     * @return PermissionResult with multiple results
     */
    suspend fun checkMultiplePermissions(
        context: Context,
        permissions: Array<String>
    ): PermissionResult = withContext(Dispatchers.IO) {
        try {
            val results = permissions.associateWith { permission ->
                val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                if (isGranted) PermissionResult.Granted else PermissionResult.Denied
            }
            
            PermissionResult.MultiplePermissionsResult(results)
        } catch (e: Exception) {
            PermissionResult.Error("Error checking multiple permissions: ${e.message}", e)
        }
    }

    /**
     * Check if all permissions in the array are granted
     * @param context The context to check permissions
     * @param permissions Array of permissions to check
     * @return true if all permissions are granted, false otherwise
     */
    suspend fun areAllPermissionsGranted(
        context: Context,
        permissions: Array<String>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the appropriate storage permission based on Android version
     * @param context The context to check Android version
     * @return The appropriate permission string
     */
    fun getStoragePermission(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    /**
     * Get the appropriate video permission based on Android version
     * @param context The context to check Android version
     * @return The appropriate permission string
     */
    fun getVideoPermission(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    /**
     * Get the appropriate audio permission based on Android version
     * @param context The context to check Android version
     * @return The appropriate permission string
     */
    fun getAudioPermission(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    /**
     * Common permission constants for easy access
     */
    object Permissions {
        const val CAMERA = Manifest.permission.CAMERA
        const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
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
}

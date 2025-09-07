package com.naveen.androidutility.userPermissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Examples showing how to use permissionHelper.getPermissionStatus()
 * This class demonstrates different ways to check and handle permission status
 */
class PermissionStatusExamples(private val activity: Activity) {

    private val permissionHelper = UserPermissionHelper(activity)

    /**
     * Example 1: Basic permission status check
     */
    fun basicStatusCheck() {
        val status = permissionHelper.getPermissionStatus(Manifest.permission.CAMERA)
        
        when (status) {
            UserPermissionHelper.PermissionStatus.GRANTED -> {
                println("✅ Camera permission is granted")
                // Proceed with camera functionality
                openCamera()
            }
            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                println("⚠️ Camera permission denied, but can ask again")
                // Show rationale and request permission
                showRationaleAndRequest()
            }
            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                println("❌ Camera permission permanently denied")
                // Redirect to app settings
                openAppSettings()
            }
        }
    }

    /**
     * Example 2: Check multiple permissions and handle each status
     */
    fun checkMultiplePermissionsStatus() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        permissions.forEach { permission ->
            val status = permissionHelper.getPermissionStatus(permission)
            val permissionName = permission.substringAfterLast(".")
            
            when (status) {
                UserPermissionHelper.PermissionStatus.GRANTED -> {
                    println("✅ $permissionName: Granted")
                }
                UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                    println("⚠️ $permissionName: Can ask again")
                }
                UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                    println("❌ $permissionName: Permanently denied")
                }
            }
        }
    }

    /**
     * Example 3: Smart permission handling with status-based actions
     */
    fun smartPermissionHandling(permission: String, onResult: (Boolean) -> Unit) {
        val status = permissionHelper.getPermissionStatus(permission)
        
        when (status) {
            UserPermissionHelper.PermissionStatus.GRANTED -> {
                // Permission already granted, proceed immediately
                onResult(true)
            }
            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                // Show rationale if needed, then request permission
                if (permissionHelper.shouldShowRationale(permission)) {
                    showRationaleDialog(permission) {
                        requestPermission(permission, onResult)
                    }
                } else {
                    requestPermission(permission, onResult)
                }
            }
            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                // Permission permanently denied, show settings dialog
                showSettingsDialog(permission)
                onResult(false)
            }
        }
    }

    /**
     * Example 4: Permission status with detailed logging
     */
    fun detailedStatusLogging(permission: String) {
        val status = permissionHelper.getPermissionStatus(permission)
        val permissionName = permission.substringAfterLast(".")
        
        println("=== Permission Status Report ===")
        println("Permission: $permissionName")
        println("Status: $status")
        
        when (status) {
            UserPermissionHelper.PermissionStatus.GRANTED -> {
                println("Action: Permission is available for use")
                println("Next Step: Proceed with functionality")
            }
            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                println("Action: Permission can be requested")
                println("Next Step: Show rationale and request permission")
                println("Rationale should be shown: ${permissionHelper.shouldShowRationale(permission)}")
            }
            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                println("Action: Permission is permanently denied")
                println("Next Step: Redirect user to app settings")
                println("User must manually enable permission in settings")
            }
        }
        println("================================")
    }

    /**
     * Example 5: Conditional permission request based on status
     */
    fun conditionalPermissionRequest(permission: String) {
        val status = permissionHelper.getPermissionStatus(permission)
        
        when (status) {
            UserPermissionHelper.PermissionStatus.GRANTED -> {
                // Already have permission, no need to request
                println("Permission already granted, proceeding...")
            }
            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                // Can request permission
                println("Requesting permission...")
                permissionHelper.requestPermission(permission) { isGranted ->
                    if (isGranted) {
                        println("Permission granted after request")
                    } else {
                        println("Permission denied after request")
                    }
                }
            }
            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                // Cannot request permission, must go to settings
                println("Cannot request permission, redirecting to settings...")
                openAppSettings()
            }
        }
    }

    // Helper methods (these would be implemented in a real app)
    private fun openCamera() {
        println("Opening camera...")
        // Implement camera opening logic
    }

    private fun showRationaleAndRequest() {
        println("Showing rationale dialog...")
        // Implement rationale dialog
    }

    private fun showRationaleDialog(permission: String, onContinue: () -> Unit) {
        println("Showing rationale for $permission")
        // Implement rationale dialog with onContinue callback
        onContinue()
    }

    private fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        permissionHelper.requestPermission(permission, onResult)
    }

    private fun showSettingsDialog(permission: String) {
        println("Showing settings dialog for $permission")
        // Implement settings dialog
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
}

/**
 * Extension function for easy permission status checking
 */
fun Activity.getPermissionStatus(permission: String): UserPermissionHelper.PermissionStatus {
    return UserPermissionHelper(this).getPermissionStatus(permission)
}

/**
 * Extension function for smart permission handling
 */
fun Activity.handlePermissionSmart(permission: String, onResult: (Boolean) -> Unit) {
    val helper = UserPermissionHelper(this)
    val status = helper.getPermissionStatus(permission)
    
    when (status) {
        UserPermissionHelper.PermissionStatus.GRANTED -> onResult(true)
        UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
            helper.requestPermission(permission, onResult)
        }
        UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
            // Open settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
            onResult(false)
        }
    }
}

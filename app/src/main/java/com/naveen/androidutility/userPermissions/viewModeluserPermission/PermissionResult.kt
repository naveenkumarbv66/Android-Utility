package com.naveen.androidutility.userPermissions.viewModeluserPermission

/**
 * Sealed class representing the result of a permission request
 * This provides a clean way to handle different permission states
 */
sealed class PermissionResult {
    /**
     * Permission is granted
     */
    object Granted : PermissionResult()
    
    /**
     * Permission is denied but can be requested again
     */
    object Denied : PermissionResult()
    
    /**
     * Permission is permanently denied (user selected "Don't ask again")
     */
    object PermanentlyDenied : PermissionResult()
    
    /**
     * Multiple permissions result
     * @param results Map of permission to its result
     */
    data class MultiplePermissionsResult(
        val results: Map<String, PermissionResult>
    ) : PermissionResult()
    
    /**
     * Error occurred during permission request
     * @param message Error message
     * @param throwable Optional throwable
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : PermissionResult()
}

/**
 * Extension function to check if permission is granted
 */
fun PermissionResult.isGranted(): Boolean = this is PermissionResult.Granted

/**
 * Extension function to check if permission is denied
 */
fun PermissionResult.isDenied(): Boolean = this is PermissionResult.Denied

/**
 * Extension function to check if permission is permanently denied
 */
fun PermissionResult.isPermanentlyDenied(): Boolean = this is PermissionResult.PermanentlyDenied

/**
 * Extension function to check if there's an error
 */
fun PermissionResult.isError(): Boolean = this is PermissionResult.Error

/**
 * Extension function to get error message if available
 */
fun PermissionResult.getErrorMessage(): String? = if (this is PermissionResult.Error) this.message else null

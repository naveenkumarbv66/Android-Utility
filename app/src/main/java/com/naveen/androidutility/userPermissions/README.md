# UserPermissionHelper

A comprehensive Android utility class for handling runtime permissions with support for all Android versions from API 24 (Android 7.0) to the latest versions.

## Features

- ✅ **Simple Permission Checking**: Check permission status with a single method call
- ✅ **Easy Permission Requests**: Request permissions with proper result handling
- ✅ **Multiple Permissions Support**: Handle multiple permissions at once
- ✅ **Version Compatibility**: Works seamlessly from Android 7.0 to latest versions
- ✅ **Modern API Support**: Uses ActivityResultContracts for modern Android versions
- ✅ **Legacy Support**: Fallback support for older Android versions
- ✅ **Detailed Status**: Get detailed permission status (granted, denied, permanently denied)
- ✅ **Rationale Handling**: Built-in support for permission rationale

## Quick Start

### 1. Initialize the Helper

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var permissionHelper: UserPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = UserPermissionHelper(this)
    }
}
```

### 2. Check Permission Status

```kotlin
// Check if camera permission is granted
val isCameraGranted = permissionHelper.isPermissionGranted(Manifest.permission.CAMERA)

// Check multiple permissions
val permissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_MEDIA_IMAGES
)
val results = permissionHelper.checkMultiplePermissions(permissions)
```

### 3. Request Permissions

```kotlin
// Request single permission
permissionHelper.requestPermission(Manifest.permission.CAMERA) { isGranted ->
    if (isGranted) {
        // Permission granted, proceed with camera functionality
        openCamera()
    } else {
        // Permission denied, handle accordingly
        showPermissionDeniedMessage()
    }
}

// Request multiple permissions
val permissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.ACCESS_FINE_LOCATION
)

permissionHelper.requestMultiplePermissions(permissions) { results ->
    val allGranted = results.values.all { it }
    if (allGranted) {
        // All permissions granted
        proceedWithAllFeatures()
    } else {
        // Some permissions denied
        handlePartialPermissions(results)
    }
}
```

### 4. One-Click Permission Check and Request

```kotlin
// Check and request permission in one call
permissionHelper.requestPermission(Manifest.permission.CAMERA) { isGranted ->
    if (isGranted) {
        // Permission is available, proceed
        openCamera()
    } else {
        // Permission denied
        showPermissionDeniedMessage()
    }
}
```

## Available Permissions

Use the standard Android Manifest permissions:

```kotlin
Manifest.permission.CAMERA
Manifest.permission.READ_EXTERNAL_STORAGE
Manifest.permission.WRITE_EXTERNAL_STORAGE
Manifest.permission.READ_MEDIA_IMAGES      // Android 13+
Manifest.permission.READ_MEDIA_VIDEO       // Android 13+
Manifest.permission.READ_MEDIA_AUDIO       // Android 13+
Manifest.permission.RECORD_AUDIO
Manifest.permission.ACCESS_FINE_LOCATION
Manifest.permission.ACCESS_COARSE_LOCATION
Manifest.permission.CALL_PHONE
Manifest.permission.READ_CONTACTS
Manifest.permission.WRITE_CONTACTS
Manifest.permission.READ_PHONE_STATE
Manifest.permission.SEND_SMS
Manifest.permission.READ_SMS
```

**Note**: The `UserPermissionHelper` also provides convenience constants in its companion object, but using `Manifest.permission.*` is the standard approach.

### Version-Aware Permission Helpers

For storage permissions that changed in Android 13, use the helper functions:

```kotlin
// Get the appropriate storage permission based on Android version
val storagePermission = UserPermissionHelper.getStoragePermission(context)
val videoPermission = UserPermissionHelper.getVideoPermission(context)
val audioPermission = UserPermissionHelper.getAudioPermission(context)

// Use in permission requests
permissionHelper.requestPermission(storagePermission) { isGranted ->
    // Handle result
}
```

## Advanced Usage

### Get Detailed Permission Status

```kotlin
val status = permissionHelper.getPermissionStatus(Manifest.permission.CAMERA)
when (status) {
    UserPermissionHelper.PermissionStatus.GRANTED -> {
        // Permission is granted
    }
    UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
        // Permission denied, can ask again
    }
    UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
        // Permission denied permanently, redirect to settings
        openAppSettings()
    }
}
```

### Check if Rationale Should be Shown

```kotlin
if (permissionHelper.shouldShowRationale(Manifest.permission.CAMERA)) {
    // Show rationale dialog explaining why permission is needed
    showRationaleDialog()
}
```

### Extension Functions

For even easier usage, you can use the provided extension functions:

```kotlin
// Quick permission check
val isGranted = this.isPermissionGranted(Manifest.permission.CAMERA)

// Quick permission request
this.requestPermission(Manifest.permission.CAMERA) { isGranted ->
    // Handle result
}
```

## Android Version Compatibility

- **Android 7.0+ (API 24+)**: Full support with modern ActivityResultContracts
- **Android 6.0 (API 23)**: Full support with legacy requestPermissions
- **Android 5.1 and below**: Permissions are granted at install time

## Best Practices

1. **Always check permission status before requesting**
2. **Show rationale when appropriate** using `shouldShowRationale()`
3. **Handle all permission states** (granted, denied, permanently denied)
4. **Use specific permissions** instead of broad ones when possible
5. **Test on different Android versions** to ensure compatibility

## Example Implementation

See `MainActivity.kt` for a complete working example that demonstrates:
- Permission status checking
- Single permission requests
- Multiple permission requests
- UI updates based on permission results

## Requirements

- Android API 24+ (Android 7.0)
- Kotlin
- AndroidX libraries
- Compose (for the demo UI)

## License

This utility is part of the Android Utility project and follows the same license terms.

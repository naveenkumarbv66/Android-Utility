# MVVM Permission Library

A comprehensive, clean architecture-based permission library for Android that supports MVVM pattern with coroutines and StateFlow. This library provides a simple, easy-to-use interface for any ViewModel to handle runtime permissions.

## 🚀 Features

- ✅ **MVVM Architecture**: Clean separation of concerns with ViewModels
- ✅ **Coroutines Support**: All operations are asynchronous using coroutines
- ✅ **StateFlow Integration**: Reactive UI updates with StateFlow
- ✅ **Clean Architecture**: Repository pattern with dependency injection ready
- ✅ **Android Version Compatibility**: Supports Android 7.0+ (API 24+)
- ✅ **Easy Integration**: Simple interface for any ViewModel
- ✅ **Type Safety**: Sealed classes for permission results
- ✅ **Error Handling**: Comprehensive error handling with proper exceptions

## 📦 Architecture

```
viewModeluserPermission/
├── PermissionResult.kt          # Sealed class for permission results
├── PermissionRepository.kt      # Repository for permission operations
├── PermissionViewModel.kt       # Main ViewModel for permission management
├── PermissionManager.kt         # Singleton manager for easy access
├── PermissionTestActivity.kt    # Test activity demonstrating usage
├── ExampleUsageViewModel.kt     # Example ViewModels showing integration
└── README.md                   # This documentation
```

## 🏗️ Components

### 1. PermissionResult
Sealed class representing different permission states:
- `Granted` - Permission is granted
- `Denied` - Permission denied but can ask again
- `PermanentlyDenied` - Permission permanently denied
- `MultiplePermissionsResult` - Result for multiple permissions
- `Error` - Error occurred during permission check

### 2. PermissionRepository
Repository layer handling all permission operations:
- Check single permission
- Check multiple permissions
- Version-aware permission handling
- Error handling and logging

### 3. PermissionViewModel
Main ViewModel providing reactive permission states:
- StateFlow for each permission type
- Coroutine-based operations
- Automatic initial permission checking
- Clean interface for UI components

### 4. PermissionManager
Singleton manager for easy integration:
- Simple interface for any ViewModel
- Extension functions for Context
- Utility methods for permission handling

## 📱 Usage

### Basic Usage in Any ViewModel

```kotlin
class MyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val permissionManager = PermissionManager.getInstance()
    
    private val _cameraPermission = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
    val cameraPermission: StateFlow<PermissionResult> = _cameraPermission.asStateFlow()
    
    fun checkCameraPermission() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val result = permissionManager.checkPermission(
                context, 
                PermissionRepository.Permissions.CAMERA
            )
            _cameraPermission.value = result
        }
    }
}
```

### Using the Dedicated PermissionViewModel

```kotlin
class MyActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val permissionManager = PermissionManager.getInstance()
        val permissionViewModel = permissionManager.getPermissionViewModel(
            viewModelStoreOwner = this,
            application = application
        )
        
        // Observe permission states
        lifecycleScope.launch {
            permissionViewModel.cameraPermissionState.collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        // Permission granted, proceed with camera functionality
                    }
                    is PermissionResult.Denied -> {
                        // Permission denied, show rationale
                    }
                    is PermissionResult.PermanentlyDenied -> {
                        // Permission permanently denied, redirect to settings
                    }
                    is PermissionResult.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }
}
```

### Extension Functions for Easy Usage

```kotlin
// In any ViewModel
viewModelScope.launch {
    val context = getApplication<Application>().applicationContext
    
    // Check single permission
    val result = context.checkPermission(Manifest.permission.CAMERA)
    
    // Check multiple permissions
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )
    val multipleResult = context.checkMultiplePermissions(permissions)
    
    // Check if permission is granted (boolean result)
    val isGranted = context.isPermissionGranted(Manifest.permission.CAMERA)
}
```

### Compose UI Integration

```kotlin
@Composable
fun MyScreen() {
    val permissionManager = PermissionManager.getInstance()
    val permissionViewModel = permissionManager.getPermissionViewModel(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
        application = LocalContext.current.applicationContext as Application
    )
    
    val cameraPermission by permissionViewModel.cameraPermissionState.collectAsStateWithLifecycle()
    
    Column {
        Text(
            text = "Camera Permission: ${permissionManager.getPermissionStatusString(cameraPermission)}"
        )
        
        Button(
            onClick = { permissionViewModel.checkCameraPermission() }
        ) {
            Text("Check Camera Permission")
        }
    }
}
```

## 🔧 Advanced Usage

### Custom Permission Checking

```kotlin
class CustomViewModel(application: Application) : AndroidViewModel(application) {
    
    private val permissionManager = PermissionManager.getInstance()
    
    fun checkCustomPermission(permission: String): StateFlow<PermissionResult> {
        val stateFlow = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
        
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val result = permissionManager.checkPermission(context, permission)
            stateFlow.value = result
        }
        
        return stateFlow
    }
}
```

### Multiple Permissions Handling

```kotlin
fun checkAllRequiredPermissions() {
    viewModelScope.launch {
        val context = getApplication<Application>().applicationContext
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        
        val result = permissionManager.checkMultiplePermissions(context, permissions)
        
        when (result) {
            is PermissionResult.MultiplePermissionsResult -> {
                result.results.forEach { (permission, permissionResult) ->
                    when (permissionResult) {
                        is PermissionResult.Granted -> {
                            // Handle granted permission
                        }
                        is PermissionResult.Denied -> {
                            // Handle denied permission
                        }
                        is PermissionResult.PermanentlyDenied -> {
                            // Handle permanently denied permission
                        }
                    }
                }
            }
            is PermissionResult.Error -> {
                // Handle error
            }
        }
    }
}
```

## 🎯 Best Practices

### 1. Use StateFlow for Reactive UI
```kotlin
private val _permissionState = MutableStateFlow<PermissionResult>(PermissionResult.Denied)
val permissionState: StateFlow<PermissionResult> = _permissionState.asStateFlow()
```

### 2. Handle All Permission States
```kotlin
when (result) {
    is PermissionResult.Granted -> { /* Proceed with functionality */ }
    is PermissionResult.Denied -> { /* Show rationale and request */ }
    is PermissionResult.PermanentlyDenied -> { /* Redirect to settings */ }
    is PermissionResult.Error -> { /* Handle error gracefully */ }
}
```

### 3. Use Coroutines for Async Operations
```kotlin
viewModelScope.launch {
    val result = permissionManager.checkPermission(context, permission)
    // Update UI state
}
```

### 4. Check Permissions on ViewModel Initialization
```kotlin
init {
    checkAllRequiredPermissions()
}
```

## 🔍 Testing

The library includes a test activity (`PermissionTestActivity`) that demonstrates:
- Basic permission checking
- Multiple permission handling
- StateFlow integration
- Error handling
- UI updates

To test the library:
1. Run the app
2. Navigate to `PermissionTestActivity` (not called from MainActivity as requested)
3. Test different permission scenarios
4. Observe StateFlow updates in the UI

## 📋 Requirements

- Android API 24+ (Android 7.0)
- Kotlin
- Coroutines
- StateFlow
- Compose (for UI components)

## 🚀 Integration

1. Copy the `viewModeluserPermission` package to your project
2. Add required dependencies (if not already present):
   ```kotlin
   implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
   implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
   ```
3. Use `PermissionManager.getInstance()` in your ViewModels
4. Observe StateFlow results in your UI

## 🎉 Benefits

- **Clean Architecture**: Follows MVVM and Repository patterns
- **Reactive**: StateFlow provides reactive UI updates
- **Type Safe**: Sealed classes prevent runtime errors
- **Easy to Use**: Simple interface for any ViewModel
- **Comprehensive**: Handles all permission scenarios
- **Testable**: Clean separation makes testing easy
- **Maintainable**: Well-structured code with clear responsibilities

This library provides a production-ready solution for permission management in Android applications following modern architecture principles.

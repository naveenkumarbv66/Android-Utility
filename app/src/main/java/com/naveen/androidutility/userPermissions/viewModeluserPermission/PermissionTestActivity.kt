package com.naveen.androidutility.userPermissions.viewModeluserPermission

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.naveen.androidutility.ui.theme.AndroidUtilityTheme

/**
 * Test Activity to demonstrate the MVVM Permission Library
 * This activity shows how to use the PermissionViewModel and PermissionManager
 * in a real application following clean architecture principles
 */
class PermissionTestActivity : ComponentActivity() {

    private lateinit var permissionRequestHandler: PermissionRequestHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize permission request handler in onCreate before setContent
        permissionRequestHandler = PermissionRequestHandler(this)
        
        setContent {
            AndroidUtilityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PermissionTestScreen(
                        modifier = Modifier.padding(innerPadding),
                        permissionRequestHandler = permissionRequestHandler
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionTestScreen(
    modifier: Modifier = Modifier,
    permissionRequestHandler: PermissionRequestHandler
) {
    val context = LocalContext.current
    val permissionManager = PermissionManager.getInstance()
    val permissionViewModel = permissionManager.getPermissionViewModel(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
        application = context.applicationContext as android.app.Application
    )

    // Collect state flows
    val cameraPermissionState by permissionViewModel.cameraPermissionState.collectAsStateWithLifecycle()
    val storagePermissionState by permissionViewModel.storagePermissionState.collectAsStateWithLifecycle()
    val locationPermissionState by permissionViewModel.locationPermissionState.collectAsStateWithLifecycle()
    val multiplePermissionsState by permissionViewModel.multiplePermissionsState.collectAsStateWithLifecycle()
    val isLoading by permissionViewModel.isLoading.collectAsStateWithLifecycle()

    // Local state for custom permission testing
    var customPermissionResult by remember { mutableStateOf<PermissionResult?>(null) }
    var allPermissionsResult by remember { mutableStateOf(false) }
    
    // Collect permission request results
    val permissionRequestResult by permissionRequestHandler.permissionResult.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "MVVM Permission Library Test",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "This demonstrates the clean MVVM permission library with coroutines",
            style = MaterialTheme.typography.bodyMedium
        )

        // Loading indicator
        if (isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Text(
                        text = "Checking permissions...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Camera Permission Section
        PermissionCard(
            title = "Camera Permission",
            permissionState = cameraPermissionState,
            onCheckClick = { permissionViewModel.checkCameraPermission() }
        )

        PermissionCard(
            title = "Get Camera Permission",
            permissionState = cameraPermissionState,
            onCheckClick = { 
                permissionRequestHandler.requestPermission(Manifest.permission.CAMERA)
            }
        )

        // Storage Permission Section
        PermissionCard(
            title = "Storage Permission",
            permissionState = storagePermissionState,
            onCheckClick = { permissionViewModel.checkStoragePermission() }
        )

        PermissionCard(
            title = "Get Storage Permission",
            permissionState = storagePermissionState,
            onCheckClick = { 
                val storagePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                permissionRequestHandler.requestPermission(storagePermission)
            }
        )

        // Location Permission Section
        PermissionCard(
            title = "Location Permission",
            permissionState = locationPermissionState,
            onCheckClick = { permissionViewModel.checkLocationPermission() }
        )

        PermissionCard(
            title = "Get Location Permission",
            permissionState = locationPermissionState,
            onCheckClick = { 
                permissionRequestHandler.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )

        // Multiple Permissions Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Multiple Permissions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Status: ${permissionManager.getPermissionStatusString(multiplePermissionsState)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val permissions = arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            permissionViewModel.checkMultiplePermissions(permissions)
                        }
                    ) {
                        Text("Check All Permissions")
                    }
                    
                    Button(
                        onClick = {
                            val permissions = arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            permissionRequestHandler.requestMultiplePermissions(permissions)
                        }
                    ) {
                        Text("Request All Permissions")
                    }
                }
            }
        }

        // Custom Permission Testing
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Custom Permission Testing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Status: ${customPermissionResult?.let { permissionManager.getPermissionStatusString(it) } ?: "Not checked"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Test custom permission checking
                            customPermissionResult = null
                            // This would be called from a coroutine in a real ViewModel
                            // For demo purposes, we'll simulate it
                        }
                    ) {
                        Text("Check Custom")
                    }
                    
                    Button(
                        onClick = {
                            // Test if all permissions are granted
                            allPermissionsResult = false
                            // This would be called from a coroutine in a real ViewModel
                        }
                    ) {
                        Text("Check All Granted")
                    }
                }
                
                if (allPermissionsResult) {
                    Text(
                        text = "All permissions are granted! ✅",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Usage Examples
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Usage Examples",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "1. Get PermissionViewModel in your ViewModel",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "2. Use StateFlow to observe permission states",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "3. Call check methods to update states",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "4. Handle results in your UI",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Permission Request Results
        if (permissionRequestResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Permission Request Result",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "Status: ${permissionManager.getPermissionStatusString(permissionRequestResult!!)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { permissionRequestHandler.clearPermissionResult() }
                    ) {
                        Text("Clear Result")
                    }
                }
            }
        }

        // Architecture Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Architecture Features",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "✅ MVVM Pattern with ViewModels",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "✅ Coroutines for async operations",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "✅ StateFlow for reactive UI updates",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "✅ Clean Architecture with Repository",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "✅ Android version compatibility",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "✅ Easy integration for any ViewModel",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    permissionState: PermissionResult,
    onCheckClick: () -> Unit
) {
    val permissionManager = PermissionManager.getInstance()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Status: ${permissionManager.getPermissionStatusString(permissionState)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(onClick = onCheckClick) {
                Text("Check Status")
            }
        }
    }
}

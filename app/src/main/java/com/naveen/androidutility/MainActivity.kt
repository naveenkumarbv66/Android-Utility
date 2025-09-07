package com.naveen.androidutility

import android.Manifest
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naveen.androidutility.ui.theme.AndroidUtilityTheme
import com.naveen.androidutility.userPermissions.UserPermissionHelper

class MainActivity : ComponentActivity() {
    private lateinit var permissionHelper: UserPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize the permission helper
        permissionHelper = UserPermissionHelper(this)
        
        setContent {
            AndroidUtilityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PermissionDemoScreen(
                        permissionHelper = permissionHelper,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDemoScreen(
    permissionHelper: UserPermissionHelper,
    modifier: Modifier = Modifier
) {
    var cameraPermissionStatus by remember { mutableStateOf("Unknown") }
    var storagePermissionStatus by remember { mutableStateOf("Unknown") }
    var locationPermissionStatus by remember { mutableStateOf("Unknown") }
    var debugMessage by remember { mutableStateOf("Ready to test buttons") }

    // Update permission statuses
    LaunchedEffect(Unit) {
        cameraPermissionStatus = if (permissionHelper.isPermissionGranted(Manifest.permission.CAMERA)) {
            "Granted"
        } else {
            "Denied"
        }
        
        storagePermissionStatus = if (permissionHelper.isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
            "Granted"
        } else {
            "Denied"
        }
        
        locationPermissionStatus = if (permissionHelper.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            "Granted"
        } else {
            "Denied"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "UserPermissionHelper Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "This demo shows how to use the UserPermissionHelper class for runtime permissions",
            style = MaterialTheme.typography.bodyMedium
        )

        // Debug info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Debug Info",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Helper initialized: true",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Activity type: ComponentActivity",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Debug: $debugMessage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = {
                        debugMessage = "Test button clicked! ✅"
                        println("Test button clicked!")
                    }
                ) {
                    Text("Test Button")
                }
            }
        }

        // Camera Permission Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Camera Permission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Status: $cameraPermissionStatus",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val isGranted = permissionHelper.isPermissionGranted(Manifest.permission.CAMERA)
                            cameraPermissionStatus = if (isGranted) "Granted" else "Denied"
                            println("Camera permission check: $isGranted")
                        }
                    ) {
                        Text("Check Status")
                    }
                    Button(
                        onClick = {
                            val status = permissionHelper.getPermissionStatus(Manifest.permission.CAMERA)
                            val statusText = when (status) {
                                UserPermissionHelper.PermissionStatus.GRANTED -> "Granted"
                                UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> "Denied (Can Ask Again)"
                                UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> "Denied Permanently"
                            }
                            cameraPermissionStatus = statusText
                            println("Camera permission detailed status: $status")
                        }
                    ) {
                        Text("Detailed Status")
                    }
                    Button(
                        onClick = {
                            println("Requesting camera permission...")
                            permissionHelper.requestPermission(Manifest.permission.CAMERA) { isGranted ->
                                println("Camera permission result: $isGranted")
                                cameraPermissionStatus = if (isGranted) "Granted" else "Denied"
                                if (!isGranted){
                                    val status = permissionHelper.getPermissionStatus(Manifest.permission.CAMERA)
                                    cameraPermissionStatus = when (status) {
                                        UserPermissionHelper.PermissionStatus.GRANTED -> "Granted"
                                        UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> "Denied (Can Ask Again)"
                                        UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> "Denied Permanently"
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Request Permission")
                    }
                }
            }
        }

        // Storage Permission Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Storage Permission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Status: $storagePermissionStatus",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            storagePermissionStatus = if (permissionHelper.isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
                                "Granted"
                            } else {
                                "Denied"
                            }
                        }
                    ) {
                        Text("Check Status")
                    }
                    Button(
                        onClick = {
                            permissionHelper.requestPermission(Manifest.permission.READ_MEDIA_IMAGES) { isGranted ->
                                storagePermissionStatus = if (isGranted) "Granted" else "Denied"
                            }
                        }
                    ) {
                        Text("Request Permission")
                    }
                }
            }
        }

        // Location Permission Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Location Permission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Status: $locationPermissionStatus",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            locationPermissionStatus = if (permissionHelper.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                "Granted"
                            } else {
                                "Denied"
                            }
                        }
                    ) {
                        Text("Check Status")
                    }
                    Button(
                        onClick = {
                            permissionHelper.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) { isGranted ->
                                locationPermissionStatus = if (isGranted) "Granted" else "Denied"
                            }
                        }
                    ) {
                        Text("Request Permission")
                    }
                }
            }
        }

        // Multiple Permissions Example
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
                    text = "Request multiple permissions at once",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        val permissions = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        permissionHelper.requestMultiplePermissions(permissions) { results ->
                            // Update all statuses based on results
                            cameraPermissionStatus = if (results[Manifest.permission.CAMERA] == true) "Granted" else "Denied"
                            storagePermissionStatus = if (results[Manifest.permission.READ_MEDIA_IMAGES] == true) "Granted" else "Denied"
                            locationPermissionStatus = if (results[Manifest.permission.ACCESS_FINE_LOCATION] == true) "Granted" else "Denied"
                        }
                    }
                ) {
                    Text("Request All Permissions")
                }
            }
        }

        // getPermissionStatus() Examples
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "getPermissionStatus() Examples",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Check detailed permission status with different handling",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Example 1: Basic status check
                            val status = permissionHelper.getPermissionStatus(Manifest.permission.CAMERA)
                            println("Camera permission status: $status")
                            
                            when (status) {
                                UserPermissionHelper.PermissionStatus.GRANTED -> {
                                    println("✅ Permission is granted - proceed with camera functionality")
                                    cameraPermissionStatus = "Granted ✅"
                                }
                                UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                                    println("⚠️ Permission denied but can ask again - show rationale")
                                    cameraPermissionStatus = "Denied (Can Ask Again) ⚠️"
                                }
                                UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                                    println("❌ Permission denied permanently - redirect to settings")
                                    cameraPermissionStatus = "Denied Permanently ❌"
                                }
                            }
                        }
                    ) {
                        Text("Check Camera Status")
                    }
                    
                    Button(
                        onClick = {
                            println("🔍 Check All Status button clicked!")
                            debugMessage = "Check All Status button clicked!"
                            
                            // Example 2: Check multiple permissions status
                            val permissions = arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            
                            println("=== Checking All Permission Statuses ===")
                            debugMessage = "Checking all permission statuses..."
                            
                            permissions.forEach { permission ->
                                val status = permissionHelper.getPermissionStatus(permission)
                                val permissionName = permission.substringAfterLast(".")
                                println("$permissionName status: $status")
                                
                                val statusText = when (status) {
                                    UserPermissionHelper.PermissionStatus.GRANTED -> {
                                        println("✅ $permissionName is granted")
                                        "Granted ✅"
                                    }
                                    UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                                        println("⚠️ $permissionName can be requested again")
                                        "Denied (Can Ask Again) ⚠️"
                                    }
                                    UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                                        println("❌ $permissionName is permanently denied")
                                        "Denied Permanently ❌"
                                    }
                                }
                                
                                // Update UI based on permission type
                                when (permission) {
                                    Manifest.permission.CAMERA -> cameraPermissionStatus = statusText
                                    Manifest.permission.READ_MEDIA_IMAGES -> storagePermissionStatus = statusText
                                    Manifest.permission.ACCESS_FINE_LOCATION -> locationPermissionStatus = statusText
                                }
                            }
                            
                            println("=== All Statuses Updated ===")
                            debugMessage = "All statuses checked and updated! ✅"
                            
                            // Visual feedback - temporarily update camera status to show button worked
                            cameraPermissionStatus = "All Statuses Checked! ✅"
                        }
                    ) {
                        Text("Check All Status")
                    }
                }
                
                Button(
                    onClick = {
                        // Example 3: Smart permission handling based on status
                        val status = permissionHelper.getPermissionStatus(Manifest.permission.CAMERA)
                        
                        when (status) {
                            UserPermissionHelper.PermissionStatus.GRANTED -> {
                                // Permission is already granted, proceed
                                println("Camera permission already granted - opening camera")
                                cameraPermissionStatus = "Already Granted - Opening Camera"
                            }
                            UserPermissionHelper.PermissionStatus.DENIED_CAN_ASK_AGAIN -> {
                                // Show rationale and request permission
                                println("Showing rationale and requesting camera permission")
                                cameraPermissionStatus = "Requesting Permission..."
                                permissionHelper.requestPermission(Manifest.permission.CAMERA) { isGranted ->
                                    cameraPermissionStatus = if (isGranted) "Granted" else "Denied"
                                }
                            }
                            UserPermissionHelper.PermissionStatus.DENIED_PERMANENTLY -> {
                                // Redirect to app settings
                                println("Permission permanently denied - redirecting to settings")
                                cameraPermissionStatus = "Please Enable in Settings"
                                // In a real app, you would open settings here
                                // openAppSettings()
                            }
                        }
                    }
                ) {
                    Text("Smart Permission Handling")
                }
            }
        }

        // Add some extra content to test scrolling
        repeat(5) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Test Card ${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "This is test content to demonstrate scrolling functionality. " +
                                "The UI should now be scrollable and you should be able to see all content.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionDemoPreview() {
    AndroidUtilityTheme {
        Text(
            text = "Permission Demo Preview",
            modifier = Modifier.padding(16.dp)
        )
    }
}
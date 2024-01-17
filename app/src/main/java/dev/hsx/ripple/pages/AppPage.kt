package dev.hsx.ripple.pages

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import dev.hsx.ripple.MainActivity
import dev.hsx.ripple.permissions.PERMISSIONS
import dev.hsx.ripple.permissions.PermissionCard
import dev.hsx.ripple.permissions.PermissionType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPage(
    modifier: Modifier = Modifier,
    packageName: String,
    navBack: () -> Unit,
    // Shell commands
    // termuxInstallPackage: (String) -> Unit,
    termux: MainActivity.Termux
) {
    val pm = LocalContext.current.packageManager
    val app: ApplicationInfo = pm.getApplicationInfo(packageName, 0)

    val appName = app.loadLabel(pm).toString()
    val icon = app.loadIcon(pm)
    val flags = (app.flags and ApplicationInfo.FLAG_SYSTEM) == 1

    // pm.checkPermission()

    val packageInfo: PackageInfo =
        pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS or PackageManager.FLAG_PERMISSION_WHITELIST_SYSTEM)

    // .PERMISSION_GRANTED

    //Get Permissions
    val requestedPermissions = packageInfo.requestedPermissions

    // ShizukuProvider.callBinder

    val runtimePermissions = requestedPermissions.filter { PERMISSIONS.contains(it) }
    val otherPermissions = requestedPermissions.filter { !PERMISSIONS.contains(it) }

    val runtimePermissionCount = runtimePermissions.size

    var showAllPermissions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Details")
                },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Surface(modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            LazyColumn(
                modifier = modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    Column(
                        modifier = modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = icon.toBitmap(config = Bitmap.Config.ARGB_8888)
                                .asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = modifier
                                .size(100.dp)
                                .padding(10.dp),

                            )
                        Text(appName)
                        Text(packageName)

                        Spacer(modifier.height(20.dp))

                        ElevatedButton(
                            onClick = { termux.installPackage(packageName) },
                        ) {
                            Text("Install Dual App")
                        }

                        Spacer(modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = modifier.align(Alignment.Start)
                        ) {
                            FilledTonalButton(onClick = {}) {
                                Text("Grant All")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Runtime Permissions: $runtimePermissionCount",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                }

                items (items = runtimePermissions) { permission ->
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    PermissionCard(modifier, permission, PermissionType.RUNTIME) { grantedPermission ->
                        termux.grantPermission(
                            packageName,
                            grantedPermission
                        )
                    }
                }

                if (!showAllPermissions) {
                    item {
                        FilledTonalButton(onClick = { showAllPermissions = !showAllPermissions }) {
                            Text(text = "Other Permissions (${otherPermissions.size})")
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Other Permissions"
                            )
                        }
                        Spacer(Modifier.height(15.dp))
                    }
                }

                if (showAllPermissions) {
                    item {
                        Spacer(Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = modifier.fillMaxWidth(),
                        ) {
                            FilledTonalButton(onClick = { showAllPermissions = !showAllPermissions}) {
                                Text("Hide")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Other Permissions: ${otherPermissions.size}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    items(items = otherPermissions) { permission ->
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        PermissionCard(modifier, permission, PermissionType.OTHER) { grantedPermission ->
                            termux.grantPermission(
                                packageName,
                                grantedPermission
                            )
                        }
                    }
                }
            }
        }
    }
}
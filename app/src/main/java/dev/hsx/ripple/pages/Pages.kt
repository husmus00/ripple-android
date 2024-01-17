package dev.hsx.ripple.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.hsx.ripple.navigation.BottomNavigationBar
import dev.hsx.ripple.MainActivity
import dev.hsx.ripple.navigateSingleTopTo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page(
    modifier: Modifier = Modifier,
    installedApps: List<AppInfo>,
    shizukuHandler: () -> Int,
    resultRequest: MutableState<String>,
    toAppScreen: (String) -> Unit,
    // Shell functions
    // termuxInstallPackage: (String) -> Unit,
    termux: MainActivity.Termux
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val navController2 = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Top app bar")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(Icons.Filled.AddCircle, contentDescription = "") },
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Snackbar")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(onNavItemClick = {route -> navController2.navigateSingleTopTo(route)})
        },
    ) { paddingValues ->

        // Log.d("PRINTT", "HERE $page")

        // A surface container using the 'background' color from the theme
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {

            NavHost(
                navController = navController2,
                startDestination = "installed",
                route = "main_nav",
            ) {
                composable(route = "installed") {
                    InstalledAppsPage(
                        installedApps = installedApps,
                        shizukuHandler = shizukuHandler,
                        resultRequest = resultRequest,
                        toAppScreen = toAppScreen,
                        snackbarHostState = snackbarHostState
                    )
                }

                composable(route = "dual") {
                    DualAppsPage(termux = termux)
                }

                composable(route = "settings") {
                    SettingsPage()
                }
            }
        }
    }
}
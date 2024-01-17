package dev.hsx.ripple

// Pages
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.hsx.ripple.pages.AppInfo
import dev.hsx.ripple.pages.AppPage
import dev.hsx.ripple.pages.Page
import dev.hsx.ripple.ui.theme.RippleTheme
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener

const val ADB_PATH = "/data/data/com.termux/files/usr/bin/adb"

class MainActivity : ComponentActivity() {

    private fun onRequestPermissionsResult(requestCode: Int, grantResult: Int) {
        val granted = grantResult == PackageManager.PERMISSION_GRANTED
        // Do stuff based on the result and the request code
    }

    private val REQUEST_PERMISSION_RESULT_LISTENER =
        OnRequestPermissionResultListener { requestCode: Int, grantResult: Int ->
            this.onRequestPermissionsResult(
                requestCode,
                grantResult
            )
        }

    private fun checkPermission(code: Int): Boolean {
        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            Log.d("PRINT","Unsupported")
            return false
        }
        return if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Granted
            Log.d("PRINT","Granted")
            true
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Users choose "Deny and don't ask again"
            Log.d("PRINT","Denied")
            false
        } else {
            // Request the permission
            Shizuku.requestPermission(code)
            Log.d("PRINT","Requested")
            false
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        // ...
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        // ...
    }

    var resultRequest = mutableStateOf("Request")

    fun doShizuku(): Int {
        try {
            resultRequest.value = checkPermission(4000).toString()
            Log.d("PRINT", "Shizuku returned result $resultRequest")
        }
        catch (e: Exception) {
            resultRequest.value = "Inactive"
            return -1
        }

        return 0
    }

//    private val PACKAGE_MANAGER: IPackageManager = IPackageManager.Stub.asInterface(
//        ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package"))
//    )

    private fun getInstalledAppsList() : List<AppInfo> {
        // get list of all the apps installed
        val infos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        // create a list with size of total number of apps
        val apps = List(infos.size) { AppInfo() }

        Log.d("PRINT", "REACHED 4")

        // add all the app name in string list
        for ((i, info) in infos.withIndex()) {
            apps[i].name = info.loadLabel(packageManager).toString()
            apps[i].packageName = info.packageName
            apps[i].icon = info.loadIcon(packageManager)
            apps[i].isSystem = (info.flags and ApplicationInfo.FLAG_SYSTEM) == 1
        }

        // Log.d("PRINT", if (apps[0].name == null) "NULL" else "NOT NULL")

        Log.d("PRINT", "REACHED 5")

        val sortedApps = apps.sortedBy { it.name }

        // Add indexes after sorting

        for ((i, app) in sortedApps.withIndex()) {
            app.index = i
        }

        return sortedApps
    }
    fun checkAndRequestCameraPermission(
        context: Context,
        permission: String,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            // Open camera because permission is already granted
        } else {
            // Request a permission
            launcher.launch(permission)
        }
    }

    fun checkTermuxPermission() : Boolean {
        val result = checkSelfPermission(getString(R.string.termux_permission))

        if (result != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(getString(R.string.termux_permission)), 200)
        }

        return result == PackageManager.PERMISSION_GRANTED
    }


    inner class Termux {
        fun installPackage(pkg: String) {
            if (!checkTermuxPermission()) {
                return
            }

            val argument = "shell pm install-existing --user 95 $pkg"
            runTermux(ADB_PATH, argument)
        }

        fun grantPermission(pkg: String, permission: String) {
            if (!checkTermuxPermission()) {
                return
            }

            val argument = "shell pm grant --user 95 $pkg $permission"
            runTermux(ADB_PATH, argument)
        }
    }

    private fun runTermux(path: String, arguments: String) {
        val intent = Intent()

        intent.setClassName("com.termux", "com.termux.app.RunCommandService")
        intent.setAction("com.termux.RUN_COMMAND")
        intent.putExtra("com.termux.RUN_COMMAND_PATH", path)
        intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arguments.split(" ").toTypedArray())
        intent.putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home")
        intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
        intent.putExtra("com.termux.RUN_COMMAND_SESSION_ACTION", "0")

        try {
            Log.d("TERMUX_LOG", "Ran command with arg: $arguments")
            startService(intent)
        }
        catch (e: Exception) {
            Log.e("TERMUX_LOG", "Failed to start service")
        }
    }

    public fun startActivityService(intent: Intent) {
        startService(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        Log.d("PRINT", "Starting...123")

        checkTermuxPermission()

        setContent {
            RippleTheme {
                MainApp(
                    installedApps = getInstalledAppsList(),
                    shizukuHandler = { doShizuku() },
                    resultRequest = resultRequest,
                    // termuxInstallPackage = { pkg: String -> termuxInstallPackage(pkg) },
                    termux = Termux()
                )
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        restoreState = true
        launchSingleTop = true
    }


@Composable
fun MainApp(
    modifier: Modifier = Modifier,
    installedApps: List<AppInfo>,
    shizukuHandler: () -> Int,
    // termuxInstallPackage: (String) -> Unit,
    resultRequest: MutableState<String>,
    termux: MainActivity.Termux
) {

    val navController = rememberNavController()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Open camera
        } else {
            // Show dialog
        }
    }

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier,
//        enterTransition = {
//            slideIntoContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
//                animationSpec = tween(700)
//            )
//        },
//        exitTransition = {
//            slideOutOfContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
//                animationSpec = tween(700)
//            )
//        }
    ) {
        composable(route = "main") {

            Log.d("PRINTT","Reached main")

            Page(
                installedApps = installedApps,
                shizukuHandler = { shizukuHandler() },
                resultRequest = resultRequest,
                toAppScreen = { name -> navController.navigateSingleTopTo("app/${name}") },
                termux = termux,
            )
        }

        composable(
            route = "app/{name}",
            arguments = listOf(
                navArgument("name") {type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            // Retrieve the passed argument
            val packageName =
                navBackStackEntry.arguments?.getString("name")

            if (packageName == null) {
                Text("NULL")
            }
            else {
                AppPage(
                    packageName = packageName,
                    navBack = { navController.popBackStack() },
                    termux = termux
                )
            }
        }
    }
}
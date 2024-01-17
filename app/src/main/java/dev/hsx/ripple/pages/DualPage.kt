package dev.hsx.ripple.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.hsx.ripple.MainActivity

@Composable
fun DualAppsPage(
    modifier: Modifier = Modifier,
    // termuxInstallPackage: (String) -> Unit,
    termux: MainActivity.Termux
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Text("Dual")
        Button(onClick = { termux.installPackage("com.aurora.store") }) {
            Text("Install")
        }
        Button(onClick = { /*TODO*/ }) {
            Text("Grant")
        }
    }
}
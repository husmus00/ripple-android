package dev.hsx.ripple.pages

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.hsx.ripple.R
import dev.hsx.ripple.ui.theme.Blue800
import dev.hsx.ripple.ui.theme.Red800
import kotlinx.coroutines.launch

data class AppInfo(
    var index: Int = -1,
    var name: String = "",
    var packageName: String = "",
    var icon: Drawable = R.drawable.ic_launcher_foreground.toDrawable(),
    var isSystem: Boolean = false,
)

@Composable
fun InstalledAppsPage(
    modifier: Modifier = Modifier,
    installedApps: List<AppInfo>,
    shizukuHandler: () -> Int,
    resultRequest: MutableState<String>,
    toAppScreen: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val scope = rememberCoroutineScope()
    var showSystemApps by remember { mutableStateOf(false) }

    ///

    // var currentScreen: RallyDestination by remember { mutableStateOf(Overview) }
    val navController = rememberNavController()

    val currentBackStack by navController.currentBackStackEntryAsState()
    // Fetch your currentDestination:
    val currentDestination = currentBackStack?.destination

    // Change the variable to this and use Overview as a backup screen if this returns null
    // val currentScreen = rallyTabRowScreens.find { it.route == currentDestination?.route } ?: Overview

    ///

    val searchState = remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .fillMaxWidth()
            // .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            FilledTonalButton(onClick = {
                var result = shizukuHandler()

                if (result < 0) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Shizuku not active")
                    }
                }
            }) {
                Text(resultRequest.value)
            }

            FilledTonalButton(
                onClick = { showSystemApps = !showSystemApps},
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showSystemApps) Red800 else Blue800
                )
            ) {
                Text(
                    text = if (showSystemApps) "Hide System" else "Show System",
                    color = Color.White
                )
            }

        }

        SearchView(state = searchState)

        LazyColumn(
            modifier = modifier,
            state = rememberLazyListState(),
            // verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items (items = installedApps) { app ->

                // Check if system apps are shown
                if (showSystemApps || !app.isSystem) {

                    // Check for search term
                    val searchedText = searchState.value.text.lowercase()
                    val appNameLower = app.name.lowercase().contains(searchedText)
                    val packageNameLower = app.packageName.lowercase().contains(searchedText)

                    if (searchedText == "" || appNameLower || packageNameLower)
                    InstalledAppsItem(modifier = modifier, appInfo = app, toAppScreen = toAppScreen)
                    // Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
fun InstalledAppsItem(
    modifier: Modifier = Modifier,
    appInfo: AppInfo,
    toAppScreen: (String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { toAppScreen(appInfo.packageName) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Image(
                bitmap = appInfo.icon.toBitmap(config = Bitmap.Config.ARGB_8888).asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(70.dp)
                    .padding(10.dp),

                )
            Column(
                modifier = modifier.padding(10.dp),//.fillMaxHeight(),
                //verticalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(2.dp)

            ) {
                Text("${appInfo.index}: ${appInfo.name}", style = MaterialTheme.typography.titleMedium)
                Text(appInfo.packageName, style = MaterialTheme.typography.labelMedium)
                if (appInfo.isSystem) {
                    Text(text ="SYSTEM", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier,
            // .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(30.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.colors(
            // textColor = Color.White,
            cursorColor = Color.White,
            // leadingIconColor = Color.White,
            // trailingIconColor = Color.White,
            // backgroundColor = colorResource(id = R.color.colorPrimary),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}
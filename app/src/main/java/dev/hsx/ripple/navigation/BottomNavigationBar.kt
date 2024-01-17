package dev.hsx.ripple.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(var title:String, var iconFilled: ImageVector, var iconOutline: ImageVector, var route:String) {
    data object Installed: NavItem("Installed", Icons.Filled.List, Icons.Outlined.List, "installed")
    data object Dual: NavItem("Dual", Icons.Filled.Star, Icons.Outlined.Star, "dual")
    data object Settings: NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "settings")
}

val nav_items = listOf(
    NavItem.Installed,
    NavItem.Dual,
    NavItem.Settings
)


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onNavItemClick: (String) -> Unit,
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar(modifier = modifier) {
        nav_items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(if (selectedItem == index) item.iconFilled else item.iconOutline, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = { selectedItem = index; onNavItemClick(item.route) }
            )
        }

    }
}
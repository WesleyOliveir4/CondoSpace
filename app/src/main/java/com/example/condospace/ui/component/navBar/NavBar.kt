package com.example.condospace.ui.component.navBar


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.condospace.navigation.NavBarRoutes

@Composable
fun NavBar(navController: NavHostController, key: String) {

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home, NavBarRoutes.Home),
        NavItem("Favorites", Icons.Default.FavoriteBorder, NavBarRoutes.Favorites),
        NavItem("Publish", Icons.Default.AddCircleOutline, NavBarRoutes.Publish),
        NavItem("Profile", Icons.Default.PersonOutline, NavBarRoutes.Profile)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.drawBehind {
            val strokeWidth = 1.dp.toPx()
            drawLine(
                color = Color.Black.copy(alpha = 0.1f),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth
            )
        },
        tonalElevation = 8.dp
    ) {

        navItems.forEach { item ->

            NavigationBarItem(
                selected = item.title == key,
                onClick = {
                    navController.navigate(item.routes) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "Home"
                    )
                },
                label = { Text(text = item.title) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.Blue,
                    selectedIconColor = Color.Blue,
                    indicatorColor = Color.Transparent,
                    unselectedTextColor = Color.DarkGray,
                    unselectedIconColor = Color.DarkGray
                )
            )


        }

    }

}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val routes: NavBarRoutes
)
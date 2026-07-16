package com.example

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.example.ui.screens.*

sealed class Screen(val route: Any, val title: String, val icon: ImageVector) {
    object Dashboard : Screen(DashboardRoute, "Dashboard", Icons.Default.Dashboard)
    object Workflows : Screen(WorkflowsRoute, "Workflows", Icons.Default.AccountTree)
    object Chat : Screen(ChatRoute, "AI Chat", Icons.Default.SmartToy)
    object Settings : Screen(SettingsRoute, "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Workflows,
    Screen.Chat,
    Screen.Settings
)

@Composable
fun MainNavigation(startDestination: Any = LoginRoute) {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Hide bottom bar on Login screen
    showBottomBar = currentDestination?.route?.let { it.contains("LoginRoute") } == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val currentItems = if (com.example.data.N8nApiClient.authMode == 1) {
                    listOf(Screen.Dashboard, Screen.Workflows, Screen.Chat, Screen.Settings)
                } else {
                    listOf(Screen.Chat, Screen.Settings)
                }

                NavigationBar {
                    currentItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains(screen.route.javaClass.simpleName) == true } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<LoginRoute> {
                LoginScreen(onLoginSuccess = {
                    val dest = if (com.example.data.N8nApiClient.authMode == 1) DashboardRoute else ChatRoute
                    navController.navigate(dest) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                })
            }
            composable<DashboardRoute> { DashboardScreen() }
            composable<WorkflowsRoute> { WorkflowsScreen() }
            composable<ChatRoute> { ChatScreen() }
            composable<SettingsRoute> { 
                SettingsScreen(
                    onNavigateToFeature = { title ->
                        navController.navigate(FeatureRoute(title))
                    },
                    onLogout = {
                        com.example.data.N8nApiClient.authMode = -1
                        com.example.data.N8nApiClient.baseUrl = ""
                        com.example.data.N8nApiClient.apiKey = ""
                        com.example.data.N8nApiClient.webhookUrl = ""
                        com.example.data.PrefManager.clear()
                        navController.navigate(LoginRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable<FeatureRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<FeatureRoute>()
                PlaceholderScreen(title = route.title)
            }
        }
    }
}

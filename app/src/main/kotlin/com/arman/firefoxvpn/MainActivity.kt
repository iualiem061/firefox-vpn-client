package com.arman.firefoxvpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arman.firefoxvpn.ui.MainViewModel
import com.arman.firefoxvpn.ui.screens.*
import com.arman.firefoxvpn.ui.theme.*

enum class MainDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    OVERVIEW("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_overview"),
    SERVERS("Servers", Icons.Filled.Public, Icons.Outlined.Public, "nav_tab_servers"),
    ACCOUNTS("Accounts", Icons.Filled.People, Icons.Outlined.People, "nav_tab_accounts"),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_tab_settings"),
    LOGS("Logs", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong, "nav_tab_logs")
}

enum class SubScreen {
    NONE,
    ROUTING,
    ABOUT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaRyVpnTheme {
                val viewModel: MainViewModel = viewModel()
                var currentTab by remember { mutableStateOf(MainDestination.OVERVIEW) }
                var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

                // Handle back navigation
                BackHandler(enabled = currentSubScreen != SubScreen.NONE || currentTab != MainDestination.OVERVIEW) {
                    if (currentSubScreen != SubScreen.NONE) {
                        currentSubScreen = SubScreen.NONE
                    } else if (currentTab != MainDestination.OVERVIEW) {
                        currentTab = MainDestination.OVERVIEW
                    }
                }

                // Ambient Glassmorphism Canvas Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Top ambient light glow
                    Box(
                        modifier = Modifier
                            .size(360.dp)
                            .offset(x = 120.dp, y = (-60).dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(VpnPrimary.copy(alpha = 0.22f), Color.Transparent),
                                    radius = 450f
                                )
                            )
                    )

                    // Bottom ambient neon aura
                    Box(
                        modifier = Modifier
                            .size(320.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-80).dp, y = 80.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(NeonCyan.copy(alpha = 0.16f), Color.Transparent),
                                    radius = 400f
                                )
                            )
                    )

                    when (currentSubScreen) {
                        SubScreen.ROUTING -> {
                            RoutingScreen(
                                viewModel = viewModel,
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.ABOUT -> {
                            AboutScreen(
                                onBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.NONE -> {
                            Scaffold(
                                containerColor = Color.Transparent,
                                contentWindowInsets = WindowInsets.statusBars,
                                bottomBar = {
                                    // Soft Glassmorphic Floating Bottom Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .navigationBarsPadding()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(
                                                    elevation = 16.dp,
                                                    shape = RoundedCornerShape(26.dp),
                                                    spotColor = Color(0x66000000),
                                                    ambientColor = Color(0x33000000)
                                                )
                                                .clip(RoundedCornerShape(26.dp))
                                                .border(
                                                    width = 1.2.dp,
                                                    brush = Brush.verticalGradient(
                                                        listOf(Color(0x40FFFFFF), Color(0x1AFFFFFF))
                                                    ),
                                                    shape = RoundedCornerShape(26.dp)
                                                )
                                                .testTag("bottom_navigation_bar"),
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                                        ) {
                                            NavigationBar(
                                                containerColor = Color.Transparent,
                                                tonalElevation = 0.dp,
                                                modifier = Modifier.height(68.dp)
                                            ) {
                                                MainDestination.values().forEach { destination ->
                                                    val isSelected = currentTab == destination
                                                    NavigationBarItem(
                                                        selected = isSelected,
                                                        onClick = { currentTab = destination },
                                                        icon = {
                                                            Icon(
                                                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                                                contentDescription = destination.label,
                                                                tint = if (isSelected) VpnPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        },
                                                        label = {
                                                            Text(
                                                                text = destination.label,
                                                                style = MaterialTheme.typography.labelSmall
                                                            )
                                                        },
                                                        colors = NavigationBarItemDefaults.colors(
                                                            indicatorColor = VpnPrimary.copy(alpha = 0.22f),
                                                            selectedIconColor = VpnPrimaryLight,
                                                            selectedTextColor = VpnPrimaryLight,
                                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                        ),
                                                        modifier = Modifier.testTag(destination.testTag)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = innerPadding.calculateTopPadding())
                                ) {
                                    when (currentTab) {
                                        MainDestination.OVERVIEW -> {
                                            OverviewScreen(
                                                viewModel = viewModel,
                                                onNavigateToServers = { currentTab = MainDestination.SERVERS },
                                                onNavigateToAccounts = { currentTab = MainDestination.ACCOUNTS },
                                                onNavigateToLogs = { currentTab = MainDestination.LOGS },
                                                onNavigateToSettings = { currentTab = MainDestination.SETTINGS }
                                            )
                                        }
                                        MainDestination.SERVERS -> {
                                            ServersScreen(
                                                viewModel = viewModel,
                                                onServerSelected = { currentTab = MainDestination.OVERVIEW }
                                            )
                                        }
                                        MainDestination.ACCOUNTS -> {
                                            AccountsScreen(
                                                viewModel = viewModel
                                            )
                                        }
                                        MainDestination.SETTINGS -> {
                                            SettingsScreen(
                                                viewModel = viewModel,
                                                onNavigateToRouting = { currentSubScreen = SubScreen.ROUTING },
                                                onNavigateToAbout = { currentSubScreen = SubScreen.ABOUT }
                                            )
                                        }
                                        MainDestination.LOGS -> {
                                            LogsScreen(
                                                viewModel = viewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

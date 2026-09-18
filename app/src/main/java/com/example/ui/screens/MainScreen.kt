package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CricketViewModel
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    SETUP("Setup", Icons.Filled.SportsCricket, Icons.Outlined.SportsCricket, "tab_setup"),
    AUDIT("Audit", Icons.Filled.Analytics, Icons.Outlined.Analytics, "tab_audit"),
    PURE_JSON("Pure JSON", Icons.Filled.Code, Icons.Outlined.Code, "tab_pure_json"),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History, "tab_history")
}

@Composable
fun MainScreen(
    viewModel: CricketViewModel = viewModel()
) {
    var isLocked by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(NavigationTab.SETUP) }

    if (isLocked) {
        PasscodeLockScreen(
            onUnlocked = { isLocked = false }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CyberBackground,
            bottomBar = {
                NavigationBar(
                    containerColor = CyberSurface,
                    tonalElevation = 8.dp
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanPrimary,
                                selectedTextColor = CyanPrimary,
                                indicatorColor = CyanPrimary.copy(alpha = 0.15f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    NavigationTab.SETUP -> {
                        MatchSetupScreen(
                            viewModel = viewModel,
                            onRunEngine = { selectedTab = NavigationTab.AUDIT }
                        )
                    }
                    NavigationTab.AUDIT -> {
                        AuditDashboardScreen(
                            viewModel = viewModel,
                            onNavigateToJson = { selectedTab = NavigationTab.PURE_JSON }
                        )
                    }
                    NavigationTab.PURE_JSON -> {
                        JsonViewScreen(
                            viewModel = viewModel
                        )
                    }
                    NavigationTab.HISTORY -> {
                        HistoryScreen(
                            viewModel = viewModel,
                            onSelectAudit = { selectedTab = NavigationTab.AUDIT }
                        )
                    }
                }
            }
        }
    }
}

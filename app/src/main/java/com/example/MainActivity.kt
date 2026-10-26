package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.AtharScreen
import com.example.ui.AtharViewModel
import com.example.ui.screens.*
import com.example.ui.theme.AtharTheme
import com.example.ui.theme.SaroojBrown
import com.example.ui.theme.SaroojContainer

class MainActivity : ComponentActivity() {

    private val viewModel: AtharViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtharTheme {
                // Force RTL Layout Direction for authentic Arabic typography
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AtharMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AtharMainApp(viewModel: AtharViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedMosque by viewModel.selectedMosque.collectAsState()

    // Handle Android system back press
    if (currentScreen != AtharScreen.HOME) {
        BackHandler {
            viewModel.navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar on primary exploration tabs
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("athar_bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == AtharScreen.HOME,
                    onClick = { viewModel.navigateTo(AtharScreen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                    label = { Text("الرئيسية") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaroojBrown,
                        indicatorColor = SaroojContainer
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )
                NavigationBarItem(
                    selected = currentScreen == AtharScreen.TOUR_360,
                    onClick = { viewModel.navigateTo(AtharScreen.TOUR_360) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "جولة 360°") },
                    label = { Text("جولة 360°") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaroojBrown,
                        indicatorColor = SaroojContainer
                    ),
                    modifier = Modifier.testTag("nav_item_tour_360")
                )
                NavigationBarItem(
                    selected = currentScreen == AtharScreen.MAP,
                    onClick = { viewModel.navigateTo(AtharScreen.MAP) },
                    icon = { Icon(Icons.Default.Map, contentDescription = "الخريطة") },
                    label = { Text("الخريطة") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaroojBrown,
                        indicatorColor = SaroojContainer
                    ),
                    modifier = Modifier.testTag("nav_item_map")
                )
                NavigationBarItem(
                    selected = currentScreen == AtharScreen.TWO_MIHRABS,
                    onClick = { viewModel.navigateTo(AtharScreen.TWO_MIHRABS) },
                    icon = { Icon(Icons.Default.Compare, contentDescription = "المحرابان") },
                    label = { Text("المحرابان") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaroojBrown,
                        indicatorColor = SaroojContainer
                    ),
                    modifier = Modifier.testTag("nav_item_two_mihrabs")
                )
                NavigationBarItem(
                    selected = currentScreen == AtharScreen.FIELD_NOTEBOOK,
                    onClick = { viewModel.navigateTo(AtharScreen.FIELD_NOTEBOOK) },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = "دفتر الباحث") },
                    label = { Text("دفتر الباحث") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SaroojBrown,
                        indicatorColor = SaroojContainer
                    ),
                    modifier = Modifier.testTag("nav_item_field_notebook")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    AtharScreen.HOME -> AtharHomeScreen(viewModel = viewModel)
                    AtharScreen.TOUR_360 -> Panoramic360Viewer(viewModel = viewModel)
                    AtharScreen.MAP -> AtharMapScreen(viewModel = viewModel)
                    AtharScreen.MOSQUE_DETAIL -> MosqueDetailScreen(mosque = selectedMosque, viewModel = viewModel)
                    AtharScreen.TWO_MIHRABS -> TwoMihrabsScreen(viewModel = viewModel)
                    AtharScreen.FIELD_NOTEBOOK -> FieldNotebookScreen(viewModel = viewModel)
                    AtharScreen.ENCYCLOPEDIA_SOURCES -> EncyclopediaSourcesScreen(viewModel = viewModel)
                    AtharScreen.COMPANION_CHAT -> AtharCompanionScreen(viewModel = viewModel)
                    AtharScreen.ABOUT -> AboutScreen(viewModel = viewModel)
                }
            }
        }
    }
}

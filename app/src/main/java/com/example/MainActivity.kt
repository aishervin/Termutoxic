package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import com.example.ui.screens.ActionLogScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FileBrowserScreen
import com.example.ui.screens.IntegrationsScreen
import com.example.ui.screens.MemoryNotesScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ShenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()
            val lang by viewModel.currentLanguage.collectAsState()
            val isFa = lang == "fa"

            val layoutDir = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr

            ShenTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                    ShenMainApp(viewModel = viewModel, isFa = isFa)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShenMainApp(viewModel: MainViewModel, isFa: Boolean) {
    var selectedTab by remember { mutableStateOf("chat") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.shen_logo),
                            contentDescription = "Shen Logo",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "SHΞN™ Termutoxic",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val drawerItems = listOf(
                        Triple("chat", if (isFa) "💬 چت و عامل هوشمند" else "💬 AI Chat", Icons.AutoMirrored.Filled.Chat),
                        Triple("bootstrap", if (isFa) "🚀 راه‌اندازی و بوتاسترپ" else "🚀 Termux Setup", Icons.Default.RocketLaunch),
                        Triple("dashboard", if (isFa) "⚡ داشبورد پروسه‌ها و پورت‌ها" else "⚡ Dashboard", Icons.Default.Dashboard),
                        Triple("logs", if (isFa) "📋 تاریخچه لاگ‌ها و اسنپ‌شات‌ها" else "📋 Action Logs", Icons.Default.History),
                        Triple("files", if (isFa) "📁 مدیر فایل‌های ترموکس" else "📁 File Manager", Icons.Default.Folder),
                        Triple("vault", if (isFa) "🔐 گاوصندوق توکن‌ها" else "🔐 Vault", Icons.Default.Key),
                        Triple("memory", if (isFa) "🧠 حافظه بلندمدت عامل" else "🧠 Agent Memory", Icons.Default.Memory),
                        Triple("settings", if (isFa) "⚙️ تنظیمات و مدل‌ها" else "⚙️ Settings", Icons.Default.Settings)
                    )

                    drawerItems.forEach { (id, label, icon) ->
                        NavigationDrawerItem(
                            label = { Text(label, fontWeight = if (selectedTab == id) FontWeight.Bold else FontWeight.Normal) },
                            selected = selectedTab == id,
                            onClick = {
                                selectedTab = id
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(imageVector = icon, contentDescription = label) },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.shen_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SHΞN™",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == "chat",
                        onClick = { selectedTab = "chat" },
                        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
                        label = { Text(if (isFa) "چت" else "Chat") },
                        modifier = Modifier.testTag("nav_chat_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == "bootstrap",
                        onClick = { selectedTab = "bootstrap" },
                        icon = { Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = "Setup") },
                        label = { Text(if (isFa) "راه‌اندازی" else "Setup") },
                        modifier = Modifier.testTag("nav_bootstrap_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == "dashboard",
                        onClick = { selectedTab = "dashboard" },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text(if (isFa) "داشبورد" else "Board") },
                        modifier = Modifier.testTag("nav_dashboard_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == "settings",
                        onClick = { selectedTab = "settings" },
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text(if (isFa) "تنظیمات" else "Settings") },
                        modifier = Modifier.testTag("nav_settings_tab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (selectedTab) {
                    "chat" -> ChatScreen(viewModel = viewModel)
                    "bootstrap" -> OnboardingScreen(viewModel = viewModel)
                    "logs" -> ActionLogScreen(viewModel = viewModel)
                    "files" -> FileBrowserScreen(viewModel = viewModel)
                    "dashboard" -> DashboardScreen(viewModel = viewModel)
                    "vault" -> IntegrationsScreen(viewModel = viewModel)
                    "memory" -> MemoryNotesScreen(viewModel = viewModel)
                    "settings" -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

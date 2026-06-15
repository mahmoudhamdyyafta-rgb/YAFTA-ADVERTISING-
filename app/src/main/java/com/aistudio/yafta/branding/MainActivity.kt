package com.aistudio.yafta.branding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.yafta.branding.data.YaftaDatabase
import com.aistudio.yafta.branding.ui.screens.*
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel
import com.aistudio.yafta.branding.viewmodel.YaftaViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YaftaTheme {
                val database = YaftaDatabase.getDatabase(applicationContext)
                val dao = database.yaftaDao()
                val viewModel: YaftaViewModel = viewModel(factory = YaftaViewModelFactory(dao))

                var currentTab by remember { mutableStateOf(0) }

                val tabsList = listOf(
                    "الرئيسية (Home)" to Icons.Default.Home,
                    "استديو (Studio)" to Icons.Default.Palette,
                    "العملاء (CRM)" to Icons.Default.GroupAdd,
                    "الورشة (Projects)" to Icons.Default.Build,
                    "المخزن (Warehouse)" to Icons.Default.Inventory,
                    "الحسابات (Ledger)" to Icons.Default.ReceiptLong
                )

                // Scaffold with background gradient for professional advertising aesthetic!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0E1118),
                                    Color(0xFF0A0C10)
                                )
                            )
                        )
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWideScreen = maxWidth > 840.dp
                        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                        val scope = rememberCoroutineScope()

                        if (isWideScreen) {
                            // Tablet / Desktop layout: Docked Sidebar + Content Area
                            Row(modifier = Modifier.fillMaxSize()) {
                                SidebarContent(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    tabsList = tabsList,
                                    modifier = Modifier
                                        .width(280.dp)
                                        .fillMaxHeight()
                                        .background(CyberSurface)
                                )
                                VerticalDivider(
                                    color = BorderColor,
                                    modifier = Modifier.fillMaxHeight().width(1.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    MainScreenContent(
                                        currentTab = currentTab,
                                        viewModel = viewModel,
                                        onNavigate = { currentTab = it }
                                    )
                                }
                            }
                        } else {
                            // Mobile layout: Sliding Drawer + TopAppBar + Content Area + Nav Shortcuts
                            ModalNavigationDrawer(
                                drawerState = drawerState,
                                drawerContent = {
                                    ModalDrawerSheet(
                                        modifier = Modifier.width(290.dp)
                                    ) {
                                        SidebarContent(
                                            currentTab = currentTab,
                                            onTabSelected = {
                                                currentTab = it
                                                scope.launch { drawerState.close() }
                                            },
                                            tabsList = tabsList,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            ) {
                                Scaffold(
                                    containerColor = Color.Transparent,
                                    topBar = {
                                        CenterAlignedTopAppBar(
                                            title = {
                                                Text(
                                                    text = "بوابة يافطة الرقمية • ERP",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                            },
                                            navigationIcon = {
                                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Menu,
                                                        contentDescription = "القائمة الرئيسية",
                                                        tint = CyberPrimary
                                                    )
                                                }
                                            },
                                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                                containerColor = CyberSurface
                                            )
                                        )
                                    },
                                    bottomBar = {
                                        NavigationBar(
                                            containerColor = CyberSurface,
                                            contentColor = Color.White,
                                            modifier = Modifier.navigationBarsPadding()
                                        ) {
                                            // Render first 4 modules in the bottom bar for sleek ergonomic mobile layout
                                            val primaryTabs = tabsList.take(4)
                                            primaryTabs.forEachIndexed { index, (label, icon) ->
                                                val cleanLabel = label.substringBefore(" (")
                                                NavigationBarItem(
                                                    selected = currentTab == index,
                                                    onClick = { currentTab = index },
                                                    icon = { Icon(icon, contentDescription = label) },
                                                    label = { Text(cleanLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                                    colors = NavigationBarItemDefaults.colors(
                                                        selectedIconColor = Color(0xFF0E1118),
                                                        selectedTextColor = CyberPrimary,
                                                        indicatorColor = CyberPrimary,
                                                        unselectedIconColor = Color(0xFF94A3B8),
                                                        unselectedTextColor = Color(0xFF94A3B8)
                                                    )
                                                )
                                            }
                                            // More (Hamburger/Drawer trigger element)
                                            NavigationBarItem(
                                                selected = currentTab >= 4,
                                                onClick = { scope.launch { drawerState.open() } },
                                                icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More Modules") },
                                                label = { Text("المزيد (More)", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = Color(0xFF0E1118),
                                                    selectedTextColor = CyberSecondary,
                                                    indicatorColor = CyberSecondary,
                                                    unselectedIconColor = Color(0xFF94A3B8),
                                                    unselectedTextColor = Color(0xFF94A3B8)
                                                )
                                            )
                                        }
                                    }
                                ) { innerPadding ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        MainScreenContent(
                                            currentTab = currentTab,
                                            viewModel = viewModel,
                                            onNavigate = { currentTab = it }
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

@Composable
fun SidebarContent(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    tabsList: List<Pair<String, ImageVector>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF131926))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // Yafta Official Title Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "YAFTA Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F1524))
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                )
                Column {
                    Text(
                        text = "يافطة للإعلان",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "ERP ADVERTISING SYSTEM",
                        color = CyberPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 8.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Divider(color = BorderColor)

            // Scrollable dynamic index of tabs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabsList.forEachIndexed { index, (label, icon) ->
                    val isSelected = currentTab == index
                    val neonColor = if (index % 2 == 0) CyberPrimary else CyberSecondary

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) neonColor.copy(alpha = 0.12f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) neonColor.copy(alpha = 0.4f) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onTabSelected(index) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) neonColor else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Live system compliance footer
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AccentSuccess)
                )
                Text(
                    text = "مزامنة سحابية نشطة",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
            Text(
                text = "يافطة ميديا للأشغال الرقمية v2.4",
                color = Color(0xFF475569),
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun MainScreenContent(
    currentTab: Int,
    viewModel: YaftaViewModel,
    onNavigate: (Int) -> Unit
) {
    when (currentTab) {
        0 -> DashboardScreen(
            viewModel = viewModel,
            onNavigateToDesign = { onNavigate(1) },
            onNavigateToCrm = { onNavigate(2) },
            onNavigateToProjects = { onNavigate(3) },
            onNavigateToInventory = { onNavigate(4) },
            onNavigateToFinance = { onNavigate(5) }
        )
        1 -> DesignStudioScreen(viewModel = viewModel)
        2 -> CrmScreen(viewModel = viewModel)
        3 -> ProjectsScreen(viewModel = viewModel)
        4 -> InventoryScreen(viewModel = viewModel)
        5 -> FinanceScreen(viewModel = viewModel)
    }
}

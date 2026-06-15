package com.aistudio.yafta.branding.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.R
import com.aistudio.yafta.branding.data.*
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: YaftaViewModel,
    onNavigateToDesign: () -> Unit,
    onNavigateToCrm: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToFinance: () -> Unit
) {
    // Current nested module view. If null, we show the main cockpit dashboard.
    var activeModule by remember { mutableStateOf<String?>(null) }

    if (activeModule != null) {
        // Nested subscreen active, render with an elegant top bar
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface)
                    .border(1.dp, BorderColor)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeModule = null }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Dashboard Menu",
                        tint = CyberPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "بوابة يافطة لتطبيقات المؤسسة • ERP",
                        color = CyberPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (activeModule) {
                            "CRM" -> "إدارة المبيعات وقنوات العملاء (CRM Pipeline)"
                            "HR" -> "الموارد البشرية وشؤون الموظفين (HR Team Portal)"
                            "Reports" -> "مركز التقارير التنفيذية ورقابة الأداء (Reports)"
                            "Settings" -> "إعدادات النظام والنسخ الاحتياطي (Admin Setup)"
                            else -> "لوحة التحكم المتكاملة"
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeModule) {
                    "CRM" -> CrmScreen(viewModel = viewModel)
                    "HR" -> HrScreen(viewModel = viewModel)
                    "Reports" -> ReportsScreen(viewModel = viewModel)
                    "Settings" -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    } else {
        // Cockpit Dashboard Screen
        val financeEntries by viewModel.allFinanceEntries.collectAsState()
        val inventoryItems by viewModel.allInventoryItems.collectAsState()
        val projects by viewModel.allProjects.collectAsState()

        // Calculate executive KPIs
        val totalIncome = remember(financeEntries) {
            financeEntries.filter { it.type == "INCOME" }.sumOf { it.amount }
        }
        val totalExpense = remember(financeEntries) {
            financeEntries.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        }
        val netProfit = totalIncome - totalExpense

        val lowStockCount = remember(inventoryItems) {
            inventoryItems.count { it.quantity <= it.minThreshold }
        }

        val activeProjects = remember(projects) {
            projects.filter { it.stage != "Installation" }
        }

        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Premium Agency Branding Banner displaying the Official unmodified logo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    CyberSurface,
                                    CyberSurface.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        CyberPrimary,
                                        CyberSecondary,
                                        CyberPrimary
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentSuccess)
                                )
                                Text(
                                    text = "بوابة يافطة المتكاملة • Enterprise ERP",
                                    color = CyberPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "مؤسسة يافطة للإعلان واللوحات الرقمية",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "YAFTA ADVERTISING & DIGITAL SIGNAGE",
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Loader of official company logo exactly as requested
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Official YAFTA Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F1524))
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        )
                    }
                }

                // Interactive Quick KPIs
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 2
                ) {
                    KpiCard(
                        title = "صافي الأرباح (Net Revenue)",
                        value = "$${String.format("%,.1f", netProfit)}",
                        subtitle = "الإيرادات مقابل النفقات",
                        icon = Icons.Default.TrendingUp,
                        tint = CyberPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        title = "اللافتات النشطة (Active Signs)",
                        value = "${activeProjects.size}",
                        subtitle = "قيد التصنيع بالورشة",
                        icon = Icons.Default.Build,
                        tint = CyberSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Low Inventory Alerts
                if (lowStockCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentWarning.copy(alpha = 0.15f))
                            .border(1.dp, AccentWarning, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentWarning.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Inventory Alert Symbol",
                                    tint = AccentWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "تنبيه انخفاض المخزون • Low Stock ($lowStockCount items)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "الأكريليك أو الـ LEDs أو معادن الشاسيهات توشك على النفاد وتطلب التوريد.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // --- 10 MODULES ERP PORTAL LAUNCH DIRECTORY ---
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "بوابة المديولات ونظم الـ ERP الأساسية",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        // Module 1: CRM Lead Tracking (Nested Module)
                        ErpPortalCard(
                            title = "تتبع صفقات وعملاء CRM",
                            subtitle = "قنوات التدفق والعملاء",
                            icon = Icons.Default.GroupAdd,
                            color = CyberPrimary,
                            onClick = onNavigateToCrm,
                            modifier = Modifier.weight(1f)
                        )

                        // Module 2: Design Studio (Tab redirect 1)
                        ErpPortalCard(
                            title = "استوديو تصميم يافطة",
                            subtitle = "حساب أحجام ومواد النيون",
                            icon = Icons.Default.Palette,
                            color = CyberSecondary,
                            onClick = onNavigateToDesign,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        // Module 3: Projects pipeline (Tab redirect 2)
                        ErpPortalCard(
                            title = "خط إنتاج ومشروعات الورشة",
                            subtitle = "توصيل تركيب تسليم لافتة",
                            icon = Icons.Default.Build,
                            color = CyberTertiary,
                            onClick = onNavigateToProjects,
                            modifier = Modifier.weight(1f)
                        )

                        // Module 4: Warehouse (Tab redirect 3)
                        ErpPortalCard(
                            title = "المخازن وإدارة اللوجستيات",
                            subtitle = "الأكريليك، شاسيهات الحديد",
                            icon = Icons.Default.Inventory,
                            color = AccentWarning,
                            onClick = onNavigateToInventory,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        // Module 5: Finance ledger (Tab redirect 4)
                        ErpPortalCard(
                            title = "المحافظ والحسابات التفصيلية",
                            subtitle = "سجل الإدخال، صافي الأرباح",
                            icon = Icons.Default.ReceiptLong,
                            color = AccentSuccess,
                            onClick = onNavigateToFinance,
                            modifier = Modifier.weight(1f)
                        )

                        // Module 6: HR & Tasks (Nested Module)
                        ErpPortalCard(
                            title = "الموارد البشرية والموظفين",
                            subtitle = "حضور غياب وتقييم الفريق",
                            icon = Icons.Default.Badge,
                            color = Color(0xFFFF7B00),
                            onClick = { activeModule = "HR" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        // Module 7: Reports analytics (Nested Module)
                        ErpPortalCard(
                            title = "مركز تقارير الاستهلاك والأرباح",
                            subtitle = "تصدير إلى PDF & Excel",
                            icon = Icons.Default.Assessment,
                            color = Color(0xFFE040FB),
                            onClick = { activeModule = "Reports" },
                            modifier = Modifier.weight(1f)
                        )

                        // Module 8: Settings (Nested Module)
                        ErpPortalCard(
                            title = "الإعدادات وقواعد المحاسبة",
                            subtitle = "عنوان ومزامنة سحابية وبانل",
                            icon = Icons.Default.Settings,
                            color = Color.LightGray,
                            onClick = { activeModule = "Settings" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Finance Performance Chart Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "التقرير المالي لشركة يافطة • Monthly Financials",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Canvas(modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                        ) {
                            val padding = 40f
                            val graphWidth = size.width - padding * 2
                            val graphHeight = size.height - padding * 2

                            drawLine(
                                color = BorderColor.copy(alpha = 0.5f),
                                start = Offset(padding, graphHeight + padding),
                                end = Offset(size.width - padding, graphHeight + padding),
                                strokeWidth = 2f
                            )

                            val maxValue = maxOf(totalIncome, totalExpense, 1.0)
                            val incomeHeight = (totalIncome / maxValue) * graphHeight
                            val expenseHeight = (totalExpense / maxValue) * graphHeight
                            val profitHeight = (maxOf(0.0, netProfit) / maxValue) * graphHeight

                            val barWidth = 60f

                            // Income
                            drawRoundRect(
                                color = CyberPrimary,
                                topLeft = Offset(padding + graphWidth / 4 - barWidth / 2, (graphHeight + padding - incomeHeight).toFloat()),
                                size = Size(barWidth, incomeHeight.toFloat()),
                                cornerRadius = CornerRadius(10f, 10f)
                            )

                            // Expense
                            drawRoundRect(
                                color = CyberSecondary,
                                topLeft = Offset(padding + graphWidth / 2 - barWidth / 2, (graphHeight + padding - expenseHeight).toFloat()),
                                size = Size(barWidth, expenseHeight.toFloat()),
                                cornerRadius = CornerRadius(10f, 10f)
                            )

                            // Profit
                            drawRoundRect(
                                color = CyberTertiary,
                                topLeft = Offset(padding + (graphWidth * 3) / 4 - barWidth / 2, (graphHeight + padding - profitHeight).toFloat()),
                                size = Size(barWidth, profitHeight.toFloat()),
                                cornerRadius = CornerRadius(10f, 10f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            LegendItem(label = "الإيرادات ($${String.format("%,.0f", totalIncome)})", color = CyberPrimary)
                            LegendItem(label = "المصروفات ($${String.format("%,.0f", totalExpense)})", color = CyberSecondary)
                            LegendItem(label = "الأرباح ($${String.format("%,.0f", netProfit)})", color = CyberTertiary)
                        }
                    }
                }

                // Active pipeline highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "لافتات قيد التصنيع بالورشة (Production)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "عرض الكل (All)",
                        color = CyberPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onNavigateToProjects() }
                    )
                }

                if (activeProjects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberSurface)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Empty Signage Logs",
                                tint = TextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد لافتات نشطة بالورشة حالياً",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "جميع عمليات التصنيع والتركيب مكتملة 100%!",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        activeProjects.take(3).forEach { project ->
                            ProjectMiniCard(project = project, onNavigateToProjects = onNavigateToProjects)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Text(value, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextPrimary, fontSize = 10.sp)
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun ProjectMiniCard(
    project: ProductionProject,
    onNavigateToProjects: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToProjects() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSecondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(16.dp))
                }
                Column {
                    Text(project.clientName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("المشروع: \"${project.signageText}\" • ${project.width}x${project.height}م", color = TextSecondary, fontSize = 11.sp)
                }
            }

            // Production Stage Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when(project.stage) {
                        "Welding" -> "لحام الهيكل"
                        "Leds" -> "توصيل الليد"
                        "Acrylic" -> "كبس الأكريليك"
                        else -> "تركيب وتسليم"
                    },
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ErpPortalCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

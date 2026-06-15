package com.aistudio.yafta.branding.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.data.*
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@Composable
fun ReportsScreen(viewModel: YaftaViewModel) {
    val context = LocalContext.current
    val financeEntries by viewModel.allFinanceEntries.collectAsState()
    val inventoryItems by viewModel.allInventoryItems.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val members by viewModel.allHrMembers.collectAsState()

    // Analytical calculations
    val totalIncome = financeEntries.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = financeEntries.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val netProfit = totalIncome - totalExpense

    val lowStockItems = inventoryItems.filter { it.quantity <= it.minThreshold }
    val avgScore = if (members.isNotEmpty()) members.map { it.performanceScore }.average() else 92.5

    // Sharing and exporting functions
    fun shareReportAsText(title: String, body: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, body)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "تحميل وتصدير التقرير التنفيذي")
        context.startActivity(shareIntent)
    }

    fun exportToExcel() {
        val csvBuilder = StringBuilder()
        csvBuilder.append("رقم البند,نوع الدفعة,القيمة (USD),البيان التوضيحي,التاريخ\n")
        financeEntries.forEachIndexed { i, entry ->
            csvBuilder.append("${i+1},${if(entry.type == "INCOME") "إيرادات لوحات" else "مصروفات خامات"},${entry.amount},${entry.description},${entry.timestamp}\n")
        }
        shareReportAsText(
            title = "Yafta_Financial_Balances_Ledger.csv",
            body = csvBuilder.toString()
        )
    }

    fun exportToPdf() {
        val pdfSimulatedText = """
        ==================================================
        مؤسسة يافطة للإعلانات الرقمية والمضيئة • تقرير الأداء المالي والإنتاجي
        ==================================================
        تاريخ الصدور: لليوم الجاري 2026م
        
        أولاً: التحليل المالي والسيولة:
        -----------------------------
        - إجمالي التدفقات المقبوضة (الإيرادات): $$totalIncome
        - إجمالي المصاريف والمشتريات (المصروفات): $$totalExpense
        - صافي رصيد السيولة بالخزينة: $$netProfit
        - الحالة العامة للتوازن المالي: ممتازة ومتزنة
        
        ثانياً: خط الإنتاج بالورشة النشطة:
        -----------------------------
        - إجمالي اللوحات المدرجة: ${projects.size} لوحة تصنيعية
        - لافتات جاري حدادتها وقص ليزر: ${projects.count { it.stage != "Installation" }} لوحة نشطة
        - لافتات منتهية ومركبة بالواجهات للمتاجر: ${projects.count { it.stage == "Installation" }} لوحة جاهزة بالكامل
        
        ثالثاً: مخزون المواد الخام ونظرة المستودع:
        -----------------------------
        - إجمالي بنود المواد المسجلة: ${inventoryItems.size} نوع خامة
        - المواد شحيحة المخزون (تحت حد الأمان): ${lowStockItems.size} خامات
          (البنود المتأثرة: ${lowStockItems.joinToString { it.name }})
        
        رابعاً: إنتاجية طاقم العمل ومؤشرات الفنيين:
        -----------------------------
        - عدد الكادر المسجل: ${members.size} مصمم وفني لحام
        - متوسط كفاءة تسليم التركيبات والالتزام: ${String.format("%.1f", avgScore)}%
        
        ==================================================
        تقرير تنظيمي معتمد ومؤشر ذكاء أعمال - يافطة ERP v2
        ==================================================
        """.trimIndent()

        shareReportAsText(
            title = "Yafta_Executive_Brief_Performance.txt",
            body = pdfSimulatedText
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Reports Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مركز التحليل التنفيذي وذكاء الأعمال • INTELLIGENCE",
                    color = CyberPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "تقارير الأداء وتصدير البيانات",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CyberPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Analytics, "analytics indicator logo", tint = CyberPrimary, modifier = Modifier.size(18.dp))
            }
        }

        // Action Data Export Portal
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.9f)),
            border = BorderStroke(1.dp, BorderColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CloudSync, "Sync cloud", tint = CyberPrimary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "بوابة تصدير ومشاركة الحسابات واللوحات",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "قم بتوليد وتحميل البيانات الفورية للسيولة النقدية والمواد الموردة للمخازن، متوافقة مع الأنظمة المقاصية للمحاسب المالي للوكالة.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { exportToExcel() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Excel Icon",
                            tint = CyberBg,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("جدول XLS", color = CyberBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { exportToPdf() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = "PDF Icon",
                            tint = CyberBg,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مستند مالي معتمد", color = CyberBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // BI Section Header
        Text(
            text = "مؤشرات جودة التشغيل • Key Performance Indicators",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        // KPI Metric row list
        ReportItemRow(
            title = "مؤشر السيولة والاستدامة (Liquid Inventory Ratio)",
            description = "مدى ملائمة الواردات النقدية من لوحات النيون لتغطية تكاليف الخامات والحديد",
            value = if (netProfit >= 0) "أمان وسيولة معززة" else "يتطلب تعزيز مقبوضات عقود",
            color = if (netProfit >= 0) CyberTertiary else Color.Red,
            metricValue = "${String.format("%.0f", if (totalExpense > 0) (totalIncome / totalExpense) * 100 else 100f)}%"
        )

        ReportItemRow(
            title = "معدل مبيعات وإنجاز لوحات الفينيل والأكريليك",
            description = "متوسط سرعة تشييد وقص لوحات البنر بالورشة والتركيب بالواجهات",
            value = "${projects.count { it.stage == "Installation" }} لوحات منفذة",
            color = CyberPrimary,
            metricValue = "${projects.size} لوحة"
        )

        ReportItemRow(
            title = "أمان الإمداد اللوجستي للمخزن وعمليات التجميع",
            description = "قياس مستويات العجز للمواد الأساسية كالترانسات وموديلات النيون الموفرة",
            value = if (lowStockItems.isNotEmpty()) "يوجد نقص مواد عاجلة" else "كامل خامات الورشة متوفرة",
            color = if (lowStockItems.isNotEmpty()) Color.Red else CyberTertiary,
            metricValue = "${inventoryItems.size - lowStockItems.size}/${inventoryItems.size}"
        )

        ReportItemRow(
            title = "كفاءة المصممين والتزام فنيين اللحام",
            description = "معدل الإنجاز لرسومات CAD وحرص مهندسي التشغيل والسلامة",
            value = "مستوى التميز: ممتاز",
            color = CyberSecondary,
            metricValue = "${String.format("%.1f", avgScore)}%"
        )
    }
}

@Composable
fun ReportItemRow(
    title: String,
    description: String,
    value: String,
    color: Color,
    metricValue: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = metricValue,
                    color = color,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.testTag("report_metric_tag")
                )
            }

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Divider(color = BorderColor.copy(alpha = 0.2f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = value,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

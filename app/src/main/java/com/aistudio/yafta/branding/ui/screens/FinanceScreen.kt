package com.aistudio.yafta.branding.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.data.FinanceEntry
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(viewModel: YaftaViewModel) {
    val financeEntries by viewModel.allFinanceEntries.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // Entry Form States
    var showAddDialog by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(true) }
    var categoryText by remember { mutableStateOf("Signage Orders") }
    var descriptionText by remember { mutableStateOf("") }

    val categoriesAr = mapOf(
        "Signage Orders" to "مبيعات لوحات نيون وعقود (Signage Sales)",
        "Raw Materials" to "شراء خامات وألواح أكريليك (Raw Materials)",
        "Labor Cost" to "أجور فنيين اللحام والتركيب (Labor Cost)",
        "Welding & Structure" to "هياكل حديد وشاسيهات (Structure Cost)",
        "Rent & Bills" to "إيجار ورشة التصنيع والكهرباء (Workshop Bills)",
        "Others" to "نثريات نقل ومصاريف عامة (Others)"
    )

    val categories = listOf("Signage Orders", "Raw Materials", "Labor Cost", "Welding & Structure", "Rent & Bills", "Others")

    // KPI Metrics
    val totalIncome = remember(financeEntries) {
        financeEntries.filter { it.type == "INCOME" }.sumOf { it.amount }
    }
    val totalExpense = remember(financeEntries) {
        financeEntries.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }
    val netProfit = totalIncome - totalExpense

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyberPrimary,
                contentColor = CyberBg,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.AddCard, "Add Transaction Icon")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Accounting Overhaul Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "الحسابات العامة والقيود المحاسبية • LEDGER",
                        color = CyberPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "الخزينة وحركة التدفقات النقدية",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "القيود الموثقة: ${financeEntries.size}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // High Fidelity Cash ledger billboard card with neon borders
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.9f)),
                border = BorderStroke(1.dp, if (netProfit >= 0) CyberTertiary.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "صافي الرصيد الحالي للمقاصة • Net Cash Balance",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (netProfit >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = "financial status trend",
                            tint = if (netProfit >= 0) CyberTertiary else Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "$${String.format("%,.2f", netProfit)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )

                    Divider(color = BorderColor.copy(alpha = 0.3f), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentSuccess))
                                Text("المقبوضات والداخل (Inflow)", color = TextSecondary, fontSize = 11.sp)
                            }
                            Text("$${String.format("%,.1f", totalIncome)}", color = AccentSuccess, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                                Text("المصروفات والخارج (Outflow)", color = TextSecondary, fontSize = 11.sp)
                            }
                            Text("$${String.format("%,.1f", totalExpense)}", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            // Transaction title list
            Text(
                text = "دفتر الحسابات والقيود المحاسبية اليومية • Ledger Records",
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Entries List
            if (financeEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RequestQuote,
                            contentDescription = "Empty Invoices Icon",
                            tint = TextSecondary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد قيود محاسبية مسجلة حديثاً",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "يتم تسجيل حركة الحسابات تلقائياً عند بيع لوحة أو شراء خامات خشبية وحديدية، كما يمكنك المتابعة يدوياً.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(financeEntries, key = { it.id }) { entry ->
                        TransactionRow(
                            entry = entry,
                            categoriesAr = categoriesAr,
                            onDelete = { 
                                viewModel.deleteFinanceEntry(entry.id)
                                Toast.makeText(context, "تم إتلاف القيد المحاسبي وحذف السجل المالي", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Add Transaction Dialog Box Popup
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { 
                    Text(
                        text = "تدوين قيد مالي جديد بالدفتر", 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    ) 
                },
                containerColor = CyberBg,
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Type Selection Row (Income/Expense Toggle)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurface)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isIncome) AccentSuccess.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { isIncome = true }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("إيراد / توريد (INCOME)", color = if (isIncome) AccentSuccess else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isIncome) Color.Red.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { isIncome = false }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("صرف / مصروق (EXPENSE)", color = if (!isIncome) Color.Red else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        // Amount Text Field
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("قيمة الدفعة المالية بالدولار ($)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = CyberPrimary,
                                unfocusedLabelColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("finance_amount"),
                            singleLine = true
                        )

                        // Description Field
                        OutlinedTextField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            label = { Text("تفاصيل وبيان الحركة العقد والعميل") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = CyberPrimary,
                                unfocusedLabelColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("finance_desc"),
                            singleLine = true
                        )

                        // Category Drop selection layout
                        Text("مبوبة وتصنيف الحركة بدفتر المقاصة:", color = TextSecondary, fontSize = 11.sp)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = cat == categoryText
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) CyberPrimary.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.2f))
                                        .border(1.dp, if (isSelected) CyberPrimary else BorderColor, RoundedCornerShape(6.dp))
                                        .clickable { categoryText = cat }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = (categoriesAr[cat] ?: cat).substringBefore(" ("), 
                                        fontSize = 10.sp, 
                                        color = if (isSelected) CyberPrimary else TextSecondary, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val value = amountText.toDoubleOrNull()
                            if (value != null && descriptionText.isNotBlank()) {
                                viewModel.addFinanceEntry(
                                    type = if (isIncome) "INCOME" else "EXPENSE",
                                    amount = value,
                                    description = descriptionText,
                                    category = categoryText
                                )
                                // Clear fields
                                amountText = ""
                                descriptionText = ""
                                categoryText = "Signage Orders"
                                isIncome = true
                                showAddDialog = false
                                keyboardController?.hide()
                                Toast.makeText(context, "تم توثيق السند وحفظ القيد بالدفتر بنجاح!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                    ) {
                        Text("توثيق السند وحفظ القيد", color = CyberBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("إلغاء الأمر", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun TransactionRow(
    entry: FinanceEntry,
    categoriesAr: Map<String, String>,
    onDelete: () -> Unit
) {
    val isInflow = entry.type == "INCOME"

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("finance_entry_${entry.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Circle visual indicator containing arrow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isInflow) AccentSuccess.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isInflow) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = "Cash Flow Status",
                        tint = if (isInflow) AccentSuccess else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.description,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Text(
                        text = (categoriesAr[entry.category] ?: entry.category).substringBefore(" ("),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${if (isInflow) "+" else "-"}$${String.format("%,.1f", entry.amount)}",
                    color = if (isInflow) AccentSuccess else Color.Red,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Remove transaction", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

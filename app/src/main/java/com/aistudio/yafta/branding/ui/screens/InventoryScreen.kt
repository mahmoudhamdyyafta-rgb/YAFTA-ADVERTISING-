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
import com.aistudio.yafta.branding.data.InventoryItem
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: YaftaViewModel) {
    val inventoryItems by viewModel.allInventoryItems.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Sheets", "Rolls", "LEDs", "Structure", "Electronics")

    // Arabic translations of inventory categories
    val categoryAr = mapOf(
        "All" to "الكل",
        "Sheets" to "أرواق أكريليك وبلاستيك (Sheets)",
        "Rolls" to "رولات فينيل وبانر (Rolls)",
        "LEDs" to "إضاءات وموديولات LED (LEDs)",
        "Structure" to "حديد وشاسيهات هيكلية (Structure)",
        "Electronics" to "محولات MeanWell ولوحات (Electronics)"
    )

    var showAddItemDialog by remember { mutableStateOf(false) }
    var nameText by remember { mutableStateOf("") }
    var categorySelect by remember { mutableStateOf("Sheets") }
    var qtyText by remember { mutableStateOf("") }
    var unitText by remember { mutableStateOf("Sht") }
    var costText by remember { mutableStateOf("") }
    var minThresholdText by remember { mutableStateOf("5") }

    val filteredItems = remember(inventoryItems, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") {
            inventoryItems
        } else {
            inventoryItems.filter { it.category == selectedCategoryFilter }
        }
    }

    val lowStockCount = remember(inventoryItems) {
        inventoryItems.count { it.quantity <= it.minThreshold }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = CyberPrimary,
                contentColor = CyberBg,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, "Add Material")
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
            // Inventory Overhaul Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "إدارة المخازن والمواد الهيكلية • WAREHOUSE",
                        color = CyberPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "مستودع الخامات والمخزون",
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
                        text = "إجمالي المواد: ${inventoryItems.size}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Warehouse Alert Billboard
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (lowStockCount > 0) AccentWarning.copy(alpha = 0.12f) else CyberSurface
                ),
                border = BorderStroke(1.dp, if (lowStockCount > 0) AccentWarning else BorderColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (lowStockCount > 0) AccentWarning.copy(alpha = 0.2f) else CyberTertiary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (lowStockCount > 0) Icons.Default.NotificationImportant else Icons.Default.GppGood,
                            contentDescription = "Inventory audit status icon",
                            tint = if (lowStockCount > 0) AccentWarning else CyberTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (lowStockCount > 0) "إشعار: مواد تحت حد الأمان والتوريد" else "حالة المخزون سليمة وآمنة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (lowStockCount > 0) "يوجد عدد $lowStockCount مواد يجب إعادة شرائها وتوريدها للورشة!" else "جميع خامات الحديد، الأكريليك، الألومنيوم والمحولات متوفرة بكثرة.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Categories list horizontal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategoryFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberPrimary else CyberSurface)
                            .border(1.dp, if (isSelected) CyberPrimary else BorderColor, RoundedCornerShape(8.dp))
                            .clickable { selectedCategoryFilter = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = categoryAr[cat] ?: cat,
                            color = if (isSelected) CyberBg else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Material List Container
            if (filteredItems.isEmpty()) {
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
                            imageVector = Icons.Default.AllInbox,
                            contentDescription = "Empty stock",
                            tint = TextSecondary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد خامات مسجلة في هذا القسم",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "اضغط على زر الإضافة (+) لتدوين خام حديد أو أكريليك أو إضاءات نيون جديدة في مستودع الوكالة.",
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
                    items(filteredItems, key = { it.id }) { item ->
                        MaterialItemRow(
                            item = item,
                            onDelete = { 
                                viewModel.deleteInventoryItem(item.id)
                                Toast.makeText(context, "تم حذف المادة الهيكلية من المستودع", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Add Dialog Material Form popup
        if (showAddItemDialog) {
            AlertDialog(
                onDismissRequest = { showAddItemDialog = false },
                title = { 
                    Text(
                        text = "سجل توريد خامة جديدة للمستودع", 
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = nameText,
                            onValueChange = { nameText = it },
                            label = { Text("اسم الخامة (مثال: أكريليك شفاف 5 ملم كاست)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = CyberPrimary,
                                unfocusedLabelColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("material_name"),
                            singleLine = true
                        )

                        // Selection Row of Categories
                        Text("حدد تصنيف الخامة لفرز الإيقونات والمخاضط:", color = TextSecondary, fontSize = 11.sp)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.drop(1).forEach { cat ->
                                val isSelected = cat == categorySelect
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) CyberPrimary.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.2f))
                                        .border(1.dp, if (isSelected) CyberPrimary else BorderColor, RoundedCornerShape(6.dp))
                                        .clickable { categorySelect = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = categoryAr[cat] ?: cat, 
                                        fontSize = 10.sp, 
                                        color = if (isSelected) CyberPrimary else TextSecondary, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Stack Quantity & Units Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = qtyText,
                                onValueChange = { qtyText = it },
                                label = { Text("الكمية الموردة") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor
                                ),
                                modifier = Modifier.weight(1f).testTag("material_qty"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = unitText,
                                onValueChange = { unitText = it },
                                label = { Text("الوحدة (متر/لوح)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor
                                ),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Costs & Alert Limit Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = costText,
                                onValueChange = { costText = it },
                                label = { Text("سعر شراء الوحدة") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor
                                ),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = minThresholdText,
                                onValueChange = { minThresholdText = it },
                                label = { Text("حد أمان المخزن") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor
                                ),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val qtyValue = qtyText.toDoubleOrNull()
                            val costValue = costText.toDoubleOrNull()
                            val thresholdValue = minThresholdText.toDoubleOrNull()
                            
                            if (nameText.isNotBlank() && qtyValue != null && costValue != null && thresholdValue != null) {
                                viewModel.addInventoryItem(
                                    name = nameText,
                                    category = categorySelect,
                                    quantity = qtyValue,
                                    unit = unitText,
                                    unitCost = costValue,
                                    minThreshold = thresholdValue
                                )
                                // Save expense ledger transaction
                                viewModel.addFinanceEntry(
                                    type = "EXPENSE",
                                    amount = qtyValue * costValue,
                                    description = "توريد مخزن الخامات واللوحات: $nameText ($qtyValue $unitText)",
                                    category = "Raw Materials"
                                )
                                // Clear inputs
                                nameText = ""
                                qtyText = ""
                                costText = ""
                                minThresholdText = "5"
                                showAddItemDialog = false
                                keyboardController?.hide()
                                Toast.makeText(context, "تم حفظ وتوزيع الكمية وتسجيلها في الحسابات العامة!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                    ) {
                        Text("تأكيد وحفظ التوريد", color = CyberBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddItemDialog = false }) {
                        Text("إلغاء المجلد", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun MaterialItemRow(
    item: InventoryItem,
    onDelete: () -> Unit
) {
    val isLowStock = item.quantity <= item.minThreshold

    // Specific category icons mappings to look incredibly tailored
    val categoryIcon = when (item.category) {
        "Sheets" -> Icons.Default.Layers
        "Rolls" -> Icons.Default.RoundedCorner
        "LEDs" -> Icons.Default.Bolt
        "Structure" -> Icons.Default.GridOn
        "Electronics" -> Icons.Default.SettingsInputComponent
        else -> Icons.Default.Inventory
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, if (isLowStock) AccentWarning.copy(alpha = 0.5f) else BorderColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isLowStock) AccentWarning.copy(alpha = 0.15f) else CyberPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = "material icon spec",
                        tint = if (isLowStock) AccentWarning else CyberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isLowStock) AccentWarning.copy(alpha = 0.15f) else CyberTertiary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isLowStock) "شحيح" else "متوفر",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLowStock) AccentWarning else CyberTertiary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "التصنيف: ${item.category} • سعر الشراء: $${item.unitCost}/${item.unit} • حد الأمان: ${item.minThreshold.toInt()} ${item.unit}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.quantity.toInt()} ${item.unit}",
                        color = if (isLowStock) AccentWarning else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "القيمة: $${String.format("%,.0f", item.quantity * item.unitCost)}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Delete material", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

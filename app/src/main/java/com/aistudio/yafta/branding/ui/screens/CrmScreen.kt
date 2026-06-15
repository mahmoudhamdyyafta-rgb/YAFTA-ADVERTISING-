package com.aistudio.yafta.branding.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.data.CrmContact
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CrmScreen(viewModel: YaftaViewModel) {
    val contacts by viewModel.allCrmContacts.collectAsState()
    val context = LocalContext.current

    var selectedTabState by remember { mutableStateOf(0) } // 0 = Pipeline Board, 1 = Customer Directory
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Contact Form States
    var nameText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var companyText by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Lead") }
    var notesText by remember { mutableStateOf("") }

    val statusList = listOf(
        "Lead" to "عملاء مستهدفين (Leads)",
        "Quoted" to "محسوبي الأسعار (Quoted)",
        "Followup" to "قيد المتابعة (Follow-up)",
        "Converted" to "تم التعاقد والتحصيل (Converted)",
        "Lost" to "صفقات ضائعة (Lost)"
    )

    val cleanStatusMap = mapOf(
        "Lead" to "عميل مستهدف",
        "Quoted" to "محسوب السعر",
        "Followup" to "متابعة نشطة",
        "Converted" to "عقد مالي نشط",
        "Lost" to "صفقة فائتة"
    )

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.company.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyberPrimary,
                contentColor = CyberBg,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PersonAdd, "Add Contact")
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
            // CRM Core Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "نظام المبيعات وإدارة العملاء • ENTERPRISE CRM & CUSTOMERS",
                        color = CyberPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "الحقيبة التسويقية وقاعدة العملاء",
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
                        text = "العدد: ${contacts.size}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Beautiful Top Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .padding(4.dp)
            ) {
                val tabLabels = listOf("دورة الصفقات (Sales Funnel)", "مجلد العملاء الموحد (Customer Directory)")
                tabLabels.forEachIndexed { index, label ->
                    val isSelected = selectedTabState == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyberPrimary else Color.Transparent)
                            .clickable { selectedTabState = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) CyberBg else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Search Bar for quick search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث باسم العميل، الشركة، أو الجوال...", color = TextSecondary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = CyberPrimary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Dynamic Tab Views
            if (selectedTabState == 0) {
                // TAB 0: Pipeline Overview and Status Board
                Text("مراحل وعقود المبيعات النشطة • Active Funnel States", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    statusList.forEach { (statusKey, statusArabic) ->
                        val count = contacts.count { it.status == statusKey }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
                            border = BorderStroke(
                                width = 1.dp,
                                color = when (statusKey) {
                                    "Lead" -> Color.LightGray.copy(alpha = 0.4f)
                                    "Quoted" -> AccentWarning.copy(alpha = 0.4f)
                                    "Followup" -> CyberSecondary.copy(alpha = 0.4f)
                                    "Converted" -> CyberTertiary.copy(alpha = 0.4f)
                                    else -> Color.Red.copy(alpha = 0.4f)
                                }
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.width(150.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = statusArabic.substringBefore(" ("),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "$count",
                                    color = when (statusKey) {
                                        "Lead" -> Color.LightGray
                                        "Quoted" -> AccentWarning
                                        "Followup" -> CyberSecondary
                                        "Converted" -> CyberTertiary
                                        else -> Color.Red
                                    },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Show only leads categorized in the horizontal board
                if (filteredContacts.isEmpty()) {
                    EmptyScreenPlaceholder(message = "لا توجد أي نتائج مطابقة")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredContacts) { contact ->
                            InteractiveContactPipelineCard(
                                contact = contact,
                                cleanStatusMap = cleanStatusMap,
                                onUpdateStatus = { newStatus ->
                                    viewModel.addCrmContact(
                                        name = contact.name,
                                        phone = contact.phone,
                                        email = contact.email,
                                        company = contact.company,
                                        status = newStatus,
                                        notes = contact.notes + "\n[تحديث تلقائي للمرحلة إلى ($newStatus)]"
                                    )
                                    viewModel.deleteCrmContact(contact.id)
                                    Toast.makeText(context, "تم حفظ وترقية مرحلة العميل!", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = { viewModel.deleteCrmContact(contact.id) }
                            )
                        }
                    }
                }

            } else {
                // TAB 1: Unified Customer Directory Database & Customer Profiling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سجل العملاء التجاري والمؤسسات • Verified Accounts Ledger", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (filteredContacts.isEmpty()) {
                    EmptyScreenPlaceholder(message = "لم يتم تسجيل أي عميل أو مؤسسة تطابق البحث")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredContacts) { contact ->
                            CustomerDirectoryCard(
                                contact = contact,
                                translationMap = cleanStatusMap,
                                onDelete = { viewModel.deleteCrmContact(contact.id) }
                            )
                        }
                    }
                }
            }
        }

        // Add Contact Dialog Box
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = CyberBg,
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                title = {
                    Text(
                        text = "تسجيل هوية عميل ومؤسسة • customer log",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = nameText,
                            onValueChange = { nameText = it },
                            label = { Text("الاسم الكريم للعميل (Client Name)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        OutlinedTextField(
                            value = phoneText,
                            onValueChange = { phoneText = it },
                            label = { Text("رقم جوال العميل (Mobile Number)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        OutlinedTextField(
                            value = emailText,
                            onValueChange = { emailText = it },
                            label = { Text("البريد الإلكتروني للعميل / المندوب") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        OutlinedTextField(
                            value = companyText,
                            onValueChange = { companyText = it },
                            label = { Text("اسم المتجر أو المؤسسة التجارية") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor
                            )
                        )

                        // Status selection chips
                        Column {
                            Text("بروز ومرحلة العميل الحالية:", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                statusList.forEach { (statusKey, statusArabic) ->
                                    FilterChip(
                                        selected = selectedStatus == statusKey,
                                        onClick = { selectedStatus = statusKey },
                                        label = { Text(statusArabic.substringBefore(" ("), fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyberPrimary,
                                            selectedLabelColor = CyberBg,
                                            containerColor = CyberSurface,
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            label = { Text("مواصفات لوحة مطلوبة / تفاصيل الطلب") },
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameText.isNotBlank()) {
                                viewModel.addCrmContact(
                                    name = nameText,
                                    phone = phoneText,
                                    email = emailText,
                                    company = companyText,
                                    status = selectedStatus,
                                    notes = notesText
                                )
                                // Reset fields
                                nameText = ""
                                phoneText = ""
                                emailText = ""
                                companyText = ""
                                selectedStatus = "Lead"
                                notesText = ""
                                showAddDialog = false
                                Toast.makeText(context, "تم حفظ وتسجيل العميل في قاعدة البيانات الموحدة!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                    ) {
                        Text("حفظ وتسجيل العميل", color = CyberBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("إلغاء الأمر", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun InteractiveContactPipelineCard(
    contact: CrmContact,
    cleanStatusMap: Map<String, String>?,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("crm_pipeline_card_${contact.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header showing name and active class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                            color = CyberPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Column {
                        Text(
                            text = contact.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (contact.company.isNotEmpty()) {
                            Text(
                                text = "مؤسسة: ${contact.company}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Active status label
                BadgeStatusLabel(statusKey = contact.status)
            }

            Divider(color = BorderColor.copy(alpha = 0.3f))

            // Action Status Transporter
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "نقل مرحلة تواصل العميل (Stage Transporter):",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickStages = listOf(
                        "Lead" to "محتمل",
                        "Quoted" to "مسعّر",
                        "Followup" to "متابعة",
                        "Converted" to "تعاقد مالي",
                        "Lost" to "ضائعة"
                    )
                    quickStages.forEach { (key, label) ->
                        val isCurrent = contact.status == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCurrent) CyberSecondary else CyberBg)
                                .border(1.dp, if (isCurrent) CyberSecondary else BorderColor, RoundedCornerShape(6.dp))
                                .clickable { if (!isCurrent) onUpdateStatus(key) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isCurrent) CyberBg else Color.LightGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Notes section
            if (contact.notes.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBg.copy(alpha = 0.6f))
                        .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = contact.notes,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Action triggers (Dial, Email or Delete)
            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (contact.phone.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                context.startActivity(dialIntent)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyberSurface)
                        ) {
                            Icon(Icons.Default.Phone, "Dial Mobile", tint = CyberPrimary, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (contact.email.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contact.email}"))
                                context.startActivity(Intent.createChooser(emailIntent, "إرسال بريد إلكتروني"))
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyberSurface)
                        ) {
                            Icon(Icons.Default.Email, "Send Mail", tint = CyberSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, "Delete Contact", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CustomerDirectoryCard(
    contact: CrmContact,
    translationMap: Map<String, String>?,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberSecondary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Business, "Business Org", tint = CyberSecondary)
                    }

                    Column {
                        Text(
                            text = contact.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (contact.company.isNotBlank()) "الشركة المشغلة: ${contact.company}" else "عميل فردي مباشر",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                BadgeStatusLabel(statusKey = contact.status)
            }

            Divider(color = BorderColor.copy(alpha = 0.2f))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (contact.phone.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Call, "Phone icon", tint = TextSecondary, modifier = Modifier.size(12.dp))
                        Text("الجوال التجاري: ${contact.phone}", color = TextPrimary, fontSize = 11.sp)
                    }
                }
                if (contact.email.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Drafts, "Email icon", tint = TextSecondary, modifier = Modifier.size(12.dp))
                        Text("البريد الإلكتروني: ${contact.email}", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }

            if (contact.notes.isNotBlank()) {
                Text(
                    text = "سجل الطلبات والخصائص: ${contact.notes}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Delete record", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BadgeStatusLabel(statusKey: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                when (statusKey) {
                    "Lead" -> Color.LightGray.copy(alpha = 0.15f)
                    "Quoted" -> AccentWarning.copy(alpha = 0.15f)
                    "Followup" -> CyberSecondary.copy(alpha = 0.15f)
                    "Converted" -> CyberTertiary.copy(alpha = 0.15f)
                    else -> Color.Red.copy(alpha = 0.15f)
                }
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = when (statusKey) {
                "Lead" -> "عميل مستهدف"
                "Quoted" -> "مسعّر عرض مالي"
                "Followup" -> "متابعة مستمرة"
                "Converted" -> "متعاقد رسمي"
                else -> "تراجع الاهتمام"
            },
            color = when (statusKey) {
                "Lead" -> Color.LightGray
                "Quoted" -> AccentWarning
                "Followup" -> CyberSecondary
                "Converted" -> CyberTertiary
                else -> Color.Red
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyScreenPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Info, "info", tint = TextSecondary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

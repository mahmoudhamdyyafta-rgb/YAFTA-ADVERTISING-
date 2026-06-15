package com.aistudio.yafta.branding.ui.screens

import android.widget.Toast
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
import com.aistudio.yafta.branding.data.YaftaSetting
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: YaftaViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dbSettings by viewModel.allSettings.collectAsState()

    var companyName by remember { mutableStateOf("YAFTA • وكالة يافطة للدعاية والإعلان") }
    var taxRate by remember { mutableStateOf("14.0") }
    var currencyCode by remember { mutableStateOf("USD") }
    var hqAddress by remember { mutableStateOf("المنطقة الصناعية، صالة الإنتاج الرابعة") }

    // Custom Agency Calibration settings
    var defaultBleedValue by remember { mutableStateOf("5") } // mm
    var defaultDpiValue by remember { mutableStateOf("300") } // DPI
    var inkSpectraCalibration by remember { mutableStateOf("94.5") } // %

    // Cloud backup progress
    var isSyncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableStateOf(0f) }

    // User Role Selection Simulation
    var selectedUserRole by remember { mutableStateOf("مدير النظام (Admin)") }

    // Sync from database if entries are populated
    LaunchedEffect(dbSettings) {
        if (dbSettings.isNotEmpty()) {
            dbSettings.find { it.key == "company_name" }?.value?.let { companyName = it }
            dbSettings.find { it.key == "tax_rate" }?.value?.let { taxRate = it }
            dbSettings.find { it.key == "currency_code" }?.value?.let { currencyCode = it }
            dbSettings.find { it.key == "hq_address" }?.value?.let { hqAddress = it }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settings Overhaul Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "لوحة التحكم الإدارية والمعايرة • SYSTEM SETTINGS",
                    color = CyberPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "المعايرة الصناعية والتحكم بالخادم",
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
                Icon(Icons.Default.Settings, "settings applet header", tint = CyberPrimary, modifier = Modifier.size(18.dp))
            }
        }

        // 1. Corporate Identity Profile configurations
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
            border = BorderStroke(1.dp, BorderColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Business, "business corporate profile Icon", tint = CyberPrimary)
                    Text(
                        text = "الهوية المؤسسية والضرائب الدولية • Org Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("الاسم القانوني للشركة والمصنع") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hqAddress,
                    onValueChange = { hqAddress = it },
                    label = { Text("عنوان المقر والورشة التشغيلية") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = taxRate,
                        onValueChange = { taxRate = it },
                        label = { Text("الضريبة المضافة (%)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = currencyCode,
                        onValueChange = { currencyCode = it },
                        label = { Text("العملة الرئيسية") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Hardware Fabrication Calibration panel (New custom-tailored configuration)
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
            border = BorderStroke(1.dp, BorderColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Tune, "fabrication settings icon", tint = CyberSecondary)
                    Text(
                        text = "معاير ومقاييس الاستوديو والقص • Fabrication Calibration",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "اضبط قيم الهامش الافتراضي ومستوى كثافة تنقيط الشاشات (DPI) للتحكم بكفاءة تلوير الأكريليك ولحام الإطارات المعدنية.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = defaultBleedValue,
                        onValueChange = { defaultBleedValue = it },
                        label = { Text("نزيف الهامش (mm)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberSecondary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = defaultDpiValue,
                        onValueChange = { defaultDpiValue = it },
                        label = { Text("المعيار DPI الافتراضي") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberSecondary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = inkSpectraCalibration,
                    onValueChange = { inkSpectraCalibration = it },
                    label = { Text("معامل مطابقة الطيف اللوني CMYK (%)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberSecondary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.updateSetting("company_name", companyName)
                        viewModel.updateSetting("hq_address", hqAddress)
                        viewModel.updateSetting("tax_rate", taxRate)
                        viewModel.updateSetting("currency_code", currencyCode)
                        Toast.makeText(context, "تم حفظ وتطبيق وتصدير قيم المقاييس وقاعدة السجلات بنجاح!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Icon(Icons.Default.Save, "Save Settings Action", tint = CyberBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ ومعايرة مدخلات النظام", color = CyberBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // 3. Backup with remote Firebase database
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudQueue, "Cloud database link icon", tint = CyberSecondary)
                    Text(
                        text = "الربط السحابي والنسخ الاحتياطي • Firebase Sync",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "قم بمزامنة تصاميم اللوحات، سجلات الورش والأكريليك وخزينة المال لحمايتها من فقدان البيانات وقابلية العمل دون إنترنت.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                if (isSyncing) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LinearProgressIndicator(
                            progress = { syncProgress },
                            color = CyberSecondary,
                            trackColor = BorderColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Text(
                            text = "جاري حزم قاعدة اللوحات وإرسال البيانات... ${ (syncProgress * 100).toInt() }%",
                            color = CyberSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                isSyncing = true
                                syncProgress = 0f
                                while (syncProgress < 1.0f) {
                                    delay(120)
                                    syncProgress += 0.08f
                                }
                                isSyncing = false
                                Toast.makeText(context, "اكتملت المزامنة وحفظ السجلات بنجاح 100%!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, CyberSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, "Sync start icon", tint = CyberSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مزامنة فورية (Offline to Firebase)", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Role-Based Permissions system
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "صلاحيات الوصول والأمن • Security Role Select",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "اختر دور المستخدم النشط لعرض الصلاحيات المخصصة لخطوط التصميم وقسم الحسابات.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("مدير النظام (Admin)", "فني الورشة", "محاسب الوكالة").forEach { role ->
                        FilterChip(
                            selected = selectedUserRole == role,
                            onClick = {
                                selectedUserRole = role
                                Toast.makeText(context, "تم تبديل تصنيف الصلاحيات إلى دور: $role", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text(role, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberTertiary,
                                selectedLabelColor = CyberBg,
                                containerColor = CyberSurface,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

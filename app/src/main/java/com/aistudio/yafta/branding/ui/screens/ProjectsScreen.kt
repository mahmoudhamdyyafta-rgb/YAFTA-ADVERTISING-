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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.data.ClientEstimate
import com.aistudio.yafta.branding.data.ProductionProject
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(viewModel: YaftaViewModel) {
    val projects by viewModel.allProjects.collectAsState()
    val estimates by viewModel.allEstimates.collectAsState()
    val context = LocalContext.current

    var activePipelineTab by remember { mutableStateOf(0) } // 0 = Workshop Floor, 1 = Estimated Leads
    val stagesOrder = listOf("Draft", "Fabrication", "Lighting", "Assembly", "Delivery", "Installation")

    // Mapping of internal stages to Arabic signage industry equivalents
    val stageTranslates = mapOf(
        "Draft" to "رسم وعقد التصميم (CAD Blueprint)",
        "Fabrication" to "الحدادة وهيكل الحديد (Iron Welding)",
        "Lighting" to "سحب وتوصيل موديولات النيون (LED Wiring)",
        "Assembly" to "تجميع وضغط الأكريليك (Face Assembly)",
        "Delivery" to "النقل اللوجستي (On-Route)",
        "Installation" to "التركيب بكرينات جدارية (Wall Install)"
    )

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Projects Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "نظام التخطيط ومتابعة خط الإنتاج بالورشة • PRODUCTION",
                        color = CyberPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "أمر التشغيل وورشة التصنيع",
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
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "جاري تنفيذ: ${projects.count { it.stage != "Installation" }}",
                        color = CyberSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tabs layout switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activePipelineTab == 0) CyberPrimary else Color.Transparent)
                        .clickable { activePipelineTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "أوامر الورشة النشطة (${projects.size})",
                        color = if (activePipelineTab == 0) CyberBg else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activePipelineTab == 1) CyberPrimary else Color.Transparent)
                        .clickable { activePipelineTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "عروض تسعير الاستوديو (${estimates.size})",
                        color = if (activePipelineTab == 1) CyberBg else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            if (activePipelineTab == 0) {
                // TAB 0: Workspace floor queue and progress
                if (projects.isEmpty()) {
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
                                imageVector = Icons.Default.PrecisionManufacturing,
                                contentDescription = "Empty production floor",
                                tint = TextSecondary,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "حالة التشغيل: صف التجميع شاغر",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "احفظ تصميماً وعرض أسعار من الاستوديو ثم قم بتحويله هنا لبدء التصنيع الفوري.",
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(projects, key = { it.id }) { project ->
                            WorkshopProjectRow(
                                project = project,
                                stagesOrder = stagesOrder,
                                stageLabelMap = stageTranslates,
                                onUpdateStage = { nextStage -> 
                                    viewModel.updateProjectStage(project.id, nextStage)
                                    Toast.makeText(context, "تم ترحيل اللوحة إلى مرحلة ($nextStage)", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = { viewModel.deleteProject(project.id) }
                            )
                        }
                    }
                }
            } else {
                // TAB 1: Leads estimation pipeline (from studio estimates)
                if (estimates.isEmpty()) {
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
                                imageVector = Icons.Default.AssignmentLate,
                                contentDescription = "Empty pipeline",
                                tint = TextSecondary,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد عقود معلقة حالياً",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "قم بإدخال بيانات عملائك وحسابات المساحات والتكاليف من الاستديو، ثم احفظ عرض السعر ليسجل كعقد فوري.",
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estimates, key = { it.id }) { estimate ->
                            EstimateLeadRow(
                                estimate = estimate,
                                onLaunch = {
                                    // Accept and convert to active shop production queue
                                    viewModel.addProject(
                                        clientName = estimate.clientName,
                                        phone = estimate.phone,
                                        signageText = "LUMINOUS NEON SIGN",
                                        width = 3.6,
                                        height = 1.2,
                                        type = "3D Glowing Acrylic Face",
                                        totalCost = estimate.estimatedPrice
                                    )
                                    // Log 50% downpayment income to Ledger
                                    viewModel.addFinanceEntry(
                                        type = "INCOME",
                                        amount = estimate.estimatedPrice * 0.5,
                                        description = "دفعة تعاقد 50% لتشغيل يافطة (${estimate.clientName})",
                                        category = "Signage Orders"
                                    )
                                    // LINK CRM: Update CRM Contact status to Converted!
                                    viewModel.addCrmContact(
                                        name = estimate.clientName,
                                        phone = estimate.phone,
                                        email = "",
                                        company = "",
                                        status = "Converted",
                                        notes = "تم التعاقد رسمياً وتحويل عرض السعر إلى أمر تشغيل برقم كود الورشة"
                                    )
                                    // Delete old estimate
                                    viewModel.deleteEstimate(estimate.id)
                                    Toast.makeText(context, "تم قبول وإطلاق التصنيع للوحة بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = { viewModel.deleteEstimate(estimate.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkshopProjectRow(
    project: ProductionProject,
    stagesOrder: List<String>,
    stageLabelMap: Map<String, String>,
    onUpdateStage: (String) -> Unit,
    onDelete: () -> Unit
) {
    val currentStageIdx = stagesOrder.indexOf(project.stage).coerceAtLeast(0)
    val progressPercent = ((currentStageIdx + 1).toFloat() / stagesOrder.size) * 100f

    // Detailed structural workshop tasks checklist per stage
    val detailedTasks = when (project.stage) {
        "Draft" -> "مخطط حديدي AutoCAD • مراجعة هامش الأمان والنزيف • موافقة العميل"
        "Fabrication" -> "قص زوايا التيوبات الحديدية • لحام هيكل الشاسيه • وضع الدهان التأسيسي المقاوم للصدأ"
        "Lighting" -> "تمديد أسلاك IP67 LED • لحام موصلات موديولات النيون • فحص استهلاك محول MeanWell"
        "Assembly" -> "تثبيت وجه أكريليك مفرغ ليزر • كَبس الحروف البارزة • سيلكون عزل الرطوبة والمطر"
        "Delivery" -> "أحزمة الشد والربط • فحص التجريب والإنارة قبل الشحن • خطوة النقل لمقر التركيب"
        else -> "الونش والمثبتات الميكانيكية • ربط مصدر الطاقة الرئيسي • الفحص الكهروضوئي النهائي"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_row_${project.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Identity Client Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = project.clientName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "جوال: ${project.phone.ifBlank { "غير مدخل" }}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "$${String.format("%,.0f", project.totalCost)}",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.testTag("project_cost_tag")
                )
            }

            // Banners and elements dimensions specs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberBg.copy(alpha = 0.8f))
                    .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "النص المطلوب: \"${project.signageText}\"",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "مواصفات اللوحة: ${project.width}م عرض x ${project.height}م ارتفاع (${project.type})",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Physical Stage progress
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stageLabelMap[project.stage] ?: project.stage,
                        color = CyberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${progressPercent.toInt()}% جاهز",
                        color = CyberTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when (project.stage) {
                        "Draft" -> BorderColor
                        "Fabrication" -> AccentWarning
                        "Lighting" -> CyberPrimary
                        "Assembly" -> CyberTertiary
                        "Delivery" -> CyberSecondary
                        else -> AccentSuccess
                    },
                    trackColor = Color.Black.copy(alpha = 0.3f)
                )

                // Render dynamic tasks checklist for engineers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.BuildCircle, "tech info", tint = TextSecondary, modifier = Modifier.size(14.dp))
                        Text(
                            text = detailedTasks,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.3f))

            // Back & Forward Stage Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (currentStageIdx > 0) {
                        OutlinedButton(
                            onClick = { onUpdateStage(stagesOrder[currentStageIdx - 1]) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, "back stage", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("رجوع", fontSize = 11.sp, color = TextPrimary)
                        }
                    }

                    if (currentStageIdx < stagesOrder.size - 1) {
                        Button(
                            onClick = { onUpdateStage(stagesOrder[currentStageIdx + 1]) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("ترقية وتسيير الإنتاج", color = CyberBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, "next stage", tint = CyberBg, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        // Project completed successfully tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Verified, "Completed icon", tint = AccentSuccess, modifier = Modifier.size(18.dp))
                            Text("تم التسجيل والتركيب بالكامل", color = AccentSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, "Cancel order", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun EstimateLeadRow(
    estimate: ClientEstimate,
    onLaunch: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f)),
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
                Column {
                    Text(
                        text = estimate.clientName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "جوال: ${estimate.phone.ifBlank { "غير مسجل" }}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "$${String.format("%,.0f", estimate.estimatedPrice)}",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Text(
                text = "مخرجات الفايل: ${estimate.details}",
                color = TextPrimary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.OfflineBolt, "Accept & Launch", tint = CyberBg, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعميد وإطلاق التصنيع", color = CyberBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Dismiss lead", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

package com.aistudio.yafta.branding.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.ui.theme.*

@Composable
fun StudioExportTools(
    context: Context,
    signageText: String,
    selectedWidth: Float,
    selectedHeight: Float,
    currentStyle: String,
    currentThemeColor: Color,
    selectedColorIndex: Int,
    colorNames: List<String>,
    isCMYKProfile: Boolean,
    onCMYKProfileChange: (Boolean) -> Unit,
    printDpiResolution: Int,
    onPrintDpiResolutionChange: (Int) -> Unit,
    bleedMarginCm: Float,
    onBleedMarginCmChange: (Float) -> Unit,
    requiredLEDPcs: Int,
    selectedLogoQuadrantIndex: Int,
    logoPositions: List<String>,
    activeLayerIronFrame: Boolean,
    activeLayerAcrylicBack: Boolean,
    activeLayerNeonLeds: Boolean,
    activeLayerTextCore: Boolean,
    itemAreaSqM: Float,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "جودة الطباعة والطبقات • Layers & Calibration",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            // Profile Color Mode and DPI Setup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // CMYK / RGB Toggle
                Column(modifier = Modifier.weight(1f)) {
                    Text("وضع اللوحة البروفايل (Color Profiles)", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBg)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isCMYKProfile) CyberPrimary else Color.Transparent)
                                .clickable { onCMYKProfileChange(false) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("RGB (شاشة)", color = if (!isCMYKProfile) CyberBg else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCMYKProfile) CyberPrimary else Color.Transparent)
                                .clickable { onCMYKProfileChange(true) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("CMYK (طباعة)", color = if (isCMYKProfile) CyberBg else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // DPI Picker
                Column(modifier = Modifier.weight(1f)) {
                    Text("الدقة والمقاييس (DPI Setup)", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBg)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf(72, 150, 300).forEach { dpi ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (printDpiResolution == dpi) CyberSecondary else Color.Transparent)
                                    .clickable { onPrintDpiResolutionChange(dpi) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$dpi", color = if (printDpiResolution == dpi) CyberBg else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Bleed Margins Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("هوامش الأمان والنزيف (Bleed Margin): ${String.format("%.1f", bleedMarginCm)} سم", color = TextPrimary, fontSize = 12.sp)
                    Text("محسوبة بالأحمر", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = bleedMarginCm,
                    onValueChange = onBleedMarginCmChange,
                    valueRange = 0.0f..10.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberPrimary,
                        activeTrackColor = CyberPrimary,
                        inactiveTrackColor = BorderColor
                    )
                )
            }

            // Report panel (either CMYK separation details or RGB power calculation)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberBg)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isCMYKProfile) {
                    // CMYK Ink Spectrum details
                    Text(
                        text = "تحليل الطيف الحبري للطباعة • CMYK Ink Spectrum Profiles",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    val cyanVal = when(selectedColorIndex) { 0 -> 95; 1 -> 5; 2 -> 65; 3 -> 10; else -> 0 }
                    val magentaVal = when(selectedColorIndex) { 0 -> 5; 1 -> 95; 2 -> 80; 3 -> 35; else -> 0 }
                    val yellowVal = when(selectedColorIndex) { 0 -> 2; 1 -> 15; 2 -> 5; 3 -> 95; else -> 0 }
                    val blackVal = when(selectedColorIndex) { 0 -> 5; 1 -> 2; 2 -> 10; 3 -> 5; else -> 5 }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Cyan (C)", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.width(62.dp))
                            LinearProgressIndicator(progress = { cyanVal / 100f }, color = Color(0xFF00F0FF), trackColor = BorderColor, modifier = Modifier.weight(1f).height(4.dp))
                            Text("$cyanVal%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Magenta (M)", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.width(62.dp))
                            LinearProgressIndicator(progress = { magentaVal / 100f }, color = Color(0xFFFF007F), trackColor = BorderColor, modifier = Modifier.weight(1f).height(4.dp))
                            Text("$magentaVal%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Yellow (Y)", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.width(62.dp))
                            LinearProgressIndicator(progress = { yellowVal / 100f }, color = Color(0xFFEAB308), trackColor = BorderColor, modifier = Modifier.weight(1f).height(4.dp))
                            Text("$yellowVal%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Key Black (K)", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.width(62.dp))
                            LinearProgressIndicator(progress = { blackVal / 100f }, color = Color.DarkGray, trackColor = BorderColor, modifier = Modifier.weight(1f).height(4.dp))
                            Text("$blackVal%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                } else {
                    // RGB Load details
                    Text(
                        text = "رقابة الأحمال واستهلاك الكهرباء للإنارة النيون • RGB Light & Power Draw",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    val estimPowerWatts = requiredLEDPcs * 0.22
                    val electricCurrentAmps = String.format("%.2f", estimPowerWatts / 12.0)
                    val recommendedPsu = if (estimPowerWatts > 240) "MeanWell LRS-350-12 (350W 12V 29A)" else "MeanWell LRS-150-12 (150W 12V 12.5A)"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("إجمالي استهلاك اللوحة", color = TextSecondary, fontSize = 10.sp)
                            Text("${estimPowerWatts.toInt()} W", color = CyberPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("تيار مستمر: $electricCurrentAmps Amps", color = TextSecondary, fontSize = 9.sp)
                        }
                        Column(modifier = Modifier.weight(1.8f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("مزود الطاقة المحول الافتراضي الموصى به", color = TextSecondary, fontSize = 10.sp)
                            Text(recommendedPsu, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("درجة حماية ممتدة IP67 ضد للماء", color = AccentSuccess, fontSize = 9.sp)
                        }
                    }
                }
            }

            // Exporter Action Button
            Button(
                onClick = {
                    val specDetails = """
                    ==================================================
                    ملف التصميم الجاهز للطباعة والقَص • PRINT-READY CAD SPECIFICATIONS
                    ==================================================
                    رمز الفايل: CAD-${System.currentTimeMillis() % 100000}
                    النص المكتوب: $signageText
                    أبعاد التصنيع الكلية: $selectedWidth x $selectedHeight متر
                    المساحة الإجمالية: ${String.format("%.2f", itemAreaSqM)} متر مربع
                    طريقة الصناعة: $currentStyle
                    اللون الرئيسي للمبدد: ${colorNames[selectedColorIndex]} ($currentThemeColor)
                    بروفايل الألوان المشغّل: ${if (isCMYKProfile) "CMYK (معتمد للمطابع)" else "RGB (شاشات رقمية)"}
                    هوامش الطباعة والنزيف المطبقة: ${String.format("%.1f", bleedMarginCm)} سم
                    دقة التصدير الفعلية: $printDpiResolution DPI
                    عدد موديولات الليد المقترحة: $requiredLEDPcs موديول
                    الشعار الإضافي المدمج: ${logoPositions[selectedLogoQuadrantIndex]}
                    طبقات الفايل النشطة:
                    - الهيكل الحديدي والدعامات: ${if(activeLayerIronFrame) "نشط" else "ملغى"}
                    - اللوح الخلفي الأكريليك: ${if(activeLayerAcrylicBack) "نشط" else "ملغى"}
                    - شبكة النيون الليد: ${if(activeLayerNeonLeds) "نشط" else "ملغى"}
                    - الحروف والأحرف ثلاثية الأبعاد: ${if(activeLayerTextCore) "نشط" else "ملغى"}
                    --------------------------------------------------
                    المحتوى التالي يمثل هيكلية SVG المجمعة لقص الليزر:
                    <svg width="${selectedWidth}m" height="${selectedHeight}m" viewBox="0 0 ${selectedWidth * 100} ${selectedHeight * 100}">
                      <rect width="100%" height="100%" fill="none" stroke="${currentThemeColor}" stroke-width="${bleedMarginCm}"/>
                      <text x="50%" y="50%" text-anchor="middle" font-weight="bold" fill="${currentThemeColor}">$signageText</text>
                    </svg>
                    ==================================================
                    مؤسسة يافطة للإعلان واللوحات الرقمية 2026
                    ==================================================
                    """.trimIndent()

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TITLE, "يافطة_تصميم_جاهز_للطباعة.svg")
                        putExtra(Intent.EXTRA_SUBJECT, "تصدير لوحة ذكية")
                        putExtra(Intent.EXTRA_TEXT, specDetails)
                        type = "text/plain"
                    }
                    val chooser = Intent.createChooser(sendIntent, "تنزيل وتصدير التصميم")
                    context.startActivity(chooser)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, CyberSecondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Output, "CAD Print spec", tint = CyberSecondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("تصدير التصميم بصيغة SVG / PDF للقص ليزر", color = CyberSecondary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

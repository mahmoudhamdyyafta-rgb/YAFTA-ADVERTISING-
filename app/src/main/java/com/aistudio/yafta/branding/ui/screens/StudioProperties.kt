package com.aistudio.yafta.branding.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioProperties(
    signageText: String,
    onSignageTextChange: (String) -> Unit,
    selectedWidth: Float,
    onSelectedWidthChange: (Float) -> Unit,
    selectedHeight: Float,
    onSelectedHeightChange: (Float) -> Unit,
    signageStyles: List<String>,
    selectedStyleIndex: Int,
    onSelectedStyleIndexChange: (Int) -> Unit,
    colorOptions: List<Color>,
    colorNames: List<String>,
    selectedColorIndex: Int,
    onSelectedColorIndexChange: (Int) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "مواصفات وأبعاد يافطة الإعلان",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            // Text Input
            OutlinedTextField(
                value = signageText,
                onValueChange = { if (it.length <= 18) onSignageTextChange(it) },
                label = { Text("النص المكتوب على اليافطة (Neon Text)") },
                leadingIcon = { Icon(Icons.Default.TextFields, "Text Icon", tint = CyberPrimary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = CyberPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("studio_signage_text"),
                singleLine = true
            )

            // Layout Width Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("العرض (Width): ${String.format("%.1f", selectedWidth)} متر", color = TextPrimary, fontSize = 13.sp)
                    Text("أقصى حد 12م", color = TextSecondary, fontSize = 11.sp)
                }
                Slider(
                    value = selectedWidth,
                    onValueChange = onSelectedWidthChange,
                    valueRange = 1.0f..12.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberPrimary,
                        activeTrackColor = CyberPrimary,
                        inactiveTrackColor = BorderColor
                    )
                )
            }

            // Height Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("الارتفاع (Height): ${String.format("%.1f", selectedHeight)} متر", color = TextPrimary, fontSize = 13.sp)
                    Text("أقصى حد 4م", color = TextSecondary, fontSize = 11.sp)
                }
                Slider(
                    value = selectedHeight,
                    onValueChange = onSelectedHeightChange,
                    valueRange = 0.5f..4.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberPrimary,
                        activeTrackColor = CyberPrimary,
                        inactiveTrackColor = BorderColor
                    )
                )
            }

            // Style Selection FlowRow
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("طريقة ونوع تصنيع اليافطة (Structure Type)", color = TextSecondary, fontSize = 12.sp)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    signageStyles.forEachIndexed { idx, style ->
                        val isSelected = idx == selectedStyleIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) CyberPrimary else BorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectedStyleIndexChange(idx) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) CyberPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Color selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("لون إضاءة النيون الليد (Neon Theme)", color = TextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorOptions.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .border(
                                    width = if (index == selectedColorIndex) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectedColorIndexChange(index) }
                        )
                        Text(
                            text = colorNames[index],
                            color = if (index == selectedColorIndex) Color.White else TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

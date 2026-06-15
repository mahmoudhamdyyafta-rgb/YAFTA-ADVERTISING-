package com.aistudio.yafta.branding.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun StudioAssets(
    renderModeIndex: Int,
    onRenderModeIndexChange: (Int) -> Unit,
    logoPositions: List<String>,
    selectedLogoQuadrantIndex: Int,
    onSelectedLogoQuadrantIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Render backdrop selectors (brick, vinyl, blueprint)
        Text(
            text = "بيئة الخلفية للمعاينة (Backdrop Canvas Style):",
            color = TextSecondary,
            fontSize = 11.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val modes = listOf("جدار الورشة (Brick Neon)", "قماش المطبعة (Vinyl Sheet)", "شاسيه الليزر (Blueprint)")
            modes.forEachIndexed { i, label ->
                val isSelected = renderModeIndex == i
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberPrimary.copy(alpha = 0.2f) else CyberSurface)
                        .border(1.dp, if (isSelected) CyberPrimary else BorderColor, RoundedCornerShape(8.dp))
                        .clickable { onRenderModeIndexChange(i) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) CyberPrimary else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Watermark original settings
        Text(
            text = "موضع الشعار المدمج للاعلان (Watermark/Logo Origin):",
            color = TextSecondary,
            fontSize = 11.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            logoPositions.forEachIndexed { i, label ->
                FilterChip(
                    selected = selectedLogoQuadrantIndex == i,
                    onClick = { onSelectedLogoQuadrantIndexChange(i) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberPrimary,
                        selectedLabelColor = CyberBg,
                        containerColor = CyberBg,
                        labelColor = Color.White
                    )
                )
            }
        }
    }
}

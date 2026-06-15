package com.aistudio.yafta.branding.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioLayers(
    activeLayerIronFrame: Boolean,
    onActiveLayerIronFrameChange: (Boolean) -> Unit,
    activeLayerAcrylicBack: Boolean,
    onActiveLayerAcrylicBackChange: (Boolean) -> Unit,
    activeLayerNeonLeds: Boolean,
    onActiveLayerNeonLedsChange: (Boolean) -> Unit,
    activeLayerTextCore: Boolean,
    onActiveLayerTextCoreChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "إدارة طبقات التصنيع باللوحة (Toggle Active Layers):",
            color = TextSecondary,
            fontSize = 11.sp
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeLayerIronFrame,
                onClick = { onActiveLayerIronFrameChange(!activeLayerIronFrame) },
                label = { Text("شاسيه حديدي", fontSize = 11.sp) },
                leadingIcon = { if(activeLayerIronFrame) Icon(Icons.Default.Check, "check", modifier = Modifier.size(12.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberTertiary,
                    selectedLabelColor = CyberBg,
                    containerColor = CyberBg,
                    labelColor = Color.White
                )
            )
            FilterChip(
                selected = activeLayerAcrylicBack,
                onClick = { onActiveLayerAcrylicBackChange(!activeLayerAcrylicBack) },
                label = { Text("خلفية أكريليك", fontSize = 11.sp) },
                leadingIcon = { if(activeLayerAcrylicBack) Icon(Icons.Default.Check, "check", modifier = Modifier.size(12.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberTertiary,
                    selectedLabelColor = CyberBg,
                    containerColor = CyberBg,
                    labelColor = Color.White
                )
            )
            FilterChip(
                selected = activeLayerNeonLeds,
                onClick = { onActiveLayerNeonLedsChange(!activeLayerNeonLeds) },
                label = { Text("إضاءة ومبدد نيون", fontSize = 11.sp) },
                leadingIcon = { if(activeLayerNeonLeds) Icon(Icons.Default.Check, "check", modifier = Modifier.size(12.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberTertiary,
                    selectedLabelColor = CyberBg,
                    containerColor = CyberBg,
                    labelColor = Color.White
                )
            )
            FilterChip(
                selected = activeLayerTextCore,
                onClick = { onActiveLayerTextCoreChange(!activeLayerTextCore) },
                label = { Text("الحروف والخطوط الأساسية", fontSize = 11.sp) },
                leadingIcon = { if(activeLayerTextCore) Icon(Icons.Default.Check, "check", modifier = Modifier.size(12.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberTertiary,
                    selectedLabelColor = CyberBg,
                    containerColor = CyberBg,
                    labelColor = Color.White
                )
            )
        }
    }
}

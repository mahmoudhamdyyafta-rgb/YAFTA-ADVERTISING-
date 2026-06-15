package com.aistudio.yafta.branding.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.ui.theme.*

@Composable
fun StudioCanvas(
    signageText: String,
    selectedWidth: Float,
    selectedHeight: Float,
    currentStyle: String,
    currentThemeColor: Color,
    selectedColorIndex: Int,
    colorNames: List<String>,
    isCMYKProfile: Boolean,
    printDpiResolution: Int,
    bleedMarginCm: Float,
    showGridLines: Boolean,
    showGuides: Boolean,
    showSafeArea: Boolean,
    renderModeIndex: Int,
    activeLayerIronFrame: Boolean,
    activeLayerAcrylicBack: Boolean,
    activeLayerNeonLeds: Boolean,
    activeLayerTextCore: Boolean,
    selectedLogoQuadrantIndex: Int,
    logoPositions: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberBg),
        border = BorderStroke(1.dp, currentThemeColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Visual proportions based on actual input dimensions
                val signageWidthRatio = selectedWidth / 12.0f
                val signageHeightRatio = selectedHeight / 4.0f

                val drawW = (canvasW * 0.75f * signageWidthRatio).coerceAtLeast(140f).coerceAtMost(canvasW * 0.85f)
                val drawH = (canvasH * 0.55f * signageHeightRatio).coerceAtLeast(60f).coerceAtMost(canvasH * 0.75f)

                val startX = (canvasW - drawW) / 2
                val startY = (canvasH - drawH) / 2

                // RENDER BACKDROP TEXTURES
                when (renderModeIndex) {
                    0 -> { // 0 = Production Brick Neon
                        // Draw horizontal brick joints
                        for (y in 0 until canvasH.toInt() step 20) {
                            drawLine(
                                color = Color(0xFF1F2937).copy(alpha = 0.3f),
                                start = Offset(0f, y.toFloat()),
                                end = Offset(canvasW, y.toFloat()),
                                strokeWidth = 2f
                            )
                            // Draw staggered vertical seams
                            val offset = if ((y / 20) % 2 == 0) 20f else 0f
                            for (x in offset.toInt() until canvasW.toInt() step 40) {
                                drawLine(
                                    color = Color(0xFF1F2937).copy(alpha = 0.3f),
                                    start = Offset(x.toFloat(), y.toFloat()),
                                    end = Offset(x.toFloat(), (y + 20).toFloat()),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                    1 -> { // 1 = Vinyl sheet banner
                        // Render fine canvas dotted texture
                        for (x in 0 until canvasW.toInt() step 6) {
                            for (y in 0 until canvasH.toInt() step 6) {
                                drawCircle(
                                    color = Color(0xFF475569).copy(alpha = 0.15f),
                                    radius = 1f,
                                    center = Offset(x.toFloat(), y.toFloat())
                                )
                            }
                        }
                    }
                    2 -> { // 2 = Laser-Cut blueprint schema
                        drawRect(
                            color = Color(0xFF041E42),
                            topLeft = Offset(0f, 0f),
                            size = Size(canvasW, canvasH)
                        )
                    }
                }

                // A. GRID OVERLAYS (Toggable)
                if (showGridLines) {
                    val gridColor = if (renderModeIndex == 2) Color(0xFF1E3A8A).copy(alpha = 0.8f) else BorderColor.copy(alpha = 0.3f)
                    for (cellX in 0 until canvasW.toInt() step 25) {
                        drawLine(color = gridColor, start = Offset(cellX.toFloat(), 0f), end = Offset(cellX.toFloat(), canvasH), strokeWidth = 0.8f)
                    }
                    for (cellY in 0 until canvasH.toInt() step 25) {
                        drawLine(color = gridColor, start = Offset(0f, cellY.toFloat()), end = Offset(canvasW, cellY.toFloat()), strokeWidth = 0.8f)
                    }
                }

                // B. PRECISION PHYSICAL RULERS (Top & Left Margins)
                val tickColor = if (renderModeIndex == 2) Color(0xFF60A5FA).copy(alpha = 0.8f) else TextSecondary.copy(alpha = 0.5f)
                drawLine(color = tickColor, start = Offset(0f, 2f), end = Offset(canvasW, 2f), strokeWidth = 1f)
                for (tx in 0 until canvasW.toInt() step 12) {
                    val tickLen = if (tx % 60 == 0) 10f else 5f
                    drawLine(color = tickColor, start = Offset(tx.toFloat(), 2f), end = Offset(tx.toFloat(), 2f + tickLen), strokeWidth = 1f)
                }
                drawLine(color = tickColor, start = Offset(2f, 0f), end = Offset(2f, canvasH), strokeWidth = 1f)
                for (ty in 0 until canvasH.toInt() step 12) {
                    val tickLen = if (ty % 60 == 0) 10f else 5f
                    drawLine(color = tickColor, start = Offset(2f, ty.toFloat()), end = Offset(2f + tickLen, ty.toFloat()), strokeWidth = 1f)
                }

                // C. GUIDES / CROSSHAIRS (Toggable construction lines)
                if (showGuides) {
                    val guideBrushColor = Color(0xFFEAB308).copy(alpha = 0.6f)
                    drawLine(
                        color = guideBrushColor,
                        start = Offset(canvasW / 2, 0f),
                        end = Offset(canvasW / 2, canvasH),
                        strokeWidth = 1f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                    drawLine(
                        color = guideBrushColor,
                        start = Offset(0f, canvasH / 2),
                        end = Offset(canvasW, canvasH / 2),
                        strokeWidth = 1f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                }

                // D. IRON BRACKETS STRUCTURAL LAYERS
                if (activeLayerIronFrame) {
                    val bracketColor = if (renderModeIndex == 2) Color(0xFF60A5FA).copy(alpha = 0.6f) else Color(0xFF334155)
                    drawLine(
                        color = bracketColor,
                        start = Offset(startX + drawW * 0.25f, 0f),
                        end = Offset(startX + drawW * 0.25f, startY),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = bracketColor,
                        start = Offset(startX + drawW * 0.75f, 0f),
                        end = Offset(startX + drawW * 0.75f, startY),
                        strokeWidth = 4f
                    )
                    drawRect(
                        color = if (renderModeIndex == 2) Color(0xFF1E3A8A).copy(alpha = 0.5f) else Color(0xFF1E293B),
                        topLeft = Offset(startX + drawW * 0.15f, startY - 8f),
                        size = Size(drawW * 0.7f, 8f)
                    )
                }

                // E. ACRYLIC BASE BOARD SHEET (Backing)
                if (activeLayerAcrylicBack) {
                    val backingColor = when (renderModeIndex) {
                        2 -> Color(0xFF1D4ED8).copy(alpha = 0.25f)
                        1 -> Color(0xFFE2E8F0).copy(alpha = 0.95f)
                        else -> Color(0xFF0F172A).copy(alpha = 0.85f)
                    }
                    drawRoundRect(
                        color = backingColor,
                        topLeft = Offset(startX, startY),
                        size = Size(drawW, drawH),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                }

                // F. PRINT SAFE ZONE AND BLEED MARGIN OVERLAY (GREEN + RED DASHED)
                if (showSafeArea && bleedMarginCm > 0f) {
                    val bleedPx = (bleedMarginCm / 10f) * 16f
                    drawRoundRect(
                        color = Color.Red.copy(alpha = 0.6f),
                        topLeft = Offset(startX + bleedPx, startY + bleedPx),
                        size = Size(drawW - bleedPx * 2, drawH - bleedPx * 2),
                        cornerRadius = CornerRadius(10f, 10f),
                        style = Stroke(width = 1.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f))
                    )
                    val safePx = bleedPx + 10f
                    drawRoundRect(
                        color = Color(0xFF10B981).copy(alpha = 0.6f),
                        topLeft = Offset(startX + safePx, startY + safePx),
                        size = Size(drawW - safePx * 2, drawH - safePx * 2),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(width = 1.2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                    )
                }

                // G. LED LIGHT BULBS & POWER BLOOM
                if (activeLayerNeonLeds && renderModeIndex == 0) {
                    drawRoundRect(
                        color = currentThemeColor.copy(alpha = 0.24f),
                        topLeft = Offset(startX - 12f, startY - 12f),
                        size = Size(drawW + 24f, drawH + 24f),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                }

                // H. WATERMARK/LOGO INTEGRATED PLACEMENT
                if (selectedLogoQuadrantIndex > 0) {
                    val badgeSize = 26f
                    val logoColor = if (renderModeIndex == 2) Color(0xFF60A5FA) else currentThemeColor
                    val logoOffset = when (selectedLogoQuadrantIndex) {
                        1 -> Offset(startX + 16f, startY + 16f)
                        2 -> Offset(startX + drawW - badgeSize - 16f, startY + 16f)
                        3 -> Offset(startX + 16f, startY + drawH - badgeSize - 16f)
                        else -> Offset(startX + drawW - badgeSize - 16f, startY + drawH - badgeSize - 16f)
                    }

                    val starPath = Path().apply {
                        moveTo(logoOffset.x + badgeSize/2, logoOffset.y)
                        lineTo(logoOffset.x + badgeSize * 0.65f, logoOffset.y + badgeSize * 0.35f)
                        lineTo(logoOffset.x + badgeSize, logoOffset.y + badgeSize * 0.35f)
                        lineTo(logoOffset.x + badgeSize * 0.75f, logoOffset.y + badgeSize * 0.6f)
                        lineTo(logoOffset.x + badgeSize * 0.85f, logoOffset.y + badgeSize)
                        lineTo(logoOffset.x + badgeSize/2, logoOffset.y + badgeSize * 0.75f)
                        lineTo(logoOffset.x + badgeSize * 0.15f, logoOffset.y + badgeSize)
                        lineTo(logoOffset.x + badgeSize * 0.25f, logoOffset.y + badgeSize * 0.6f)
                        lineTo(logoOffset.x, logoOffset.y + badgeSize * 0.35f)
                        lineTo(logoOffset.x + badgeSize * 0.35f, logoOffset.y + badgeSize * 0.35f)
                        close()
                    }
                    drawPath(path = starPath, color = logoColor)
                }

                // I. CORE ADS TEXT DISPLAY
                if (activeLayerTextCore) {
                    val paint = android.graphics.Paint().apply {
                        color = if (renderModeIndex == 2) Color(0xFF00F0FF).toArgb() else currentThemeColor.toArgb()
                        textSize = if (signageText.length > 9) 32f else 42f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                        if (activeLayerNeonLeds && renderModeIndex == 0) {
                            setShadowLayer(18f, 0f, 0f, currentThemeColor.toArgb())
                        } else if (renderModeIndex == 1) {
                            setShadowLayer(4f, 2f, 2f, Color(0xFF64748B).toArgb())
                        }
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        signageText.uppercase(),
                        canvasW / 2,
                        canvasH / 2 + paint.textSize / 3,
                        paint
                    )
                }

                // Render vector frame border
                if (renderModeIndex == 2) {
                    drawRoundRect(
                        color = Color(0xFF3B82F6),
                        topLeft = Offset(startX, startY),
                        size = Size(drawW, drawH),
                        cornerRadius = CornerRadius(14f, 14f),
                        style = Stroke(width = 1.5f)
                    )
                } else if (activeLayerAcrylicBack) {
                    drawRoundRect(
                        color = currentThemeColor.copy(alpha = 0.5f),
                        topLeft = Offset(startX, startY),
                        size = Size(drawW, drawH),
                        cornerRadius = CornerRadius(14f, 14f),
                        style = Stroke(width = 3f)
                    )
                }
            }

            // Specs marker overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val mpResolution = String.format("%.2f", (selectedWidth * 100 * selectedHeight * 100 * printDpiResolution) / 1_000_000.0f)
                Text(
                    text = "${String.format("%.1f", selectedWidth)}m x ${String.format("%.1f", selectedHeight)}m (" +
                           "${if(isCMYKProfile) "CMYK" else "RGB"}) • $printDpiResolution DPI ($mpResolution MP)",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

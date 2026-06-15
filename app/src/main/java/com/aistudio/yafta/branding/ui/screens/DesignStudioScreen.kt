package com.aistudio.yafta.branding.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

fun Double.topUpToNearestTen(): Double {
    return kotlin.math.ceil(this / 10.0) * 10.0
}

@Composable
fun SingleSelectTabs(
    selectedSegmentIndex: Int,
    options: List<String>,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberBg)
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(4.dp)
    ) {
        options.forEachIndexed { i, title ->
            val isSelected = selectedSegmentIndex == i
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) CyberPrimary else Color.Transparent)
                    .clickable { onSegmentSelected(i) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isSelected) CyberBg else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DesignStudioScreen(viewModel: YaftaViewModel) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Local Design States
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var signageText by remember { mutableStateOf("YAFTA CO") }
    var selectedWidth by remember { mutableStateOf(3.0f) } // meters
    var selectedHeight by remember { mutableStateOf(1.0f) } // meters
    
    val signageStyles = listOf("3D Glowing Acrylic", "Neon Flex", "Backlit Box", "Flex Billboard")
    var selectedStyleIndex by remember { mutableStateOf(0) }
    val currentStyle = signageStyles[selectedStyleIndex]

    val colorOptions = listOf(CyberPrimary, CyberSecondary, CyberTertiary, AccentWarning, Color.White)
    val colorNames = listOf("Cyan Glow", "Hot Pink", "Purple", "Gold Light", "Cold White")
    var selectedColorIndex by remember { mutableStateOf(0) }
    val currentThemeColor = colorOptions[selectedColorIndex]

    // Print & Layers States
    var isCMYKProfile by remember { mutableStateOf(false) } // false = RGB, true = CMYK
    var printDpiResolution by remember { mutableStateOf(150) } // 72, 150, 300 DPI
    var bleedMarginCm by remember { mutableStateOf(2.0f) } // bleed margins in cm

    // Calibration Display Toggles
    var showGridLines by remember { mutableStateOf(true) }
    var showGuides by remember { mutableStateOf(true) }
    var showSafeArea by remember { mutableStateOf(true) }
    var renderModeIndex by remember { mutableStateOf(0) } // 0 = Brick Neon, 1 = Vinyl texture, 2 = Blueprint

    // Manufacturing Layers visibility toggle
    var activeLayerIronFrame by remember { mutableStateOf(true) }
    var activeLayerAcrylicBack by remember { mutableStateOf(true) }
    var activeLayerNeonLeds by remember { mutableStateOf(true) }
    var activeLayerTextCore by remember { mutableStateOf(true) }

    // Logo corner placement index
    val logoPositions = listOf("بدون شعار", "أعلى اليسار", "أعلى اليمين", "أسفل اليسار", "أسفل اليمين")
    var selectedLogoQuadrantIndex by remember { mutableStateOf(0) }

    // AI Workspace States
    var businessType by remember { mutableStateOf("Cafe & Lounge") }
    var businessDesc by remember { mutableStateOf("Specialty coffee, modern industrial lounge vibe, targets youths.") }
    var aiExtraInstructions by remember { mutableStateOf("We prefer a modern glowing sign that has both Arabic and English text.") }

    val aiResult by viewModel.aiResult.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    var activeTabSubIndex by remember { mutableStateOf(0) } // 0 = Visual Studio, 1 = Gemini AI Assistant

    // Material dynamic calculations (BOM)
    val itemAreaSqM = selectedWidth * selectedHeight
    val baseMaterialCostPerSqm = when (currentStyle) {
        "3D Glowing Acrylic" -> 120.0
        "Neon Flex" -> 90.0
        "Backlit Box" -> 70.0
        else -> 40.0 // Flex Billboard
    }
    val materialsCost = itemAreaSqM * baseMaterialCostPerSqm
    val requiredLEDPcs = (itemAreaSqM * if (currentStyle == "Neon Flex") 120 else 80).toInt()
    val ledCost = requiredLEDPcs * 0.45
    val structureCost = itemAreaSqM * 25.0
    val totalDirectCost = materialsCost + ledCost + structureCost
    val estimatedSellingPrice = (totalDirectCost * 1.6).topUpToNearestTen()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 840.dp

        if (isWideScreen) {
            // Adaptive Responsive Side-by-Side Split Workspace
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Workspace column (Left area taking remainder space)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "استديو يافطة الرائد للتصاميم ثنائية الأبعاد • Digital Workstation",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // 1. Studio Canvas
                    StudioCanvas(
                        signageText = signageText,
                        selectedWidth = selectedWidth,
                        selectedHeight = selectedHeight,
                        currentStyle = currentStyle,
                        currentThemeColor = currentThemeColor,
                        selectedColorIndex = selectedColorIndex,
                        colorNames = colorNames,
                        isCMYKProfile = isCMYKProfile,
                        printDpiResolution = printDpiResolution,
                        bleedMarginCm = bleedMarginCm,
                        showGridLines = showGridLines,
                        showGuides = showGuides,
                        showSafeArea = showSafeArea,
                        renderModeIndex = renderModeIndex,
                        activeLayerIronFrame = activeLayerIronFrame,
                        activeLayerAcrylicBack = activeLayerAcrylicBack,
                        activeLayerNeonLeds = activeLayerNeonLeds,
                        activeLayerTextCore = activeLayerTextCore,
                        selectedLogoQuadrantIndex = selectedLogoQuadrantIndex,
                        logoPositions = logoPositions
                    )

                    // Interactive calibration overlays
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = showGridLines,
                            onClick = { showGridLines = !showGridLines },
                            label = { Text("شبكة الأبعاد (Grid)", fontSize = 10.sp) },
                            leadingIcon = { if (showGridLines) Icon(Icons.Default.GridOn, "grid", modifier = Modifier.size(10.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberSecondary.copy(alpha = 0.2f), selectedLabelColor = CyberSecondary, containerColor = CyberSurface, labelColor = Color.White)
                        )
                        FilterChip(
                            selected = showGuides,
                            onClick = { showGuides = !showGuides },
                            label = { Text("المحاور (Guides)", fontSize = 10.sp) },
                            leadingIcon = { if (showGuides) Icon(Icons.Default.SquareFoot, "guides", modifier = Modifier.size(10.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberSecondary.copy(alpha = 0.2f), selectedLabelColor = CyberSecondary, containerColor = CyberSurface, labelColor = Color.White)
                        )
                        FilterChip(
                            selected = showSafeArea,
                            onClick = { showSafeArea = !showSafeArea },
                            label = { Text("الأمان والنزيف (Margins)", fontSize = 10.sp) },
                            leadingIcon = { if (showSafeArea) Icon(Icons.Default.Fullscreen, "safe", modifier = Modifier.size(10.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberSecondary.copy(alpha = 0.2f), selectedLabelColor = CyberSecondary, containerColor = CyberSurface, labelColor = Color.White)
                        )
                    }

                    // 2. Setup Properties
                    StudioProperties(
                        signageText = signageText,
                        onSignageTextChange = { signageText = it },
                        selectedWidth = selectedWidth,
                        onSelectedWidthChange = { selectedWidth = it },
                        selectedHeight = selectedHeight,
                        onSelectedHeightChange = { selectedHeight = it },
                        signageStyles = signageStyles,
                        selectedStyleIndex = selectedStyleIndex,
                        onSelectedStyleIndexChange = { selectedStyleIndex = it },
                        colorOptions = colorOptions,
                        colorNames = colorNames,
                        selectedColorIndex = selectedColorIndex,
                        onSelectedColorIndexChange = { selectedColorIndex = it }
                    )

                    // 3. Dynamic Backdrop environment & Logo Selection
                    StudioAssets(
                        renderModeIndex = renderModeIndex,
                        onRenderModeIndexChange = { renderModeIndex = it },
                        logoPositions = logoPositions,
                        selectedLogoQuadrantIndex = selectedLogoQuadrantIndex,
                        onSelectedLogoQuadrantIndexChange = { selectedLogoQuadrantIndex = it }
                    )

                    // 4. Calibration & Quality tools
                    StudioExportTools(
                        context = context,
                        signageText = signageText,
                        selectedWidth = selectedWidth,
                        selectedHeight = selectedHeight,
                        currentStyle = currentStyle,
                        currentThemeColor = currentThemeColor,
                        selectedColorIndex = selectedColorIndex,
                        colorNames = colorNames,
                        isCMYKProfile = isCMYKProfile,
                        onCMYKProfileChange = { isCMYKProfile = it },
                        printDpiResolution = printDpiResolution,
                        onPrintDpiResolutionChange = { printDpiResolution = it },
                        bleedMarginCm = bleedMarginCm,
                        onBleedMarginCmChange = { bleedMarginCm = it },
                        requiredLEDPcs = requiredLEDPcs,
                        selectedLogoQuadrantIndex = selectedLogoQuadrantIndex,
                        logoPositions = logoPositions,
                        activeLayerIronFrame = activeLayerIronFrame,
                        activeLayerAcrylicBack = activeLayerAcrylicBack,
                        activeLayerNeonLeds = activeLayerNeonLeds,
                        activeLayerTextCore = activeLayerTextCore,
                        itemAreaSqM = itemAreaSqM
                    )

                    // 5. Layer Selection
                    StudioLayers(
                        activeLayerIronFrame = activeLayerIronFrame,
                        onActiveLayerIronFrameChange = { activeLayerIronFrame = it },
                        activeLayerAcrylicBack = activeLayerAcrylicBack,
                        onActiveLayerAcrylicBackChange = { activeLayerAcrylicBack = it },
                        activeLayerNeonLeds = activeLayerNeonLeds,
                        onActiveLayerNeonLedsChange = { activeLayerNeonLeds = it },
                        activeLayerTextCore = activeLayerTextCore,
                        onActiveLayerTextCoreChange = { activeLayerTextCore = it }
                    )

                    // 6. Cost Evaluation Summary Card
                    CostSummaryCard(
                        itemAreaSqM = itemAreaSqM,
                        materialsCost = materialsCost,
                        ledCost = ledCost,
                        requiredLEDPcs = requiredLEDPcs,
                        structureCost = structureCost,
                        estimatedSellingPrice = estimatedSellingPrice
                    )

                    // 7. Pipeline Action card
                    SalesActionCard(
                        clientName = clientName,
                        onClientNameChange = { clientName = it },
                        clientPhone = clientPhone,
                        onClientPhoneChange = { clientPhone = it },
                        currentStyle = currentStyle,
                        signageText = signageText,
                        selectedWidth = selectedWidth,
                        selectedHeight = selectedHeight,
                        selectedColorIndex = selectedColorIndex,
                        colorNames = colorNames,
                        estimatedSellingPrice = estimatedSellingPrice,
                        viewModel = viewModel
                    )
                }

                // AI Companion Sidebar (Constrained and adaptive, taking max 320.dp width)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "مستشار الذكاء الاصطناعي • Gemini AI Planner",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Text(
                            text = "اكتب تفاصيل نشاط متجرك أو شركتك ليقوم الذكاء الاصطناعي بنمذجة مقاسات لوحات مفصلة وشعار ترويجي ملائم.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        OutlinedTextField(
                            value = businessType,
                            onValueChange = { businessType = it },
                            label = { Text("تصنيف الـ Business", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = CyberPrimary,
                                unfocusedLabelColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = businessDesc,
                            onValueChange = { businessDesc = it },
                            label = { Text("تفاصيل وإبداع العلامة", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = CyberPrimary,
                                unfocusedLabelColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        OutlinedTextField(
                            value = aiExtraInstructions,
                            onValueChange = { aiExtraInstructions = it },
                            label = { Text("أي توجيهات مميزة", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberPrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = CyberPrimary,
                                unfocusedLabelColor = TextSecondary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        Button(
                            onClick = {
                                viewModel.generateAiSuggestions(businessType, businessDesc, aiExtraInstructions)
                                keyboardController?.hide()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_ai_button"),
                            enabled = !aiLoading
                        ) {
                            if (aiLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Icon(Icons.Default.AutoAwesome, "Spark", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("استشارة (Gemini AI)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))

                        // Response Section
                        if (aiResult.isNotBlank() || aiLoading) {
                            if (aiLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "توليد أفضل المقترحات والأحجام الإعلانية...",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Text(
                                    text = aiResult,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Compact Mobile & Tablet Portrait UI with Single Select Category Tabs
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tab Selection Controller
                SingleSelectTabs(
                    selectedSegmentIndex = activeTabSubIndex,
                    options = listOf("استديو التصميم (Design Studio)", "مستشار الذكاء الاصطناعي (Gemini AI)"),
                    onSegmentSelected = { activeTabSubIndex = it }
                )

                if (activeTabSubIndex == 0) {
                    // Mobile View: Canvas Controls Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf("جدار الورشة", "قماش المطبعة", "Blueprint")
                        modes.forEachIndexed { i, label ->
                            val isSelected = renderModeIndex == i
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberPrimary.copy(alpha = 0.2f) else CyberSurface)
                                    .border(1.dp, if (isSelected) CyberPrimary else BorderColor, RoundedCornerShape(8.dp))
                                    .clickable { renderModeIndex = i }
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

                    // Mobile View: Guide layers indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = showGridLines,
                            onClick = { showGridLines = !showGridLines },
                            label = { Text("شبكة الأبعاد", fontSize = 10.sp) },
                            leadingIcon = { if (showGridLines) Icon(Icons.Default.GridOn, "grid", modifier = Modifier.size(10.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberSecondary.copy(alpha = 0.2f), selectedLabelColor = CyberSecondary, containerColor = CyberSurface, labelColor = Color.White)
                        )
                        FilterChip(
                            selected = showGuides,
                            onClick = { showGuides = !showGuides },
                            label = { Text("المحاور والـ Center", fontSize = 10.sp) },
                            leadingIcon = { if (showGuides) Icon(Icons.Default.SquareFoot, "guides", modifier = Modifier.size(10.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberSecondary.copy(alpha = 0.2f), selectedLabelColor = CyberSecondary, containerColor = CyberSurface, labelColor = Color.White)
                        )
                        FilterChip(
                            selected = showSafeArea,
                            onClick = { showSafeArea = !showSafeArea },
                            label = { Text("حدود الأمان", fontSize = 10.sp) },
                            leadingIcon = { if (showSafeArea) Icon(Icons.Default.Fullscreen, "safe", modifier = Modifier.size(10.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberSecondary.copy(alpha = 0.2f), selectedLabelColor = CyberSecondary, containerColor = CyberSurface, labelColor = Color.White)
                        )
                    }

                    // Render Vector Canvas
                    StudioCanvas(
                        signageText = signageText,
                        selectedWidth = selectedWidth,
                        selectedHeight = selectedHeight,
                        currentStyle = currentStyle,
                        currentThemeColor = currentThemeColor,
                        selectedColorIndex = selectedColorIndex,
                        colorNames = colorNames,
                        isCMYKProfile = isCMYKProfile,
                        printDpiResolution = printDpiResolution,
                        bleedMarginCm = bleedMarginCm,
                        showGridLines = showGridLines,
                        showGuides = showGuides,
                        showSafeArea = showSafeArea,
                        renderModeIndex = renderModeIndex,
                        activeLayerIronFrame = activeLayerIronFrame,
                        activeLayerAcrylicBack = activeLayerAcrylicBack,
                        activeLayerNeonLeds = activeLayerNeonLeds,
                        activeLayerTextCore = activeLayerTextCore,
                        selectedLogoQuadrantIndex = selectedLogoQuadrantIndex,
                        logoPositions = logoPositions
                    )

                    // Properties Configuration
                    StudioProperties(
                        signageText = signageText,
                        onSignageTextChange = { signageText = it },
                        selectedWidth = selectedWidth,
                        onSelectedWidthChange = { selectedWidth = it },
                        selectedHeight = selectedHeight,
                        onSelectedHeightChange = { selectedHeight = it },
                        signageStyles = signageStyles,
                        selectedStyleIndex = selectedStyleIndex,
                        onSelectedStyleIndexChange = { selectedStyleIndex = it },
                        colorOptions = colorOptions,
                        colorNames = colorNames,
                        selectedColorIndex = selectedColorIndex,
                        onSelectedColorIndexChange = { selectedColorIndex = it }
                    )

                    // Assets Backdrop modes and logos config
                    StudioAssets(
                        renderModeIndex = renderModeIndex,
                        onRenderModeIndexChange = { renderModeIndex = it },
                        logoPositions = logoPositions,
                        selectedLogoQuadrantIndex = selectedLogoQuadrantIndex,
                        onSelectedLogoQuadrantIndexChange = { selectedLogoQuadrantIndex = it }
                    )

                    // Layer settings
                    StudioLayers(
                        activeLayerIronFrame = activeLayerIronFrame,
                        onActiveLayerIronFrameChange = { activeLayerIronFrame = it },
                        activeLayerAcrylicBack = activeLayerAcrylicBack,
                        onActiveLayerAcrylicBackChange = { activeLayerAcrylicBack = it },
                        activeLayerNeonLeds = activeLayerNeonLeds,
                        onActiveLayerNeonLedsChange = { activeLayerNeonLeds = it },
                        activeLayerTextCore = activeLayerTextCore,
                        onActiveLayerTextCoreChange = { activeLayerTextCore = it }
                    )

                    // Printer & Calibration output
                    StudioExportTools(
                        context = context,
                        signageText = signageText,
                        selectedWidth = selectedWidth,
                        selectedHeight = selectedHeight,
                        currentStyle = currentStyle,
                        currentThemeColor = currentThemeColor,
                        selectedColorIndex = selectedColorIndex,
                        colorNames = colorNames,
                        isCMYKProfile = isCMYKProfile,
                        onCMYKProfileChange = { isCMYKProfile = it },
                        printDpiResolution = printDpiResolution,
                        onPrintDpiResolutionChange = { printDpiResolution = it },
                        bleedMarginCm = bleedMarginCm,
                        onBleedMarginCmChange = { bleedMarginCm = it },
                        requiredLEDPcs = requiredLEDPcs,
                        selectedLogoQuadrantIndex = selectedLogoQuadrantIndex,
                        logoPositions = logoPositions,
                        activeLayerIronFrame = activeLayerIronFrame,
                        activeLayerAcrylicBack = activeLayerAcrylicBack,
                        activeLayerNeonLeds = activeLayerNeonLeds,
                        activeLayerTextCore = activeLayerTextCore,
                        itemAreaSqM = itemAreaSqM
                    )

                    // Dynamic BOM cost estimates
                    CostSummaryCard(
                        itemAreaSqM = itemAreaSqM,
                        materialsCost = materialsCost,
                        ledCost = ledCost,
                        requiredLEDPcs = requiredLEDPcs,
                        structureCost = structureCost,
                        estimatedSellingPrice = estimatedSellingPrice
                    )

                    // Sales Actions
                    SalesActionCard(
                        clientName = clientName,
                        onClientNameChange = { clientName = it },
                        clientPhone = clientPhone,
                        onClientPhoneChange = { clientPhone = it },
                        currentStyle = currentStyle,
                        signageText = signageText,
                        selectedWidth = selectedWidth,
                        selectedHeight = selectedHeight,
                        selectedColorIndex = selectedColorIndex,
                        colorNames = colorNames,
                        estimatedSellingPrice = estimatedSellingPrice,
                        viewModel = viewModel
                    )
                } else {
                    // Mobile View: Adaptive AI Sidebar
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "مستشار الهوية بالذكاء الاصطناعي • AI Planner",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "اكتب تفاصيل نشاط متجرك أو شركتك ليقوم الذكاء الاصطناعي بنمذجة مقاسات لوحات ممتازة لإعلاناتك.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            OutlinedTextField(
                                value = businessType,
                                onValueChange = { businessType = it },
                                label = { Text("نوع وتصنيف النشاط") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = CyberPrimary,
                                    unfocusedLabelColor = TextSecondary
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = businessDesc,
                                onValueChange = { businessDesc = it },
                                label = { Text("أهداف وتفاصيل مجالك الإبداعي") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = CyberPrimary,
                                    unfocusedLabelColor = TextSecondary
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = aiExtraInstructions,
                                onValueChange = { aiExtraInstructions = it },
                                label = { Text("أي متطلبات وتوجيهات خاصة") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = CyberPrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLabelColor = CyberPrimary,
                                    unfocusedLabelColor = TextSecondary
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )

                            Button(
                                onClick = {
                                    viewModel.generateAiSuggestions(businessType, businessDesc, aiExtraInstructions)
                                    keyboardController?.hide()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_ai_button"),
                                enabled = !aiLoading
                            ) {
                                if (aiLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.AutoAwesome, "AI Spark", tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("طلب الاستشارة التلقائية (AI)", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (aiResult.isNotBlank() || aiLoading) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface),
                            border = BorderStroke(1.dp, if (aiLoading) BorderColor else CyberSecondary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyberSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "الاستجابة والتقرير الرقمي للذكاء الاصطناعي",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                
                                Divider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))

                                if (aiLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "جاري تجميع أفضل أحجام اللوحات وصياغة هوية ممتازة...",
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Text(
                                        text = aiResult,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Separate Cost Estimator report
@Composable
fun CostSummaryCard(
    itemAreaSqM: Float,
    materialsCost: Double,
    ledCost: Double,
    requiredLEDPcs: Int,
    structureCost: Double,
    estimatedSellingPrice: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "التكلفة التقديرية للمواد والمصنعية (BOM)",
                color = CyberPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Divider(color = BorderColor)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("المساحة الإجمالية المخططة", color = TextSecondary, fontSize = 13.sp)
                Text("${String.format("%.2f", itemAreaSqM)} متر مربع", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("تكلفة الأكريليك / البنر التقديرية", color = TextSecondary, fontSize = 13.sp)
                Text("$${String.format("%.1f", materialsCost)}", color = Color.White, fontSize = 13.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("تكلفة وحدات الـ LEDs ومزود الطاقة", color = TextSecondary, fontSize = 13.sp)
                Text("$${String.format("%.1f", ledCost)} (عدد $requiredLEDPcs موديول)", color = Color.White, fontSize = 13.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("شاسيه الحديد وأعمال اللحام التأسيسية", color = TextSecondary, fontSize = 13.sp)
                Text("$${String.format("%.1f", structureCost)}", color = Color.White, fontSize = 13.sp)
            }

            Divider(color = BorderColor)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سعر البيع المقترح للعميل", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "$${estimatedSellingPrice.toInt()}",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

// Pipeline action Dispatching
@Composable
fun SalesActionCard(
    clientName: String,
    onClientNameChange: (String) -> Unit,
    clientPhone: String,
    onClientPhoneChange: (String) -> Unit,
    currentStyle: String,
    signageText: String,
    selectedWidth: Float,
    selectedHeight: Float,
    selectedColorIndex: Int,
    colorNames: List<String>,
    estimatedSellingPrice: Double,
    viewModel: YaftaViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "إجراءات العميل وعقود الإنشاء (Leads & Orders)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            OutlinedTextField(
                value = clientName,
                onValueChange = onClientNameChange,
                label = { Text("اسم العميل / اسم الشركة (Client Name)") },
                leadingIcon = { Icon(Icons.Default.Person, "Client Icon", tint = CyberPrimary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = CyberPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = clientPhone,
                onValueChange = onClientPhoneChange,
                label = { Text("رقم موبايل العميل (Mobile Number)") },
                leadingIcon = { Icon(Icons.Default.Phone, "Phone Icon", tint = CyberPrimary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedLabelColor = CyberPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Save lead
                OutlinedButton(
                    onClick = {
                        if (clientName.isNotBlank()) {
                            val logDetails = "$currentStyle visual signage of $signageText, Size: ${selectedWidth}x${selectedHeight}m, Theme: ${colorNames[selectedColorIndex]}"
                            viewModel.addEstimate(clientName, clientPhone, logDetails, estimatedSellingPrice)
                            viewModel.addCrmContact(
                                name = clientName,
                                phone = clientPhone,
                                email = "",
                                company = "",
                                status = "Quoted",
                                notes = "تم إنشاء عرض سعر من استوديو التصميم: $logDetails بسعر تقديري $$estimatedSellingPrice"
                            )
                            onClientNameChange("")
                            onClientPhoneChange("")
                            keyboardController?.hide()
                            Toast.makeText(context, "تم تسجيل عرض السعر والعميل في الـ CRM بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPrimary),
                    border = BorderStroke(1.dp, CyberPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("تسجيل كعميل محتمل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Push to production queue
                Button(
                    onClick = {
                        if (clientName.isNotBlank()) {
                            viewModel.addProject(
                                clientName = clientName,
                                phone = clientPhone,
                                signageText = signageText,
                                width = selectedWidth.toDouble(),
                                height = selectedHeight.toDouble(),
                                type = currentStyle,
                                totalCost = estimatedSellingPrice
                            )
                            // Add downpayment log of 50%
                            viewModel.addFinanceEntry(
                                type = "INCOME",
                                amount = estimatedSellingPrice * 0.5,
                                description = "50% مقدم تعاقد تصنيع يافطة $clientName لوحة \"$signageText\"",
                                category = "Signage Orders"
                            )
                            viewModel.addCrmContact(
                                name = clientName,
                                phone = clientPhone,
                                email = "",
                                company = "",
                                status = "Converted",
                                notes = "تم تحويل التصميم إلى الورشة لبدء تصنيع اللوحة الخاصة بنص \"$signageText\" بمساحة $selectedWidth x $selectedHeight"
                            )
                            onClientNameChange("")
                            onClientPhoneChange("")
                            keyboardController?.hide()
                            Toast.makeText(context, "تم تحويل التصميم إلى خط تصنيع الورشة وحفظ المعاملة المالية وتحديث الـ CRM!", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("بدء تصنيع اليافطة", color = CyberBg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

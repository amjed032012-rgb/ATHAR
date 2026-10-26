package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Mosque
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun ArchitecturalBlueprintCard(
    mosque: Mosque,
    modifier: Modifier = Modifier
) {
    var is3DMode by remember { mutableStateOf(true) }
    var showDimensionsRuler by remember { mutableStateOf(true) }
    var showRoofLayer by remember { mutableStateOf(false) } // Default false so interior is visible
    var showPillarsLayer by remember { mutableStateOf(true) }
    var isAutoSpinning by remember { mutableStateOf(false) }
    var rotationYaw by remember { mutableStateOf(45f) }
    var rotationPitch by remember { mutableStateOf(28f) }
    var zoomScale by remember { mutableStateOf(1.0f) }

    // Auto-spin animation when enabled
    LaunchedEffect(isAutoSpinning) {
        if (isAutoSpinning) {
            while (true) {
                rotationYaw = (rotationYaw + 0.6f) % 360f
                kotlinx.coroutines.delay(16)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("architectural_blueprint_card_${mosque.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceVariant),
        border = BorderStroke(1.dp, SandOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header & View Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (is3DMode) "المجسم المعماري التفاعلي 3D" else "المسقط الهندسي الأفقي 2D",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                    Text(
                        text = "محاكاة واقعية موثقة استناداً إلى «الموسوعة العُمانية»",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                    )
                }

                // Switcher Button
                FilledTonalButton(
                    onClick = { is3DMode = !is3DMode },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (is3DMode) MihrabGold else SaroojContainer,
                        contentColor = if (is3DMode) Color.Black else OnSaroojContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (is3DMode) Icons.Default.ViewInAr else Icons.Default.Layers,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (is3DMode) "مجسم 3D" else "مسقط 2D",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Controls Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = showDimensionsRuler,
                    onClick = { showDimensionsRuler = !showDimensionsRuler },
                    label = { Text("القياسات") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OasisGreenContainer)
                )

                if (is3DMode) {
                    FilterChip(
                        selected = showRoofLayer,
                        onClick = { showRoofLayer = !showRoofLayer },
                        label = { Text(if (showRoofLayer) "إخفاء السقف" else "إظهار السقف") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Roofing,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                    FilterChip(
                        selected = isAutoSpinning,
                        onClick = { isAutoSpinning = !isAutoSpinning },
                        label = { Text(if (isAutoSpinning) "إيقاف الدوران" else "دوران تلقائي") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }

            // Canvas Box with Touch Orbit Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color(0xFF221A15), RoundedCornerShape(16.dp))
                    .border(1.dp, SaroojBrownLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .pointerInput(is3DMode) {
                        if (is3DMode) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                rotationYaw = (rotationYaw + pan.x * 0.4f) % 360f
                                rotationPitch = (rotationPitch - pan.y * 0.3f).coerceIn(10f, 75f)
                                zoomScale = (zoomScale * zoom).coerceIn(0.7f, 2.2f)
                            }
                        }
                    }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("blueprint_interactive_canvas")
                ) {
                    val w = size.width
                    val h = size.height

                    if (is3DMode) {
                        draw3DArchitecturalModel(
                            w = w,
                            h = h,
                            yaw = rotationYaw,
                            pitch = rotationPitch,
                            zoom = zoomScale,
                            mosqueId = mosque.id,
                            showRoof = showRoofLayer,
                            showPillars = showPillarsLayer,
                            showDimensions = showDimensionsRuler
                        )
                    } else {
                        when (mosque.id) {
                            "nizwa_imams" -> drawNizwaBlueprint(w, h, showDimensionsRuler)
                            "saal_site1" -> drawSaalSite1Blueprint(w, h, showDimensionsRuler)
                            "saal_site2" -> drawSaalSite2Blueprint(w, h, showDimensionsRuler)
                            else -> drawNizwaBlueprint(w, h, showDimensionsRuler)
                        }
                    }
                }

                // Touch hint watermark overlay
                if (is3DMode) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = MihrabGoldLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "المس واسحب للتدوير في جميع الاتجاهات",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Dimension Summary Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SandSurfaceLight,
                border = BorderStroke(1.dp, SandOutlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MihrabGoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Architecture,
                            contentDescription = null,
                            tint = OnMihrabGoldContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المعايير الإنشائية الموثقة بالموسوعة العُمانية",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                        Text(
                            text = when (mosque.id) {
                                "nizwa_imams" -> "المحراب: عمق 1.4م وارتفاع 3.5م • 3 أعمدة مستطيلة تفصل رواقين • 7 مداخل خارجية"
                                "saal_site1" -> "مبني مباشرة فوق النتوء الصخري • صرح صخري مدرج بارتفاع 3 أمتار • 7 مداخل • مساحة 26 م²"
                                "saal_site2" -> "عمودان أسطوانيان • عقد نصف دائري وعقد شبه مخروطي • قبو وضوء شبه مخروطي مع بئر ملاصق"
                                else -> "هيكل صاروجي متين يعكس بساطة وزهد مساجد العباد"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimaryDark,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// 3D Perspective Projection Engine for Jetpack Compose Canvas
private fun DrawScope.draw3DArchitecturalModel(
    w: Float,
    h: Float,
    yaw: Float,
    pitch: Float,
    zoom: Float,
    mosqueId: String,
    showRoof: Boolean,
    showPillars: Boolean,
    showDimensions: Boolean
) {
    val cx = w / 2f
    val cy = h / 2f + 20f

    // Radians
    val yawRad = Math.toRadians(yaw.toDouble())
    val pitchRad = Math.toRadians(pitch.toDouble())

    // 3D Point Projection Helper
    fun project(x: Float, y: Float, z: Float): Offset {
        // Rotate around Y axis (yaw)
        val x1 = (x * cos(yawRad) - z * sin(yawRad)).toFloat()
        val z1 = (x * sin(yawRad) + z * cos(yawRad)).toFloat()

        // Rotate around X axis (pitch)
        val y2 = (y * cos(pitchRad) - z1 * sin(pitchRad)).toFloat()
        val z2 = (y * sin(pitchRad) + z1 * cos(pitchRad)).toFloat()

        // Perspective scale factor
        val distance = 700f
        val factor = (distance / (distance + z2)) * zoom

        return Offset(cx + x1 * factor, cy + y2 * factor)
    }

    val stoneWallColor = Color(0xFFC4B19A)
    val stoneWallDark = Color(0xFFA69076)
    val saroojPillarColor = Color(0xFFD6A77A)
    val mihrabColor = Color(0xFFD6AB4F)
    val bedrockColor = Color(0xFF5A4437)
    val woodColor = Color(0xFF8B5A2B)
    val waterColor = Color(0xFF4A90E2)

    // Draw Grid / Ground Shadow
    val groundPoints = listOf(
        project(-180f, 80f, -140f),
        project(180f, 80f, -140f),
        project(180f, 80f, 140f),
        project(-180f, 80f, 140f)
    )
    val groundPath = Path().apply {
        moveTo(groundPoints[0].x, groundPoints[0].y)
        groundPoints.forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(groundPath, Color(0xFF191310).copy(alpha = 0.8f))
    drawPath(groundPath, Color(0xFF382C24), style = Stroke(width = 2f))

    if (mosqueId == "saal_site1") {
        // Draw Rocky Outcrop Base (High Natural Plateau)
        val rockBase = listOf(
            project(-150f, 75f, -110f),
            project(150f, 75f, -110f),
            project(170f, 40f, 120f),
            project(-170f, 40f, 120f)
        )
        val rockPath = Path().apply {
            moveTo(rockBase[0].x, rockBase[0].y)
            rockBase.forEach { lineTo(it.x, it.y) }
            close()
        }
        drawPath(rockPath, bedrockColor)
        drawPath(rockPath, Color(0xFF7A5E4D), style = Stroke(width = 3f))

        // Natural stepped terrace
        val terraceP1 = project(-160f, 50f, 110f)
        val terraceP2 = project(160f, 50f, 110f)
        drawLine(Color(0xFF8D6E5C), terraceP1, terraceP2, strokeWidth = 4f)
    }

    // Outer Walls (Box)
    val wallWidth = if (mosqueId == "saal_site1") 110f else 140f
    val wallDepth = if (mosqueId == "saal_site1") 90f else 110f
    val wallHeight = 85f // ~3.5 meters scaled

    val bFL = project(-wallWidth, 35f, wallDepth)
    val bFR = project(wallWidth, 35f, wallDepth)
    val bBL = project(-wallWidth, 35f, -wallDepth)
    val bBR = project(wallWidth, 35f, -wallDepth)

    val tFL = project(-wallWidth, 35f - wallHeight, wallDepth)
    val tFR = project(wallWidth, 35f - wallHeight, wallDepth)
    val tBL = project(-wallWidth, 35f - wallHeight, -wallDepth)
    val tBR = project(wallWidth, 35f - wallHeight, -wallDepth)

    // Floor slab
    val floorPath = Path().apply {
        moveTo(bBL.x, bBL.y)
        lineTo(bBR.x, bBR.y)
        lineTo(bFR.x, bFR.y)
        lineTo(bFL.x, bFL.y)
        close()
    }
    drawPath(floorPath, Color(0xFF4A3E36))
    drawPath(floorPath, stoneWallColor, style = Stroke(width = 3f))

    // Back Walls
    val backWallPath = Path().apply {
        moveTo(bBL.x, bBL.y)
        lineTo(bBR.x, bBR.y)
        lineTo(tBR.x, tBR.y)
        lineTo(tBL.x, tBL.y)
        close()
    }
    drawPath(backWallPath, stoneWallDark.copy(alpha = 0.85f))
    drawPath(backWallPath, stoneWallDark, style = Stroke(width = 2f))

    // Mihrab Cavity on North/Back Wall
    val mihrabCenterBottom = project(0f, 35f, -wallDepth)
    val mihrabCenterTop = project(0f, 35f - wallHeight * 0.75f, -wallDepth)
    val mihrabDeepBottom = project(0f, 35f, -wallDepth - 35f) // 1.4m depth projection
    val mihrabDeepTop = project(0f, 35f - wallHeight * 0.75f, -wallDepth - 35f)

    val mihrabNiche = Path().apply {
        moveTo(mihrabCenterBottom.x - 18f, mihrabCenterBottom.y)
        lineTo(mihrabDeepBottom.x - 15f, mihrabDeepBottom.y)
        lineTo(mihrabDeepTop.x - 15f, mihrabDeepTop.y)
        lineTo(mihrabDeepTop.x + 15f, mihrabDeepTop.y)
        lineTo(mihrabDeepBottom.x + 15f, mihrabDeepBottom.y)
        lineTo(mihrabCenterBottom.x + 18f, mihrabCenterBottom.y)
        close()
    }
    drawPath(mihrabNiche, Color(0xFF1B140E))
    drawPath(mihrabNiche, mihrabColor, style = Stroke(width = 3f))
    drawCircle(mihrabColor, radius = 6f, center = mihrabDeepTop)

    // Pillars inside the sanctuary
    if (showPillars) {
        if (mosqueId == "saal_site2") {
            // 2 Cylindrical Pillars
            val pPositions = listOf(-45f, 45f)
            pPositions.forEach { px ->
                val pBot = project(px, 35f, 0f)
                val pTop = project(px, 35f - wallHeight * 0.85f, 0f)
                drawLine(saroojPillarColor, pBot, pTop, strokeWidth = 14f)
                drawCircle(Color(0xFFE8C29D), radius = 7f, center = pTop)

                // Sub-conical and round arch connecting
                val archTop = project(px, 35f - wallHeight * 0.95f, 0f)
                drawLine(mihrabColor, pTop, archTop, strokeWidth = 3f)
            }

            // Attached Wudu conical vault on east side
            val wuduCenter = project(wallWidth + 50f, 35f, 0f)
            val wuduPeak = project(wallWidth + 50f, 35f - wallHeight * 0.65f, 0f)
            val wuduCone = Path().apply {
                moveTo(wuduCenter.x - 28f, wuduCenter.y)
                lineTo(wuduPeak.x, wuduPeak.y)
                lineTo(wuduCenter.x + 28f, wuduCenter.y)
                close()
            }
            drawPath(wuduCone, Color(0xFF5D7B69).copy(alpha = 0.75f))
            drawPath(wuduCone, Color(0xFF81C784), style = Stroke(width = 2f))

            // Well next to wudu
            val wellPos = project(wallWidth + 90f, 35f, 0f)
            drawCircle(waterColor, radius = 10f, center = wellPos)
            drawCircle(Color.White, radius = 10f, center = wellPos, style = Stroke(width = 2f))
        } else {
            // 3 Rectangular Pillars dividing into two aisles (Nizwa style)
            val pPositions = listOf(-65f, 0f, 65f)
            pPositions.forEach { px ->
                val pBot = project(px, 35f, 0f)
                val pTop = project(px, 35f - wallHeight * 0.85f, 0f)
                // Draw rectangular column face
                val colPath = Path().apply {
                    moveTo(pBot.x - 8f, pBot.y)
                    lineTo(pTop.x - 8f, pTop.y)
                    lineTo(pTop.x + 8f, pTop.y)
                    lineTo(pBot.x + 8f, pBot.y)
                    close()
                }
                drawPath(colPath, saroojPillarColor)
                drawPath(colPath, Color(0xFF8B5A2B), style = Stroke(width = 2f))

                // Arch overhead connecting to adjacent
                if (px < 65f) {
                    val nextP = project(px + 65f, 35f - wallHeight * 0.85f, 0f)
                    val archMid = project(px + 32.5f, 35f - wallHeight, 0f)
                    val archPath = Path().apply {
                        moveTo(pTop.x, pTop.y)
                        quadraticTo(archMid.x, archMid.y, nextP.x, nextP.y)
                    }
                    drawPath(archPath, mihrabColor, style = Stroke(width = 3f))
                }
            }
        }
    }

    // Front cutaway wall with entrances
    val leftWallPath = Path().apply {
        moveTo(bBL.x, bBL.y)
        lineTo(bFL.x, bFL.y)
        lineTo(tFL.x, tFL.y)
        lineTo(tBL.x, tBL.y)
        close()
    }
    drawPath(leftWallPath, stoneWallDark.copy(alpha = 0.5f))
    drawPath(leftWallPath, stoneWallColor, style = Stroke(width = 2f))

    val rightWallPath = Path().apply {
        moveTo(bBR.x, bBR.y)
        lineTo(bFR.x, bFR.y)
        lineTo(tFR.x, tFR.y)
        lineTo(tBR.x, tBR.y)
        close()
    }
    drawPath(rightWallPath, stoneWallDark.copy(alpha = 0.5f))
    drawPath(rightWallPath, stoneWallColor, style = Stroke(width = 2f))

    // Front Wall (Semi-transparent cutaway to reveal interior)
    val frontWallPath = Path().apply {
        moveTo(bFL.x, bFL.y)
        lineTo(bFR.x, bFR.y)
        lineTo(tFR.x, tFR.y)
        lineTo(tFL.x, tFL.y)
        close()
    }
    drawPath(frontWallPath, stoneWallColor.copy(alpha = 0.22f))
    drawPath(frontWallPath, stoneWallColor.copy(alpha = 0.7f), style = Stroke(width = 2f))

    // 7 Doorway openings indicators
    val doorPositions = listOf(-100f, -60f, -20f, 20f, 60f, 100f)
    doorPositions.forEach { dx ->
        val dBot = project(dx, 35f, wallDepth)
        val dTop = project(dx, 35f - wallHeight * 0.6f, wallDepth)
        drawLine(Color(0xFF8FD8A0), dBot, dTop, strokeWidth = 3f)
    }

    // Roof Beams (if visible)
    if (showRoof) {
        val roofElevation = 35f - wallHeight - 10f
        val rFL = project(-wallWidth, roofElevation, wallDepth)
        val rFR = project(wallWidth, roofElevation, wallDepth)
        val rBL = project(-wallWidth, roofElevation, -wallDepth)
        val rBR = project(wallWidth, roofElevation, -wallDepth)

        val roofSlab = Path().apply {
            moveTo(rBL.x, rBL.y)
            lineTo(rBR.x, rBR.y)
            lineTo(rFR.x, rFR.y)
            lineTo(rFL.x, rFL.y)
            close()
        }
        drawPath(roofSlab, woodColor.copy(alpha = 0.65f))
        drawPath(roofSlab, woodColor, style = Stroke(width = 3f))

        // Kandal timber beams
        for (step in -5..5) {
            val zOff = step * (wallDepth / 6f)
            val beamL = project(-wallWidth, roofElevation, zOff)
            val beamR = project(wallWidth, roofElevation, zOff)
            drawLine(Color(0xFF5D3A1A), beamL, beamR, strokeWidth = 2.5f)
        }
    }

    // 3D Architectural Dimensions Markers
    if (showDimensions) {
        val rulerColor = Color(0xFF64B5F6)

        // Mihrab depth marker (1.4m)
        val mStart = project(0f, 35f - wallHeight * 0.3f, -wallDepth)
        val mEnd = project(0f, 35f - wallHeight * 0.3f, -wallDepth - 35f)
        drawLine(rulerColor, mStart, mEnd, strokeWidth = 2f)
        drawCircle(rulerColor, radius = 4f, center = mEnd)

        // Mosque height marker (3.5m)
        val hStart = project(-wallWidth - 25f, 35f, wallDepth)
        val hEnd = project(-wallWidth - 25f, 35f - wallHeight, wallDepth)
        drawLine(rulerColor, hStart, hEnd, strokeWidth = 2.5f)
        drawLine(rulerColor, Offset(hStart.x - 8f, hStart.y), Offset(hStart.x + 8f, hStart.y), strokeWidth = 2f)
        drawLine(rulerColor, Offset(hEnd.x - 8f, hEnd.y), Offset(hEnd.x + 8f, hEnd.y), strokeWidth = 2f)
    }
}

// 2D Floor Plan Blueprints
private fun DrawScope.drawNizwaBlueprint(w: Float, h: Float, showDimensions: Boolean) {
    val wallColor = Color(0xFFC7B299)
    val pillarColor = Color(0xFFE2B797)
    val mihrabColor = Color(0xFFD6AB4F)
    val gridColor = Color(0xFF3D322B)
    val rulerColor = Color(0xFF64B5F6)

    // Grid lines
    for (i in 0..10) {
        val y = h * (i / 10f)
        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
        val x = w * (i / 10f)
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
    }

    val left = w * 0.12f
    val right = w * 0.88f
    val top = h * 0.22f
    val bottom = h * 0.82f
    val roomWidth = right - left
    val roomHeight = bottom - top

    // Outer Walls
    drawRect(
        color = wallColor,
        topLeft = Offset(left, top),
        size = Size(roomWidth, roomHeight),
        style = Stroke(width = 8f)
    )

    // Deep Mihrab (1.4m depth)
    val mihrabWidth = roomWidth * 0.22f
    val mihrabDepth = roomHeight * 0.25f
    val mihrabX = left + (roomWidth - mihrabWidth) / 2f

    val mihrabPath = Path().apply {
        moveTo(mihrabX, top)
        cubicTo(mihrabX, top - mihrabDepth, mihrabX + mihrabWidth, top - mihrabDepth, mihrabX + mihrabWidth, top)
    }
    drawPath(mihrabPath, mihrabColor, style = Stroke(width = 6f))

    // Rectangular Pillars in a Central Row
    val pillarW = roomWidth * 0.18f
    val pillarH = roomHeight * 0.10f
    val centerY = top + roomHeight * 0.50f
    val pillarSpacing = (roomWidth - (3 * pillarW)) / 4f

    for (i in 0 until 3) {
        val px = left + pillarSpacing + i * (pillarW + pillarSpacing)
        val py = centerY - pillarH / 2f
        drawRoundRect(
            color = pillarColor,
            topLeft = Offset(px, py),
            size = Size(pillarW, pillarH),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // 7 Entrances
    val doorColor = Color(0xFF8FD8A0)
    for (i in 1..3) {
        drawCircle(doorColor, radius = 6f, center = Offset(left + roomWidth * (i / 4f), bottom))
    }
    for (i in 1..2) {
        drawCircle(doorColor, radius = 6f, center = Offset(right, top + roomHeight * (i / 3f)))
    }
    for (i in 1..2) {
        drawCircle(doorColor, radius = 6f, center = Offset(left, top + roomHeight * (i / 3f)))
    }

    // Architectural Dimension Callout Rulers
    if (showDimensions) {
        val rx = mihrabX + mihrabWidth + 12f
        drawLine(rulerColor, Offset(rx, top), Offset(rx, top - mihrabDepth), strokeWidth = 2f)
        drawLine(rulerColor, Offset(rx - 6f, top), Offset(rx + 6f, top), strokeWidth = 2f)
        drawLine(rulerColor, Offset(rx - 6f, top - mihrabDepth), Offset(rx + 6f, top - mihrabDepth), strokeWidth = 2f)

        val rx2 = left - 18f
        drawLine(rulerColor, Offset(rx2, top), Offset(rx2, bottom), strokeWidth = 2f)
        drawLine(rulerColor, Offset(rx2 - 6f, top), Offset(rx2 + 6f, top), strokeWidth = 2f)
        drawLine(rulerColor, Offset(rx2 - 6f, bottom), Offset(rx2 + 6f, bottom), strokeWidth = 2f)
    }
}

private fun DrawScope.drawSaalSite1Blueprint(w: Float, h: Float, showDimensions: Boolean) {
    val wallColor = Color(0xFFC7B299)
    val rockColor = Color(0xFF6B584D)
    val rulerColor = Color(0xFF64B5F6)

    val rockPath = Path().apply {
        moveTo(w * 0.05f, h * 0.5f)
        lineTo(w * 0.25f, h * 0.12f)
        lineTo(w * 0.75f, h * 0.10f)
        lineTo(w * 0.95f, h * 0.45f)
        lineTo(w * 0.85f, h * 0.90f)
        lineTo(w * 0.15f, h * 0.88f)
        close()
    }
    drawPath(rockPath, rockColor.copy(alpha = 0.4f))
    drawPath(rockPath, rockColor, style = Stroke(width = 3f))

    val mLeft = w * 0.25f
    val mTop = h * 0.25f
    val mW = w * 0.50f
    val mH = h * 0.50f

    drawRect(color = wallColor, topLeft = Offset(mLeft, mTop), size = Size(mW, mH), style = Stroke(width = 8f))

    drawArc(
        color = Color(0xFFD6AB4F),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(mLeft + mW * 0.35f, mTop - mH * 0.15f),
        size = Size(mW * 0.30f, mH * 0.30f),
        style = Stroke(width = 5f)
    )

    val doorColor = Color(0xFF8FD8A0)
    for (i in 1..3) drawCircle(doorColor, radius = 5f, center = Offset(mLeft + mW * (i / 4f), mTop + mH))
    for (i in 1..2) drawCircle(doorColor, radius = 5f, center = Offset(mLeft + mW, mTop + mH * (i / 3f)))
    for (i in 1..2) drawCircle(doorColor, radius = 5f, center = Offset(mLeft, mTop + mH * (i / 3f)))

    if (showDimensions) {
        val rx = mLeft - 18f
        drawLine(rulerColor, Offset(rx, mTop), Offset(rx, mTop + mH), strokeWidth = 2f)
        drawLine(rulerColor, Offset(rx - 5f, mTop), Offset(rx + 5f, mTop), strokeWidth = 2f)
        drawLine(rulerColor, Offset(rx - 5f, mTop + mH), Offset(rx + 5f, mTop + mH), strokeWidth = 2f)
    }
}

private fun DrawScope.drawSaalSite2Blueprint(w: Float, h: Float, showDimensions: Boolean) {
    val wallColor = Color(0xFFC7B299)
    val pillarColor = Color(0xFFE2B797)
    val saroojTubColor = Color(0xFF45795D)
    val wellColor = Color(0xFF5A8DBA)
    val rulerColor = Color(0xFF64B5F6)

    val mLeft = w * 0.15f
    val mTop = h * 0.20f
    val mW = w * 0.48f
    val mH = h * 0.45f

    drawRect(color = wallColor, topLeft = Offset(mLeft, mTop), size = Size(mW, mH), style = Stroke(width = 7f))

    val cornerSize = 10f
    drawRect(pillarColor, Offset(mLeft - cornerSize / 2, mTop - cornerSize / 2), Size(cornerSize, cornerSize))
    drawRect(pillarColor, Offset(mLeft + mW - cornerSize / 2, mTop - cornerSize / 2), Size(cornerSize, cornerSize))
    drawRect(pillarColor, Offset(mLeft - cornerSize / 2, mTop + mH - cornerSize / 2), Size(cornerSize, cornerSize))
    drawRect(pillarColor, Offset(mLeft + mW - cornerSize / 2, mTop + mH - cornerSize / 2), Size(cornerSize, cornerSize))

    val p1 = Offset(mLeft + mW * 0.35f, mTop + mH * 0.5f)
    val p2 = Offset(mLeft + mW * 0.65f, mTop + mH * 0.5f)
    drawCircle(color = pillarColor, radius = 14f, center = p1)
    drawCircle(color = pillarColor, radius = 14f, center = p2)

    val wuduLeft = mLeft + mW + w * 0.08f
    val wuduTop = mTop + h * 0.05f
    val wuduSize = w * 0.22f

    drawCircle(color = saroojTubColor.copy(alpha = 0.5f), radius = wuduSize / 2, center = Offset(wuduLeft + wuduSize / 2, wuduTop + wuduSize / 2))
    drawCircle(color = saroojTubColor, radius = wuduSize / 2, center = Offset(wuduLeft + wuduSize / 2, wuduTop + wuduSize / 2), style = Stroke(width = 5f))

    val wellCenter = Offset(wuduLeft + wuduSize + 22f, wuduTop + wuduSize / 2)
    drawCircle(color = wellColor, radius = 16f, center = wellCenter)
    drawCircle(color = Color.White, radius = 16f, center = wellCenter, style = Stroke(width = 3f))

    if (showDimensions) {
        drawLine(rulerColor, Offset(mLeft - 20f, mTop), Offset(mLeft - 4f, mTop), strokeWidth = 2f)
    }
}

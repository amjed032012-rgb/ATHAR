package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AmbientLightingMode
import com.example.data.ArchitecturalHotspot
import com.example.data.PanoramicSceneType
import com.example.data.ViewpointStation
import com.example.ui.AtharScreen
import com.example.ui.AtharViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

@Composable
fun Panoramic360Viewer(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sceneType by viewModel.sceneType.collectAsState()
    val panOffset by viewModel.panOffset.collectAsState()
    val zoomScale by viewModel.zoomScale.collectAsState()
    val selectedHotspot by viewModel.selectedHotspot.collectAsState()
    val isGuideModeActive by viewModel.isGuideModeActive.collectAsState()
    val guideStepIndex by viewModel.guideStepIndex.collectAsState()
    val ambientLighting by viewModel.ambientLighting.collectAsState()
    val isMotionGyroActive by viewModel.isMotionGyroActive.collectAsState()
    val isAudioPlaying by viewModel.ambientAudioPlaying.collectAsState()
    val activeViewpoint by viewModel.activeViewpoint.collectAsState()
    val isAllLabelsVisible by viewModel.isAllLabelsVisible.collectAsState()
    val isAutoRotating by viewModel.isAutoRotating.collectAsState()
    val isDimensionRulerVisible by viewModel.isDimensionRulerVisible.collectAsState()
    val selectedAudioTrack by viewModel.selectedAudioTrack.collectAsState()
    val isLivePhotoHudOpen by viewModel.isLivePhotoHudOpen.collectAsState()

    val viewpoints = viewModel.viewpointStations
    val hotspots = viewModel.getActiveHotspots()

    var containerSize by remember { mutableStateOf(IntSize(1080, 1920)) }
    var selectedHudPhotoRes by remember { mutableStateOf<Int?>(null) }
    var isNarratorPlaying by remember { mutableStateOf(false) }

    // Auto-Orbit Camera Pan (Smooth 60fps cinematic 360 rotation)
    LaunchedEffect(isAutoRotating) {
        if (isAutoRotating) {
            while (isActive) {
                viewModel.updatePan(Offset(-1.2f, 0f))
                kotlinx.coroutines.delay(16)
            }
        }
    }

    // Compass Heading (0 to 360 degrees)
    val headingDegrees = remember(panOffset.x, containerSize.width, zoomScale) {
        val panoramaWidth = containerSize.width * 2.5f * zoomScale
        val fraction = ((-panOffset.x % panoramaWidth + panoramaWidth) % panoramaWidth) / panoramaWidth
        (fraction * 360f) % 360f
    }

    // Motion Sensor (Gyroscope / Accelerometer) integration
    DisposableEffect(isMotionGyroActive) {
        if (!isMotionGyroActive) return@DisposableEffect onDispose {}

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && isMotionGyroActive) {
                    val dx = -event.values[1] * 28f
                    val dy = event.values[0] * 22f
                    viewModel.updatePan(Offset(dx, dy))
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (gyroSensor != null) {
            sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Pulsating animation for Hotspots
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Wave animation for Soundscape
    val audioWaveHeight by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    viewModel.updatePan(pan)
                    if (zoom != 1.0f) {
                        viewModel.updateZoom(zoom)
                    }
                }
            }
            .testTag("panoramic_360_canvas")
    ) {
        // High-Resolution Texture selection per scene
        val drawableRes = when (sceneType) {
            PanoramicSceneType.INTERIOR_SANCTUARY -> R.drawable.panoramic_interior_athar_1792913518479
            PanoramicSceneType.VIEW_FROM_MIHRAB -> R.drawable.panoramic_from_mihrab_view_1792920467742
            PanoramicSceneType.EXTERIOR_SAHN -> R.drawable.panoramic_exterior_athar_1792913532059
            PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> R.drawable.mihrab_gypsum_seals_detail_1792920451688
        }

        val panoWidth = containerSize.width.toFloat() * 2.5f * zoomScale
        val panoHeight = containerSize.height.toFloat() * 1.35f * zoomScale

        // Double-tiled infinite horizontal wrap: continuous seamless 360 wrap
        val normalizedPanX = ((panOffset.x % panoWidth) + panoWidth) % panoWidth

        // Tile 1
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "جولة 360 التفاعلية",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (normalizedPanX - panoWidth).roundToInt(),
                        y = (panOffset.y).roundToInt()
                    )
                }
                .width(panoWidth.dp / 2.6f)
                .height(panoHeight.dp / 2.6f)
        )

        // Tile 2
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "جولة 360 التفاعلية",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = normalizedPanX.roundToInt(),
                        y = (panOffset.y).roundToInt()
                    )
                }
                .width(panoWidth.dp / 2.6f)
                .height(panoHeight.dp / 2.6f)
        )

        // Ambient Lighting Tint Overlay
        if (ambientLighting != AmbientLightingMode.DAYLIGHT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(ambientLighting.tintColorHex))
            )
        }

        // Vignette Shadow Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        radius = 1200f
                    )
                )
        )

        // Spatial Architectural Dimension Ruler Overlay (When enabled)
        if (isDimensionRulerVisible) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val stroke = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f)))

                when (sceneType) {
                    PanoramicSceneType.INTERIOR_SANCTUARY -> {
                        // Depth of Mihrab
                        drawLine(
                            color = MihrabGold,
                            start = Offset(cx - 140f, cy + 60f),
                            end = Offset(cx + 140f, cy + 60f),
                            strokeWidth = 3f
                        )
                        // Height of Arch
                        drawLine(
                            color = OasisGreen,
                            start = Offset(cx, cy - 200f),
                            end = Offset(cx, cy + 80f),
                            strokeWidth = 3f
                        )
                    }
                    PanoramicSceneType.VIEW_FROM_MIHRAB -> {
                        // Aisle span
                        drawLine(
                            color = MihrabGold,
                            start = Offset(cx - 260f, cy),
                            end = Offset(cx + 260f, cy),
                            strokeWidth = 3f
                        )
                    }
                    PanoramicSceneType.EXTERIOR_SAHN -> {
                        // Dome height
                        drawLine(
                            color = MihrabGold,
                            start = Offset(cx + 120f, cy - 140f),
                            end = Offset(cx + 120f, cy + 100f),
                            strokeWidth = 3f
                        )
                    }
                    PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> {
                        // Width of Mihrab 2.80m
                        drawLine(
                            color = MihrabGold,
                            start = Offset(cx - 220f, cy - 80f),
                            end = Offset(cx + 220f, cy - 80f),
                            strokeWidth = 3f
                        )
                    }
                }
            }

            // Dimension text badge floating
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = BorderStroke(1.5.dp, MihrabGold),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 80.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val dimensionTitle = when (sceneType) {
                        PanoramicSceneType.INTERIOR_SANCTUARY -> "عمق المحراب: 1.40 م • ارتفاع القوس: 3.50 م"
                        PanoramicSceneType.VIEW_FROM_MIHRAB -> "عرض بحر الرواقين: 4.20 م • 3 أعمدة مستطيلة"
                        PanoramicSceneType.EXTERIOR_SAHN -> "ارتفاع قبو الوضوء: 2.80 م • بئر الحجارة الملاصق"
                        PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> "عرض المحراب: 2.80 م • 30 ختماً جصياً • 3 صحون مينغ"
                    }
                    Text(
                        text = dimensionTitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MihrabGoldLight,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "المصدر: «الموسوعة العُمانية» (الصادرة عن وزارة التراث والثقافة)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }

        // Interactive Hotspot Pins & Spatial AR Billboard Labels
        hotspots.forEachIndexed { index, hotspot ->
            val hotspotWorldX = hotspot.xNormalized * panoWidth
            val hotspotWorldY = hotspot.yNormalized * containerSize.height + panOffset.y

            // Compute relative screen offset
            val diffX = (hotspotWorldX + normalizedPanX) % panoWidth
            val screenX = if (diffX > containerSize.width && diffX > panoWidth / 2) diffX - panoWidth else diffX
            val screenY = hotspotWorldY.coerceIn(90f, containerSize.height.toFloat() - 260f)

            val isSelected = selectedHotspot?.id == hotspot.id
            val isCurrentGuide = isGuideModeActive && guideStepIndex == index

            // Render Anchor Pin
            Box(
                modifier = Modifier
                    .offset { IntOffset(screenX.roundToInt(), screenY.roundToInt()) }
                    .size(56.dp)
                    .clickable { viewModel.selectHotspot(hotspot) }
                    .testTag("hotspot_pin_${hotspot.id}"),
                contentAlignment = Alignment.Center
            ) {
                // Pulsating ring
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .scale(if (isSelected || isCurrentGuide) pulseScale * 1.15f else pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isSelected || isCurrentGuide) MihrabGold.copy(alpha = 0.55f)
                            else OasisGreen.copy(alpha = 0.45f)
                        )
                )
                // Solid center badge
                Surface(
                    shape = CircleShape,
                    color = if (isSelected || isCurrentGuide) MihrabGold else OasisGreen,
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier.size(28.dp),
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Spatial AR Billboard Callout Label (When enabled or selected)
            if (isAllLabelsVisible || isSelected || isCurrentGuide) {
                val labelX = (screenX - 70f).coerceIn(20f, (containerSize.width - 240f).coerceAtLeast(20f))
                val labelY = (screenY - 80f).coerceIn(75f, (containerSize.height - 220f).coerceAtLeast(75f))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.88f),
                    border = BorderStroke(
                        1.5.dp,
                        if (isSelected || isCurrentGuide) MihrabGold else Color.White.copy(alpha = 0.4f)
                    ),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .offset { IntOffset(labelX.roundToInt(), labelY.roundToInt()) }
                        .widthIn(max = 220.dp)
                        .clickable { viewModel.selectHotspot(hotspot) }
                        .testTag("ar_billboard_label_${hotspot.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected || isCurrentGuide) MihrabGold else OasisGreen,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected || isCurrentGuide) Color.Black else Color.White,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = hotspot.titleAr,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = hotspot.subtitleAr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MihrabGoldLight,
                                    fontSize = 9.sp
                                ),
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MihrabGoldLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Top HUD: Compass & Radar Minimap & Floating Technology Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Compass & Qibla Direction HUD
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.Black.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, MihrabGold.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .rotate(-headingDegrees),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // North needle (Red)
                            drawLine(
                                color = Color(0xFFE53935),
                                start = Offset(size.width / 2, size.height / 2),
                                end = Offset(size.width / 2, 2f),
                                strokeWidth = 4f
                            )
                            // South needle (White)
                            drawLine(
                                color = Color.White,
                                start = Offset(size.width / 2, size.height / 2),
                                end = Offset(size.width / 2, size.height - 2f),
                                strokeWidth = 4f
                            )
                        }
                    }

                    Column {
                        val cardinal = when {
                            headingDegrees in 337f..360f || headingDegrees in 0f..22f -> "القبلة (شمال غرب)"
                            headingDegrees in 23f..67f -> "الشمال"
                            headingDegrees in 68f..112f -> "الشرق (البئر)"
                            headingDegrees in 113f..157f -> "الجنوب الشرقي"
                            headingDegrees in 158f..202f -> "الجنوب (المداخل)"
                            headingDegrees in 203f..247f -> "الجنوب الغربي"
                            headingDegrees in 248f..292f -> "الغرب (الصرح)"
                            else -> "القبلة والمحراب"
                        }
                        Text(
                            text = "${headingDegrees.toInt()}° • $cardinal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = activeViewpoint.nameAr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = MihrabGoldLight
                            )
                        )
                    }
                }
            }

            // Radar Architectural Minimap with Sweeping View Cone
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .border(1.5.dp, MihrabGold, CircleShape)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.width / 2
                    val centerPt = Offset(r, r)

                    // Grid rings
                    drawCircle(Color.White.copy(alpha = 0.15f), radius = r * 0.75f, style = Stroke(1f))
                    drawCircle(Color.White.copy(alpha = 0.25f), radius = r * 0.45f, style = Stroke(1f))

                    // Mosque Sanctuary Boundary Walls
                    drawRect(
                        color = SaroojBrown.copy(alpha = 0.5f),
                        topLeft = Offset(r * 0.3f, r * 0.3f),
                        size = Size(r * 1.4f, r * 1.4f),
                        style = Stroke(2f)
                    )

                    // Mihrab Niche Indicator (North-West)
                    drawArc(
                        color = MihrabGold,
                        startAngle = 210f,
                        sweepAngle = 60f,
                        useCenter = true,
                        topLeft = Offset(r * 0.7f, r * 0.15f),
                        size = Size(r * 0.6f, r * 0.4f)
                    )

                    // Sweeping FOV Cone based on heading
                    val fovAngle = 55f
                    val startAngle = (headingDegrees - 90f - fovAngle / 2f)
                    val radStart = Math.toRadians(startAngle.toDouble())
                    val radEnd = Math.toRadians((startAngle + fovAngle).toDouble())
                    val coneLength = r * 0.95f

                    val path = Path().apply {
                        moveTo(centerPt.x, centerPt.y)
                        lineTo(
                            (centerPt.x + coneLength * Math.cos(radStart)).toFloat(),
                            (centerPt.y + coneLength * Math.sin(radStart)).toFloat()
                        )
                        arcTo(
                            rect = androidx.compose.ui.geometry.Rect(centerPt.x - coneLength, centerPt.y - coneLength, centerPt.x + coneLength, centerPt.y + coneLength),
                            startAngleDegrees = startAngle,
                            sweepAngleDegrees = fovAngle,
                            forceMoveTo = false
                        )
                        close()
                    }
                    drawPath(path, MihrabGold.copy(alpha = 0.45f))
                }
            }

            // High-Tech Quick Action Toggles (Labels, Auto-Orbit, Ruler, Gyro, Audio, Reset)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                // Spatial AR Labels Toggle (Show/Hide all labels)
                FilledIconToggleButton(
                    checked = isAllLabelsVisible,
                    onCheckedChange = { viewModel.toggleAllLabels() },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        checkedContainerColor = MihrabGold,
                        checkedContentColor = Color.Black,
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(34.dp).testTag("toggle_all_labels")
                ) {
                    Icon(
                        imageVector = if (isAllLabelsVisible) Icons.Default.Label else Icons.Default.LabelOff,
                        contentDescription = "اللوحات المعمارية",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Auto-Orbit Cinematic Pan Toggle
                FilledIconToggleButton(
                    checked = isAutoRotating,
                    onCheckedChange = { viewModel.toggleAutoRotate() },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        checkedContainerColor = OasisGreen,
                        checkedContentColor = Color.White,
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(34.dp).testTag("toggle_auto_orbit")
                ) {
                    Icon(
                        imageVector = if (isAutoRotating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "جولة سينمائية تلقائية",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Architectural Ruler Toggle
                FilledIconToggleButton(
                    checked = isDimensionRulerVisible,
                    onCheckedChange = { viewModel.toggleDimensionRuler() },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        checkedContainerColor = Color(0xFFE65100),
                        checkedContentColor = Color.White,
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(34.dp).testTag("toggle_dimension_ruler")
                ) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = "أبعاد المعمار",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Motion Gyroscope Toggle
                FilledIconToggleButton(
                    checked = isMotionGyroActive,
                    onCheckedChange = { viewModel.toggleMotionGyro() },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        checkedContainerColor = OasisGreen,
                        checkedContentColor = Color.White,
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(34.dp).testTag("toggle_motion_gyro")
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = "حساس الجيروسكوب",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Ambient Audio Soundscape Toggle
                FilledIconToggleButton(
                    checked = isAudioPlaying,
                    onCheckedChange = { viewModel.toggleAmbientAudio() },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        checkedContainerColor = MihrabGold,
                        checkedContentColor = Color.Black,
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(34.dp).testTag("toggle_ambient_audio")
                ) {
                    Icon(
                        imageVector = if (isAudioPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeMute,
                        contentDescription = "أصوات المكان",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Zoom Lens Stepper (1.0x -> 1.8x -> 2.8x)
                IconButton(
                    onClick = {
                        val nextZoom = when {
                            zoomScale < 1.4f -> 1.8f
                            zoomScale < 2.3f -> 2.8f
                            else -> 1.0f
                        }
                        viewModel.updateZoom(nextZoom / zoomScale)
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "تكبير مجهري",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Real Photos Live Comparison HUD Toggle
                FilledIconToggleButton(
                    checked = isLivePhotoHudOpen,
                    onCheckedChange = { viewModel.toggleLivePhotoHud() },
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        checkedContainerColor = Color(0xFF1976D2),
                        checkedContentColor = Color.White,
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(34.dp).testTag("toggle_live_photo_hud")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "الصور الحقيقية للموقع",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Reset Camera
                IconButton(
                    onClick = { viewModel.reset360View() },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "إعادة الضبط",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Viewpoints Teleport Walkthrough Bar (Floating horizontal row)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 0.82f),
            border = BorderStroke(1.dp, SaroojBrownLight.copy(alpha = 0.4f)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 78.dp)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(viewpoints) { station ->
                    val isCurrent = activeViewpoint.id == station.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCurrent) MihrabGold else Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { viewModel.selectViewpoint(station) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isCurrent) Icons.Default.Navigation else Icons.Default.Place,
                                contentDescription = null,
                                tint = if (isCurrent) Color.Black else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = station.nameAr,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.Black else Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Ambient Lighting Mode selector chip (Daylight / Golden / Night)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.75f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AmbientLightingMode.values().forEach { mode ->
                    val isSelected = ambientLighting == mode
                    IconButton(
                        onClick = { viewModel.setAmbientLighting(mode) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MihrabGold else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = when (mode) {
                                AmbientLightingMode.DAYLIGHT -> Icons.Default.WbSunny
                                AmbientLightingMode.GOLDEN_HOUR -> Icons.Default.FilterVintage
                                AmbientLightingMode.SPIRITUAL_NIGHT -> Icons.Default.NightsStay
                            },
                            contentDescription = mode.titleAr,
                            tint = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Soundscape Audio Waves banner & Track Switcher
        AnimatedVisibility(
            visible = isAudioPlaying,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, MihrabGold),
                modifier = Modifier.clickable {
                    val nextTrack = (selectedAudioTrack + 1) % 3
                    viewModel.setAudioTrack(nextTrack)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.width(3.dp).height(audioWaveHeight.dp).background(MihrabGold))
                    Box(modifier = Modifier.width(3.dp).height((audioWaveHeight * 0.7f).dp).background(OasisGreen))
                    Box(modifier = Modifier.width(3.dp).height((audioWaveHeight * 1.2f).dp).background(MihrabGold))
                    Column {
                        val trackLabel = when (selectedAudioTrack) {
                            0 -> "نسيم واحة سعال ونخيل نزوى"
                            1 -> "خرير فلج دارس وحوض الوضوء"
                            else -> "الصدى المعماري والسكينة"
                        }
                        Text(
                            text = trackLabel,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                        )
                        Text(
                            text = "انقر لتبديل المؤثر الصوتي",
                            style = MaterialTheme.typography.bodySmall.copy(color = MihrabGoldLight, fontSize = 8.sp)
                        )
                    }
                }
            }
        }

        // Live Photo Comparison HUD Overlay (Matching real uploaded field photos with 360 space)
        AnimatedVisibility(
            visible = isLivePhotoHudOpen,
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut() + slideOutVertically { -it / 2 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 135.dp, start = 14.dp, end = 14.dp)
        ) {
            LivePhotoComparisonHud(
                sceneType = sceneType,
                selectedPhotoRes = selectedHudPhotoRes,
                onSelectPhoto = { selectedHudPhotoRes = it },
                onClose = { viewModel.toggleLivePhotoHud() }
            )
        }

        // Bottom Controls: Walkway Steppers, Hotspot Carousel, Detail Card & Guided Tour Control Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Walk-through Street-view style Navigational Steppers Row
            WalkwayNavigationOverlay(
                sceneType = sceneType,
                onWalkForward = {
                    when (sceneType) {
                        PanoramicSceneType.INTERIOR_SANCTUARY -> viewModel.walkToScene(PanoramicSceneType.VIEW_FROM_MIHRAB)
                        PanoramicSceneType.VIEW_FROM_MIHRAB -> viewModel.walkToScene(PanoramicSceneType.MIHRAB_MACRO_INSPECTION)
                        PanoramicSceneType.EXTERIOR_SAHN -> viewModel.walkToScene(PanoramicSceneType.INTERIOR_SANCTUARY)
                        PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> viewModel.walkToScene(PanoramicSceneType.INTERIOR_SANCTUARY)
                    }
                },
                onTurnAround = {
                    viewModel.updatePan(Offset(containerSize.width * 1.25f, 0f))
                },
                onTogglePhotoHud = { viewModel.toggleLivePhotoHud() },
                onAskCompanion = { viewModel.navigateTo(AtharScreen.COMPANION_CHAT) }
            )

            // Hotspot Quick-Jump Carousel Dock (Horizontal Tray of labeled elements)
            Surface(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = "المعالم والعناصر المعمارية في المشهد (انقر للتوجه المباشر):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MihrabGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                    )
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(hotspots) { index, spot ->
                            val isSelected = selectedHotspot?.id == spot.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MihrabGold else Color.White.copy(alpha = 0.12f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color.White else Color.Transparent
                                ),
                                modifier = Modifier.clickable {
                                    viewModel.jumpToHotspot(spot, containerSize.width.toFloat())
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) Color.Black else OasisGreen,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = spot.titleAr,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = AmiriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hotspot Deep Inspection Card (Popup when selected)
            AnimatedVisibility(
                visible = selectedHotspot != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                selectedHotspot?.let { spot ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                        border = BorderStroke(1.5.dp, MihrabGold),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .testTag("hotspot_detail_card")
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(OasisGreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Architecture,
                                            contentDescription = null,
                                            tint = OasisGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = spot.titleAr,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = AmiriFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = SaroojBrownDark
                                            )
                                        )
                                        Text(
                                            text = spot.subtitleAr,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SaroojBrown,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.selectHotspot(null) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إغلاق",
                                        tint = Color.Gray
                                    )
                                }
                            }

                            Text(
                                text = spot.descriptionAr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp,
                                    color = TextPrimaryDark
                                )
                            )

                            // Citation and Audio Simulation
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SandSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = OasisGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = spot.encyclopediaReference,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextSecondaryDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    FilledTonalButton(
                                        onClick = { isNarratorPlaying = !isNarratorPlaying },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (isNarratorPlaying) OasisGreen else MihrabGoldContainer,
                                            contentColor = if (isNarratorPlaying) Color.White else OnMihrabGoldContainer
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isNarratorPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isNarratorPlaying) "إيقاف الشرح" else "استمع للمرشد",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Guided Tour Navigation Bar
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.88f),
                border = BorderStroke(1.dp, MihrabGold.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledIconToggleButton(
                            checked = isGuideModeActive,
                            onCheckedChange = { viewModel.toggleGuideMode() },
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                checkedContainerColor = MihrabGold,
                                checkedContentColor = Color.Black,
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("toggle_guide_mode")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = "المرشد التراثي"
                            )
                        }
                        Column {
                            Text(
                                text = "الجولة المعمارية المصحوبة بمرشد",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (isGuideModeActive)
                                    "المعلم ${guideStepIndex + 1} من ${hotspots.size}: ${hotspots.getOrNull(guideStepIndex)?.titleAr ?: ""}"
                                else
                                    "اضغط لتفعيل مسار الجولة الاستكشافية",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MihrabGoldLight
                                )
                            )
                        }
                    }

                    if (isGuideModeActive) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { viewModel.prevGuideStep() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NavigateBefore,
                                    contentDescription = "السابق",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = { viewModel.nextGuideStep() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MihrabGold)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = "التالي",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalkwayNavigationOverlay(
    sceneType: PanoramicSceneType,
    onWalkForward: () -> Unit,
    onTurnAround: () -> Unit,
    onTogglePhotoHud: () -> Unit,
    onAskCompanion: () -> Unit
) {
    val forwardLabel = when (sceneType) {
        PanoramicSceneType.INTERIOR_SANCTUARY -> "امشِ نحو المحراب ⬆"
        PanoramicSceneType.VIEW_FROM_MIHRAB -> "افحص نقوش الجص 🔍"
        PanoramicSceneType.EXTERIOR_SAHN -> "ادخل لقاعة الصلاة 🕌"
        PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> "تراجع للوراء ⬇"
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MihrabGold,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .clickable { onWalkForward() }
                    .testTag("walkway_forward_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = forwardLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                modifier = Modifier.clickable { onTurnAround() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "استدر 180°",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1976D2),
                modifier = Modifier.clickable { onTogglePhotoHud() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "الصور الحقيقية 📸",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = OasisGreen,
                modifier = Modifier.clickable { onAskCompanion() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "اسأل رفيق أثَر 💬",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LivePhotoComparisonHud(
    sceneType: PanoramicSceneType,
    selectedPhotoRes: Int?,
    onSelectPhoto: (Int) -> Unit,
    onClose: () -> Unit
) {
    val defaultPhotoRes = when (sceneType) {
        PanoramicSceneType.INTERIOR_SANCTUARY -> R.drawable.photo_mosque_interior_arches
        PanoramicSceneType.VIEW_FROM_MIHRAB -> R.drawable.photo_mosque_ruins_arch
        PanoramicSceneType.EXTERIOR_SAHN -> R.drawable.photo_mosque_rocky_terrace
        PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> R.drawable.photo_mihrab_mazara_field
    }

    val activeRes = selectedPhotoRes ?: defaultPhotoRes

    val allPhotosList = listOf(
        Pair(R.drawable.photo_mosque_interior_arches, "قاعة الصلاة والأعمدة الصاروجية والعقود"),
        Pair(R.drawable.photo_mihrab_mazara_field, "محراب مسجد المزارعة بسمائل (رمضان 974هـ)"),
        Pair(R.drawable.photo_mihrab_uwaynah_museum, "محراب مسجد العوينة معروضاً بالمتحف الوطني"),
        Pair(R.drawable.photo_mosque_rocky_terrace, "مسجد العباد المشيد فوق النتوء الصخري"),
        Pair(R.drawable.photo_mosque_twin_arches, "الواجهة ذات العقدين ومدرج الصعود الحجري"),
        Pair(R.drawable.photo_mosque_ruins_arch, "العقد والمدخل الحجري الأثري المشيد بالحجارة"),
        Pair(R.drawable.photo_mosque_exterior_perch, "الأساسات الصخرية الحاملة للمسجد فوق الجرف"),
        Pair(R.drawable.photo_site_saal_wide, "مشهد عام للمسجد والنتوء الجبلي المطل على الواحة"),
        Pair(R.drawable.photo_site_saal_cliff, "جرف الصخرة والسلالم الحجرية المؤدية للصرح"),
        Pair(R.drawable.photo_site_saal_path, "المسار الصاعد للمسجد بين الصخور والواحة"),
        Pair(R.drawable.photo_mosque_compound_views, "لقطات مقارنة متعددة للمسجد الصخري")
    )

    val currentPair = allPhotosList.find { it.first == activeRes } ?: allPhotosList.first()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.92f)),
        border = BorderStroke(1.5.dp, Color(0xFF1976D2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_photo_comparison_hud")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "المطابقة بالصور الحقيقية للموقع",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.LightGray
                    )
                }
            }

            // Main Featured Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = activeRes),
                    contentDescription = currentPair.second,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "المصدر: «الموسوعة العُمانية»",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MihrabGoldLight,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = currentPair.second,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
            )

            // Horizontal Thumbnails Strip
            Text(
                text = "اختر زاوية من الصور الحقيقية الـ 11:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF90CAF9),
                    fontSize = 9.sp
                )
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(allPhotosList) { item ->
                    val isThumbActive = item.first == activeRes
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            if (isThumbActive) 2.dp else 1.dp,
                            if (isThumbActive) MihrabGold else Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .size(52.dp)
                            .clickable { onSelectPhoto(item.first) }
                    ) {
                        Image(
                            painter = painterResource(id = item.first),
                            contentDescription = item.second,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

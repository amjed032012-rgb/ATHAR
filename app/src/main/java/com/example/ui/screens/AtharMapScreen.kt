package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Mosque
import com.example.ui.AtharScreen
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharTopAppBar
import com.example.ui.theme.*
import kotlin.math.roundToInt

enum class HeritageSiteCategory {
    UBBAD_MOSQUE,             // مساجد العباد في نزوى وسعال (الموضوع الرئيسي)
    INDEPENDENT_MIHRAB_STUDY  // محراب المزارعة والعوينة (دراسة تكميلية مستقلة)
}

data class MapHeritagePoint(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: HeritageSiteCategory,
    val governorate: String,
    val wilayat: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Int,
    val distanceFromNizwaKm: String,
    val travelTimeFromNizwa: String,
    val architecturalHighlight: String,
    val encyclopediaRef: String,
    // Relative canvas normalized coordinates on Oman map
    val normX: Float,
    val normY: Float
)

@Composable
fun AtharMapScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeFilter by viewModel.mapRegionFilter.collectAsState()

    // 5 Geographically Verified Heritage Points
    val allPoints = remember {
        listOf(
            MapHeritagePoint(
                id = "nizwa_imams",
                title = "مساجد العباد في مقبرة الأئمة",
                subtitle = "نزوى التاريخية (المركز الروحي للأئمة)",
                category = HeritageSiteCategory.UBBAD_MOSQUE,
                governorate = "محافظة الداخلية",
                wilayat = "ولاية نزوى",
                latitude = 22.9348,
                longitude = 57.5312,
                altitudeMeters = 492,
                distanceFromNizwaKm = "0 كم (نقطة الارتكاز)",
                travelTimeFromNizwa = "في قلب نزوى القديمة",
                architecturalHighlight = "محراب صاروجي بعمق 1.4م، وارتفاع 3.5م، و3 أعمدة مستطيلة تقسم الفضاء إلى رواقين، مع 7 مداخل متوازنة.",
                encyclopediaRef = "الموسوعة العُمانية — المجلد العاشر (ص 3482)",
                normX = 0.32f,
                normY = 0.52f
            ),
            MapHeritagePoint(
                id = "saal_site1",
                title = "مسجد العباد بسعال — الموقع الأول",
                subtitle = "النتوء الصخري التأسيسي قرب الشرجة",
                category = HeritageSiteCategory.UBBAD_MOSQUE,
                governorate = "محافظة الداخلية",
                wilayat = "ولاية نزوى (واحة سُعال)",
                latitude = 22.9240,
                longitude = 57.5420,
                altitudeMeters = 510,
                distanceFromNizwaKm = "3.2 كم من مقبرة الأئمة",
                travelTimeFromNizwa = "5 دقائق بالسيارة",
                architecturalHighlight = "مبني مباشرة فوق صخرة صلبة طبيعية بدون أعمدة داخلية، بمساحة 26 م² و7 مداخل وصرح صخري مرتفع 3 أمتار.",
                encyclopediaRef = "الموسوعة العُمانية — المجلد السادس (ص 2110)",
                normX = 0.38f,
                normY = 0.56f
            ),
            MapHeritagePoint(
                id = "saal_site2",
                title = "مسجد العباد بسعال — الموقع الثاني",
                subtitle = "الأعمدة الأسطوانية وقبو الوضوء المخروطي",
                category = HeritageSiteCategory.UBBAD_MOSQUE,
                governorate = "محافظة الداخلية",
                wilayat = "ولاية نزوى (حارة سُعال)",
                latitude = 22.9215,
                longitude = 57.5455,
                altitudeMeters = 485,
                distanceFromNizwaKm = "3.8 كم من مقبرة الأئمة",
                travelTimeFromNizwa = "6 دقائق بالسيارة",
                architecturalHighlight = "عمودان أسطوانيان من الصاروج، عقد نصف دائري وعقد شبه مخروطي، بروز ركني 4 سم، وغرفة وضوء ذات قبو مخروطي وبئر ملاصق.",
                encyclopediaRef = "الموسوعة العُمانية — المجلد السادس والعاشر (ص 3490)",
                normX = 0.42f,
                normY = 0.60f
            ),
            MapHeritagePoint(
                id = "mazara_samail",
                title = "محراب مسجد المزارعة",
                subtitle = "ولاية سمائل (دراسة تكميلية مستقلة — ليس من مساجد العباد)",
                category = HeritageSiteCategory.INDEPENDENT_MIHRAB_STUDY,
                governorate = "محافظة الداخلية",
                wilayat = "ولاية سمائل (وادي بني رواحة)",
                latitude = 23.3050,
                longitude = 57.9850,
                altitudeMeters = 415,
                distanceFromNizwaKm = "58 كم شمال شرق نزوى",
                travelTimeFromNizwa = "40 دقيقة عبر طريق نزوى - سمائل السريع",
                architecturalHighlight = "محراب جصي قائم في موقعه الأصلي مؤرخ في رمضان 974هـ (1567م) للصانع طالب بن مشمل المنحي، يتميز بإكليل كوفي يحمل الشهادتين.",
                encyclopediaRef = "الموسوعة العُمانية — المجلد التاسع (ص 3120)",
                normX = 0.58f,
                normY = 0.35f
            ),
            MapHeritagePoint(
                id = "uwaynah_wadi_bani_khalid",
                title = "محراب مسجد العوينة",
                subtitle = "وادي بني خالد (دراسة تكميلية مستقلة — ليس من مساجد العباد)",
                category = HeritageSiteCategory.INDEPENDENT_MIHRAB_STUDY,
                governorate = "محافظة شمال الشرقية",
                wilayat = "ولاية وادي بني خالد (قرية العوينة)",
                latitude = 22.5620,
                longitude = 59.0880,
                altitudeMeters = 620,
                distanceFromNizwaKm = "172 كم شرق نزوى",
                travelTimeFromNizwa = "ساعتان و 15 دقيقة عبر طريق أزكي - إبراء",
                architecturalHighlight = "عرض 2.80م، يضم 30 ختماً جصياً و3 صحون من الخزف الصيني Ming، الأصل بالمتحف الوطني ونسخة مطابقة بالمسجد بعد الترميم.",
                encyclopediaRef = "الموسوعة العُمانية — المجلد السابع والعاشر (ص 2418)",
                normX = 0.82f,
                normY = 0.65f
            )
        )
    }

    var selectedPoint by remember { mutableStateOf<MapHeritagePoint?>(allPoints.first()) }
    var mapZoom by remember { mutableStateOf(1.0f) }
    var mapPan by remember { mutableStateOf(Offset.Zero) }
    var isTopographicMode by remember { mutableStateOf(true) } // true: Relief/Wadis, false: Heritage Routes

    val filteredPoints = remember(activeFilter) {
        when (activeFilter) {
            "UBBAD" -> allPoints.filter { it.category == HeritageSiteCategory.UBBAD_MOSQUE }
            "MIHRABS" -> allPoints.filter { it.category == HeritageSiteCategory.INDEPENDENT_MIHRAB_STUDY }
            else -> allPoints
        }
    }

    // Pulse animation for selected pin
    val infiniteTransition = rememberInfiniteTransition(label = "map_pulse")
    val pinPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("athar_map_screen")
    ) {
        AtharTopAppBar(
            title = "الخريطة الجغرافية للتراث المعماري",
            subtitle = "مساجد العباد (الموضوع الرئيسي) ودراسة المحرابين المستقلة",
            showBackButton = true,
            onBackClick = { viewModel.navigateBack() }
        )

        // Category Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeFilter == "ALL",
                onClick = { viewModel.setMapRegionFilter("ALL") },
                label = { Text("كافة المواقع (5)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SaroojBrown,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeFilter == "UBBAD",
                onClick = { viewModel.setMapRegionFilter("UBBAD") },
                label = { Text("مساجد العباد (نزوى وسعال) [الرئيسي]") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = OasisGreen,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeFilter == "MIHRABS",
                onClick = { viewModel.setMapRegionFilter("MIHRABS") },
                label = { Text("دراسة المحرابين [مستقلة]") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MihrabGold,
                    selectedLabelColor = Color.Black
                )
            )
        }

        // Map Viewport with interactive Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(if (isTopographicMode) Color(0xFFEFE8DB) else Color(0xFF1E232A))
                .border(1.5.dp, SandOutline, RoundedCornerShape(22.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        mapZoom = (mapZoom * zoom).coerceIn(0.85f, 3.2f)
                        mapPan += pan
                    }
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(filteredPoints, mapZoom, mapPan) {
                        detectTapGestures { tapOffset ->
                            val w = size.width
                            val h = size.height
                            var closest: MapHeritagePoint? = null
                            var minDist = Float.MAX_VALUE

                            filteredPoints.forEach { p ->
                                val pinX = (p.normX * w * mapZoom) + mapPan.x
                                val pinY = (p.normY * h * mapZoom) + mapPan.y
                                val dist = (tapOffset - Offset(pinX, pinY)).getDistance()
                                if (dist < 75f && dist < minDist) {
                                    minDist = dist
                                    closest = p
                                }
                            }
                            if (closest != null) {
                                selectedPoint = closest
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw Oman mountain ranges and topography relief
                if (isTopographicMode) {
                    // Al-Hajar Mountains Contour Ridge 1 (Al-Hajar al-Gharbi)
                    val mtnPath1 = Path().apply {
                        moveTo((0.15f * w * mapZoom) + mapPan.x, (0.35f * h * mapZoom) + mapPan.y)
                        quadraticBezierTo(
                            (0.35f * w * mapZoom) + mapPan.x, (0.20f * h * mapZoom) + mapPan.y,
                            (0.65f * w * mapZoom) + mapPan.x, (0.32f * h * mapZoom) + mapPan.y
                        )
                    }
                    drawPath(mtnPath1, Color(0xFFD6C8B4), style = Stroke(width = 12f * mapZoom))

                    // Al-Hajar al-Sharqi Ridge
                    val mtnPath2 = Path().apply {
                        moveTo((0.65f * w * mapZoom) + mapPan.x, (0.32f * h * mapZoom) + mapPan.y)
                        quadraticBezierTo(
                            (0.78f * w * mapZoom) + mapPan.x, (0.45f * h * mapZoom) + mapPan.y,
                            (0.92f * w * mapZoom) + mapPan.x, (0.68f * h * mapZoom) + mapPan.y
                        )
                    }
                    drawPath(mtnPath2, Color(0xFFD6C8B4), style = Stroke(width = 10f * mapZoom))

                    // Wadi Kalbuh & Wadi Saal Flow (Near Nizwa)
                    val wadiNizwa = Path().apply {
                        moveTo((0.28f * w * mapZoom) + mapPan.x, (0.45f * h * mapZoom) + mapPan.y)
                        quadraticBezierTo(
                            (0.35f * w * mapZoom) + mapPan.x, (0.54f * h * mapZoom) + mapPan.y,
                            (0.42f * w * mapZoom) + mapPan.x, (0.64f * h * mapZoom) + mapPan.y
                        )
                    }
                    drawPath(wadiNizwa, Color(0xFFA5C4B4).copy(alpha = 0.7f), style = Stroke(width = 8f * mapZoom))

                    // Wadi Samail Flow
                    val wadiSamail = Path().apply {
                        moveTo((0.52f * w * mapZoom) + mapPan.x, (0.25f * h * mapZoom) + mapPan.y)
                        lineTo((0.62f * w * mapZoom) + mapPan.x, (0.42f * h * mapZoom) + mapPan.y)
                    }
                    drawPath(wadiSamail, Color(0xFFA5C4B4).copy(alpha = 0.6f), style = Stroke(width = 6f * mapZoom))

                    // Wadi Bani Khalid Springs Flow
                    val wadiBaniKhalid = Path().apply {
                        moveTo((0.76f * w * mapZoom) + mapPan.x, (0.58f * h * mapZoom) + mapPan.y)
                        lineTo((0.86f * w * mapZoom) + mapPan.x, (0.72f * h * mapZoom) + mapPan.y)
                    }
                    drawPath(wadiBaniKhalid, Color(0xFFA5C4B4).copy(alpha = 0.6f), style = Stroke(width = 6f * mapZoom))
                }

                // Connecting Road Corridors & Distance Lines between Nizwa and other sites
                val nizwaX = (0.32f * w * mapZoom) + mapPan.x
                val nizwaY = (0.52f * h * mapZoom) + mapPan.y

                filteredPoints.forEach { pt ->
                    if (pt.id != "nizwa_imams") {
                        val ptX = (pt.normX * w * mapZoom) + mapPan.x
                        val ptY = (pt.normY * h * mapZoom) + mapPan.y

                        val isTargetSelected = selectedPoint?.id == pt.id
                        val routeStroke = Stroke(
                            width = if (isTargetSelected) 3f * mapZoom else 1.5f * mapZoom,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                        )
                        drawLine(
                            color = if (isTargetSelected) MihrabGold else Color.Gray.copy(alpha = 0.45f),
                            start = Offset(nizwaX, nizwaY),
                            end = Offset(ptX, ptY),
                            strokeWidth = routeStroke.width,
                            pathEffect = routeStroke.pathEffect
                        )
                    }
                }

                // Draw Heritage Location Pins
                filteredPoints.forEach { pt ->
                    val pinX = (pt.normX * w * mapZoom) + mapPan.x
                    val pinY = (pt.normY * h * mapZoom) + mapPan.y
                    val isSelected = selectedPoint?.id == pt.id

                    val pinPrimaryColor = when (pt.category) {
                        HeritageSiteCategory.UBBAD_MOSQUE -> if (isSelected) OasisGreen else SaroojBrown
                        HeritageSiteCategory.INDEPENDENT_MIHRAB_STUDY -> if (isSelected) MihrabGold else Color(0xFFC2185B)
                    }

                    // Pulsating selection halo
                    if (isSelected) {
                        drawCircle(
                            color = pinPrimaryColor.copy(alpha = 0.35f),
                            radius = 32f * pinPulse,
                            center = Offset(pinX, pinY)
                        )
                    }

                    // Outer circle
                    drawCircle(
                        color = pinPrimaryColor,
                        radius = if (isSelected) 18f else 13f,
                        center = Offset(pinX, pinY)
                    )

                    // Inner core
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 7f else 4.5f,
                        center = Offset(pinX, pinY)
                    )
                }
            }

            // Top-Left Floating Controls: Reset Zoom & Map Mode Toggle
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { isTopographicMode = !isTopographicMode },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (isTopographicMode) Icons.Default.Terrain else Icons.Default.AltRoute,
                            contentDescription = "نمط الخريطة",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            mapZoom = 1.0f
                            mapPan = Offset.Zero
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "إعادة ضبط",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = if (isTopographicMode) "تضاريس ووديان" else "مسارات ومسافات",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                    )
                }
            }

            // Map Legend Overlay at Top-Right
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SaroojBrown))
                        Text("مساجد العباد (نزوى وسعال) [الرئيسي]", style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 9.sp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MihrabGold))
                        Text("دراسة المحرابين (سمائل/العوينة) [مستقلة]", style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 9.sp))
                    }
                }
            }
        }

        // Bottom Selected Site Detailed Architectural Sheet
        AnimatedVisibility(
            visible = selectedPoint != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut()
        ) {
            selectedPoint?.let { pt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("map_selected_point_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                    border = BorderStroke(
                        1.5.dp,
                        if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) OasisGreen else MihrabGold
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Title & Category Clarification
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pt.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = SaroojBrownDark
                                    )
                                )
                                Text(
                                    text = pt.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) OasisGreen else Color(0xFFC2185B),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) OasisGreenContainer else MihrabGoldContainer
                            ) {
                                Text(
                                    text = if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) "مساجد العباد [الرئيسي]" else "دراسة مستقلة",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) OnOasisGreenContainer else OnMihrabGoldContainer,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Geography & Distance Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SandSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text("المسافة من نزوى", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondaryDark))
                                    Text(pt.distanceFromNizwaKm, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SaroojBrownDark))
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SandSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text("الارتفاع عن البحر", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondaryDark))
                                    Text("${pt.altitudeMeters} متراً", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SaroojBrownDark))
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SandSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text("زمن الوصول", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondaryDark))
                                    Text(pt.travelTimeFromNizwa, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OasisGreen, fontSize = 9.sp), maxLines = 1)
                                }
                            }
                        }

                        // Architectural Highlight from Omani Encyclopedia
                        Text(
                            text = pt.architecturalHighlight,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 18.sp,
                                color = TextPrimaryDark
                            )
                        )

                        // Coordinates & Citation Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الإحداثيات: ${pt.latitude}° N, ${pt.longitude}° E",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextSecondaryDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = pt.encyclopediaRef,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = OasisGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Action Buttons: Open Google Maps & View 360 / Study
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) {
                                        viewModel.navigateTo(AtharScreen.TOUR_360)
                                    } else {
                                        viewModel.navigateTo(AtharScreen.TWO_MIHRABS)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = SaroojContainer)
                            ) {
                                Icon(
                                    imageVector = if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) Icons.Default.Explore else Icons.Default.Compare,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (pt.category == HeritageSiteCategory.UBBAD_MOSQUE) "جولة 360° الحية" else "استوديو المحرابين",
                                    color = OnSaroojContainer,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Button(
                                onClick = {
                                    val geoUri = Uri.parse("geo:${pt.latitude},${pt.longitude}?q=${pt.latitude},${pt.longitude}(${Uri.encode(pt.title)})")
                                    val intent = Intent(Intent.ACTION_VIEW, geoUri)
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${pt.latitude},${pt.longitude}")
                                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = OasisGreen)
                            ) {
                                Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("المسار عبر Google Maps", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }
}

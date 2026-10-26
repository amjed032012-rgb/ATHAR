package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Mosque
import com.example.ui.AtharScreen
import com.example.ui.AtharViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MosqueDetailScreen(
    mosque: Mosque,
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var fullScreenPhotoIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("mosque_detail_screen_${mosque.id}"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top App Bar
        item {
            AtharTopAppBar(
                title = mosque.shortTitle,
                subtitle = "${mosque.wilayah} • ${mosque.villageOrArea}",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() }
            )
        }

        // Hero Image
        item {
            if (mosque.imageDrawableRes != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, SandOutlineVariant)
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                            Image(
                                painter = painterResource(id = mosque.imageDrawableRes),
                                contentDescription = mosque.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Fullscreen zoom indicator
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .clickable { fullScreenPhotoIndex = 0 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomIn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "معاينة مكبرة",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                                    )
                                }
                            }
                        }

                        if (mosque.imageAttribution != null) {
                            Surface(
                                color = SandSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mosque.imageAttribution,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Multi-Photo Gallery Horizontal Strip
        if (mosque.photos.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "معرض الصور الحقيقية الموثقة (${mosque.photos.size} صور)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                        Text(
                            text = "انقر للتكبير",
                            style = MaterialTheme.typography.labelSmall.copy(color = OasisGreen)
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(mosque.photos) { index, photoRes ->
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(115.dp)
                                    .clickable { fullScreenPhotoIndex = index },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, SandOutlineVariant)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = photoRes),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Index Badge
                                    Surface(
                                        shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.BottomEnd)
                                    ) {
                                        Text(
                                            text = "${index + 1}/${mosque.photos.size}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dedicated Directions & Navigation Card (MANDATORY REQUEST)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.5.dp, OasisGreen)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(OasisGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Directions,
                                    contentDescription = null,
                                    tint = OnOasisGreenContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "الاتجاهات والوصول الميداني",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = SaroojBrownDark
                                    )
                                )
                                Text(
                                    text = mosque.roadAccessibility,
                                    style = MaterialTheme.typography.labelSmall.copy(color = OasisGreen)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SandSurfaceVariant
                        ) {
                            Text(
                                text = "ملاحة فورية",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SaroojBrownDark,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Driving estimate badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = OasisGreenContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = OnOasisGreenContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = mosque.estimatedDriveTime,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnOasisGreenContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Detailed Turn-by-Turn Route Guidance
                    Text(
                        text = mosque.directionsGuide,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryDark,
                            lineHeight = 22.sp
                        )
                    )

                    // GPS Coordinates Box with Copy
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SandSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = SaroojBrown,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "الإحداثيات: ${String.format("%.4f", mosque.latitude)}° N, ${String.format("%.4f", mosque.longitude)}° E",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SaroojBrownDark
                                    )
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Coordinates", "${mosque.latitude}, ${mosque.longitude}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "تم نسخ إحداثيات ${mosque.shortTitle}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ الإحداثيات",
                                    tint = SaroojBrown,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Direct Navigation Button (Google Maps)
                    Button(
                        onClick = {
                            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${mosque.latitude},${mosque.longitude}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_navigate_mosque_${mosque.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = OasisGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بدء التوجيه الملاحي (Google Maps)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Interactive 3D Model & Floor Plan Viewer
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                ArchitecturalBlueprintCard(mosque = mosque)
            }
        }

        // Historical Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "النبذة التاريخية والتوثيق",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                        DocumentationStatusBadge(status = mosque.documentationStatus)
                    }

                    Text(
                        text = mosque.historicalOverview,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryDark,
                            lineHeight = 24.sp
                        )
                    )

                    // Quick Specs Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecBadge(
                            label = "المساحة التقريبية",
                            value = mosque.approximateAreaM2,
                            modifier = Modifier.weight(1f)
                        )
                        SpecBadge(
                            label = "المداخل",
                            value = "${mosque.entrancesCount} مداخل",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Architectural Elements Deep Dive Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "الخصائص المعمارية والإنشائية",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )

                    ArchitecturalFeatureRow(
                        title = "الأعمدة والأروقة",
                        description = mosque.pillarsDescription,
                        icon = Icons.Default.ViewColumn
                    )
                    ArchitecturalFeatureRow(
                        title = "العقود والأقواس",
                        description = mosque.archesDescription,
                        icon = Icons.Default.AllInclusive
                    )
                    ArchitecturalFeatureRow(
                        title = "المحراب والتجويف الصوتي",
                        description = mosque.mihrabDescription,
                        icon = Icons.Default.MeetingRoom
                    )
                    ArchitecturalFeatureRow(
                        title = "السقف وعناصره الإنشائية",
                        description = mosque.ceilingDescription,
                        icon = Icons.Default.Roofing
                    )
                    ArchitecturalFeatureRow(
                        title = "مرافق المسجد الملحقة",
                        description = mosque.facilitiesDescription,
                        icon = Icons.Default.WaterDrop
                    )
                }
            }
        }

        // Key Architectural Highlights (Consumer-ready verified facts)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = MihrabGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "أبرز المعالم والملامح الفريدة للمسجد",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                    }

                    mosque.keyFeatures.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MihrabGold)
                            )
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimaryDark,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Building Materials Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "مواد البناء العُمانية التقليدية",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )

                    mosque.buildingMaterials.forEach { material ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MihrabGold)
                            )
                            Text(
                                text = material,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryDark
                                )
                            )
                        }
                    }
                }
            }
        }

        // Citations & References Section (الموسوعة العُمانية)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = OasisGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "التوثيق والمراجع المعتمدة (الموسوعة العُمانية)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                    }

                    mosque.citationSources.forEach { citation ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SandSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = citation.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SaroojBrownDark
                                    )
                                )
                                Text(
                                    text = "${citation.sourceVolume} • ${citation.pageOrEntry}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OasisGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = citation.publisher,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                                if (citation.notes.isNotBlank()) {
                                    Text(
                                        text = citation.notes,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(AtharScreen.TOUR_360) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SaroojBrown)
                ) {
                    Icon(imageVector = Icons.Default.Explore, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("جولة 360°")
                }

                FilledTonalButton(
                    onClick = { viewModel.navigateTo(AtharScreen.COMPANION_CHAT) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = OasisGreenContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = OnOasisGreenContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مرشد أثَر الذكي", color = OnOasisGreenContainer)
                }
            }
        }
    }

    // Full-Screen Photo Zoom Viewer Modal
    fullScreenPhotoIndex?.let { index ->
        val photos = mosque.photos
        if (index in photos.indices) {
            var currentIdx by remember { mutableStateOf(index) }
            var zoom by remember { mutableStateOf(1f) }
            var pan by remember { mutableStateOf(Offset.Zero) }

            Dialog(
                onDismissRequest = { fullScreenPhotoIndex = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    // Zoomable Image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(currentIdx) {
                                detectTransformGestures { _, panDelta, zoomDelta, _ ->
                                    zoom = (zoom * zoomDelta).coerceIn(1f, 4f)
                                    pan += panDelta
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = photos[currentIdx]),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = zoom,
                                    scaleY = zoom,
                                    translationX = pan.x,
                                    translationY = pan.y
                                )
                        )
                    }

                    // Top Bar with Close and Counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(onClick = { fullScreenPhotoIndex = null }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "${currentIdx + 1} من ${photos.size}",
                                style = MaterialTheme.typography.titleSmall.copy(color = Color.White),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(onClick = { zoom = 1f; pan = Offset.Zero }) {
                                Icon(Icons.Default.Refresh, contentDescription = "إعادة ضبط", tint = Color.White)
                            }
                        }
                    }

                    // Bottom Caption Overlay with Next/Prev
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (currentIdx in mosque.photoCaptions.indices) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mosque.photoCaptions[currentIdx],
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }

                        // Next / Prev Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    if (currentIdx > 0) {
                                        currentIdx--
                                        zoom = 1f
                                        pan = Offset.Zero
                                    }
                                },
                                enabled = currentIdx > 0
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "السابق")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("السابق")
                            }

                            FilledTonalButton(
                                onClick = {
                                    if (currentIdx < photos.size - 1) {
                                        currentIdx++
                                        zoom = 1f
                                        pan = Offset.Zero
                                    }
                                },
                                enabled = currentIdx < photos.size - 1
                            ) {
                                Text("التالي")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowBack, contentDescription = "التالي")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SandSurfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SaroojBrownDark
                )
            )
        }
    }
}

@Composable
private fun ArchitecturalFeatureRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(SandSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SaroojBrown,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SaroojBrownDark
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimaryDark,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

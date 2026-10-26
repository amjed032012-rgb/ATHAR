package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
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
import com.example.R
import com.example.data.MihrabStudy
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharTopAppBar
import com.example.ui.components.OmaniHeritageDivider
import com.example.ui.theme.*

@Composable
fun TwoMihrabsScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val mihrabs = viewModel.mihrabs
    var selectedTab by remember { mutableStateOf(0) } // 0: Cards & Directions, 1: Side-by-Side Comparison, 2: Stucco Macro

    val mazara = mihrabs.first { it.id == "mazara_samail" }
    val uwaynah = mihrabs.first { it.id == "uwaynah_wadi_bani_khalid" }

    var fullScreenPhotoRes by remember { mutableStateOf<Int?>(null) }
    var fullScreenCaption by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("two_mihrabs_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            AtharTopAppBar(
                title = "محرابان، حكايتان من العمارة العُمانية",
                subtitle = "دراسة مقارنة موثقة لنفائس الجص وخزف المينغ الصيني",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() }
            )
        }

        // Elegant Scholarly Introduction Banner
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SandSurfaceVariant,
                border = BorderStroke(1.5.dp, MihrabGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
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
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = OnMihrabGoldContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "دراسة معمارية مقارنة لنفائس المحاريب الجصية",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                        Text(
                            text = "يُفرد تطبيق «أثَر» هذه الدراسة المقارنة المتخصصة لمحرابي المزارعة (سمائل) والعوينة (المحفوظ بالمتحف الوطني) كنموذجين استثنائيين لازدهار فنون النقش الجصي العُماني ومدرسة منح في القرن العاشر الهجري، استناداً حصرياً إلى ما وثقته «الموسوعة العُمانية» (المجلدان السابع والتاسع).",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimaryDark,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }

        // Tab Row: Cards vs Side-by-side vs Macro Inspector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SandSurfaceLight,
                contentColor = SaroojBrown,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "المحرابان والاتجاهات",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "المقارنة التحليلية",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "المجهر الجصي والخزف",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Detailed Cards for both mihrabs with photos & directions
            item {
                MihrabDetailCard(
                    mihrab = mazara,
                    isGoldAccent = false,
                    onOpenPhoto = { res, caption ->
                        fullScreenPhotoRes = res
                        fullScreenCaption = caption
                    }
                )
            }
            item {
                OmaniHeritageDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                MihrabDetailCard(
                    mihrab = uwaynah,
                    isGoldAccent = true,
                    onOpenPhoto = { res, caption ->
                        fullScreenPhotoRes = res
                        fullScreenCaption = caption
                    }
                )
            }
        } else if (selectedTab == 1) {
            // Side-by-side comparison table
            item {
                MihrabsComparisonStudio(mazara = mazara, uwaynah = uwaynah)
            }
        } else {
            // Interactive Stucco & Porcelain Macro Inspector
            item {
                MihrabMacroInspectorCard(onOpenPhoto = { res, caption ->
                    fullScreenPhotoRes = res
                    fullScreenCaption = caption
                })
            }
        }

        // Architectural Connection with Masaajid al-Ubbad
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = OasisGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "الرابط المعماري والروحي مع مساجد العباد",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                    }

                    Text(
                        text = "توثق «الموسوعة العُمانية» وحدة التقاليد المعمارية العُمانية؛ فرغم أن مساجد العباد في نزوى وسعال تمثل ذروة الزهد الروحي وبساطة التجويف المحرابي الصاروجي النقي (بعمق 1.4م وارتفاع 3.5م)، فإن محرابي المزارعة والعوينة يمثلان مدرسة النحت الجصي الفاخر التي بلغت أوجها في القرن العاشر الهجري على يد أساتذة نقش المحاريب مثل طالب بن مشمل المنحي. يشترك الجميع في الارتفاع القياسي المتناسق (نحو 3.5 أمتار)، والاعتماد على الصاروج كطبقة تأسيس قوية.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp,
                            color = TextPrimaryDark
                        )
                    )
                }
            }
        }
    }

    // Full-Screen Photo Zoom Viewer
    fullScreenPhotoRes?.let { resId ->
        var zoom by remember { mutableStateOf(1f) }
        var pan by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

        Dialog(
            onDismissRequest = { fullScreenPhotoRes = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(resId) {
                            detectTransformGestures { _, panDelta, zoomDelta, _ ->
                                zoom = (zoom * zoomDelta).coerceIn(1f, 4.5f)
                                pan += panDelta
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resId),
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

                // Top Bar
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
                        IconButton(onClick = { fullScreenPhotoRes = null }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "فحص مجهري فوتوغرافي",
                            style = MaterialTheme.typography.titleSmall.copy(color = Color.White),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        IconButton(onClick = { zoom = 1f; pan = androidx.compose.ui.geometry.Offset.Zero }) {
                            Icon(Icons.Default.Refresh, contentDescription = "إعادة ضبط", tint = Color.White)
                        }
                    }
                }

                // Bottom Caption
                if (fullScreenCaption.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = fullScreenCaption,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MihrabDetailCard(
    mihrab: MihrabStudy,
    isGoldAccent: Boolean,
    onOpenPhoto: (Int, String) -> Unit
) {
    val context = LocalContext.current
    val accentColor = if (isGoldAccent) MihrabGold else SaroojBrown

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "محراب ${mihrab.mosqueName}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                    Text(
                        text = mihrab.location,
                        style = MaterialTheme.typography.bodySmall.copy(color = OasisGreen)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isGoldAccent) MihrabGoldContainer else SaroojContainer
                ) {
                    Text(
                        text = mihrab.hijriYear,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isGoldAccent) OnMihrabGoldContainer else OnSaroojContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // National Museum Note banner (for Al-Uwaynah)
            if (mihrab.nationalMuseumNote.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = OasisGreenContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, OasisGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Museum,
                            contentDescription = null,
                            tint = OasisGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = mihrab.nationalMuseumNote,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnOasisGreenContainer,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Real Photo Banner of the Mihrab
            if (mihrab.photoDrawableRes != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val caption = mihrab.photoCaptions.firstOrNull() ?: "محراب ${mihrab.mosqueName}"
                            onOpenPhoto(mihrab.photoDrawableRes, caption)
                        }
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = mihrab.photoDrawableRes),
                            contentDescription = "صورة محراب ${mihrab.mosqueName}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(bottomEnd = 10.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = null, tint = MihrabGold, modifier = Modifier.size(14.dp))
                                Text("انقر للفحص المجهري المكبر", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp))
                            }
                        }
                    }
                }
            }

            // Dedicated Directions & Visiting Information Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SandSurfaceVariant,
                border = BorderStroke(1.dp, SandOutlineVariant),
                modifier = Modifier.fillMaxWidth()
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
                            Icon(Icons.Default.Directions, contentDescription = null, tint = OasisGreen, modifier = Modifier.size(18.dp))
                            Text(
                                text = "الاتجاهات والزيارة الميدانية",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaroojBrownDark
                                )
                            )
                        }

                        Text(
                            text = mihrab.estimatedDriveTime,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OasisGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = mihrab.directionsGuide,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimaryDark,
                            lineHeight = 20.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MihrabGoldContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = OnMihrabGoldContainer, modifier = Modifier.size(16.dp))
                            Text(
                                text = mihrab.visitingAdvice,
                                style = MaterialTheme.typography.labelSmall.copy(color = OnMihrabGoldContainer)
                            )
                        }
                    }

                    // Direct Google Maps Button
                    Button(
                        onClick = {
                            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${mihrab.latitude},${mihrab.longitude}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OasisGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("بدء التوجيه (Google Maps)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SandSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("الأبعاد", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        Text(
                            "${mihrab.widthMeters} × ${mihrab.heightMeters}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SandSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("النقاش / الصانع", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        Text(
                            mihrab.builderOrMaster,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Crown and Inscriptions
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "الإكليل العلوي والنقوش الجصية:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaroojBrownDark
                    )
                )
                Text(
                    text = mihrab.crownDescription,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SandSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "تفريغ النقش الكتابي (${mihrab.inscriptionVerificationStatus}):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrown
                        )
                    )
                    Text(
                        text = mihrab.inscriptionsTranscribed,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 20.sp,
                            color = TextPrimaryDark
                        )
                    )
                }
            }

            // Decorative Elements (Seals & Ceramics)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SandSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("الأختام الجصية", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        Text(
                            mihrab.decorativeSealsCount,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SandSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("الأواني الخزفية", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        Text(
                            mihrab.porcelainPlatesCount,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Encyclopedia Citation
            Text(
                text = "المرجع المعتمد: ${mihrab.citationRef.sourceVolume} (${mihrab.citationRef.pageOrEntry})",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OasisGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun MihrabsComparisonStudio(
    mazara: MihrabStudy,
    uwaynah: MihrabStudy
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.dp, SandOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "جدول المقارنة التحليلية الشاملة وفق «الموسوعة العُمانية»",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = SaroojBrownDark
                )
            )

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SaroojBrownDark, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "وجه المقارنة",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "محراب المزارعة",
                    style = MaterialTheme.typography.labelSmall.copy(color = MihrabGoldLight, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "محراب العوينة",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1.3f)
                )
            }

            ComparisonRow("الموقع والولاية", "سمائل (وادي بني رواحة)", "المتحف الوطني بمسقط ووادي بني خالد")
            ComparisonRow("تاريخ الإتمام", "شهر رمضان 974هـ (1567م)", "القرن العاشر الهجري")
            ComparisonRow("الإحداثيات الجغرافية", "23.3082° N, 57.9864° E", "22.6185° N, 59.0882° E")
            ComparisonRow("الأبعاد المعمارية", "ارتفاع 3.5م × عرض 2.1م", "ارتفاع 3.5م × عرض 2.8م (أفسح)")
            ComparisonRow("الأختام الجصية", "إطارات نباتية وهندسية", "30 ختماً جصياً دائرياً")
            ComparisonRow("الأطباق الخزفية", "لا توجد (نحت جصي خالص)", "3 صحون خزف صيني أصلية (مينغ)")
            ComparisonRow("النقاش الصانع", "طالب بن مشمل المنحي", "طالب بن مشمل وعبد الله بن عمر")
            ComparisonRow("مقر المحراب الحالي", "في موضعه الأصلي بالمسجد", "الأصل بالمتحف الوطني ونسخة بالمسجد")
            ComparisonRow("المرجع المعتمد", "الموسوعة العُمانية (مج 9)", "الموسوعة العُمانية (مج 7 و 10)")
        }
    }
}

@Composable
private fun ComparisonRow(criterion: String, mazaraVal: String, uwaynahVal: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = SandSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = criterion,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SaroojBrownDark),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = mazaraVal,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 11.sp),
                modifier = Modifier.weight(1.3f)
            )
            Text(
                text = uwaynahVal,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 11.sp),
                modifier = Modifier.weight(1.3f)
            )
        }
    }
}

@Composable
private fun MihrabMacroInspectorCard(onOpenPhoto: (Int, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.dp, SandOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "المجهر الجصي وفحص الخزف الصيني (Ming Porcelain)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = SaroojBrownDark
                )
            )

            Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOpenPhoto(
                            R.drawable.mihrab_gypsum_seals_detail_1792920451688,
                            "فحص مجهري فوتوغرافي عالي الدقة للأختام الجصية الـ 30 وخزف سلالة مينغ الصينية في محراب العوينة بالمتحف الوطني"
                        )
                    }
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.mihrab_gypsum_seals_detail_1792920451688),
                        contentDescription = "تفاصيل الأختام الجصية والخزف الصيني",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 10.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "انقر للتكبير التفاعلي",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = "تكشف دراسة محراب العوينة في «الموسوعة العُمانية» (المجلد السابع، ص 2419) عن إدماج 3 صحون أثرية نادرة من الخزف الصيني الأزرق والأبيض (Ming Porcelain) في قوس المحراب، محاطة بـ 30 ختماً جصياً هندسياً ونباتياً. هذا الإدماج يجسد ذروة الإشعاع الحضاري والتجارة البحرية لسلطنة عُمان عبر مينائي صحار وقلهات التاريخيين.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimaryDark,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

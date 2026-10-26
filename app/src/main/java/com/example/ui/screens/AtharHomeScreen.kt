package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Mosque
import com.example.ui.AtharScreen
import com.example.ui.AtharViewModel
import com.example.ui.components.DocumentationStatusBadge
import com.example.ui.components.OmaniHeritageDivider
import com.example.ui.theme.*

@Composable
fun AtharHomeScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val mosques = viewModel.mosques
    val fieldNotes by viewModel.fieldNotes.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.panoramic_interior_athar_1792913518479),
                    contentDescription = "جوف مساجد العباد في نزوى",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.85f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Competition badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MihrabGoldContainer,
                            border = BorderStroke(1.dp, MihrabGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = OnMihrabGoldContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "مشروع أثَر | التوثيق المعماري العُماني",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnMihrabGoldContainer
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            IconButton(onClick = { viewModel.navigateTo(AtharScreen.COMPANION_CHAT) }) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "رفيق أثر",
                                    tint = MihrabGoldLight
                                )
                            }
                        }
                    }

                    // Title & Slogan
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "أثَر | ATHAR",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "رحلة تفاعلية في ذاكرة مساجد العباد ونفائس العمارة العُمانية",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MihrabGoldLight
                            )
                        )
                        Text(
                            text = "«نحفظ أثر الماضي، ونصنع به تجربة المستقبل»",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }

                    // Authors Strip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "إعداد الطلبة: أمجد بن أحمد الفرقاني • ماجد بن أحمد الفرقاني • الخليل بن إبراهيم السناني",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Live Real Statistics Cards (Calculated directly from real data)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        count = "${mosques.size}",
                        label = "مساجد موثقة",
                        subtitle = "نزوى وسعال",
                        icon = Icons.Default.Mosque,
                        accentColor = SaroojBrown,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        count = "2",
                        label = "محاريب تاريخية",
                        subtitle = "سمائل والعوينة",
                        icon = Icons.Default.Architecture,
                        accentColor = MihrabGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        count = "10",
                        label = "نقاط 360° حية",
                        subtitle = "محاكاة واقعية",
                        icon = Icons.Default.Explore,
                        accentColor = OasisGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Field Notes stat banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SandSurfaceVariant,
                    border = BorderStroke(1.dp, SandOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryBooks,
                                contentDescription = null,
                                tint = OasisGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "المرجع التوثيقي الرئيسي: «الموسوعة العُمانية» (وزارة التراث والثقافة)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaroojBrownDark
                                )
                            )
                        }
                        Text(
                            text = "${fieldNotes.size} توثيقات ميدانية",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OasisGreen
                            )
                        )
                    }
                }
            }
        }

        // Primary Action Grid (Immersive 360 & Map)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Big 360 Experience Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AtharScreen.TOUR_360) }
                        .testTag("action_tour_360_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SaroojBrownDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MihrabGoldContainer
                            ) {
                                Text(
                                    text = "استكشاف تفاعلي حي",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnMihrabGoldContainer
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "عِش التجربة بزاوية 360° مع المرشد التراثي",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "جولة في جوف المسجد والصرح وغرفة الوضوء المقببة مع نقاط استدلال تفاعلية وشرح صوتي.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MihrabGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                // Two Secondary Buttons (Interactive Map & Mihrab Studio)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionNavCard(
                        title = "الخريطة الميدانية",
                        desc = "إحداثيات نزوى وسعال",
                        icon = Icons.Default.Map,
                        color = OasisGreen,
                        onClick = { viewModel.navigateTo(AtharScreen.MAP) },
                        modifier = Modifier.weight(1f)
                    )
                    ActionNavCard(
                        title = "استوديو المحرابين",
                        desc = "مقارنة المزارعة والعوينة",
                        icon = Icons.Default.Compare,
                        color = MihrabGold,
                        onClick = { viewModel.navigateTo(AtharScreen.TWO_MIHRABS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Additional Smart Action Row (Encyclopedia, Companion Guide, Team)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionNavCard(
                        title = "الموسوعة العُمانية",
                        desc = "المجلدات 6 و7 و9 و10",
                        icon = Icons.Default.MenuBook,
                        color = SaroojBrown,
                        onClick = { viewModel.navigateTo(AtharScreen.ENCYCLOPEDIA_SOURCES) },
                        modifier = Modifier.weight(1f)
                    )
                    ActionNavCard(
                        title = "رفيق أثَر الذكي",
                        desc = "إجابات تراثية موثقة",
                        icon = Icons.Default.SmartToy,
                        color = OasisGreen,
                        onClick = { viewModel.navigateTo(AtharScreen.COMPANION_CHAT) },
                        modifier = Modifier.weight(1f)
                    )
                    ActionNavCard(
                        title = "عن المشروع",
                        desc = "فريق أثَر الطلابي",
                        icon = Icons.Default.Info,
                        color = MihrabGold,
                        onClick = { viewModel.navigateTo(AtharScreen.ABOUT) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section Title: مساجد العباد الموثقة
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OmaniHeritageDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مساجد العباد في نزوى وسعال",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                        Text(
                            text = "المحور الأساسي للمشروع — صروح روحية ومعمارية",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )
                    }
                }
            }
        }

        // Mosque Cards List
        items(mosques) { mosque ->
            MosqueOverviewCard(
                mosque = mosque,
                onClick = { viewModel.selectMosque(mosque) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Why Athar Section
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
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MihrabGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "لماذا «أثَر»؟",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                    }
                    Text(
                        text = "يُمثل تطبيق «أثَر» نموذجاً متميزاً للمشاريع الطلابية العُمانية في توظيف التقنيات الرقمية المتقدمة لخدمة التراث الوطني. نلتزم بالأمانة التوثيقية الصارمة؛ فلا صور مولدة تُنسب لمعلم، ولا إحصاءات وهمية، بل بيانات حقيقية مستقاة من «الموسوعة العُمانية» والمسح الميداني المسؤول.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            color = TextPrimaryDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    count: String,
    label: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.dp, SandOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SaroojBrownDark
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryDark,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun ActionNavCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.dp, SandOutlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaroojBrownDark
                    )
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MosqueOverviewCard(
    mosque: Mosque,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("mosque_card_${mosque.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.dp, SandOutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Optional Photo Thumbnail
            if (mosque.imageDrawableRes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Image(
                        painter = painterResource(id = mosque.imageDrawableRes),
                        contentDescription = mosque.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MihrabGold, modifier = Modifier.size(12.dp))
                            Text(
                                text = "${mosque.photos.size} صور موثقة",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mosque.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                    Text(
                        text = "${mosque.wilayah} • ${mosque.villageOrArea}",
                        style = MaterialTheme.typography.bodySmall.copy(color = OasisGreen)
                    )
                }

                DocumentationStatusBadge(status = mosque.documentationStatus)
            }

            // Road & Distance Quick Info
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SandSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = OasisGreen, modifier = Modifier.size(14.dp))
                        Text(
                            text = mosque.estimatedDriveTime,
                            style = MaterialTheme.typography.labelSmall.copy(color = OasisGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            maxLines = 1
                        )
                    }
                }
            }

            Text(
                text = mosque.architecturalDescription,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimaryDark,
                    lineHeight = 18.sp
                )
            )

            // Chips for Key Architectural Elements
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SandSurfaceVariant
                ) {
                    Text(
                        text = "${mosque.entrancesCount} مداخل",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SaroojBrown,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                if (mosque.pillarsCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SandSurfaceVariant
                    ) {
                        Text(
                            text = "${mosque.pillarsCount} أعمدة",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SaroojBrown,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (mosque.mihrabDepthMeters != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MihrabGoldContainer
                    ) {
                        Text(
                            text = "عمق المحراب: ${mosque.mihrabDepthMeters}م",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnMihrabGoldContainer,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = SandOutlineVariant)

            // Bottom Actions: Directions + Details & 3D
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = {
                        val uri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${mosque.latitude},${mosque.longitude}")
                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = OasisGreenContainer),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnOasisGreenContainer)
                    Spacer(Modifier.width(4.dp))
                    Text("الاتجاهات", fontSize = 11.sp, color = OnOasisGreenContainer, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.buttonColors(containerColor = SaroojBrown),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("التفاصيل ومجسم 3D", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

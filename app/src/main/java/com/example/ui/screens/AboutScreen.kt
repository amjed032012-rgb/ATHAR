package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharTopAppBar
import com.example.ui.theme.*

@Composable
fun AboutScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("about_screen"),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AtharTopAppBar(
                title = "عن مشروع «أثَر | ATHAR»",
                subtitle = "المنظومة التوثيقية التفاعلية للعمارة العُمانية",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() }
            )
        }

        // Emblem & Logo
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SaroojBrownDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MihrabGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.athar_app_icon_1792913503172),
                            contentDescription = "شعار أثر",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                    }

                    Text(
                        text = "أثَر | ATHAR",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "«نحفظ أثر الماضي، ونصنع به تجربة المستقبل»",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MihrabGoldLight,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MihrabGoldContainer
                    ) {
                        Text(
                            text = "مشروع توثيقي معتمد استناداً للموسوعة العُمانية",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnMihrabGoldContainer
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Official Description Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "التعريف الرسمي بالمشروع",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                    Text(
                        text = "«أثَر مشروع رقمي طلابي يهدف إلى توثيق مساجد العباد في نزوى وسعال، والتعريف بملامح العمارة العُمانية التقليدية من خلال الصور الموثقة والخرائط والمعلومات المعمارية والمصادر المرجعية. يسعى المشروع إلى ربط التراث بالتقنية، وتشجيع التوثيق الميداني المسؤول، وحفظ المعلومات للأجيال القادمة».",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryDark,
                            lineHeight = 24.sp
                        )
                    )
                }
            }
        }

        // Student Team Credits Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = OasisGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "فريق العمل وإعداد المشروع:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SaroojBrownDark
                            )
                        )
                    }

                    StudentMemberRow("أمجد بن أحمد الفرقاني", "باحث ميداني ومهندس برمجيات")
                    StudentMemberRow("ماجد بن أحمد الفرقاني", "توثيق معماري ومصمم تجربة المستخدم")
                    StudentMemberRow("الخليل بن إبراهيم السناني", "أرشفة تاريخية وتدقيق مصادر الموسوعة العُمانية")
                }
            }
        }

        // Scientific Authority Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
                border = BorderStroke(1.dp, SandOutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "المرجع التوثيقي الرئيسي المعتمد:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                    Text(
                        text = "«الموسوعة العُمانية» — الصادرة عن وزارة التراث والثقافة بسلطنة عُمان (المجلدات: السادس والسابع والتاسع والعاشر).",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OasisGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentMemberRow(name: String, role: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SandSurfaceVariant,
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
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = OnMihrabGoldContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaroojBrownDark
                    )
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

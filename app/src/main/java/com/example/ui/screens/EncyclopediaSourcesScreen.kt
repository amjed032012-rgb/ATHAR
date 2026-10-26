package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharTopAppBar
import com.example.ui.theme.*

@Composable
fun EncyclopediaSourcesScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("sources_screen"),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AtharTopAppBar(
                title = "سجل المصادر والتوثيق المعتمد",
                subtitle = "المرجع العلمي والتوثيقي الرئيسي للمشروع",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() }
            )
        }

        // Hero Encyclopedia Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SaroojBrownDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MihrabGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "الموسوعة العُمانية — المصدر السيادي المعتمد",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Text(
                        text = "التزاماً بأعلى معايير الدقة العلمية والتوثيقية، يستند مشروع «أثَر» حصرياً في حقائقه التاريخية والمعمارية وأبعاد المحاريب وتوصيف الصاروج إلى «الموسوعة العُمانية» الصادرة عن وزارة التراث والثقافة بسلطنة عُمان باعتبارها المرجع السيادي الأول.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Volume 10
        item {
            SourceVolumeCard(
                volumeTitle = "المجلد العاشر — مساجد نزوى ومقبرة الأئمة",
                entries = listOf(
                    "مدخل: مساجد نزوى الأثرية ومساجد العباد (ص 3482 - 3486)",
                    "عمارة الأروقة المقسمة بصف الأعمدة المستطيلة الثلاثة",
                    "أبعاد المحراب الصاروجي النزواني (عمق 1.4م وارتفاع 3.5م)",
                    "تقارير أعمال الترميم الحكومية لمساجد العباد بمقبرة الأئمة"
                ),
                status = "موثق أكاديمياً بالكامل"
            )
        }

        // Volume 6
        item {
            SourceVolumeCard(
                volumeTitle = "المجلد السادس — العمارة الطينية ومساجد سعال",
                entries = listOf(
                    "مدخل: قرية سعال ونظم المساجد الطينية المقامة على النتوءات الصخرية (ص 2110 - 2114)",
                    "استخدام جذوع خشب الكندل وسعف النخيل في التسقيف وتوزيع الأحمال",
                    "هندسة الصاروج العُماني المقاوم للرطوبة في أحواض الوضوء والأعمدة الأسطوانية",
                    "القباب شبه المخروطية في غرف الوضوء ومجاورة الآبار المائية"
                ),
                status = "موثق بمطابقة ميدانية"
            )
        }

        // Volume 9
        item {
            SourceVolumeCard(
                volumeTitle = "المجلد التاسع — محراب مسجد المزارعة في سمائل",
                entries = listOf(
                    "مدخل: محاريب الجص المنحوتة في عُمان (ص 3120 - 3124)",
                    "نقش الإتمام في شهر رمضان سنة 974هـ (1567م)",
                    "ترجمة صانع المحراب: طالب بن مشمل المنحي",
                    "تحليل الإكليل العلوي الكوفي والشهادتين والنقوش القرآنية"
                ),
                status = "قراءة جصية موثقة"
            )
        }

        // Volume 7
        item {
            SourceVolumeCard(
                volumeTitle = "المجلد السابع — محراب مسجد العوينة بوادي بني خالد",
                entries = listOf(
                    "مدخل: مساجد محافظة شمال الشرقية والتحف المعمارية (ص 2418 - 2422)",
                    "الأبعاد المعمارية للمحراب: العرض 2.80م والارتفاع 3.50م",
                    "توثيق الأختام الجصية الثلاثين والصحون الخزفية الصينية الأصلية",
                    "توثيق نقل المحراب الأصلي للمتحف الوطني وصنع نسخة مطابقة بالمسجد"
                ),
                status = "موثق ومسجل بالمتحف الوطني"
            )
        }
    }
}

@Composable
private fun SourceVolumeCard(
    volumeTitle: String,
    entries: List<String>,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SandSurfaceLight),
        border = BorderStroke(1.dp, SandOutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = OasisGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = volumeTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OasisGreenContainer
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnOasisGreenContainer,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            entries.forEach { entry ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("•", color = MihrabGold, fontWeight = FontWeight.Bold)
                    Text(
                        text = entry,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimaryDark,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FieldNoteEntity
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharTopAppBar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldNotebookScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notes by viewModel.fieldNotes.collectAsState()
    val exportedReport by viewModel.exportedReportText.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog Input State
    var selectedMonument by remember { mutableStateOf("مساجد العباد في مقبرة الأئمة بنزوى") }
    var researcherName by remember { mutableStateOf("") }
    var gpsCoordinates by remember { mutableStateOf("22.9348° N, 57.5312° E") }
    var architecturalElement by remember { mutableStateOf("الأعمدة والمحراب") }
    var measurements by remember { mutableStateOf("") }
    var buildingMaterials by remember { mutableStateOf("الصاروج العُماني وطين اللبن") }
    var observations by remember { mutableStateOf("") }
    var verificationStatus by remember { mutableStateOf("موثق ميدانياً — فريق أثر") }

    Scaffold(
        topBar = {
            AtharTopAppBar(
                title = "دفتر الباحث الميداني",
                subtitle = "سجل التوثيق المحلي — قاعدة بيانات Room",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { viewModel.exportFieldNotesReport() },
                        modifier = Modifier.testTag("export_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "تصدير التقرير",
                            tint = SaroojBrown
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = SaroojBrown,
                contentColor = Color.White,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("add_field_note_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة توثيق ميداني")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("field_notes_list"),
            contentPadding = PaddingValues(16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SandSurfaceVariant,
                    border = BorderStroke(1.dp, SandOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = OasisGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "حفظ محلي آمن في قاعدة بيانات Room",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaroojBrownDark
                                )
                            )
                        }
                        Text(
                            text = "صُمم هذا الدفتر خصيصاً لفريق المشروع (أمجد، ماجد، الخليل) والباحثين الزوار لتدوين الملاحظات المعمارية والقياسات وحالة التحقق من الميدان مباشرة، مع إمكانية تصدير تقرير التوثيق الميداني ومشاركته.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimaryDark,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            if (notes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = SaroojBrownLight,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "لا توجد ملاحظات ميدانية مسجلة حتى الآن",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark)
                            )
                            Button(onClick = { showAddDialog = true }) {
                                Text("إضافة أول معاينة ميدانية")
                            }
                        }
                    }
                }
            } else {
                items(notes) { note ->
                    FieldNoteCard(
                        note = note,
                        onDelete = { viewModel.deleteFieldNote(note) }
                    )
                }
            }
        }
    }

    // Exported Report Modal Dialog
    if (exportedReport != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearExportedReport() },
            title = {
                Text(
                    text = "تقرير التوثيق الميداني المعتمد",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = SaroojBrownDark
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = exportedReport ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, exportedReport)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة تقرير أثر الميداني"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaroojBrown)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مشاركة التقرير")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Athar Report", exportedReport)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ التقرير إلى الحافظة بنجاح", Toast.LENGTH_SHORT).show()
                        viewModel.clearExportedReport()
                    }
                ) {
                    Text("نسخ النص")
                }
            }
        )
    }

    // Add New Field Note Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "تدوين معاينة ميدانية جديدة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = SaroojBrownDark
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedMonument,
                        onValueChange = { selectedMonument = it },
                        label = { Text("اسم المعلم الأثري") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = researcherName,
                        onValueChange = { researcherName = it },
                        label = { Text("اسم الباحث الموثّق") },
                        placeholder = { Text("مثال: أمجد بن أحمد الفرقاني") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = architecturalElement,
                        onValueChange = { architecturalElement = it },
                        label = { Text("العنصر المعماري المرصود") },
                        placeholder = { Text("مثال: المحراب، القبو الصاروجي، الحوض...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = measurements,
                        onValueChange = { measurements = it },
                        label = { Text("القياسات والأبعاد الميدانية") },
                        placeholder = { Text("مثال: العمق: 1.4م، الارتفاع: 3.5م") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = observations,
                        onValueChange = { observations = it },
                        label = { Text("ملاحظات الباحث والتفاصيل الميدانية") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addFieldNote(
                            monumentId = "custom_monument",
                            monumentName = selectedMonument.ifBlank { "مسجد العباد" },
                            researcherName = researcherName.ifBlank { "باحث في فريق أثر" },
                            gps = gpsCoordinates,
                            element = architecturalElement.ifBlank { "عنصر معماري" },
                            measurements = measurements.ifBlank { "بحاجة لقياسات تفصيلية" },
                            materials = buildingMaterials,
                            observations = observations.ifBlank { "معاينة ميدانية موثقة." },
                            status = verificationStatus
                        )
                        showAddDialog = false
                        Toast.makeText(context, "تم حفظ المعاينة في قاعدة بيانات Room", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaroojBrown)
                ) {
                    Text("حفظ التوثيق")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun FieldNoteCard(
    note: FieldNoteEntity,
    onDelete: () -> Unit
) {
    val dateFormatted = remember(note.visitDate) {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(note.visitDate))
    }

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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.monumentName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                    Text(
                        text = "الباحث: ${note.researcherName} • $dateFormatted",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = OasisGreenContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "الحالة: ${note.verificationStatus}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnOasisGreenContainer,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Text(
                text = "العنصر: ${note.architecturalElement} (${note.measurements})",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SaroojBrown
                )
            )

            Text(
                text = note.observations,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimaryDark,
                    lineHeight = 20.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الموقع: ${note.gpsCoordinates}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                )
                Text(
                    text = note.imageSourceAttribution,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                )
            }
        }
    }
}

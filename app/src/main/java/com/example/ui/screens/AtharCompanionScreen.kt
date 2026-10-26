package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ui.AtharScreen
import com.example.ui.AtharViewModel
import com.example.ui.ChatMessage
import com.example.ui.components.AtharTopAppBar
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun AtharCompanionScreen(
    viewModel: AtharViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var isTtsSpeaking by remember { mutableStateOf(false) }

    // Android TextToSpeech Setup
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Arabic locale
                val result = ttsInstance?.setLanguage(Locale("ar"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsInstance?.language = Locale.getDefault()
                }
            }
        }
        ttsInstance = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val suggestedQuestions = listOf(
        "📍 كيف أصل إلى مساجد العباد في نزوى وسعال؟",
        "🚗 مسار القيادة وزيارة محراب المزارعة والعوينة",
        "🕌 ما هي أبعاد المحراب الصاروجي والأعمدة؟",
        "💎 ما الفرق بين محرابي المزارعة والعوينة؟",
        "🧱 ما هو الصاروج العُماني وقبو الوضوء؟",
        "🧭 ما الذي تضمه جولة 360° والمجسمات 3D؟",
        "📖 ما هي مجلدات الموسوعة العُمانية المعتمدة؟"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("companion_chat_screen")
    ) {
        AtharTopAppBar(
            title = "مرشد أثَر التراثي الذكي",
            subtitle = "إجابات معمارية ومسارات ملاحية موثقة حصرياً من الموسوعة العُمانية",
            showBackButton = true,
            onBackClick = { viewModel.navigateBack() }
        )

        // Banner
        Surface(
            color = SandSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = OasisGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "المرجع الحصري والوحيد: «الموسوعة العُمانية»",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaroojBrownDark
                        )
                    )
                }

                if (isTtsSpeaking) {
                    IconButton(
                        onClick = {
                            ttsInstance?.stop()
                            isTtsSpeaking = false
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeOff,
                            contentDescription = "إيقاف الصوت",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Chat Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    msg = msg,
                    onNavigateToScreen = { screen -> viewModel.navigateTo(screen) },
                    onSpeakText = { text ->
                        ttsInstance?.let { tts ->
                            tts.stop()
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AtharGuideTTS")
                            isTtsSpeaking = true
                        }
                    }
                )
            }
        }

        // Quick Suggestions Horizontal Carousel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SandSurfaceLight)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "أسئلة مقترحة للمرشد التراثي:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedQuestions) { question ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SandSurfaceVariant,
                        border = BorderStroke(1.dp, SandOutlineVariant),
                        modifier = Modifier.clickable {
                            viewModel.sendUserMessage(question)
                        }
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SaroojBrownDark,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Input Field
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "اسأل مرشد أثَر عن المواقع، الاتجاهات، المحاريب...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("companion_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaroojBrown,
                        unfocusedBorderColor = SandOutlineVariant,
                        focusedContainerColor = SandSurfaceVariant,
                        unfocusedContainerColor = SandSurfaceVariant
                    ),
                    maxLines = 3
                )

                FilledIconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendUserMessage(textToSend)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_send_chat"),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SaroojBrown)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    msg: ChatMessage,
    onNavigateToScreen: (AtharScreen) -> Unit,
    onSpeakText: (String) -> Unit
) {
    val context = LocalContext.current
    val isUser = msg.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp, top = 2.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MihrabGoldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = OnMihrabGoldContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) SaroojBrown else SandSurfaceLight
            ),
            border = BorderStroke(1.dp, if (isUser) SaroojBrownDark else SandOutlineVariant),
            modifier = Modifier.widthIn(max = 330.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUser) Color.White else TextPrimaryDark,
                        lineHeight = 22.sp
                    )
                )

                if (!isUser && msg.citation != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = OasisGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = msg.citation,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OasisGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Text-to-speech speaker button
                        IconButton(
                            onClick = { onSpeakText(msg.text) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "قراءة صوتية",
                                tint = SaroojBrown,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Interactive Action Pills if the answer mentions directions, 360, or mihrabs
                if (!isUser) {
                    val textLower = msg.text.lowercase()
                    if (textLower.contains("اتجاه") || textLower.contains("إحداثيات") || textLower.contains("google maps") || textLower.contains("طريق")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onNavigateToScreen(AtharScreen.MAP) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = OasisGreenContainer)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnOasisGreenContainer)
                                Spacer(Modifier.width(4.dp))
                                Text("فتح الخريطة", fontSize = 11.sp, color = OnOasisGreenContainer)
                            }

                            FilledTonalButton(
                                onClick = {
                                    val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=22.9348,57.5312")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MihrabGoldContainer)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnMihrabGoldContainer)
                                Spacer(Modifier.width(4.dp))
                                Text("بدء الملاحة", fontSize = 11.sp, color = OnMihrabGoldContainer)
                            }
                        }
                    }

                    if (textLower.contains("360") || textLower.contains("بانوراما") || textLower.contains("جولة")) {
                        Button(
                            onClick = { onNavigateToScreen(AtharScreen.TOUR_360) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SaroojBrown),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("بدء جولة 360° التفاعلية", fontSize = 11.sp)
                        }
                    }

                    if (textLower.contains("محراب") && (textLower.contains("المزارعة") || textLower.contains("العوينة"))) {
                        FilledTonalButton(
                            onClick = { onNavigateToScreen(AtharScreen.TWO_MIHRABS) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = MihrabGoldContainer),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Compare, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnMihrabGoldContainer)
                            Spacer(Modifier.width(6.dp))
                            Text("فتح استوديو المحرابين والمقارنة", fontSize = 11.sp, color = OnMihrabGoldContainer)
                        }
                    }
                }
            }
        }
    }
}

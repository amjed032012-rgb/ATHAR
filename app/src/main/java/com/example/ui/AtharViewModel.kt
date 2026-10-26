package com.example.ui

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AmbientLightingMode
import com.example.data.ArchitecturalHotspot
import com.example.data.AtharHeritageRepository
import com.example.data.MihrabStudy
import com.example.data.Mosque
import com.example.data.PanoramicSceneType
import com.example.data.ViewpointStation
import com.example.data.local.AtharDatabase
import com.example.data.local.FieldNoteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AtharScreen {
    HOME,
    TOUR_360,
    MAP,
    MOSQUE_DETAIL,
    TWO_MIHRABS,
    FIELD_NOTEBOOK,
    ENCYCLOPEDIA_SOURCES,
    COMPANION_CHAT,
    ABOUT
}

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val citation: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class AtharViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AtharDatabase.getDatabase(application)
    private val repository = AtharHeritageRepository(database.fieldNoteDao())

    // Navigation State
    private val _currentScreen = MutableStateFlow(AtharScreen.HOME)
    val currentScreen: StateFlow<AtharScreen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<AtharScreen>()

    // Mosques Data
    val mosques: List<Mosque> = repository.getDocumentedMosques()
    private val _selectedMosque = MutableStateFlow<Mosque>(mosques.first())
    val selectedMosque: StateFlow<Mosque> = _selectedMosque.asStateFlow()

    // Mihrabs Study Data
    val mihrabs: List<MihrabStudy> = repository.getTwoMihrabsStudy()
    private val _selectedMihrab = MutableStateFlow<MihrabStudy>(mihrabs.first())
    val selectedMihrab: StateFlow<MihrabStudy> = _selectedMihrab.asStateFlow()

    // 360 Tour State
    val viewpointStations = repository.getViewpointStations()
    private val _activeViewpoint = MutableStateFlow(viewpointStations.first())
    val activeViewpoint: StateFlow<ViewpointStation> = _activeViewpoint.asStateFlow()

    private val _sceneType = MutableStateFlow(PanoramicSceneType.INTERIOR_SANCTUARY)
    val sceneType: StateFlow<PanoramicSceneType> = _sceneType.asStateFlow()

    private val _ambientLighting = MutableStateFlow(AmbientLightingMode.DAYLIGHT)
    val ambientLighting: StateFlow<AmbientLightingMode> = _ambientLighting.asStateFlow()

    private val _isMotionGyroActive = MutableStateFlow(false)
    val isMotionGyroActive: StateFlow<Boolean> = _isMotionGyroActive.asStateFlow()

    private val _ambientAudioPlaying = MutableStateFlow(false)
    val ambientAudioPlaying: StateFlow<Boolean> = _ambientAudioPlaying.asStateFlow()

    private val _panOffset = MutableStateFlow(Offset.Zero)
    val panOffset: StateFlow<Offset> = _panOffset.asStateFlow()

    private val _zoomScale = MutableStateFlow(1.0f)
    val zoomScale: StateFlow<Float> = _zoomScale.asStateFlow()

    private val _selectedHotspot = MutableStateFlow<ArchitecturalHotspot?>(null)
    val selectedHotspot: StateFlow<ArchitecturalHotspot?> = _selectedHotspot.asStateFlow()

    private val _isGuideModeActive = MutableStateFlow(false)
    val isGuideModeActive: StateFlow<Boolean> = _isGuideModeActive.asStateFlow()

    private val _guideStepIndex = MutableStateFlow(0)
    val guideStepIndex: StateFlow<Int> = _guideStepIndex.asStateFlow()

    private val _isAllLabelsVisible = MutableStateFlow(true)
    val isAllLabelsVisible: StateFlow<Boolean> = _isAllLabelsVisible.asStateFlow()

    private val _isAutoRotating = MutableStateFlow(false)
    val isAutoRotating: StateFlow<Boolean> = _isAutoRotating.asStateFlow()

    private val _isDimensionRulerVisible = MutableStateFlow(false)
    val isDimensionRulerVisible: StateFlow<Boolean> = _isDimensionRulerVisible.asStateFlow()

    private val _selectedAudioTrack = MutableStateFlow(0)
    val selectedAudioTrack: StateFlow<Int> = _selectedAudioTrack.asStateFlow()

    // Map Filters: "ALL", "UBBAD" (Nizwa & Saal), "MIHRABS" (Samail & Al-Uwaynah)
    private val _mapRegionFilter = MutableStateFlow("ALL")
    val mapRegionFilter: StateFlow<String> = _mapRegionFilter.asStateFlow()

    // Room Field Notes
    val fieldNotes: StateFlow<List<FieldNoteEntity>> = repository.allFieldNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _exportedReportText = MutableStateFlow<String?>(null)
    val exportedReportText: StateFlow<String?> = _exportedReportText.asStateFlow()

    // Athar Companion Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome",
                isUser = false,
                text = "مرحباً بكم في تطبيق «أثَر». أنا «رفيق أثَر» مرشدكم التراثي الذكي المستند حصرياً إلى «الموسوعة العُمانية». كيف يمكنني إرشادكم اليوم في استكشاف مساجد العباد أو دراسة المحرابين وتفاصيلهما المعمارية والصور الحقيقية؟",
                citation = "الموسوعة العُمانية"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Real Photo HUD overlay in 360 viewer
    private val _isLivePhotoHudOpen = MutableStateFlow(false)
    val isLivePhotoHudOpen: StateFlow<Boolean> = _isLivePhotoHudOpen.asStateFlow()

    init {
        // Pre-populate with initial field notes if DB is empty
        viewModelScope.launch {
            repository.allFieldNotes.collect { list ->
                if (list.isEmpty()) {
                    repository.insertFieldNote(
                        FieldNoteEntity(
                            monumentId = "nizwa_imams",
                            monumentName = "مساجد العباد في مقبرة الأئمة بنزوى",
                            researcherName = "أمجد بن أحمد الفرقاني",
                            gpsCoordinates = "22.9348° N, 57.5312° E",
                            architecturalElement = "المحراب الصاروجي والأعمدة المستطيلة",
                            measurements = "العمق: 1.4م، الارتفاع: 3.5م، 3 أعمدة مستطيلة، 7 مداخل",
                            buildingMaterials = "الصاروج العُماني، الحجارة المشذبة، خشب الكندل",
                            observations = "المحراب يتسم بعمق استثنائي يوفر ارتداداً صوتياً طبيعياً. الأعمدة المستطيلة تتوسط رواقين متوازيين متناسقين تماماً مع معطيات الموسوعة العُمانية.",
                            verificationStatus = "مسجل في الموسوعة العُمانية",
                            imageSourceAttribution = "فريق أثر — معاينة ميدانية"
                        )
                    )
                    repository.insertFieldNote(
                        FieldNoteEntity(
                            monumentId = "saal_site2",
                            monumentName = "مسجد العباد في سعال — الموقع الثاني",
                            researcherName = "ماجد الفرقاني والخليل السناني",
                            gpsCoordinates = "22.9215° N, 57.5455° E",
                            architecturalElement = "غرفة الوضوء ذات القبو المخروطي والبئر الملاصق",
                            measurements = "عمودان أسطوانيان من الصاروج، عقدان (نصف دائري وشبه مخروطي)، بروز ركني 4 سم، 6 مداخل",
                            buildingMaterials = "الصاروج الأملس، طين اللبن، صخور الوادي",
                            observations = "حوض الوضوء مكسو بخلطة صاروجية ناعمة تقاوم الرطوبة. البئر الشرقي ملاصق مباشرة للحوض في توزيع هندسي عبقري يحفظ الماء.",
                            verificationStatus = "مسجل في الموسوعة العُمانية",
                            imageSourceAttribution = "فريق أثر — استكشاف ميداني"
                        )
                    )
                }
            }
        }
    }

    fun navigateTo(screen: AtharScreen) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            _currentScreen.value = _screenHistory.removeAt(_screenHistory.lastIndex)
            return true
        }
        return false
    }

    fun selectMosque(mosque: Mosque) {
        _selectedMosque.value = mosque
        navigateTo(AtharScreen.MOSQUE_DETAIL)
    }

    fun selectMihrab(mihrab: MihrabStudy) {
        _selectedMihrab.value = mihrab
        navigateTo(AtharScreen.TWO_MIHRABS)
    }

    fun setMapRegionFilter(filter: String) {
        _mapRegionFilter.value = filter
    }

    // 360 Tour Controls
    fun selectViewpoint(station: ViewpointStation) {
        _activeViewpoint.value = station
        _sceneType.value = station.sceneType
        _selectedHotspot.value = null
        _guideStepIndex.value = 0
        _panOffset.value = Offset(station.defaultHeading * 5f, 0f)
    }

    fun setAmbientLighting(mode: AmbientLightingMode) {
        _ambientLighting.value = mode
    }

    fun toggleMotionGyro() {
        _isMotionGyroActive.value = !_isMotionGyroActive.value
    }

    fun toggleAmbientAudio() {
        _ambientAudioPlaying.value = !_ambientAudioPlaying.value
    }

    fun updateZoom(multiplier: Float) {
        val newScale = (_zoomScale.value * multiplier).coerceIn(0.7f, 2.8f)
        _zoomScale.value = newScale
    }

    fun toggleSceneType() {
        val next = when (_sceneType.value) {
            PanoramicSceneType.INTERIOR_SANCTUARY -> PanoramicSceneType.VIEW_FROM_MIHRAB
            PanoramicSceneType.VIEW_FROM_MIHRAB -> PanoramicSceneType.EXTERIOR_SAHN
            PanoramicSceneType.EXTERIOR_SAHN -> PanoramicSceneType.MIHRAB_MACRO_INSPECTION
            PanoramicSceneType.MIHRAB_MACRO_INSPECTION -> PanoramicSceneType.INTERIOR_SANCTUARY
        }
        val matchingStation = viewpointStations.find { it.sceneType == next } ?: viewpointStations.first()
        selectViewpoint(matchingStation)
    }

    fun toggleLivePhotoHud() {
        _isLivePhotoHudOpen.value = !_isLivePhotoHudOpen.value
    }

    fun walkToScene(target: PanoramicSceneType) {
        val matchingStation = viewpointStations.find { it.sceneType == target } ?: viewpointStations.first()
        selectViewpoint(matchingStation)
    }

    fun updatePan(dragDelta: Offset) {
        val current = _panOffset.value
        val newX = current.x + dragDelta.x
        val newY = (current.y + dragDelta.y).coerceIn(-400f, 400f)
        _panOffset.value = Offset(newX, newY)
    }

    fun reset360View() {
        _panOffset.value = Offset.Zero
        _zoomScale.value = 1.0f
        _selectedHotspot.value = null
        _isGuideModeActive.value = false
    }

    fun selectHotspot(hotspot: ArchitecturalHotspot?) {
        _selectedHotspot.value = hotspot
    }

    fun toggleGuideMode() {
        _isGuideModeActive.value = !_isGuideModeActive.value
        if (_isGuideModeActive.value) {
            _guideStepIndex.value = 0
            val currentHotspots = getActiveHotspots()
            if (currentHotspots.isNotEmpty()) {
                _selectedHotspot.value = currentHotspots[0]
            }
        } else {
            _selectedHotspot.value = null
        }
    }

    fun nextGuideStep() {
        val currentHotspots = getActiveHotspots()
        if (currentHotspots.isNotEmpty()) {
            val nextIndex = (_guideStepIndex.value + 1) % currentHotspots.size
            _guideStepIndex.value = nextIndex
            _selectedHotspot.value = currentHotspots[nextIndex]
        }
    }

    fun prevGuideStep() {
        val currentHotspots = getActiveHotspots()
        if (currentHotspots.isNotEmpty()) {
            val prevIndex = if (_guideStepIndex.value - 1 < 0) currentHotspots.size - 1 else _guideStepIndex.value - 1
            _guideStepIndex.value = prevIndex
            _selectedHotspot.value = currentHotspots[prevIndex]
        }
    }

    fun toggleAllLabels() {
        _isAllLabelsVisible.value = !_isAllLabelsVisible.value
    }

    fun toggleAutoRotate() {
        _isAutoRotating.value = !_isAutoRotating.value
    }

    fun toggleDimensionRuler() {
        _isDimensionRulerVisible.value = !_isDimensionRulerVisible.value
    }

    fun setAudioTrack(track: Int) {
        _selectedAudioTrack.value = track
    }

    fun jumpToHotspot(hotspot: ArchitecturalHotspot, containerWidth: Float) {
        _selectedHotspot.value = hotspot
        val panoWidth = containerWidth * 2.5f * _zoomScale.value
        val targetX = -(hotspot.xNormalized * panoWidth - containerWidth / 2f)
        _panOffset.value = Offset(targetX, (hotspot.yNormalized - 0.5f) * -200f)
    }

    fun getActiveHotspots(): List<ArchitecturalHotspot> {
        return repository.getHotspotsForScene(_sceneType.value)
    }

    // Field Notes Actions
    fun addFieldNote(
        monumentId: String,
        monumentName: String,
        researcherName: String,
        gps: String,
        element: String,
        measurements: String,
        materials: String,
        observations: String,
        status: String
    ) {
        viewModelScope.launch {
            val note = FieldNoteEntity(
                monumentId = monumentId,
                monumentName = monumentName,
                researcherName = researcherName.ifBlank { "باحث في فريق أثَر" },
                gpsCoordinates = gps,
                architecturalElement = element,
                measurements = measurements,
                buildingMaterials = materials,
                observations = observations,
                verificationStatus = status,
                imageSourceAttribution = "توثيق ميداني — فريق أثَر (الموسوعة العُمانية)"
            )
            repository.insertFieldNote(note)
        }
    }

    fun deleteFieldNote(note: FieldNoteEntity) {
        viewModelScope.launch {
            repository.deleteFieldNote(note)
        }
    }

    fun exportFieldNotesReport() {
        val notes = fieldNotes.value
        val sb = StringBuilder()
        sb.appendLine("═══════════════════════════════════════════════════════")
        sb.appendLine("تقرير التوثيق الميداني المعماري — مشروع «أثَر | ATHAR»")
        sb.appendLine("توثيق مساجد العباد ونفائس العمارة العُمانية")
        sb.appendLine("إعداد الطلبة: أمجد بن أحمد الفرقاني، ماجد بن أحمد الفرقاني، الخليل بن إبراهيم السناني")
        sb.appendLine("المرجع الأساسي المعتمد: «الموسوعة العُمانية» (وزارة التراث والثقافة)")
        sb.appendLine("تاريخ التصدير: ${java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
        sb.appendLine("═══════════════════════════════════════════════════════\n")

        notes.forEachIndexed { index, note ->
            sb.appendLine("【معلم #${index + 1}】: ${note.monumentName}")
            sb.appendLine("• الباحث الموثّق: ${note.researcherName}")
            sb.appendLine("• الإحداثيات: ${note.gpsCoordinates}")
            sb.appendLine("• العنصر المعماري: ${note.architecturalElement}")
            sb.appendLine("• القياسات: ${note.measurements}")
            sb.appendLine("• مواد البناء: ${note.buildingMaterials}")
            sb.appendLine("• الملاحظات الميدانية: ${note.observations}")
            sb.appendLine("• حالة التوثيق: ${note.verificationStatus}")
            sb.appendLine("───────────────────────────────────────────────────────")
        }
        _exportedReportText.value = sb.toString()
    }

    fun clearExportedReport() {
        _exportedReportText.value = null
    }

    // Athar Companion Actions
    fun sendUserMessage(question: String) {
        if (question.isBlank()) return
        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            isUser = true,
            text = question
        )
        _chatMessages.value = _chatMessages.value + userMsg

        val (answer, citation) = repository.answerAtharQuestion(question)
        val assistantMsg = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            isUser = false,
            text = answer,
            citation = citation
        )
        _chatMessages.value = _chatMessages.value + assistantMsg
    }
}

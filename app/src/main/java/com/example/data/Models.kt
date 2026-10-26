package com.example.data

enum class DocumentationStatus(val labelAr: String, val badgeColorHex: Long) {
    VERIFIED_ENCYCLOPEDIA("موثّق في الموسوعة العُمانية", 0xFF2A573F),
    ARCHITECTURAL_LANDMARK("معلم معماري نادر", 0xFF8D6E18)
}

enum class PanoramicSceneType(val titleAr: String, val subtitleAr: String) {
    INTERIOR_SANCTUARY("وسط قاعة الصلاة والأروقة", "مواجهة المحراب وعمودي الصاروج"),
    VIEW_FROM_MIHRAB("من داخل تجويف المحراب", "إطلالة على الرواقين والمداخل السبعة"),
    EXTERIOR_SAHN("الصرح الخارجي وبئر الوضوء", "القبو شبه المخروطي والنتوء الصخري"),
    MIHRAB_MACRO_INSPECTION("المجهر الجصي والخزف الصيني", "فحص الأختام الـ 30 والكتابة الكوفية")
}

enum class AmbientLightingMode(val titleAr: String, val overlayAlpha: Float, val tintColorHex: Long) {
    DAYLIGHT("ضوء النهار الشمسي", 0.0f, 0x00000000),
    GOLDEN_HOUR("العصر الذهبي الدافئ", 0.25f, 0x40D69530),
    SPIRITUAL_NIGHT("الليل والتهجد والسكينة", 0.55f, 0x900D1525)
}

data class ViewpointStation(
    val id: String,
    val nameAr: String,
    val descriptionAr: String,
    val sceneType: PanoramicSceneType,
    val radarPositionX: Float, // Normalized on minimap (0.0 to 1.0)
    val radarPositionY: Float,
    val defaultHeading: Float
)

data class CitationRef(
    val title: String,
    val sourceVolume: String,
    val pageOrEntry: String,
    val publisher: String = "الموسوعة العُمانية — وزارة التراث والثقافة",
    val notes: String = ""
)

data class ArchitecturalHotspot(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val descriptionAr: String,
    val xNormalized: Float, // 0.0f to 1.0f (horizontal position in panorama)
    val yNormalized: Float, // 0.0f to 1.0f (vertical position in panorama)
    val architecturalTag: String,
    val encyclopediaReference: String
)

data class Mosque(
    val id: String,
    val name: String,
    val shortTitle: String,
    val alternativeNames: List<String>,
    val governorate: String,
    val wilayah: String,
    val villageOrArea: String,
    val latitude: Double,
    val longitude: Double,
    val coordinatesVerified: Boolean = true,
    val directionsGuide: String,
    val roadAccessibility: String,
    val estimatedDriveTime: String,
    val approximateAreaM2: String,
    val historicalOverview: String,
    val architecturalDescription: String,
    val buildingMaterials: List<String>,
    val pillarsDescription: String,
    val pillarsCount: Int,
    val archesDescription: String,
    val entrancesCount: Int,
    val mihrabDescription: String,
    val mihrabDepthMeters: Double?,
    val mihrabHeightMeters: Double?,
    val ceilingDescription: String,
    val facilitiesDescription: String,
    val documentationStatus: DocumentationStatus = DocumentationStatus.VERIFIED_ENCYCLOPEDIA,
    val keyFeatures: List<String>,
    val citationSources: List<CitationRef>,
    val imageDrawableRes: Int? = null,
    val imageAttribution: String? = null,
    val photos: List<Int> = emptyList(),
    val photoCaptions: List<String> = emptyList()
)

data class MihrabStudy(
    val id: String,
    val mosqueName: String,
    val location: String,
    val wilayat: String,
    val hijriYear: String,
    val gregorianYear: String,
    val builderOrMaster: String,
    val widthMeters: String,
    val heightMeters: String,
    val crownDescription: String,
    val calligraphyStyle: String,
    val inscriptionsTranscribed: String,
    val inscriptionVerificationStatus: String,
    val decorativeSealsCount: String,
    val porcelainPlatesCount: String,
    val materialDetails: String,
    val nationalMuseumNote: String,
    val sharedFeaturesWithUbbad: String,
    val uniqueHighlights: String,
    val directionsGuide: String,
    val roadAccessibility: String,
    val estimatedDriveTime: String,
    val visitingAdvice: String,
    val citationRef: CitationRef,
    val photoDrawableRes: Int? = null,
    val photos: List<Int> = emptyList(),
    val photoCaptions: List<String> = emptyList(),
    val latitude: Double,
    val longitude: Double
)

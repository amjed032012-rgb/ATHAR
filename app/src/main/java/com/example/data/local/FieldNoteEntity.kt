package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_notes")
data class FieldNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monumentId: String,
    val monumentName: String,
    val researcherName: String,
    val visitDate: Long = System.currentTimeMillis(),
    val gpsCoordinates: String,
    val architecturalElement: String,
    val measurements: String,
    val buildingMaterials: String,
    val observations: String,
    val verificationStatus: String, // "موثق ميدانياً" / "مسودة أولية" / "يحتاج تدقيق"
    val imageSourceAttribution: String = "توثيق ميداني — فريق أثر",
    val isApproved: Boolean = true
)

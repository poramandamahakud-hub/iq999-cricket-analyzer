package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_audits")
data class MatchAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teamA: String,
    val teamB: String,
    val pitchType: String,
    val matchFormat: String,
    val winner: String,
    val accuracyIndex: String,
    val jsonOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)

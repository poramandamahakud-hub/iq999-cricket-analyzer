package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiCricketEngine
import com.example.data.PreloadedMatchEngine
import com.example.data.TeamPresets
import com.example.data.local.AppDatabase
import com.example.data.local.MatchAuditEntity
import com.example.model.CricketTeamPreset
import com.example.model.MatchAuditResult
import com.example.model.MatchConditions
import com.example.model.MatchFormat
import com.example.model.PitchType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class EngineState {
    object Idle : EngineState()
    object Analyzing : EngineState()
    data class Success(val result: MatchAuditResult, val rawJson: String, val conditions: MatchConditions) : EngineState()
    data class Error(val message: String) : EngineState()
}

class CricketViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.matchAuditDao()

    val savedAudits: StateFlow<List<MatchAuditEntity>> = dao.getAllAudits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Form inputs
    val teamAInput = MutableStateFlow("India")
    val teamBInput = MutableStateFlow("Australia")
    val selectedPitch = MutableStateFlow(PitchType.GREEN_SEAMER)
    val selectedFormat = MutableStateFlow(MatchFormat.T20I)
    val weatherInput = MutableStateFlow("Humid & Overcast (Early Swing)")
    val customNotesInput = MutableStateFlow("")
    val useAiEngine = MutableStateFlow(GeminiCricketEngine.isApiKeyAvailable())

    // Engine output state
    private val _engineState = MutableStateFlow<EngineState>(EngineState.Idle)
    val engineState: StateFlow<EngineState> = _engineState.asStateFlow()

    init {
        // Run initial default match audit on launch
        runMatchAudit()
    }

    fun selectPresetForTeamA(preset: CricketTeamPreset) {
        teamAInput.value = preset.name
    }

    fun selectPresetForTeamB(preset: CricketTeamPreset) {
        teamBInput.value = preset.name
    }

    fun swapTeams() {
        val temp = teamAInput.value
        teamAInput.value = teamBInput.value
        teamBInput.value = temp
    }

    fun runMatchAudit() {
        viewModelScope.launch {
            _engineState.value = EngineState.Analyzing

            val conditions = MatchConditions(
                teamA = teamAInput.value.ifBlank { "Team A" },
                teamB = teamBInput.value.ifBlank { "Team B" },
                pitchType = selectedPitch.value,
                format = selectedFormat.value,
                weather = weatherInput.value.ifBlank { "Standard Conditions" },
                customNotes = customNotesInput.value
            )

            try {
                val auditResult: MatchAuditResult = if (useAiEngine.value && GeminiCricketEngine.isApiKeyAvailable()) {
                    GeminiCricketEngine.analyzeMatch(conditions)
                } else {
                    PreloadedMatchEngine.computeMatchAudit(conditions)
                }

                val jsonOutput = PreloadedMatchEngine.toJsonString(auditResult)
                _engineState.value = EngineState.Success(auditResult, jsonOutput, conditions)

                // Save to database
                dao.insertAudit(
                    MatchAuditEntity(
                        teamA = conditions.teamA,
                        teamB = conditions.teamB,
                        pitchType = conditions.pitchType.displayName,
                        matchFormat = conditions.format.displayName,
                        winner = auditResult.winnerName,
                        accuracyIndex = auditResult.winAccuracy,
                        jsonOutput = jsonOutput
                    )
                )

            } catch (e: Exception) {
                _engineState.value = EngineState.Error("Analysis failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun loadSavedAudit(auditEntity: MatchAuditEntity) {
        val parsed = PreloadedMatchEngine.parseJsonString(auditEntity.jsonOutput)
        if (parsed != null) {
            val pitch = PitchType.values().find { it.displayName.equals(auditEntity.pitchType, ignoreCase = true) } ?: PitchType.GREEN_SEAMER
            val format = MatchFormat.values().find { it.displayName.equals(auditEntity.matchFormat, ignoreCase = true) } ?: MatchFormat.T20I

            val cond = MatchConditions(
                teamA = auditEntity.teamA,
                teamB = auditEntity.teamB,
                pitchType = pitch,
                format = format,
                weather = "Saved Historical Audit"
            )

            teamAInput.value = auditEntity.teamA
            teamBInput.value = auditEntity.teamB
            selectedPitch.value = pitch
            selectedFormat.value = format

            _engineState.value = EngineState.Success(parsed, auditEntity.jsonOutput, cond)
        }
    }

    fun deleteSavedAudit(id: Long) {
        viewModelScope.launch {
            dao.deleteAuditById(id)
        }
    }

    fun clearAllSavedAudits() {
        viewModelScope.launch {
            dao.clearAllAudits()
        }
    }
}

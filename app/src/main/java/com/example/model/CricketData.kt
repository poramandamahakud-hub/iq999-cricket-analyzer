package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 11-vs-11 Deterministic Cricket Exploit Engine Data Structures
 */

@JsonClass(generateAdapter = true)
data class PlayerMatchup(
    @Json(name = "batsman_name") val batsmanName: String,
    @Json(name = "fatal_weakness") val fatalWeakness: String,
    @Json(name = "lethal_bowler_threat") val lethalBowlerThreat: String,
    @Json(name = "likely_dismissal_mode") val likelyDismissalMode: String,
    @Json(name = "projected_survival_balls") val projectedSurvivalBalls: String,
    @Json(name = "projected_runs") val projectedRuns: String,
    @Json(name = "wicket_risk_tier") val wicketRiskTier: String
)

@JsonClass(generateAdapter = true)
data class CumulativeMatchCalculation(
    @Json(name = "team_a_total_projected_runs") val teamATotalProjectedRuns: String,
    @Json(name = "team_b_total_projected_runs") val teamBTotalProjectedRuns: String,
    @Json(name = "dominant_winner") val dominantWinner: String,
    @Json(name = "decisive_reason") val decisiveReason: String
)

@JsonClass(generateAdapter = true)
data class MatchVerdict(
    @Json(name = "predicted_winner") val predictedWinner: String = "",
    @Json(name = "win_accuracy_index") val winAccuracyIndex: String = "88%",
    @Json(name = "dominant_edge_summary") val dominantEdgeSummary: String = ""
)

@JsonClass(generateAdapter = true)
data class TeamAudit(
    @Json(name = "team_name") val teamName: String = "",
    @Json(name = "fatal_flaws_a_to_z") val fatalFlawsAToZ: List<String> = emptyList(),
    @Json(name = "vulnerability_score") val vulnerabilityScore: String = "High"
)

@JsonClass(generateAdapter = true)
data class FantasyPlayerItem(
    @Json(name = "player_name") val playerName: String,
    @Json(name = "team") val team: String,
    @Json(name = "role") val role: String,
    @Json(name = "projected_points") val projectedPoints: String,
    @Json(name = "matchup_reason") val matchupReason: String
)

@JsonClass(generateAdapter = true)
data class DreamTeamSelection(
    @Json(name = "captain") val captain: String = "",
    @Json(name = "vice_captain") val viceCaptain: String = "",
    @Json(name = "selected_11_players") val selected11Players: List<FantasyPlayerItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class IndividualPlayerProjection(
    @Json(name = "name") val name: String,
    @Json(name = "projected_runs") val projectedRuns: String,
    @Json(name = "projected_wickets") val projectedWickets: String,
    @Json(name = "primary_threat_bowler") val primaryThreatBowler: String
)

@JsonClass(generateAdapter = true)
data class PlayerProjections(
    @Json(name = "team_a") val teamA: List<IndividualPlayerProjection> = emptyList(),
    @Json(name = "team_b") val teamB: List<IndividualPlayerProjection> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MatchAuditResult(
    @Json(name = "team_a_batting_vs_team_b_bowling") val teamABattingVsTeamBBowling: List<PlayerMatchup> = emptyList(),
    @Json(name = "team_b_batting_vs_team_a_bowling") val teamBBattingVsTeamABowling: List<PlayerMatchup> = emptyList(),
    @Json(name = "cumulative_match_calculation") val cumulativeMatchCalculation: CumulativeMatchCalculation? = null,
    @Json(name = "dream_team_selection") val dreamTeamSelection: DreamTeamSelection? = null,
    @Json(name = "player_projections") val playerProjections: PlayerProjections? = null,
    
    // Optional legacy fields
    @Json(name = "match_verdict") val matchVerdict: MatchVerdict? = null,
    @Json(name = "team_a_audit") val teamAAudit: TeamAudit? = null,
    @Json(name = "team_b_audit") val teamBAudit: TeamAudit? = null,
    @Json(name = "decisive_head_to_head_mismatch") val decisiveHeadToHeadMismatch: List<String>? = null
) {
    val winnerName: String
        get() = cumulativeMatchCalculation?.dominantWinner?.ifBlank { null }
            ?: matchVerdict?.predictedWinner?.ifBlank { null }
            ?: "Undefined Winner"

    val winnerReason: String
        get() = cumulativeMatchCalculation?.decisiveReason?.ifBlank { null }
            ?: matchVerdict?.dominantEdgeSummary?.ifBlank { null }
            ?: "Tactical imbalance between bowling attack and batsman vulnerabilities."

    val winAccuracy: String
        get() = matchVerdict?.winAccuracyIndex ?: "89%"
}

data class CricketTeamPreset(
    val id: String,
    val name: String,
    val shortCode: String,
    val category: String, // "International", "T20 League"
    val primaryColorHex: String,
    val keyBatters: List<String>,
    val keyBowlers: List<String>,
    val defaultStrengths: List<String>,
    val defaultWeaknesses: List<String>
)

enum class PitchType(val displayName: String, val description: String) {
    GREEN_SEAMER("Green Seamer", "High lateral movement, early seam & swing"),
    DUSTBOWL("Dustbowl Spin", "Excessive turn, low bounce, spin friendly"),
    FLAT_HIGHWAY("Flat Highway", "High boundary concession, batter friendly"),
    SLOW_LOW("Slow & Low Deck", "Sticky surface, difficult boundary hitting")
}

enum class MatchFormat(val displayName: String) {
    T20I("T20 International"),
    IPL_T20("Franchise T20"),
    ODI("One Day International (50 Overs)"),
    TEST("Test Match (5 Days)")
}

data class MatchConditions(
    val teamA: String,
    val teamB: String,
    val pitchType: PitchType = PitchType.GREEN_SEAMER,
    val format: MatchFormat = MatchFormat.T20I,
    val weather: String = "Humid & Overcast (Early Swing)",
    val customNotes: String = ""
)


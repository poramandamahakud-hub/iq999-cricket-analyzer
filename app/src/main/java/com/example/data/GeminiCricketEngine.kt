package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.MatchAuditResult
import com.example.model.MatchConditions
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiCricketEngine {

    private const val TAG = "GeminiCricketEngine"
    private const val MODEL_NAME = "gemini-3.6-flash"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(90, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val matchAuditAdapter = moshi.adapter(MatchAuditResult::class.java)

    fun isApiKeyAvailable(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun analyzeMatch(conditions: MatchConditions): MatchAuditResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key found, using local preloaded engine.")
            return@withContext PreloadedMatchEngine.computeMatchAudit(conditions)
        }

        val promptText = """
            You are the IQ999+ "11-vs-11 Deterministic Cricket Exploit Engine". Analyze all 11 players of Team A (${conditions.teamA}) against all bowlers of Team B (${conditions.teamB}) and vice versa, mapping out exact dismissal patterns, run limits, and survival expectations.

            Match Details:
            - Team A: ${conditions.teamA}
            - Team B: ${conditions.teamB}
            - Pitch Conditions: ${conditions.pitchType.displayName} (${conditions.pitchType.description})
            - Match Format: ${conditions.format.displayName}
            - Weather/Atmosphere: ${conditions.weather}
            - Context: ${conditions.customNotes}

            ANALYSIS PROTOCOL PER BATSMAN (ALL 11 PLAYERS FOR BOTH TEAMS):
            1. Primary Fatal Flaw: The exact ball type (e.g., Left-arm inswinger, wide leg-cutter, googly) and line/length that causes their dismissal.
            2. Bowler Counter-Matchup: Cross-reference this flaw with opposition bowlers. Identify which specific bowler has highest probability of triggering this dismissal.
            3. Survival Ceiling & Projected Run Limit: Estimate balls faced before fatal error and expected individual score.
            4. Dismissal Type Confirmation: LBW/Bowled, Caught behind, Caught at deep boundary trap, or Stumped.

            ACCURACY OUTPUT RULE:
            - Consolidate all 11 player breakdowns for both teams.
            - Calculate total projected team runs range, dominant winner, and decisive tactical reason.
            - Output STRICTLY in valid, pure JSON without markdown codeblocks or extra text.

            JSON FORMAT SCHEMA:
            {
              "team_a_batting_vs_team_b_bowling": [
                {
                  "batsman_name": "Player Name",
                  "fatal_weakness": "Exact line, length, ball movement trap",
                  "lethal_bowler_threat": "Opposition Bowler Name",
                  "likely_dismissal_mode": "Bowled / LBW / Edge to Slip / Boundary Catch",
                  "projected_survival_balls": "XX - XX balls",
                  "projected_runs": "XX - XX runs",
                  "wicket_risk_tier": "CRITICAL (80%+) / MODERATE / LOW"
                }
              ],
              "team_b_batting_vs_team_a_bowling": [
                {
                  "batsman_name": "Player Name",
                  "fatal_weakness": "Exact line, length, ball movement trap",
                  "lethal_bowler_threat": "Opposition Bowler Name",
                  "likely_dismissal_mode": "Bowled / LBW / Edge to Slip / Boundary Catch",
                  "projected_survival_balls": "XX - XX balls",
                  "projected_runs": "XX - XX runs",
                  "wicket_risk_tier": "CRITICAL (80%+) / MODERATE / LOW"
                }
              ],
              "cumulative_match_calculation": {
                "team_a_total_projected_runs": "XXX - XXX",
                "team_b_total_projected_runs": "XXX - XXX",
                "dominant_winner": "${conditions.teamA} or ${conditions.teamB}",
                "decisive_reason": "Clear tactical explanation of which bowling attack systematically dismantles the other's 11 players"
              },
              "dream_team_selection": {
                "captain": "Player Name (Reason)",
                "vice_captain": "Player Name (Reason)",
                "selected_11_players": [
                  {
                    "player_name": "Player Name",
                    "team": "${conditions.teamA} / ${conditions.teamB}",
                    "role": "WK / BAT / AR / BOWL",
                    "projected_points": "XX pts",
                    "matchup_reason": "Matchup & expected performance ceiling reason"
                  }
                ]
              },
              "player_projections": {
                "team_a": [
                  {
                    "name": "Player Name",
                    "projected_runs": "XX-XX",
                    "projected_wickets": "X",
                    "primary_threat_bowler": "Opponent Bowler Name"
                  }
                ],
                "team_b": [
                  {
                    "name": "Player Name",
                    "projected_runs": "XX-XX",
                    "projected_wickets": "X",
                    "primary_threat_bowler": "Opponent Bowler Name"
                  }
                ]
              }
            }
        """.trimIndent()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", promptText))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        var attempts = 0
        val maxAttempts = 3
        while (attempts < maxAttempts) {
            attempts++
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                if (response.code == 503 || response.code == 429) {
                    Log.w(TAG, "Gemini API high demand ${response.code} (Attempt $attempts/$maxAttempts)")
                    if (attempts < maxAttempts) {
                        kotlinx.coroutines.delay(1000L * attempts)
                        continue
                    }
                }

                if (!response.isSuccessful || responseString.isBlank()) {
                    Log.e(TAG, "Gemini API error ${response.code}: $responseString")
                    return@withContext PreloadedMatchEngine.computeMatchAudit(conditions)
                }

                val responseObj = JSONObject(responseString)
                val candidates = responseObj.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val rawJsonText = parts?.optJSONObject(0)?.optString("text") ?: ""

                val cleanedJson = rawJsonText
                    .trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val parsedResult = matchAuditAdapter.fromJson(cleanedJson)
                return@withContext parsedResult ?: PreloadedMatchEngine.computeMatchAudit(conditions)

            } catch (e: Exception) {
                Log.e(TAG, "Exception calling Gemini API (Attempt $attempts/$maxAttempts): ${e.message}", e)
                if (attempts < maxAttempts) {
                    kotlinx.coroutines.delay(1000L * attempts)
                }
            }
        }

        return@withContext PreloadedMatchEngine.computeMatchAudit(conditions)
    }
}

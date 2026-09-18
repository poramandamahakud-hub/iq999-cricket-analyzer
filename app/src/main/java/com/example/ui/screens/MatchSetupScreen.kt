package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TeamPresets
import com.example.data.GeminiCricketEngine
import com.example.model.CricketTeamPreset
import com.example.model.MatchFormat
import com.example.model.PitchType
import com.example.ui.CricketViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchSetupScreen(
    viewModel: CricketViewModel,
    onRunEngine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val teamA by viewModel.teamAInput.collectAsState()
    val teamB by viewModel.teamBInput.collectAsState()
    val selectedPitch by viewModel.selectedPitch.collectAsState()
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val weather by viewModel.weatherInput.collectAsState()
    val customNotes by viewModel.customNotesInput.collectAsState()
    val useAiEngine by viewModel.useAiEngine.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Engine Header with IQ999+ Match Intelligence & Tabs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CyanPrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Engine Icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "IQ999+ MATCH INTELLIGENCE",
                            color = CyanPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Live Matches & 11-vs-11 Dream Team",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                var selectedMatchTab by remember { mutableStateOf(0) } // 0 = Live, 1 = Upcoming, 2 = History
                TabRow(
                    selectedTabIndex = selectedMatchTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    containerColor = CyberSurfaceVariant,
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedMatchTab == 0,
                        onClick = { selectedMatchTab = 0 },
                        text = {
                            Text(
                                "🔴 LIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMatchTab == 0) Color.Red else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedMatchTab == 1,
                        onClick = { selectedMatchTab = 1 },
                        text = {
                            Text(
                                "UPCOMING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMatchTab == 1) CyanPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedMatchTab == 2,
                        onClick = { selectedMatchTab = 2 },
                        text = {
                            Text(
                                "HISTORY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMatchTab == 2) AmberWarning else TextMuted
                            )
                        }
                    )
                }

                // Match Cards based on selected tab
                val matchesForTab = when (selectedMatchTab) {
                    0 -> listOf(
                        MatchCardData("IND", "AUS", "LINEUP OUT", "Wankhede, Mumbai"),
                        MatchCardData("ENG", "SA", "INNINGS BREAK", "Kensington, Barbados"),
                        MatchCardData("IND", "PAK", "LIVE - 14.2 OVS", "R. Premadasa, Colombo")
                    )
                    1 -> listOf(
                        MatchCardData("NZ", "WI", "TODAY 19:30 IST", "Eden Park, Auckland"),
                        MatchCardData("SA", "AUS", "TOMORROW 18:00 IST", "Wanderers, Jo'burg"),
                        MatchCardData("ENG", "IND", "SEP 20 15:30 IST", "The Oval, London")
                    )
                    else -> listOf(
                        MatchCardData("IND", "SA", "FINAL - RESULT", "Kensington, Barbados"),
                        MatchCardData("AUS", "ENG", "RESULT - AUS WON", "MCG, Melbourne"),
                        MatchCardData("PAK", "NZ", "RESULT - NZ WON", "Gaddafi, Lahore")
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    matchesForTab.forEach { match ->
                        LiveMatchCard(
                            match = match,
                            onSelectMatch = {
                                viewModel.teamAInput.value = match.teamA
                                viewModel.teamBInput.value = match.teamB
                                viewModel.runMatchAudit()
                                onRunEngine()
                            }
                        )
                    }
                }

                HorizontalDivider(color = CyberSurfaceBorder, modifier = Modifier.padding(vertical = 2.dp))

                // AI Engine Indicator / Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Mode",
                            tint = if (useAiEngine) AmberWarning else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = if (useAiEngine) "Gemini Flash AI Active" else "Local Analytical Engine Active",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (useAiEngine) "Real-time LLM A-to-Z Flaw Audit" else "High-precision rule engine",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Switch(
                        checked = useAiEngine,
                        onCheckedChange = { viewModel.useAiEngine.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CyanPrimary
                        )
                    )
                }
            }
        }

        // Teams Selection Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SELECT MATCH CONTENDERS",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Team A Input + Presets
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "TEAM A", color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = teamA,
                        onValueChange = { viewModel.teamAInput.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team_a_input"),
                        placeholder = { Text("e.g. India", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Presets for Team A
                    Text(text = "Quick Select Team A:", color = TextMuted, fontSize = 11.sp)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(TeamPresets.PRESET_TEAMS) { preset ->
                            FilterChip(
                                selected = teamA.equals(preset.name, ignoreCase = true),
                                onClick = { viewModel.selectPresetForTeamA(preset) },
                                label = { Text(preset.shortCode, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanPrimaryContainer,
                                    selectedLabelColor = Color.White,
                                    containerColor = CyberSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }

                // Swap Button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.swapTeams() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(CyberSurfaceVariant, CircleShape)
                            .border(1.dp, CyberSurfaceBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Teams",
                            tint = CyanPrimary
                        )
                    }
                }

                // Team B Input + Presets
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "TEAM B", color = AmberWarning, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = teamB,
                        onValueChange = { viewModel.teamBInput.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team_b_input"),
                        placeholder = { Text("e.g. Australia", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberWarning,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Presets for Team B
                    Text(text = "Quick Select Team B:", color = TextMuted, fontSize = 11.sp)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(TeamPresets.PRESET_TEAMS) { preset ->
                            FilterChip(
                                selected = teamB.equals(preset.name, ignoreCase = true),
                                onClick = { viewModel.selectPresetForTeamB(preset) },
                                label = { Text(preset.shortCode, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberWarning,
                                    selectedLabelColor = Color.Black,
                                    containerColor = CyberSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Pitch & Environmental Conditions Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "PITCH & ENVIRONMENTAL MATCH CONDITIONS",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Pitch Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Pitch Type:", color = TextSecondary, fontSize = 12.sp)
                    PitchType.values().forEach { pitch ->
                        val isSelected = selectedPitch == pitch
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectedPitch.value = pitch },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CyanPrimaryContainer.copy(alpha = 0.3f) else CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyanPrimary else CyberSurfaceBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = pitch.displayName,
                                    tint = if (isSelected) CyanPrimary else TextMuted
                                )
                                Column {
                                    Text(
                                        text = pitch.displayName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = pitch.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Match Format Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Match Format:", color = TextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MatchFormat.values().forEach { fmt ->
                            val isSelected = selectedFormat == fmt
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectedFormat.value = fmt },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CyanPrimary else CyberSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = fmt.name,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Atmosphere / Weather Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Overcast / Atmosphere Factor:", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = weather,
                        onValueChange = { viewModel.weatherInput.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Primary Action Button
        Button(
            onClick = {
                viewModel.runMatchAudit()
                onRunEngine()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("run_exploit_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Run",
                    tint = Color.White
                )
                Text(
                    text = "RUN EXPLOIT & FLAW ENGINE",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class MatchCardData(
    val teamA: String,
    val teamB: String,
    val statusBadge: String,
    val venue: String
)

@Composable
fun LiveMatchCard(
    match: MatchCardData,
    onSelectMatch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF161B22),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Match Status & Venue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1F6FEB)
                ) {
                    Text(
                        text = match.statusBadge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = match.venue,
                    color = Color(0xFF8B949E),
                    fontSize = 11.sp
                )
            }

            // Teams & Live Score / VS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.teamA,
                    color = Color(0xFFF0F6FC),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "VS",
                    color = Color(0xFF8B949E),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Text(
                    text = match.teamB,
                    color = Color(0xFFF0F6FC),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            // Dynamic Action Button: Generate Dream Team via AI Studio
            Button(
                onClick = onSelectMatch,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238636)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "⚡ 11-vs-11 SCAN & DREAM TEAM",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

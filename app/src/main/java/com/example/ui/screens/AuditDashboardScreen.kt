package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.model.PlayerMatchup
import com.example.ui.CricketViewModel
import com.example.ui.EngineState
import com.example.ui.theme.*

@Composable
fun AuditDashboardScreen(
    viewModel: CricketViewModel,
    onNavigateToJson: () -> Unit,
    modifier: Modifier = Modifier
) {
    val engineState by viewModel.engineState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    when (val state = engineState) {
        is EngineState.Idle -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(CyberBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = "Idle",
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Text("No Match Dissection Loaded", color = TextSecondary, fontSize = 16.sp)
                    Text("Select teams in Setup tab and run engine", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
        is EngineState.Analyzing -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(CyberBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = CyanPrimary,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "DISSECTING 11-VS-11 MATCHUPS...",
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Auditing all 11 players for exact traps, run limits, and survival ceilings",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
        is EngineState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(CyberBackground)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CyberSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonVulnerability)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = CrimsonVulnerability,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Dissection Engine Error", color = CrimsonVulnerability, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(state.message, color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        is EngineState.Success -> {
            val audit = state.result
            val cumulative = audit.cumulativeMatchCalculation
            val rawJson = state.rawJson

            var selectedMainTab by remember { mutableStateOf(0) } // 0 = 11-vs-11 Traps, 1 = Fantasy Dream 11
            var selectedBattingTeamTab by remember { mutableStateOf(0) } // 0 = Team A Batting, 1 = Team B Batting
            val scrollState = rememberScrollState()

            val teamAPlayers = audit.teamABattingVsTeamBBowling
            val teamBPlayers = audit.teamBBattingVsTeamABowling
            val dreamTeam = audit.dreamTeamSelection

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(CyberBackground)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. MATCH VERDICT & CUMULATIVE RUNS BANNER
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = CyberSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(EmeraldSuccess, CircleShape)
                                )
                                Text(
                                    text = "DOMINANT MATCH WINNER",
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Accuracy Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmeraldContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Accuracy",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "ACCURACY: ${audit.winAccuracy}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Winner Name Display
                        Text(
                            text = audit.winnerName.uppercase(),
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            letterSpacing = 0.5.sp
                        )

                        // Cumulative Run Projections Bar
                        if (cumulative != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyberSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "TEAM A PROJECTED RUNS",
                                            color = TextMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = cumulative.teamATotalProjectedRuns,
                                            color = CyanPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier
                                            .height(30.dp)
                                            .width(1.dp),
                                        color = CyberSurfaceBorder
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "TEAM B PROJECTED RUNS",
                                            color = TextMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = cumulative.teamBTotalProjectedRuns,
                                            color = AmberWarning,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = CyberSurfaceBorder)

                        // Decisive Reason Summary
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DECISIVE TACTICAL DISMANTLING REASON:",
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "\"${audit.winnerReason}\"",
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }

                        // Quick JSON Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberSurfaceVariant)
                                .clickable { onNavigateToJson() }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "JSON",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "11-vs-11 Pure JSON Output Generated",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "View JSON",
                                    color = CyanPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // MAIN ENGINE MODE TAB SELECTOR
                TabRow(
                    selectedTabIndex = selectedMainTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    containerColor = CyberSurface,
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedMainTab == 0,
                        onClick = { selectedMainTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, tint = if (selectedMainTab == 0) CyanPrimary else TextMuted, modifier = Modifier.size(18.dp))
                                Text("11-vs-11 Wicket Traps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedMainTab == 0) CyanPrimary else TextMuted)
                            }
                        }
                    )
                    Tab(
                        selected = selectedMainTab == 1,
                        onClick = { selectedMainTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = if (selectedMainTab == 1) AmberWarning else TextMuted, modifier = Modifier.size(18.dp))
                                Text("Fantasy Dream 11", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedMainTab == 1) AmberWarning else TextMuted)
                            }
                        }
                    )
                }

                if (selectedMainTab == 0) {
                    // 2. 11-VS-11 PLAYER DISSECTION MATRIX
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CyberSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "11-VS-11 PLAYER DISSECTION",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Dismissal Traps, Survival & Run Limits",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Toggle Tabs
                            TabRow(
                                selectedTabIndex = selectedBattingTeamTab,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                containerColor = CyberSurfaceVariant,
                                indicator = {},
                                divider = {}
                            ) {
                                Tab(
                                    selected = selectedBattingTeamTab == 0,
                                    onClick = { selectedBattingTeamTab = 0 },
                                    text = {
                                        Text(
                                            text = "Team A Batting (${teamAPlayers.size})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedBattingTeamTab == 0) CyanPrimary else TextMuted
                                        )
                                    }
                                )
                                Tab(
                                    selected = selectedBattingTeamTab == 1,
                                    onClick = { selectedBattingTeamTab = 1 },
                                    text = {
                                        Text(
                                            text = "Team B Batting (${teamBPlayers.size})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedBattingTeamTab == 1) AmberWarning else TextMuted
                                        )
                                    }
                                )
                            }

                            val activeList = if (selectedBattingTeamTab == 0) teamAPlayers else teamBPlayers
                            val teamAccent = if (selectedBattingTeamTab == 0) CyanPrimary else AmberWarning

                            if (activeList.isEmpty()) {
                                Text(
                                    text = "No player breakdowns loaded for this team.",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            } else {
                                activeList.forEachIndexed { index, player ->
                                    PlayerMatchupCard(
                                        index = index + 1,
                                        player = player,
                                        accentColor = teamAccent
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // 3. FANTASY DREAM 11 EXPLOIT ENGINE
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CyberSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "OPTIMAL FANTASY DREAM 11 TEAM",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Role-Balancing: 1-4 WK | 3-6 BAT | 1-4 AR | 3-6 BOWL",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (dreamTeam != null) {
                                // Captain Card
                                if (dreamTeam.captain.isNotBlank()) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = AmberWarning.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(AmberWarning, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("C", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                            }
                                            Column {
                                                Text("CAPTAIN (2x POINTS CEILING)", color = AmberWarning, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                                                Text(dreamTeam.captain, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }

                                // Vice Captain Card
                                if (dreamTeam.viceCaptain.isNotBlank()) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = CyanPrimary.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(CyanPrimary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("VC", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                            }
                                            Column {
                                                Text("VICE-CAPTAIN (1.5x POINTS CEILING)", color = CyanPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                                                Text(dreamTeam.viceCaptain, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = CyberSurfaceBorder)

                                // Selected 11 Players
                                dreamTeam.selected11Players.forEachIndexed { idx, player ->
                                    FantasyPlayerCard(index = idx + 1, player = player)
                                }
                            } else {
                                Text("Fantasy projections parsing...", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 4. ACTION BAR (Copy JSON & Share Report)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(rawJson))
                            Toast.makeText(context, "11-vs-11 Pure JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("copy_json_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanPrimary)
                            Text("COPY PURE JSON", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { onNavigateToJson() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DataObject, contentDescription = "Raw", tint = Color.White)
                            Text("VIEW PURE JSON", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PlayerMatchupCard(
    index: Int,
    player: PlayerMatchup,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CyberSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Player Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(accentColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = player.batsmanName,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                // Risk Tier Badge
                val isCritical = player.wicketRiskTier.contains("CRITICAL", ignoreCase = true)
                val isModerate = player.wicketRiskTier.contains("MODERATE", ignoreCase = true)
                val badgeBg = when {
                    isCritical -> CrimsonContainer
                    isModerate -> AmberWarning.copy(alpha = 0.25f)
                    else -> EmeraldContainer
                }
                val badgeFg = when {
                    isCritical -> CrimsonVulnerability
                    isModerate -> AmberWarning
                    else -> EmeraldSuccess
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = player.wicketRiskTier,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = CyberSurfaceBorder)

            // Lethal Bowler Threat
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsCricket,
                    contentDescription = "Bowler Threat",
                    tint = CrimsonVulnerability,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Lethal Bowler Threat: ",
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = player.lethalBowlerThreat,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Fatal Weakness Trap
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Dangerous,
                    contentDescription = "Fatal Weakness",
                    tint = AmberWarning,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Column {
                    Text(
                        text = "Fatal Weakness Trap:",
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = player.fatalWeakness,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Dismissal Mode & Projections Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberBackground)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DISMISSAL MODE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = player.likelyDismissalMode,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SURVIVAL CEILING",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = player.projectedSurvivalBalls,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PROJECTED RUNS",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = player.projectedRuns,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FantasyPlayerCard(
    index: Int,
    player: FantasyPlayerItem
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CyberSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(CyanPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$index", color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = player.playerName, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyberBackground
                        ) {
                            Text(
                                text = player.role,
                                color = AmberWarning,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${player.team} • ${player.matchupReason}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldContainer
            ) {
                Text(
                    text = player.projectedPoints,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}


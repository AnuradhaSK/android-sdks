// Copyright 2026 The ThunderID Authors
// SPDX-License-Identifier: Apache-2.0

package dev.thunderid.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thunderid.compose.LocalThunderID

enum class Tab(
    val label: String,
    val icon: ImageVector,
) {
    CHECKS("Checks", Icons.Outlined.CheckCircle),
    COMPONENTS("Components", Icons.Outlined.GridView),
    SESSION("Session", Icons.Outlined.Key),
    LOGS("Logs", Icons.Outlined.FormatListBulleted),
}

@Composable
fun PlaygroundApp(storage: RecordingStorageAdapter) {
    val state = LocalThunderID.current
    val context = LocalContext.current
    val checks = remember { ChecksController(context.applicationContext, storage) }
    var tab by rememberSaveable { mutableStateOf(Tab.CHECKS) }

    // The SDK has no event stream, so log the auth state the provider publishes.
    LaunchedEffect(state) {
        snapshotFlow { Triple(state.isInitialized, state.user?.sub, state.error) }.collect { (ready, sub, error) ->
            when {
                error != null -> EventLog.error("state:error", error)
                !ready -> EventLog.info("sdk:initializing", PlaygroundSettings.hostLabel)
                sub != null -> EventLog.ok("state:signedIn", "sub=$sub")
                else -> EventLog.info("state:signedOut", "isSignedIn=false")
            }
        }
    }
    LaunchedEffect(Unit) { checks.checkHealth() }

    Column(
        Modifier
            .fillMaxSize()
            .background(Pg.Background)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Box(Modifier.weight(1f)) {
            when (tab) {
                Tab.CHECKS -> ChecksScreen(checks = checks, onOpenSession = { tab = Tab.SESSION })
                Tab.COMPONENTS -> ExplorerScreen()
                Tab.SESSION -> SessionScreen(storage = storage, onOpenChecks = { tab = Tab.CHECKS })
                Tab.LOGS -> LogsScreen()
            }
        }
        TabBar(selected = tab, onSelect = { tab = it })
    }
}

@Composable
private fun TabBar(
    selected: Tab,
    onSelect: (Tab) -> Unit,
) {
    Column(Modifier.background(Color(0xF5060D1C))) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Pg.Border),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Tab.entries.forEach { item ->
                val on = item == selected
                val color = if (on) Pg.PrimarySoft else Color.White.copy(alpha = 0.55f)
                Column(
                    modifier =
                        Modifier
                            .defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
                            .semantics { this.selected = on }
                            .clickable(role = Role.Tab) { onSelect(item) }
                            .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(item.icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                    Text(item.label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
                }
            }
        }
    }
}

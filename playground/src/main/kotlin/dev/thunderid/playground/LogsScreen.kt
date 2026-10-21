// Copyright 2026 The ThunderID Authors
// SPDX-License-Identifier: Apache-2.0

package dev.thunderid.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LogFilters =
    listOf(
        "all" to "All",
        "sdk" to "SDK",
        "storage" to "Storage",
        "redirect" to "Redirect",
        "check" to "Checks",
        "error" to "Errors",
    )

/** Everything the playground observed from the SDK, newest first. */
@Composable
fun LogsScreen() {
    var filter by rememberSaveable { mutableStateOf("all") }
    val events =
        EventLog.events.filter {
            when (filter) {
                "all" -> true
                "error" -> it.kind == EventKind.ERROR
                else -> it.source == filter
            }
        }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Logs", fontSize = 26.sp, color = Pg.Text)
                Text("${EventLog.events.size} events", fontSize = 12.sp, fontFamily = Pg.Mono, color = Pg.TextMuted)
            }
            SmallButton("Clear", { EventLog.clear() })
        }
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LogFilters.forEach { (id, label) ->
                FilterChip(
                    selected = filter == id,
                    onClick = { filter = id },
                    label = { Text(label) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Pg.Primary.copy(alpha = 0.2f),
                            selectedLabelColor = Pg.PrimarySoft,
                            labelColor = Pg.TextMuted,
                        ),
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
        ) {
            if (events.isEmpty()) {
                item { EmptyNote("Nothing logged for this filter yet.") }
            }
            itemsIndexed(events) { index, event ->
                val top = index == 0
                val bottom = index == events.lastIndex
                val shape =
                    RoundedCornerShape(
                        topStart = if (top) 14.dp else 0.dp,
                        topEnd = if (top) 14.dp else 0.dp,
                        bottomStart = if (bottom) 14.dp else 0.dp,
                        bottomEnd = if (bottom) 14.dp else 0.dp,
                    )
                Column(
                    Modifier
                        .clip(shape)
                        .background(Pg.Fill),
                ) {
                    EventRow(event, showSource = true)
                }
            }
        }
    }
}

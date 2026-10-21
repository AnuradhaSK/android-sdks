// Copyright 2026 The ThunderID Authors
// SPDX-License-Identifier: Apache-2.0

package dev.thunderid.playground

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

object Pg {
    val Background = Color(0xFF050B18)
    val Surface = Color(0xFF0A1428)
    val CardTop = Color(0xFA09142C)
    val CardBottom = Color(0xFF040A18)
    val Primary = Color(0xFF3688FF)
    val PrimaryDeep = Color(0xFF2060C8)
    val PrimarySoft = Color(0xFF9CC4FF)
    val Accent = Color(0xFF8BF9FA)
    val Pass = Color(0xFF34D399)
    val PassSoft = Color(0xFF6EE7B7)
    val Fail = Color(0xFFEF4444)
    val FailSoft = Color(0xFFFCA5A5)
    val Warn = Color(0xFFF59E0B)
    val WarnSoft = Color(0xFFFCD34D)
    val Idle = Color(0xFF94A3B8)
    val Text = Color.White
    val TextMuted = Color.White.copy(alpha = 0.55f)
    val TextFaint = Color.White.copy(alpha = 0.45f)
    val Border = Color.White.copy(alpha = 0.08f)
    val Fill = Color.White.copy(alpha = 0.025f)
    val Control = Color.White.copy(alpha = 0.06f)
    val CodeBg = Color.Black.copy(alpha = 0.35f)
    val Code = Color(0xFFCFE3FF)
    val Mono = FontFamily.Monospace

    val PrimaryGradient = Brush.horizontalGradient(listOf(Primary, PrimaryDeep))
    val CardGradient = Brush.linearGradient(listOf(CardTop, CardBottom))
    val TokenGradient = Brush.horizontalGradient(listOf(Primary, Accent))
}

/** Material 3 dark scheme in the ThunderID palette. The SDK's Compose components read these colors. */
val PlaygroundDarkScheme =
    darkColorScheme(
        primary = Pg.Primary,
        onPrimary = Color.White,
        secondary = Pg.Accent,
        background = Pg.Background,
        onBackground = Color.White,
        surface = Pg.Surface,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF13203A),
        onSurfaceVariant = Color.White.copy(alpha = 0.7f),
        outline = Color.White.copy(alpha = 0.2f),
        outlineVariant = Color.White.copy(alpha = 0.1f),
        error = Pg.FailSoft,
    )

val PlaygroundLightScheme =
    lightColorScheme(
        primary = Pg.Primary,
        secondary = Color(0xFF345F91),
        background = Color(0xFFEEF1F6),
        surface = Color.White,
        surfaceVariant = Color(0xFFF2F2F2),
        onSurfaceVariant = Color(0xFF49454F),
        outlineVariant = Color(0x1F1D1B20),
    )

@Composable
fun PlaygroundTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlaygroundDarkScheme, content = content)
}

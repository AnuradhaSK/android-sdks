// Copyright 2026 The ThunderID Authors
// SPDX-License-Identifier: Apache-2.0

package dev.thunderid.playground

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The ThunderID mark, with the top bar lightened for the playground's dark background. */
@Composable
fun ThunderIDMark(
    height: Dp = 26.dp,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier
                .size(width = height * (207f / 257f), height = height)
                .semantics { contentDescription = "ThunderID" },
    ) {
        val scaleX = size.width / 207f
        val scaleY = size.height / 257f

        fun sx(x: Float) = x * scaleX

        fun sy(y: Float) = y * scaleY

        val topBar =
            Path().apply {
                moveTo(sx(55.4763f), sy(26.4391f))
                lineTo(sx(58.8866f), sy(0f))
                lineTo(sx(0f), sy(0f))
                lineTo(sx(0f), sy(26.4391f))
                close()
            }
        drawPath(topBar, Color(0xFFE8F4FF))

        val leftBar =
            Path().apply {
                moveTo(sx(39.8438f), sy(147.407f))
                lineTo(sx(49.5455f), sy(72.2839f))
                lineTo(sx(0f), sy(72.2839f))
                lineTo(sx(0f), sy(256.743f))
                lineTo(sx(60.5602f), sy(256.743f))
                lineTo(sx(80.048f), sy(147.407f))
                close()
            }
        drawPath(leftBar, Pg.Primary)

        val bolt =
            Path().apply {
                moveTo(sx(192.42f), sy(59.361f))
                cubicTo(sx(182.782f), sy(40.2307f), sx(168.929f), sy(25.5705f), sx(150.903f), sy(15.3381f))
                cubicTo(sx(145.501f), sy(12.2662f), sx(139.761f), sy(9.6605f), sx(133.703f), sy(7.5208f))
                lineTo(sx(115.401f), sy(103.702f))
                lineTo(sx(159.757f), sy(103.702f))
                lineTo(sx(76.2987f), sy(256.743f))
                lineTo(sx(83.3735f), sy(256.743f))
                cubicTo(sx(109.449f), sy(256.743f), sx(131.69f), sy(251.574f), sx(150.14f), sy(241.236f))
                cubicTo(sx(168.569f), sy(230.897f), sx(182.634f), sy(216.131f), sx(192.356f), sy(196.959f))
                cubicTo(sx(202.058f), sy(177.765f), sx(206.909f), sy(154.8f), sx(206.909f), sy(128.043f))
                cubicTo(sx(206.909f), sy(101.286f), sx(202.079f), sy(78.5123f), sx(192.441f), sy(59.3821f))
                close()
            }
        drawPath(bolt, Pg.Primary)
    }
}

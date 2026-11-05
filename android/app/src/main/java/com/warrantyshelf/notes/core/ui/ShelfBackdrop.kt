package com.warrantyshelf.notes.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.R

@Composable
fun ShelfBackdrop(
    overlay: Brush,
    modifier: Modifier = Modifier,
    ruleColor: Color = Color.Transparent,
    glow: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(overlay))
        if (glow != null) {
            Box(modifier = Modifier.fillMaxSize().background(glow))
        }
        if (ruleColor != Color.Transparent) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 1.dp.toPx()
                listOf(0.18f, 0.38f, 0.58f, 0.72f, 0.86f).forEach { fraction ->
                    val y = size.height * fraction
                    drawLine(
                        color = ruleColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = stroke
                    )
                }
            }
        }
        content()
    }
}

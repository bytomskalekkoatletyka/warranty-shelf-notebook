package com.warrantyshelf.notes.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SectionRule(
    color: Color,
    width: Dp,
    modifier: Modifier = Modifier,
    thickness: Dp = 3.dp
) {
    Box(
        modifier = modifier
            .width(width)
            .height(thickness)
            .background(color, RoundedCornerShape(thickness / 2))
    )
}

@Composable
fun HairLine(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .height(thickness)
            .background(color)
    )
}

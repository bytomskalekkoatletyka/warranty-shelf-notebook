package com.warrantyshelf.notes.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.config.GameConfig
import com.warrantyshelf.notes.core.theme.ShelfLine
import com.warrantyshelf.notes.core.theme.ShelfSurfaceMuted

private val TILTS = listOf(-4f, 0f, 5f)

@Composable
fun DocumentStack(
    expanded: Boolean,
    onToggle: () -> Unit,
    description: String,
    modifier: Modifier = Modifier,
    count: Int = 3
) {
    val shift by animateDpAsState(
        targetValue = if (expanded) 0.dp else (-18).dp,
        animationSpec = tween(GameConfig.FAN_OUT_MS, easing = FastOutSlowInEasing),
        label = "docShift"
    )
    val tilt by animateFloatAsState(
        targetValue = if (expanded) 0f else 1f,
        animationSpec = tween(GameConfig.FAN_OUT_MS, easing = FastOutSlowInEasing),
        label = "docTilt"
    )
    Row(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onToggle)
    ) {
        repeat(count) { index ->
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = if (index == 0) description else null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = shift * index)
                    .rotate(TILTS[index % TILTS.size] * tilt)
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShelfSurfaceMuted)
                    .border(BorderStroke(1.dp, ShelfLine), RoundedCornerShape(8.dp))
                    .padding(4.dp)
            )
        }
    }
}

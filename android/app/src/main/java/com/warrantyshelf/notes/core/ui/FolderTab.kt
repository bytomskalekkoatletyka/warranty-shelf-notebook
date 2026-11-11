package com.warrantyshelf.notes.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.core.config.GameConfig
import com.warrantyshelf.notes.core.theme.AmberSoon
import com.warrantyshelf.notes.core.theme.ClayExpired
import com.warrantyshelf.notes.core.theme.OnDark
import com.warrantyshelf.notes.core.theme.SageCovered
import com.warrantyshelf.notes.domain.model.CoverageBand

fun bandColor(band: CoverageBand): Color = when (band) {
    CoverageBand.COVERED -> SageCovered
    CoverageBand.ENDING_SOON -> AmberSoon
    CoverageBand.EXPIRED -> ClayExpired
}

@Composable
fun FolderTab(
    band: CoverageBand,
    label: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "tabPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.PULSE_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tabAlpha"
    )
    val alpha = if (band == CoverageBand.ENDING_SOON) pulse else 1f
    Box(
        modifier = modifier
            .height(26.dp)
            .defaultMinSize(minWidth = 96.dp)
            .background(
                bandColor(band).copy(alpha = alpha),
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .padding(horizontal = 12.dp)
            .semantics { stateDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnDark
        )
    }
}

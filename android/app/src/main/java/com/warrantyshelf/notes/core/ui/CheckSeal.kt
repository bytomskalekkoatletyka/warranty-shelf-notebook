package com.warrantyshelf.notes.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.core.config.GameConfig
import com.warrantyshelf.notes.core.theme.SageCovered

@Composable
fun CheckSeal(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn(
            animationSpec = tween(GameConfig.SEAL_MS, easing = FastOutSlowInEasing),
            initialScale = 0.4f
        ) + fadeIn(animationSpec = tween(120)),
        exit = fadeOut(animationSpec = tween(220)),
        label = "checkSeal"
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(SageCovered.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.CheckCircle,
                contentDescription = null,
                tint = SageCovered,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

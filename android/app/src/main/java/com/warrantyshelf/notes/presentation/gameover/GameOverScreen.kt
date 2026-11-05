package com.warrantyshelf.notes.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.config.GameConfig
import com.warrantyshelf.notes.core.di.ServiceLocator
import com.warrantyshelf.notes.core.theme.OrangeAccent
import com.warrantyshelf.notes.core.theme.PetrolPrimary
import com.warrantyshelf.notes.core.theme.SageCovered
import com.warrantyshelf.notes.core.theme.ShelfBackground
import com.warrantyshelf.notes.core.theme.ShelfBackgroundDeep
import com.warrantyshelf.notes.core.theme.ShelfLine
import com.warrantyshelf.notes.core.theme.ShelfSurface
import com.warrantyshelf.notes.core.theme.ShelfSurfaceMuted
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.core.theme.TextSecondary
import com.warrantyshelf.notes.core.ui.PrimaryButton
import com.warrantyshelf.notes.core.ui.SecondaryButton
import com.warrantyshelf.notes.core.ui.SectionRule
import com.warrantyshelf.notes.core.ui.ShelfBackdrop
import com.warrantyshelf.notes.core.ui.StatCard

@Composable
fun GameOverScreen(
    kept: Int,
    repairs: Int,
    retired: Int,
    endingSoon: Int,
    onReviewAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = ServiceLocator.factory)
    val state by gameOverViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameOverViewModel.refresh()
    }

    GameOverContent(
        kept = kept,
        repairs = repairs,
        retired = retired,
        endingSoon = endingSoon,
        itemsOnShelf = state.itemsOnShelf,
        onReviewAgain = onReviewAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    kept: Int,
    repairs: Int,
    retired: Int,
    endingSoon: Int,
    itemsOnShelf: Int,
    onReviewAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val sealState = remember { MutableTransitionState(false).apply { targetState = true } }
    val transition = rememberInfiniteTransition(label = "resultPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.PULSE_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calloutAlpha"
    )
    val cards = buildList {
        if (kept > 0) add(Triple(stringResource(R.string.label_kept), kept, SageCovered))
        if (repairs > 0) {
            add(Triple(stringResource(R.string.label_repairs_logged), repairs, PetrolPrimary))
        }
        if (retired > 0) add(Triple(stringResource(R.string.label_retired), retired, TextMuted))
        if (endingSoon > 0) {
            add(Triple(stringResource(R.string.label_ending_soon), endingSoon, OrangeAccent))
        }
    }

    ShelfBackdrop(
        overlay = Brush.verticalGradient(
            listOf(
                ShelfSurfaceMuted.copy(alpha = 0.94f),
                ShelfBackground.copy(alpha = 0.93f),
                ShelfBackgroundDeep.copy(alpha = 0.97f)
            )
        ),
        ruleColor = PetrolPrimary.copy(alpha = 0.05f),
        glow = Brush.radialGradient(
            colors = listOf(SageCovered.copy(alpha = 0.10f), SageCovered.copy(alpha = 0f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.result_title),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            SectionRule(color = OrangeAccent, width = 72.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.result_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            AnimatedVisibility(
                visibleState = sealState,
                enter = scaleIn(
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(360))
            ) {
                Surface(
                    modifier = Modifier.size(128.dp),
                    shape = CircleShape,
                    color = ShelfSurface,
                    border = BorderStroke(1.dp, ShelfLine),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                if (cards.size >= 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        cards.chunked(2).forEach { pair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                pair.forEach { card ->
                                    StatCard(
                                        label = card.first,
                                        value = card.second.toString(),
                                        accent = card.third,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(84.dp)
                                    )
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.result_single, itemsOnShelf),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (endingSoon > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = OrangeAccent.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.45f * pulse))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.result_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.result_footer),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_review_again),
                onClick = onReviewAgain,
                icon = Icons.Default.Refresh,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                icon = Icons.Default.Home,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

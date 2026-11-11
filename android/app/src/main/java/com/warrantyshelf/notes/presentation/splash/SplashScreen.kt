package com.warrantyshelf.notes.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.di.ServiceLocator
import com.warrantyshelf.notes.core.theme.AmberSoon
import com.warrantyshelf.notes.core.theme.OnDark
import com.warrantyshelf.notes.core.theme.OrangeAccent
import com.warrantyshelf.notes.core.theme.PetrolDeep
import com.warrantyshelf.notes.core.theme.PetrolPrimary
import com.warrantyshelf.notes.core.theme.SageCovered
import com.warrantyshelf.notes.core.theme.ShelfLine
import com.warrantyshelf.notes.core.theme.ShelfSurface
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.core.ui.SectionRule
import com.warrantyshelf.notes.core.ui.ShelfBackdrop

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) {
            onFinished()
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splashLoops")
    val tabPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tabPhase"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.62f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    val cardState = remember { MutableTransitionState(false).apply { targetState = true } }
    val tabColors = listOf(SageCovered, AmberSoon, OrangeAccent)

    ShelfBackdrop(
        overlay = Brush.verticalGradient(
            listOf(
                PetrolDeep.copy(alpha = 0.94f),
                PetrolPrimary.copy(alpha = 0.90f),
                TextPrimary.copy(alpha = 0.96f)
            )
        ),
        ruleColor = OnDark.copy(alpha = 0.07f),
        glow = Brush.radialGradient(
            colors = listOf(OrangeAccent.copy(alpha = 0.10f), OrangeAccent.copy(alpha = 0f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabColors.forEachIndexed { index, color ->
                    val lit = (tabPhase.toInt() % 3) == index
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(8.dp)
                            .alpha(if (lit) 1f else 0.32f)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            AnimatedVisibility(
                visibleState = cardState,
                enter = scaleIn(
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 260f),
                    initialScale = 0.88f
                ) + fadeIn(animationSpec = tween(520))
            ) {
                Surface(
                    modifier = Modifier.size(168.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = ShelfSurface.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, ShelfSurface.copy(alpha = 0.22f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(132.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.splash_title_top),
                style = MaterialTheme.typography.displaySmall,
                color = OnDark,
                letterSpacing = 3.2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.splash_title_bottom),
                style = MaterialTheme.typography.displaySmall,
                color = OnDark,
                letterSpacing = 3.2.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            SectionRule(color = OrangeAccent, width = 56.dp, thickness = 2.dp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = ShelfLine.copy(alpha = 0.80f * breathe),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(184.dp)
                        .height(3.dp),
                    color = OrangeAccent,
                    trackColor = ShelfSurface.copy(alpha = 0.16f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.splash_loading),
                    style = MaterialTheme.typography.labelSmall,
                    color = ShelfLine.copy(alpha = 0.70f),
                    letterSpacing = 2.0.sp
                )
            }
        }
    }
}

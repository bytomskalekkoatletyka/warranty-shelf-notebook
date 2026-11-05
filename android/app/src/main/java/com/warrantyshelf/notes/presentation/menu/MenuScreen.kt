package com.warrantyshelf.notes.presentation.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.warrantyshelf.notes.core.theme.TabularStyle
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.core.theme.TextSecondary
import com.warrantyshelf.notes.core.ui.SectionRule
import com.warrantyshelf.notes.core.ui.ShelfBackdrop
import com.warrantyshelf.notes.core.ui.SecondaryButton
import com.warrantyshelf.notes.core.ui.PrimaryButton
import com.warrantyshelf.notes.core.ui.StatCard
import com.warrantyshelf.notes.core.ui.bandColor
import com.warrantyshelf.notes.presentation.dialog.AddItemDialog

@Composable
fun MenuScreen(
    onStart: () -> Unit,
    onOpenItem: (Int) -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory)
    val state by menuViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onStart = onStart,
        onOpenItem = onOpenItem,
        onSettings = onSettings,
        onToggleSearch = menuViewModel::toggleSearch,
        onQueryChange = menuViewModel::updateQuery,
        onOpenAdd = menuViewModel::openAdd
    )

    if (state.addOpen) {
        AddItemDialog(
            onDismiss = menuViewModel::dismissAdd,
            onSave = menuViewModel::addItem
        )
    }
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStart: () -> Unit,
    onOpenItem: (Int) -> Unit,
    onSettings: () -> Unit,
    onToggleSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    onOpenAdd: () -> Unit
) {
    ShelfBackdrop(
        overlay = Brush.verticalGradient(
            listOf(
                ShelfBackground.copy(alpha = 0.93f),
                ShelfSurfaceMuted.copy(alpha = 0.95f),
                ShelfBackgroundDeep.copy(alpha = 0.97f)
            )
        ),
        ruleColor = PetrolPrimary.copy(alpha = 0.06f),
        glow = Brush.radialGradient(
            colors = listOf(OrangeAccent.copy(alpha = 0.06f), OrangeAccent.copy(alpha = 0f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp, vertical = 10.dp)
        ) {
            TopRail(onAdd = onOpenAdd, onSettings = onSettings)
            Spacer(modifier = Modifier.height(12.dp))

            if (state.searchOpen) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (state.rows.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.search_empty),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.rows, key = { row -> row.id }) { row ->
                            ShelfRowCard(row = row, onClick = { onOpenItem(row.id) })
                        }
                    }
                }
            } else {
                HeroBlock()
                Spacer(modifier = Modifier.height(16.dp))
                CoverageBandPanel(
                    endingSoon = state.endingSoonCount,
                    covered = state.coveredCount
                )
                if (state.activeItems > 0 && state.repairsCount > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = stringResource(R.string.label_items),
                            value = state.activeItems.toString(),
                            accent = SageCovered,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.label_repairs),
                            value = state.repairsCount.toString(),
                            accent = PetrolPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_start),
                onClick = onStart,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.cta_open_shelf),
                    onClick = { onOpenItem(state.firstItemId) },
                    icon = Icons.Default.Menu,
                    enabled = state.firstItemId > 0,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = if (state.searchOpen) {
                        stringResource(R.string.cta_close_search)
                    } else {
                        stringResource(R.string.cta_search)
                    },
                    onClick = onToggleSearch,
                    icon = if (state.searchOpen) Icons.Default.Close else Icons.Default.Search,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TopRail(onAdd: () -> Unit, onSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = ShelfSurface,
            border = BorderStroke(1.dp, ShelfLine)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = stringResource(R.string.cd_shelf_icon),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.menu_brand),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 2.0.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onAdd, modifier = Modifier.size(56.dp)) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_add_item),
                tint = PetrolPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = onSettings, modifier = Modifier.size(56.dp)) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.cd_settings),
                tint = PetrolPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun HeroBlock() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(end = 96.dp)) {
            Text(
                text = stringResource(R.string.menu_hero_top),
                style = MaterialTheme.typography.headlineMedium,
                color = TextSecondary
            )
            Text(
                text = stringResource(R.string.menu_hero_bottom),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            SectionRule(color = OrangeAccent, width = 64.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.menu_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                maxLines = 2
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
        )
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 34.dp)
                .size(72.dp)
        )
    }
}

@Composable
private fun CoverageBandPanel(endingSoon: Int, covered: Int) {
    val transition = rememberInfiniteTransition(label = "coveragePulse")
    val pulse by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(GameConfig.PULSE_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coverageAlpha"
    )
    val barAlpha = if (endingSoon > 0) pulse else 0.4f
    val description = stringResource(R.string.cd_coverage_band, endingSoon)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .semantics { stateDescription = description },
        shape = RoundedCornerShape(12.dp),
        color = ShelfSurface,
        border = BorderStroke(1.dp, ShelfLine),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(OrangeAccent.copy(alpha = barAlpha), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_ending_soon),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = endingSoon.toString(),
                    style = TabularStyle,
                    fontSize = 28.sp,
                    color = OrangeAccent
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(ShelfLine)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_covered),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = covered.toString(),
                    style = TabularStyle,
                    fontSize = 28.sp,
                    color = PetrolPrimary
                )
            }
        }
    }
}

@Composable
private fun ShelfRowCard(row: ShelfRow, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(10.dp),
        color = ShelfSurface,
        border = BorderStroke(1.dp, ShelfLine)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .background(bandColor(row.band), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = row.room,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1
                )
            }
            Text(
                text = stringResource(R.string.days_left, row.daysRemaining.coerceAtLeast(0)),
                style = MaterialTheme.typography.labelSmall,
                color = bandColor(row.band),
                textAlign = TextAlign.End
            )
        }
    }
}

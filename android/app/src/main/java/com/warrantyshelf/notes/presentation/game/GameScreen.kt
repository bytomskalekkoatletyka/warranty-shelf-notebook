package com.warrantyshelf.notes.presentation.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
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
import com.warrantyshelf.notes.core.ui.CheckSeal
import com.warrantyshelf.notes.core.ui.DocumentStack
import com.warrantyshelf.notes.core.ui.FolderCard
import com.warrantyshelf.notes.core.ui.FolderTab
import com.warrantyshelf.notes.core.ui.HairLine
import com.warrantyshelf.notes.core.ui.PrimaryButton
import com.warrantyshelf.notes.core.ui.SecondaryButton
import com.warrantyshelf.notes.domain.model.CoverageBand
import com.warrantyshelf.notes.presentation.dialog.AddItemDialog
import com.warrantyshelf.notes.presentation.dialog.ConfirmRetireDialog
import com.warrantyshelf.notes.presentation.dialog.PauseDialog
import com.warrantyshelf.notes.presentation.dialog.RepairEntryDialog
import com.warrantyshelf.notes.core.ui.AppIcons

@Composable
fun GameScreen(
    session: Int,
    onMenu: () -> Unit,
    onFinished: (Int, Int, Int, Int) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(session) {
        gameViewModel.startSession(session)
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(
                    event.kept,
                    event.repairs,
                    event.retired,
                    event.endingSoon
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameContent(
        state = state,
        onMenu = onMenu,
        onPause = gameViewModel::openPause,
        onKeep = gameViewModel::keep,
        onLogRepair = gameViewModel::openRepair,
        onRetire = gameViewModel::openRetire,
        onFinishNow = gameViewModel::finishNow,
        onToggleDocuments = gameViewModel::toggleDocuments,
        onOpenAdd = gameViewModel::openAdd
    )

    if (state.pauseOpen) {
        PauseDialog(
            onResume = gameViewModel::dismissPause,
            onMenu = onMenu
        )
    }
    if (state.repairOpen) {
        RepairEntryDialog(
            onDismiss = gameViewModel::dismissRepair,
            onSave = gameViewModel::saveRepair
        )
    }
    if (state.retireOpen) {
        ConfirmRetireDialog(
            itemName = state.itemName,
            onDismiss = gameViewModel::dismissRetire,
            onConfirm = gameViewModel::confirmRetire
        )
    }
    if (state.addOpen) {
        AddItemDialog(
            onDismiss = gameViewModel::dismissAdd,
            onSave = gameViewModel::addItem
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onMenu: () -> Unit,
    onPause: () -> Unit,
    onKeep: () -> Unit,
    onLogRepair: () -> Unit,
    onRetire: () -> Unit,
    onFinishNow: () -> Unit,
    onToggleDocuments: () -> Unit,
    onOpenAdd: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(300),
        label = "reviewProgress"
    )
    ShelfBackdropArea {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(state = state, onMenu = onMenu, onPause = onPause)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(ShelfLine)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(OrangeAccent)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_2),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(64.dp)
                        .rotate(-6f)
                        .alpha(0.16f)
                )
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(64.dp)
                        .rotate(6f)
                        .alpha(0.16f)
                )
                if (state.empty) {
                    EmptyShelf(onAdd = onOpenAdd)
                } else if (!state.loading) {
                    AnimatedContent(
                        targetState = state.itemId,
                        transitionSpec = {
                            (
                                slideInVertically(
                                    animationSpec = tween(
                                        GameConfig.FOLDER_PULL_MS,
                                        easing = FastOutSlowInEasing
                                    )
                                ) { -34 } +
                                    fadeIn(animationSpec = tween(GameConfig.FOLDER_PULL_MS)) +
                                    scaleIn(
                                        animationSpec = tween(GameConfig.FOLDER_PULL_MS),
                                        initialScale = 0.96f
                                    )
                                ) togetherWith (
                                slideOutVertically(
                                    animationSpec = tween(180)
                                ) { 26 } + fadeOut(animationSpec = tween(180))
                                )
                        },
                        label = "folderPull"
                    ) { _ ->
                        FolderPanel(
                            state = state,
                            onToggleDocuments = onToggleDocuments
                        )
                    }
                }
                CheckSeal(
                    visible = state.sealVisible,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if (!state.empty) {
                StatStrip(state = state)
                ControlsPanel(
                    enabled = !state.saving,
                    onKeep = onKeep,
                    onLogRepair = onLogRepair,
                    onRetire = onRetire,
                    onFinishNow = onFinishNow
                )
            }
        }
    }
}

@Composable
private fun ShelfBackdropArea(content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit) {
    com.warrantyshelf.notes.core.ui.ShelfBackdrop(
        overlay = Brush.verticalGradient(
            listOf(
                ShelfBackgroundDeep.copy(alpha = 0.95f),
                ShelfBackground.copy(alpha = 0.92f),
                ShelfSurfaceMuted.copy(alpha = 0.96f)
            )
        ),
        ruleColor = PetrolPrimary.copy(alpha = 0.06f),
        content = content
    )
}

@Composable
private fun GameHeader(state: GameUiState, onMenu: () -> Unit, onPause: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = ShelfSurface.copy(alpha = 0.92f)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenu, modifier = Modifier.size(56.dp)) {
                    Icon(
                        imageVector = AppIcons.Home,
                        contentDescription = stringResource(R.string.cd_back_menu),
                        tint = PetrolPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.game_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 2.0.sp
                    )
                    Text(
                        text = stringResource(R.string.step_counter, state.step, state.total),
                        style = TabularStyle,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onPause, modifier = Modifier.size(56.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_pause),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            HairLine(color = ShelfLine, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FolderPanel(state: GameUiState, onToggleDocuments: () -> Unit) {
    val tabLabel = when (state.band) {
        CoverageBand.EXPIRED -> stringResource(R.string.tab_expired)
        CoverageBand.ENDING_SOON -> stringResource(R.string.tab_ending)
        CoverageBand.COVERED -> stringResource(R.string.days_left, state.daysRemaining)
    }
    val description = stringResource(
        R.string.cd_folder_tab,
        state.daysRemaining.coerceAtLeast(0)
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        FolderTab(
            band = state.band,
            label = tabLabel,
            description = description
        )
        FolderCard {
            Text(
                text = state.itemName,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.room,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            HairLine(color = ShelfLine, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailCell(
                    label = stringResource(R.string.label_acquired),
                    value = state.acquiredText,
                    modifier = Modifier.weight(1f)
                )
                DetailCell(
                    label = stringResource(R.string.label_cover_ends),
                    value = state.coverEndsText,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailCell(
                    label = stringResource(R.string.label_serial),
                    value = state.serialText,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(40.dp)
                            .alpha(0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DetailCell(
                        label = stringResource(R.string.label_repairs),
                        value = state.repairsText
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            DocumentStack(
                expanded = state.documentsExpanded,
                onToggle = onToggleDocuments,
                description = stringResource(R.string.cd_document)
            )
        }
    }
}

@Composable
private fun DetailCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = TabularStyle,
            color = TextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun StatStrip(state: GameUiState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        color = ShelfSurfaceMuted
    ) {
        Column {
            HairLine(color = ShelfLine, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StripCell(
                    label = stringResource(R.string.label_kept),
                    value = state.kept,
                    accent = SageCovered,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(ShelfLine)
                )
                StripCell(
                    label = stringResource(R.string.label_repairs),
                    value = state.repairsLogged,
                    accent = PetrolPrimary,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(ShelfLine)
                )
                StripCell(
                    label = stringResource(R.string.label_retired),
                    value = state.retired,
                    accent = OrangeAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StripCell(
    label: String,
    value: Int,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = com.warrantyshelf.notes.core.text.ShelfFormat.dash(value),
            style = TabularStyle,
            color = if (value > 0) accent else TextMuted
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun ControlsPanel(
    enabled: Boolean,
    onKeep: () -> Unit,
    onLogRepair: () -> Unit,
    onRetire: () -> Unit,
    onFinishNow: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        color = ShelfSurface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.cta_log_repair),
                    onClick = onLogRepair,
                    icon = AppIcons.Build,
                    accent = OrangeAccent,
                    enabled = enabled,
                    height = 56.dp,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_retire),
                    onClick = onRetire,
                    icon = AppIcons.Delete,
                    accent = TextMuted,
                    enabled = enabled,
                    height = 56.dp,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_keep),
                onClick = onKeep,
                icon = AppIcons.Check,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = onFinishNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.cta_finish),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EmptyShelf(onAdd: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_empty_shelf),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(120.dp)
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_shelf),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.empty_shelf_hint),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            text = stringResource(R.string.cta_add_item),
            onClick = onAdd,
            icon = AppIcons.Add,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

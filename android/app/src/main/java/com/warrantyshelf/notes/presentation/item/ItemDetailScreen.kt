package com.warrantyshelf.notes.presentation.item

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.di.ServiceLocator
import com.warrantyshelf.notes.core.theme.OrangeAccent
import com.warrantyshelf.notes.core.theme.PetrolPrimary
import com.warrantyshelf.notes.core.theme.ShelfBackground
import com.warrantyshelf.notes.core.theme.ShelfBackgroundDeep
import com.warrantyshelf.notes.core.theme.ShelfLine
import com.warrantyshelf.notes.core.theme.ShelfSurface
import com.warrantyshelf.notes.core.theme.ShelfSurfaceMuted
import com.warrantyshelf.notes.core.theme.TabularStyle
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.core.theme.TextSecondary
import com.warrantyshelf.notes.core.ui.DocumentStack
import com.warrantyshelf.notes.core.ui.FolderCard
import com.warrantyshelf.notes.core.ui.FolderTab
import com.warrantyshelf.notes.core.ui.HairLine
import com.warrantyshelf.notes.core.ui.SecondaryButton
import com.warrantyshelf.notes.core.ui.bandColor
import com.warrantyshelf.notes.domain.model.CoverageBand
import com.warrantyshelf.notes.presentation.dialog.ConfirmRetireDialog
import com.warrantyshelf.notes.presentation.dialog.RepairEntryDialog

@Composable
fun ItemDetailScreen(itemId: Int, onBack: () -> Unit) {
    val detailViewModel: ItemDetailViewModel = viewModel(
        key = "item_" + itemId,
        factory = ServiceLocator.itemFactory(itemId)
    )
    val state by detailViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        detailViewModel.refresh()
    }

    ItemDetailContent(
        state = state,
        onBack = onBack,
        onAddRepair = detailViewModel::openRepair,
        onRetire = detailViewModel::openRetire,
        onToggleDocuments = detailViewModel::toggleDocuments
    )

    if (state.repairOpen) {
        RepairEntryDialog(
            onDismiss = detailViewModel::dismissRepair,
            onSave = detailViewModel::saveRepair
        )
    }
    if (state.retireOpen) {
        ConfirmRetireDialog(
            itemName = state.name,
            onDismiss = detailViewModel::dismissRetire,
            onConfirm = detailViewModel::confirmRetire
        )
    }
}

@Composable
private fun ItemDetailContent(
    state: ItemDetailUiState,
    onBack: () -> Unit,
    onAddRepair: () -> Unit,
    onRetire: () -> Unit,
    onToggleDocuments: () -> Unit
) {
    com.warrantyshelf.notes.core.ui.ShelfBackdrop(
        overlay = Brush.verticalGradient(
            listOf(
                ShelfBackground.copy(alpha = 0.94f),
                ShelfSurfaceMuted.copy(alpha = 0.94f),
                ShelfBackgroundDeep.copy(alpha = 0.97f)
            )
        ),
        ruleColor = PetrolPrimary.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            DetailHeader(title = state.name, onBack = onBack)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Column {
                        FolderTab(
                            band = state.band,
                            label = when (state.band) {
                                CoverageBand.EXPIRED -> stringResource(R.string.tab_expired)
                                CoverageBand.ENDING_SOON -> stringResource(R.string.tab_ending)
                                CoverageBand.COVERED -> stringResource(
                                    R.string.days_left,
                                    state.daysRemaining
                                )
                            },
                            description = stringResource(
                                R.string.cd_folder_tab,
                                state.daysRemaining.coerceAtLeast(0)
                            )
                        )
                        FolderCard {
                            DetailLine(
                                label = stringResource(R.string.label_room),
                                value = state.room
                            )
                            HairLine(
                                color = ShelfLine,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                            DetailLine(
                                label = stringResource(R.string.label_acquired),
                                value = state.acquiredText
                            )
                            HairLine(
                                color = ShelfLine,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                            DetailLine(
                                label = stringResource(R.string.label_cover_ends),
                                value = state.coverEndsText
                            )
                            HairLine(
                                color = ShelfLine,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                            DetailLine(
                                label = stringResource(R.string.label_warranty),
                                value = state.warrantyText
                            )
                            HairLine(
                                color = ShelfLine,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                            DetailLine(
                                label = stringResource(R.string.label_serial),
                                value = state.serialText
                            )
                        }
                    }
                }
                item {
                    SectionTitle(text = stringResource(R.string.section_documents))
                }
                item {
                    DocumentStack(
                        expanded = state.documentsExpanded,
                        onToggle = onToggleDocuments,
                        description = stringResource(R.string.cd_document)
                    )
                }
                item {
                    SectionTitle(text = stringResource(R.string.section_repairs))
                }
                if (state.repairs.isEmpty()) {
                    item {
                        EmptyRepairs()
                    }
                } else {
                    items(state.repairs) { row ->
                        RepairRowCard(row = row)
                    }
                }
            }
            BottomBar(onAddRepair = onAddRepair, onRetire = onRetire, enabled = state.found)
        }
    }
}

@Composable
private fun DetailHeader(title: String, onBack: () -> Unit) {
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
                IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_back_menu),
                        tint = PetrolPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }
            HairLine(color = ShelfLine, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        letterSpacing = 2.0.sp
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = TabularStyle,
            color = TextPrimary,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun RepairRowCard(row: RepairRow) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ShelfSurface,
        border = BorderStroke(1.dp, ShelfLine)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.dateText,
                    style = TabularStyle,
                    color = TextPrimary
                )
                Text(
                    text = row.note,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
            if (row.costText.isNotEmpty()) {
                Text(
                    text = row.costText,
                    style = TabularStyle,
                    color = OrangeAccent
                )
            }
        }
    }
}

@Composable
private fun EmptyRepairs() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_5),
            contentDescription = stringResource(R.string.cd_empty_repairs),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(72.dp)
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_repairs),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun BottomBar(onAddRepair: () -> Unit, onRetire: () -> Unit, enabled: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        color = ShelfSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                text = stringResource(R.string.cta_add_repair),
                onClick = onAddRepair,
                icon = Icons.Default.Build,
                accent = OrangeAccent,
                enabled = enabled,
                height = 56.dp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onRetire,
                    enabled = enabled,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cta_retire),
                        tint = if (enabled) bandColor(CoverageBand.EXPIRED) else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

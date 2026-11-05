package com.warrantyshelf.notes.presentation.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.core.ui.HairLine
import com.warrantyshelf.notes.core.ui.SectionRule
import com.warrantyshelf.notes.core.ui.ShelfBackdrop
import com.warrantyshelf.notes.domain.model.ShelfSettings

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
    }

    SettingsContent(
        state = state,
        onBack = onBack,
        onAccent = settingsViewModel::selectAccent,
        onCurrency = settingsViewModel::selectCurrency,
        onDateFormat = settingsViewModel::selectDateFormat
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onAccent: (String) -> Unit,
    onCurrency: (String) -> Unit,
    onDateFormat: (Boolean) -> Unit
) {
    ShelfBackdrop(
        overlay = Brush.verticalGradient(
            listOf(
                ShelfBackground.copy(alpha = 0.95f),
                ShelfSurfaceMuted.copy(alpha = 0.95f),
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
            SettingsHeader(onBack = onBack)
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsBlock(title = stringResource(R.string.settings_accent)) {
                    ChipRow {
                        ShelfChip(
                            label = stringResource(R.string.chip_petrol),
                            selected = state.settings.accent == ShelfSettings.ACCENT_PETROL,
                            accent = PetrolPrimary,
                            onClick = { onAccent(ShelfSettings.ACCENT_PETROL) }
                        )
                        ShelfChip(
                            label = stringResource(R.string.chip_orange),
                            selected = state.settings.accent == ShelfSettings.ACCENT_ORANGE,
                            accent = OrangeAccent,
                            onClick = { onAccent(ShelfSettings.ACCENT_ORANGE) }
                        )
                    }
                }
                SettingsBlock(title = stringResource(R.string.settings_currency)) {
                    ChipRow {
                        ShelfSettings.CURRENCIES.forEach { code ->
                            ShelfChip(
                                label = code,
                                selected = state.settings.currency == code,
                                accent = PetrolPrimary,
                                onClick = { onCurrency(code) }
                            )
                        }
                    }
                }
                SettingsBlock(title = stringResource(R.string.settings_date)) {
                    ChipRow {
                        ShelfChip(
                            label = stringResource(R.string.chip_date_dots),
                            selected = !state.settings.isoDates,
                            accent = PetrolPrimary,
                            onClick = { onDateFormat(false) }
                        )
                        ShelfChip(
                            label = stringResource(R.string.chip_date_iso),
                            selected = state.settings.isoDates,
                            accent = PetrolPrimary,
                            onClick = { onDateFormat(true) }
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.settings_note),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
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
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    letterSpacing = 2.0.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            HairLine(color = ShelfLine, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SettingsBlock(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            letterSpacing = 2.0.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        SectionRule(color = OrangeAccent, width = 36.dp, thickness = 2.dp)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        content()
    }
}

@Composable
private fun ShelfChip(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val description = if (selected) {
        stringResource(R.string.cd_chip_selected)
    } else {
        stringResource(R.string.cd_chip_idle)
    }
    Surface(
        onClick = onClick,
        modifier = Modifier
            .height(48.dp)
            .semantics { stateDescription = description },
        shape = RoundedCornerShape(10.dp),
        color = if (selected) accent.copy(alpha = 0.12f) else ShelfSurface,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) accent else ShelfLine)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) accent else TextPrimary
            )
        }
    }
}

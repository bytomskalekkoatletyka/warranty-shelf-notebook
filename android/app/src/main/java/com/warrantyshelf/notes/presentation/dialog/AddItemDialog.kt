package com.warrantyshelf.notes.presentation.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.theme.PetrolPrimary
import com.warrantyshelf.notes.core.theme.ShelfLine
import com.warrantyshelf.notes.core.theme.ShelfSurface
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.data.sample.SampleData

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var room by rememberSaveable { mutableStateOf(SampleData.ROOMS.first()) }
    var months by rememberSaveable { mutableIntStateOf(SampleData.WARRANTY_CHOICES[1]) }
    val canSave = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(14.dp),
        containerColor = ShelfSurface,
        title = {
            Text(
                text = stringResource(R.string.dialog_add_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    label = {
                        Text(
                            text = stringResource(R.string.field_name),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.field_room),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SampleData.ROOMS.chunked(2).forEach { group ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            group.forEach { option ->
                                PickerChip(
                                    label = option,
                                    selected = room == option,
                                    onClick = { room = option },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (group.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.field_warranty),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SampleData.WARRANTY_CHOICES.forEach { option ->
                        PickerChip(
                            label = stringResource(R.string.months_value, option),
                            selected = months == option,
                            onClick = { months = option },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, room, months) },
                enabled = canSave
            ) {
                Text(
                    text = stringResource(R.string.cta_save),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (canSave) PetrolPrimary else TextMuted
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_cancel),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )
            }
        }
    )
}

@Composable
private fun PickerChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) PetrolPrimary.copy(alpha = 0.12f) else ShelfSurface,
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            if (selected) PetrolPrimary else ShelfLine
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) PetrolPrimary else TextPrimary,
                maxLines = 1
            )
        }
    }
}

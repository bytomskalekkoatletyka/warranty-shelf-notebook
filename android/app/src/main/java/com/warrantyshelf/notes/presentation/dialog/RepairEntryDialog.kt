package com.warrantyshelf.notes.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.theme.OrangeAccent
import com.warrantyshelf.notes.core.theme.ShelfSurface
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary

@Composable
fun RepairEntryDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var note by rememberSaveable { mutableStateOf("") }
    var cost by rememberSaveable { mutableStateOf("") }
    val canSave = note.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(14.dp),
        containerColor = ShelfSurface,
        title = {
            Text(
                text = stringResource(R.string.dialog_repair_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    label = {
                        Text(
                            text = stringResource(R.string.field_note),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = cost,
                    onValueChange = { value -> cost = value.filter { it.isDigit() }.take(6) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(
                            text = stringResource(R.string.field_cost),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(note, cost.toIntOrNull() ?: 0) },
                enabled = canSave
            ) {
                Text(
                    text = stringResource(R.string.cta_save),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (canSave) OrangeAccent else TextMuted
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

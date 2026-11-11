package com.warrantyshelf.notes.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.R
import com.warrantyshelf.notes.core.theme.PetrolPrimary
import com.warrantyshelf.notes.core.theme.ShelfSurface
import com.warrantyshelf.notes.core.theme.TextMuted
import com.warrantyshelf.notes.core.theme.TextPrimary
import com.warrantyshelf.notes.core.theme.TextSecondary

@Composable
fun PauseDialog(onResume: () -> Unit, onMenu: () -> Unit) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = RoundedCornerShape(14.dp),
        containerColor = ShelfSurface,
        title = {
            Text(
                text = stringResource(R.string.dialog_pause_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_pause_body),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.cta_resume),
                    style = MaterialTheme.typography.labelLarge,
                    color = PetrolPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.cta_menu),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )
            }
        }
    )
}

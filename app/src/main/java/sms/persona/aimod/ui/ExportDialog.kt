package sms.persona.aimod.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import sms.persona.aimod.R

@Composable
fun ExportFormatDialog(
    title: String,
    onDismiss: () -> Unit,
    onPick: (asPdf: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                TextButton(onClick = { onPick(false) }) {
                    Text(stringResource(R.string.export_txt))
                }
                TextButton(onClick = { onPick(true) }) {
                    Text(stringResource(R.string.export_pdf))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

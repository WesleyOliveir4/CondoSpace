package com.example.condospace.presentation.ui.feature.condominium.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.condospace.R

@Composable
fun SuccessDialog(
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(id = R.string.success)) },
        text = { Text(stringResource(id = R.string.condominium_update_success)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SuccessDialogPreview() {
    MaterialTheme {
        SuccessDialog(onConfirm = {})
    }
}

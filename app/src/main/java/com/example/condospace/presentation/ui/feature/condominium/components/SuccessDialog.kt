package com.example.condospace.presentation.ui.feature.condominium.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SuccessDialog(
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Sucesso!") },
        text = { Text("Condomínio atualizado com sucesso!") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
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

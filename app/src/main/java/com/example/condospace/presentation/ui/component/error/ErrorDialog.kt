package com.example.condospace.presentation.ui.component.error

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun ErrorDialog(
    title: String = "Ops, ocorreu um erro",
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        confirmButton = {
            TextButton(onClick =
                onDismiss
            ) {
                Text(
                    text = "Ok, entendi!",
                    color = Color(0xFF1A56B3),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(text = message)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {
    CondoSpaceTheme {
        ErrorDialog(
            message = "Ocorreu um erro inesperado ao carregar as publicações.",
            onDismiss = {}
        )
    }
}

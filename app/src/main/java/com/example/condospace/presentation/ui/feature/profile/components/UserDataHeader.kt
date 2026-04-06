package com.example.condospace.presentation.ui.feature.profile.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.R
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.feature.publications.components.EditableProfileImage
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun UserDataHeader(
    user: UserUiModel,
    profileImageUri: Uri?,
    isEditable: Boolean = true,
    onImageSelected: (Uri) -> Unit = {},
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2962FF),
                        Color(0xFF1E4ED8)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EditableProfileImage(
                imageUri = if (!user.profilePicture.isNullOrEmpty()) Uri.parse(user.profilePicture) else null,
                imageRes = R.drawable.img_person_default,
                isEditable = isEditable,
                onImageSelected = onImageSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = user.name.ifBlank { "Usuário" },
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = user.condominium?.name ?: "",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun UserDataHeaderPreview() {
    CondoSpaceTheme {
        UserDataHeader(
            user = UserUiModel(name = "João Silva"),
            profileImageUri = null,
            isEditable = false,
            onImageSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserDataHeaderEditablePreview() {
    CondoSpaceTheme {
        UserDataHeader(
            user = UserUiModel(name = "João Silva"),
            profileImageUri = null,
            isEditable = true,
            onImageSelected = {}
        )
    }
}

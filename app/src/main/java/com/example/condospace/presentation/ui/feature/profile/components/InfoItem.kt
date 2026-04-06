package com.example.condospace.presentation.ui.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.R
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun InfoItem(
    icon: ImageVector,
    title: String,
    value: String,
    showEditOption: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChangeConfirmed: (String) -> Unit = {}
) {

    var isEditing by remember { mutableStateOf(false) }
    var editedValue by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF1FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2962FF)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isEditing) {

                if (visualTransformation != VisualTransformation.None){
                    OutlinedTextField(
                        value = "",
                        onValueChange = { editedValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions
                    )
                }else{
                    OutlinedTextField(
                        value = editedValue,
                        onValueChange = { editedValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions
                    )
                }

            } else {

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (showEditOption) {

            if (isEditing) {

                Row {
                    IconButton(
                        onClick = {
                            isEditing = false
                            onValueChangeConfirmed(editedValue)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.confirm),
                            tint = Color(0xFF2962FF)
                        )
                    }

                    IconButton(
                        onClick = {
                            isEditing = false
                            editedValue = value
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cancel),
                            tint = Color.Red
                        )
                    }
                }

            } else {

                IconButton(
                    onClick = {
                        isEditing = true
                        editedValue = value
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoItemPreview() {
    CondoSpaceTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoItem(
                icon = Icons.Default.Person,
                title = stringResource(R.string.full_name),
                value = "João da Silva Sauro",
                showEditOption = true
            )
        }
    }
}

package com.example.condospace.presentation.ui.feature.publications.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun EditableProfileImage(
    imageUri: Uri? = null,
    imageRes: Int? = null,
    size: Dp = 90.dp,
    isEditable: Boolean = true,
    onImageSelected: (Uri) -> Unit = {}
) {

    var selectedImage by remember { mutableStateOf(imageUri) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImage = it
            onImageSelected(it)
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {

        if (selectedImage != null) {

            AsyncImage(
                model = selectedImage,
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Image(
                painter = painterResource(id = imageRes ?: android.R.drawable.ic_menu_camera),
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        if (isEditable) {
            Box(
                modifier = Modifier
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar foto",
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
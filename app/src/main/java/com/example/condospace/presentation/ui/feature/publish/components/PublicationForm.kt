package com.example.condospace.presentation.ui.feature.publish.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.condospace.domain.model.Publication
import com.example.condospace.presentation.ui.enums.CategoryType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationForm(
    title: String,
    publicationType: PublicationType,
    onPublish: (Publication) -> Unit
) {
    var titleState by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryType?>(null) }
    var descriptionState by remember { mutableStateOf("") }
    var locationState by remember { mutableStateOf("") }
    var contactState by remember { mutableStateOf("") }
    var priceState by remember { mutableStateOf("") }
    var providerNameState by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var expanded by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val isButtonEnabled = when (publicationType) {
        PublicationType.PRODUCT -> {
            titleState.isNotBlank() &&
                    selectedCategory != null &&
                    descriptionState.isNotBlank() &&
                    selectedImageUri != null &&
                    priceState.isNotBlank()
        }
        PublicationType.SERVICE -> {
            titleState.isNotBlank() &&
                    descriptionState.isNotBlank() &&
                    selectedImageUri != null
        }
        PublicationType.RECOMMENDATION -> {
            titleState.isNotBlank() &&
                    descriptionState.isNotBlank() &&
                    providerNameState.isNotBlank() &&
                    selectedImageUri != null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color.Black),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            Text("Título")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = titleState,
                onValueChange = { titleState = it },
                placeholder = { Text("Ex: Sofá 3 lugares semi-novo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (publicationType == PublicationType.PRODUCT) {
                Spacer(Modifier.height(12.dp))

                Text("Categoria")
                Spacer(Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.title ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione a categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        CategoryType.entries.filter { it != CategoryType.ALLTYPES }.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.title) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            if (publicationType == PublicationType.RECOMMENDATION) {
                Spacer(Modifier.height(12.dp))
                Text("Nome do Prestador de Serviço")
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = providerNameState,
                    onValueChange = { providerNameState = it },
                    placeholder = { Text("Ex: João Silva") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Descrição")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = descriptionState,
                onValueChange = { descriptionState = it },
                placeholder = { Text("Descreva seu produto ou serviço...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Fotos")
            Spacer(Modifier.height(6.dp))

            PhotoPicker(
                imageUri = selectedImageUri,
                onPickPhoto = { galleryLauncher.launch("image/*") }
            )

            if (publicationType == PublicationType.PRODUCT) {
                Spacer(Modifier.height(12.dp))
                Text("Preço")
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = priceState,
                    onValueChange = { priceState = it },
                    placeholder = { Text("Ex: 150.00") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            if (publicationType == PublicationType.SERVICE) {
                Spacer(Modifier.height(12.dp))
                Text("Localização (Opcional)")
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = locationState,
                    onValueChange = { locationState = it },
                    placeholder = { Text("Ex: Bloco A, Apto 101") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            if (publicationType == PublicationType.RECOMMENDATION) {
                Text("Contato")
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = contactState,
                    onValueChange = { contactState = it },
                    placeholder = { Text("Telefone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val publication = Publication(
                        id = (1..10000).random(),
                        publicationOwner = "Morador CondoSpace",
                        serviceProvider = if (publicationType == PublicationType.RECOMMENDATION) providerNameState else null,
                        imageUrl = selectedImageUri.toString(),
                        title = titleState,
                        description = descriptionState,
                        detailedDescription = descriptionState,
                        publicationType = publicationType.toString(),
                        price = priceState.toDoubleOrNull() ?: 0.0,
                        likes = 0,
                        date = "24/05/2024",
                        imageRes = 0
                    )
                    onPublish(publication)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF),
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text("Publicar anúncio")
            }
        }
    }
}

@Composable
fun PhotoPicker(
    imageUri: Uri?,
    onPickPhoto: () -> Unit
) {
    val hasPhoto = imageUri != null

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (hasPhoto) Color(0xFF2962FF) else Color.LightGray),
        color = if (hasPhoto) Color(0xFFE8F0FE) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onPickPhoto() }
    ) {
        if (hasPhoto) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = "Clique para abrir a galeria",
                    color = Color.Gray
                )
            }
        }
    }
}

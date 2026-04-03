package com.example.condospace.presentation.ui.feature.publish.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.enums.CategoryType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationForm(
    title: String,
    publicationType: PublicationType,
    onPublish: (PublicationUiModel) -> Unit,
    initialPublication: PublicationUiModel? = null,
    user: UserUiModel
) {

    var titleState by remember { mutableStateOf(initialPublication?.title ?: "") }
    var selectedCategory by remember { 
        mutableStateOf(
            if (publicationType == PublicationType.PRODUCT) {
                CategoryType.entries.find { it.title == initialPublication?.publicationType }
            } else null
        ) 
    }
    var descriptionState by remember { mutableStateOf(initialPublication?.description ?: "") }
    var locationState by remember { mutableStateOf("") }
    var contactState by remember { mutableStateOf(initialPublication?.contact ?: "") }
    var priceState by remember { mutableStateOf(initialPublication?.price?.toString() ?: "") }
    var providerNameState by remember { mutableStateOf(initialPublication?.serviceProvider ?: "") }

    var images by remember { 
        mutableStateOf(
            initialPublication?.imagesSelectList ?: 
            initialPublication?.imageUrlList?.map { it.url.toUri() } ?:
            emptyList()
        ) 
    }

    var expanded by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        images = images + uris
    }

    val isButtonEnabled = when (publicationType) {
        PublicationType.PRODUCT -> {
            titleState.isNotBlank() &&
                    (selectedCategory != null || (initialPublication != null && titleState.isNotBlank())) &&
                    descriptionState.isNotBlank() &&
                    images.isNotEmpty() &&
                    priceState.isNotBlank()
        }
        PublicationType.SERVICE -> {
            titleState.isNotBlank() &&
                    descriptionState.isNotBlank() &&
                    images.isNotEmpty()
        }
        PublicationType.RECOMMENDATION -> {
            titleState.isNotBlank() &&
                    descriptionState.isNotBlank() &&
                    providerNameState.isNotBlank() &&
                    images.isNotEmpty()
        }
    }

    val isEditMode = initialPublication != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(title, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(16.dp))

            Text("Título")
            OutlinedTextField(
                value = titleState,
                onValueChange = { titleState = it },
                placeholder = { Text("Ex: Sofá 3 lugares semi-novo") },
                modifier = Modifier.fillMaxWidth()
            )

            if (publicationType == PublicationType.PRODUCT) {

                Spacer(Modifier.height(12.dp))
                Text("Categoria")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = selectedCategory?.title ?: initialPublication?.publicationType ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecione a categoria") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        CategoryType.entries
                            .filter { it != CategoryType.ALLTYPES }
                            .forEach { category ->

                                DropdownMenuItem(
                                    text = { Text(category.title) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                    }
                }
            }

            if (publicationType == PublicationType.RECOMMENDATION) {

                Spacer(Modifier.height(12.dp))
                Text("Nome do prestador")

                OutlinedTextField(
                    value = providerNameState,
                    onValueChange = { providerNameState = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Descrição")

            OutlinedTextField(
                value = descriptionState,
                onValueChange = { descriptionState = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Fotos")

            PhotoCarousel(
                images = images,
                onAddPhoto = { galleryLauncher.launch("image/*") },
                onRemovePhoto = { uri ->
                    images = images - uri
                }
            )

            if (publicationType == PublicationType.PRODUCT) {
                Spacer(Modifier.height(12.dp))
                Text("Preço")

                OutlinedTextField(
                    value = priceState,
                    onValueChange = { priceState = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (publicationType == PublicationType.SERVICE) {

                Spacer(Modifier.height(12.dp))
                Text("Localização")

                OutlinedTextField(
                    value = locationState,
                    onValueChange = { locationState = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (publicationType == PublicationType.RECOMMENDATION) {

                Spacer(Modifier.height(12.dp))
                Text("Contato")

                OutlinedTextField(
                    value = contactState,
                    onValueChange = { contactState = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val date = initialPublication?.date ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                    val publication = PublicationUiModel(
                        id = initialPublication?.id ?: UUID.randomUUID().toString(),
                        publicationOwnerUuid = user.uuid,
                        publicationCondominiumId = user.condominium?.id ?: "",
                        publicationOwner = user.name,
                        serviceProvider = providerNameState,
                        contact = contactState,
                        price = priceState.toDoubleOrNull() ?: 0.0,
                        imagesSelectList = images,
                        title = titleState,
                        description = descriptionState,
                        publicationType = selectedCategory?.title ?: initialPublication?.publicationType ?: publicationType.value,
                        likes = initialPublication?.likes ?: 0,
                        date = date,
                        imageUrlList = if (images.all { it.toString().startsWith("http") }) initialPublication?.imageUrlList else null
                    )
                    onPublish(publication)
                },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF354EAB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isEditMode) "Salvar alterações" else "Publicar anúncio")
            }
        }
    }
}

@Composable
fun PhotoCarousel(
    images: List<Uri>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Uri) -> Unit
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(images) { image ->

            Box {

                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { onRemovePhoto(image) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0))
                    .clickable { onAddPhoto() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Adicionar foto"
                )
            }
        }
    }
}

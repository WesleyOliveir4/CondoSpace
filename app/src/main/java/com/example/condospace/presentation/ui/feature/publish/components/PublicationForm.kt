package com.example.condospace.presentation.ui.feature.publish.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.OutlinedTextFieldCS
import com.example.condospace.presentation.ui.enums.CategoryType
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.utils.CurrencyUtils
import com.example.condospace.presentation.utils.PhoneUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Componente Stateful que gerencia a lógica e o estado do formulário.
 */
@Composable
fun PublicationForm(
    title: String,
    publicationType: PublicationType,
    onPublish: (PublicationUiModel) -> Unit,
    initialPublication: PublicationUiModel? = null,
    user: UserUiModel
) {
    // --- Estados ---
    var titleState by remember { mutableStateOf(initialPublication?.title ?: "") }
    var descriptionState by remember { mutableStateOf(initialPublication?.description ?: "") }
    var priceState by remember {
        mutableStateOf(
            initialPublication?.price?.let {
                val cents = (it * 100).toLong().toString()
                CurrencyUtils.formatToBRL(cents)
            } ?: ""
        )
    }
    // contactState armazena apenas números. Usado manualmente apenas em RECOMMENDATION.
    var contactState by remember {
        mutableStateOf(if (publicationType == PublicationType.RECOMMENDATION) initialPublication?.contact?.filter { it.isDigit() } ?: "" else "")
    }
    var providerNameState by remember { mutableStateOf(initialPublication?.serviceProvider ?: "") }
    var locationState by remember { mutableStateOf("") }

    var selectedCategory by remember {
        mutableStateOf(
            if (publicationType == PublicationType.PRODUCT) {
                CategoryType.entries.find { it.title == initialPublication?.publicationType }
            } else null
        )
    }

    var images by remember {
        mutableStateOf(
            initialPublication?.imagesSelectList ?:
            initialPublication?.imageUrlList?.map { it.url.toUri() } ?:
            emptyList()
        )
    }

    // --- Auxiliares ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> -> images = images + uris }

    val isButtonEnabled = remember(publicationType, titleState, descriptionState, images, priceState, providerNameState, selectedCategory, contactState) {
        val baseValid = titleState.isNotBlank() && descriptionState.isNotBlank() && images.isNotEmpty()
        when (publicationType) {
            PublicationType.PRODUCT -> baseValid && selectedCategory != null && priceState.isNotBlank()
            PublicationType.SERVICE -> baseValid
            PublicationType.RECOMMENDATION -> baseValid && providerNameState.isNotBlank() && contactState.length >= 10
        }
    }

    // --- Configuração Dinâmica baseada no PublicationType ---
    val finalPublicationType = when (publicationType) {
        PublicationType.PRODUCT -> selectedCategory?.title ?: initialPublication?.publicationType ?: ""
        PublicationType.SERVICE -> ServiceType.SERVICE.value
        PublicationType.RECOMMENDATION -> ServiceType.RECOMMENDATION.value
    }

    PublicationFormCard(
        formTitle = title,
        titleValue = titleState,
        onTitleChange = { titleState = it },
        descriptionValue = descriptionState,
        onDescriptionChange = { descriptionState = it },
        images = images,
        onAddPhoto = { galleryLauncher.launch("image/*") },
        onRemovePhoto = { uri -> images = images - uri },
        buttonText = if (initialPublication != null) "Salvar alterações" else "Publicar anúncio",
        isButtonEnabled = isButtonEnabled,
        onButtonClick = {
            val date = initialPublication?.date ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            val publication = PublicationUiModel(
                id = initialPublication?.id ?: UUID.randomUUID().toString(),
                publicationOwnerUuid = user.uuid,
                publicationCondominiumId = user.condominium?.id ?: "",
                publicationOwner = user.name,
                serviceProvider = providerNameState,
                // Regra: RECOMMENDATION usa contactState, SERVICE e PRODUCT usam user.phoneNumber
                contact = if (publicationType == PublicationType.RECOMMENDATION) {
                    PhoneUtils.removeMask(contactState)
                } else {
                    PhoneUtils.removeMask(user.phoneNumber)
                },
                price = CurrencyUtils.currencyToDouble(priceState),
                imagesSelectList = images,
                title = titleState,
                description = descriptionState,
                publicationType = finalPublicationType,
                likes = initialPublication?.likes ?: 0,
                date = date,
                imageUrlList = if (images.all { it.toString().startsWith("http") }) initialPublication?.imageUrlList else null
            )
            onPublish(publication)
        },
        // --- Parâmetros de Configuração de Campos ---
        showCategorySelector = publicationType == PublicationType.PRODUCT,
        selectedCategory = selectedCategory,
        onCategorySelected = { selectedCategory = it },

        showProviderField = publicationType == PublicationType.RECOMMENDATION,
        providerValue = providerNameState,
        onProviderChange = { providerNameState = it },

        showPriceField = publicationType == PublicationType.PRODUCT,
        priceValue = priceState,
        onPriceChange = { priceState = CurrencyUtils.formatToBRL(it) },

        showLocationField = publicationType == PublicationType.SERVICE,
        locationValue = locationState,
        onLocationChange = { locationState = it },

        showContactField = publicationType == PublicationType.RECOMMENDATION,
        contactValue = contactState,
        onContactChange = { input ->
            val digits = input.filter { it.isDigit() }
            if (digits.length <= 11) contactState = digits
        },
        contactVisualTransformation = PhoneUtils.phoneVisualTransformation
    )
}

/**
 * Componente Stateless (Dumb) que define a estrutura visual do Card do formulário.
 */
@Composable
fun PublicationFormCard(
    formTitle: String,
    titleValue: String,
    onTitleChange: (String) -> Unit,
    descriptionValue: String,
    onDescriptionChange: (String) -> Unit,
    images: List<Uri>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Uri) -> Unit,
    buttonText: String,
    isButtonEnabled: Boolean,
    onButtonClick: () -> Unit,

    // Configurações de visibilidade e valores específicos
    showCategorySelector: Boolean = false,
    selectedCategory: CategoryType? = null,
    onCategorySelected: (CategoryType) -> Unit = {},

    showProviderField: Boolean = false,
    providerValue: String = "",
    onProviderChange: (String) -> Unit = {},

    showPriceField: Boolean = false,
    priceValue: String = "",
    onPriceChange: (String) -> Unit = {},

    showLocationField: Boolean = false,
    locationValue: String = "",
    onLocationChange: (String) -> Unit = {},

    showContactField: Boolean = false,
    contactValue: String = "",
    onContactChange: (String) -> Unit = {},
    contactVisualTransformation: VisualTransformation = VisualTransformation.None
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = formTitle, style = MaterialTheme.typography.titleMedium)

            FormField(
                label = "Título",
                value = titleValue,
                onValueChange = onTitleChange,
                placeholder = "Ex: Título do seu anúncio"
            )

            if (showCategorySelector) {
                CategorySelector(selectedCategory, onCategorySelected)
            }

            if (showProviderField) {
                FormField(
                    label = "Nome do prestador",
                    value = providerValue,
                    onValueChange = onProviderChange,
                    placeholder = "Quem você está indicando?"
                )
            }

            FormField(
                label = "Descrição",
                value = descriptionValue,
                onValueChange = onDescriptionChange,
                modifier = Modifier.height(120.dp),
                singleLine = false,
                placeholder = "Dê mais detalhes sobre o que está anunciando..."
            )

            Text(text = "Fotos", style = MaterialTheme.typography.labelMedium)
            PhotoCarousel(
                images = images,
                onAddPhoto = onAddPhoto,
                onRemovePhoto = onRemovePhoto
            )

            if (showPriceField) {
                FormField(
                    label = "Preço em R$",
                    value = priceValue,
                    onValueChange = onPriceChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = "0,00"
                )
            }

            if (showLocationField) {
                FormField(
                    label = "Localização / Atendimento",
                    value = locationValue,
                    onValueChange = onLocationChange,
                    placeholder = "Onde você atende?"
                )
            }

            if (showContactField) {
                FormField(
                    label = "Contato do prestador",
                    value = contactValue,
                    onValueChange = onContactChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    placeholder = "(00) 00000-0000",
                    visualTransformation = contactVisualTransformation
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onButtonClick,
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF354EAB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}

// --- Componentes de Apoio ---

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextFieldCS(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder ?: "",
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        visualTransformation = visualTransformation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(selectedCategory: CategoryType?, onCategorySelected: (CategoryType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextFieldCS(
                label = "Categoria",
                value = selectedCategory?.title ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = "Selecione a categoria",
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CategoryType.entries.filter { it != CategoryType.ALLTYPES }.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.title) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoCarousel(images: List<Uri>, onAddPhoto: () -> Unit, onRemovePhoto: (Uri) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .padding(4.dp)
                        .background(Color.White.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
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
                Icon(Icons.Default.Add, contentDescription = "Adicionar foto")
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "Product Form")
@Composable
fun PreviewProductForm() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            PublicationForm(
                title = "Anunciar Produto",
                publicationType = PublicationType.PRODUCT,
                onPublish = {},
                user = UserUiModel(name = "Usuário Teste", phoneNumber = "11999999999")
            )
        }
    }
}

@Preview(showBackground = true, name = "Service Form")
@Composable
fun PreviewServiceForm() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            PublicationForm(
                title = "Oferecer Serviço",
                publicationType = PublicationType.SERVICE,
                onPublish = {},
                user = UserUiModel(name = "Usuário Teste", phoneNumber = "11999999999")
            )
        }
    }
}

@Preview(showBackground = true, name = "Recommendation Form")
@Composable
fun PreviewRecommendationForm() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            PublicationForm(
                title = "Indicar Serviço",
                publicationType = PublicationType.RECOMMENDATION,
                onPublish = {},
                user = UserUiModel(name = "Usuário Teste", phoneNumber = "11999999999")
            )
        }
    }
}

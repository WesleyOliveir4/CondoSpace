package com.example.condospace.presentation.ui.feature.publish.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.condospace.R
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.model.UserUiModel

enum class PublicationType(var value: String) {
    PRODUCT("Produto"),
    SERVICE("Serviço"),
    RECOMMENDATION("Recomendação")
}

@Composable
fun PublicationTypeSelector(
    selectedType: PublicationType?,
    onTypeSelected: (PublicationType) -> Unit
) {

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color.Black)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = stringResource(R.string.publish_type_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            PublicationTypeItem(
                title = stringResource(R.string.publish_type_product_title),
                description = stringResource(R.string.publish_type_product_desc),
                icon = Icons.Default.Inventory2,
                selected = selectedType == PublicationType.PRODUCT
            ) {
                onTypeSelected(PublicationType.PRODUCT)
            }

            Spacer(Modifier.height(12.dp))

            PublicationTypeItem(
                title = stringResource(R.string.publish_type_service_title),
                description = stringResource(R.string.publish_type_service_desc),
                icon = Icons.Default.Build,
                selected = selectedType == PublicationType.SERVICE
            ) {
                onTypeSelected(PublicationType.SERVICE)
            }

            Spacer(Modifier.height(12.dp))

            PublicationTypeItem(
                title = stringResource(R.string.publish_type_recommendation_title),
                description = stringResource(R.string.publish_type_recommendation_desc),
                icon = Icons.Default.ThumbUp,
                selected = selectedType == PublicationType.RECOMMENDATION
            ) {
                onTypeSelected(PublicationType.RECOMMENDATION)
            }
        }
    }
}

@Composable
fun PublicationTypeItem(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val borderColor =
        if (selected) Color(0xFF2962FF) else Color(0xFFE0E0E0)

    val background =
        if (selected) Color(0xFFE8F0FE) else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = background,
        border = BorderStroke(1.dp, borderColor)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color(0xFF2962FF) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CreatePublicationScreen(
    userUiModel: UserUiModel,
    onPublicationCreated: (PublicationUiModel) -> Unit = {}
) {
    var selectedType by remember { mutableStateOf<PublicationType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PublicationTypeSelector(
            selectedType = selectedType,
            onTypeSelected = { type ->
                selectedType = if (selectedType == type) null else type
            }
        )

        Spacer(Modifier.height(16.dp))

        if (selectedType != null) {
            PublicationForm(
                user = userUiModel,
                title = when (selectedType) {
                    PublicationType.PRODUCT -> stringResource(R.string.publication_info_ad)
                    PublicationType.SERVICE -> stringResource(R.string.publication_info_service)
                    PublicationType.RECOMMENDATION -> stringResource(R.string.publication_info_recommendation)
                    null -> ""
                },
                publicationType = selectedType!!,
                onPublish = { publication ->
                    onPublicationCreated(publication)
                    selectedType = null
                }
            )
        }
    }
}

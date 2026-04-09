package com.example.condospace.presentation.ui.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.condospace.presentation.model.PublicationImageUiModel
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.ui.feature.home.mocks.PublicationsMocksPreview

@Composable
fun PublicationsCard(
    publication: PublicationUiModel,
    onClick: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(240.dp)
            .padding(end = 12.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color.Black)
            .clickable { onClick(publication.id, publication.publicationType) },
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent)
    ) {

        Column {

            AsyncImage(
                model = publication.imageUrlList?.firstOrNull()?.url,
                contentDescription = publication.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = publication.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = publication.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!publication.publicationType.equals(ServiceType.EXTERNAL.value, ignoreCase = true)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${publication.likes}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PublicationsSection(
    title: String,
    publications: List<PublicationUiModel>,
    onSeeMoreClick: (String, List<String>?) -> Unit = { _, _ -> },
    onItemClick: (String, String) -> Unit = { _, _ -> }
) {
    if (publications.isEmpty()) return
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Ver mais",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    onSeeMoreClick(
                        publications.first().publicationType,
                        publications.map { it.id }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(start = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            items(publications) { publication ->
                PublicationsCard(
                    publication = publication,
                    onClick = { id, type ->
                        onItemClick(id, type)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationsCardPreview() {
    MaterialTheme {
        PublicationsCard(
            publication = PublicationUiModel(
                id = "1",
                publicationOwnerUuid = "uuid",
                publicationCondominiumId = "condoId",
                publicationOwner = "João Silva",
                title = "Pintura Residencial",
                description = "Ofereço serviços de pintura interna e externa com ótimo acabamento e preço justo.",
                publicationType = "Serviço",
                price = 150.0,
                likes = 42,
                date = "2023-10-27",
                imageUrlList = listOf(PublicationImageUiModel(url = "https://example.com/image.jpg", publicId = "1"))
            ),
            onClick = {id, type -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationsSectionPreview() {
    MaterialTheme {
        PublicationsSection(
            title = "Serviços em destaque na região",
            publications = PublicationsMocksPreview().listMockUi
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationsSectionEmptyPreview() {
    MaterialTheme {
        PublicationsSection(
            title = "Serviços em destaque na região",
            publications = emptyList()
        )
    }
}

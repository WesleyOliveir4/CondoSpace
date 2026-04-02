package com.example.condospace.presentation.ui.feature.favorites.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.condospace.presentation.model.PublicationUiModel

@Composable
fun SearchPublications(
    publications: List<PublicationUiModel>,
    onPublicationClick: (PublicationUiModel) -> Unit = {}
) {

    var searchText by remember { mutableStateOf("") }

    val filteredList = publications.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
    }

    Column {

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Buscar favoritos...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        LazyColumn {
            items(filteredList) { publication ->
                PublicationItem(
                    publication = publication,
                    onClick = { onPublicationClick(publication) }
                )
            }
        }
    }
}

@Composable
fun PublicationItem(
    publication: PublicationUiModel,
    onClick: () -> Unit = {}
) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color.Black),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = publication.imageUrlList?.firstOrNull(),
                contentDescription = publication.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    publication.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    publication.description,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    publication.description,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8F0FE)
                ) {
                    Text(
                        text = publication.publicationType,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

package com.example.condospace.presentation.ui.feature.publish.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.condospace.presentation.model.PublicationUiModel

@Composable
fun PublicationCardList(
    title: String,
    publications: List<PublicationUiModel>,
    modifier: Modifier = Modifier,
    onEditClick: (PublicationUiModel) -> Unit = {},
    onDeleteClick: (PublicationUiModel) -> Unit = {}
) {
    if (publications.isEmpty()) {
        return
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                publications.forEach { publication ->
                    PublicationCardItem(
                        publication = publication,
                        onEditClick = { onEditClick(publication) },
                        onDeleteClick = { onDeleteClick(publication) }
                    )
                }
            }
        }
    }
}

@Composable
fun PublicationCardItem(
    publication: PublicationUiModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {

        Row(
            modifier = Modifier
                .padding(12.dp),
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
                    text = publication.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = publication.description,
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

            Column {

                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar"
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

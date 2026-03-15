package com.example.condospace.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.condospace.model.Product

@Composable
fun ProductCard(product: Product) {

    Card(
        modifier = Modifier
            .width(220.dp)
            .padding(end = 12.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column {

            Image(
                painter = painterResource(product.imageRes),
                contentDescription = product.tituloProduto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = product.tituloProduto,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = product.descricaoProduto,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB400),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text("${product.notaProduto}")

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        "(${product.numeroAvaliacoesProduto})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProductSection(
    title: String,
    products: List<Product>,
    onSeeMoreClick: () -> Unit = {}
) {

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
                modifier = Modifier.clickable { onSeeMoreClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(start = 16.dp)
        ) {

            items(products) { product ->
                ProductCard(product)
            }

        }
    }
}
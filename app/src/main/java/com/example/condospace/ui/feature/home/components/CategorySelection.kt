package com.example.condospace.ui.feature.home.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.ui.enums.CategoryType

@Composable
fun CategoriesSection(
    onCategoryClick: (CategoryType) -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(
            bottomStart = 20.dp,
            bottomEnd = 20.dp
        ),
        shadowElevation = 4.dp
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Categorias",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryItem(CategoryType.PROPERTIES, onCategoryClick)
                    CategoryItem(CategoryType.ELECTRONICS, onCategoryClick)
                    CategoryItem(CategoryType.BOOKS, onCategoryClick)
                    CategoryItem(CategoryType.OUTHERS, onCategoryClick)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryItem(CategoryType.OUTFIT, onCategoryClick)
                    CategoryItem(CategoryType.FURNITURE, onCategoryClick)
                    CategoryItem(CategoryType.SERVICES, onCategoryClick)
                    CategoryItem(CategoryType.ALLTYPES, onCategoryClick)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryType,
    onClick: (CategoryType) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClick(category)
        }
    ) {

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(category.background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.title
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.title,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CategoriesPreview() {
    CategoriesSection(
        onCategoryClick = {}
    )
}

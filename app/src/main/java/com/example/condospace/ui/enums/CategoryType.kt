package com.example.condospace.ui.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class CategoryType(
    val title: String,
    val icon: ImageVector,
    val background: Color
) {
    PROPERTIES("Imóveis", Icons.Default.Home, Color(0xFFD6E4FF)),
    ELECTRONICS("Eletro", Icons.Default.Tv, Color(0xFFE9D8FD)),
    BOOKS("Livros", Icons.Default.MenuBook, Color(0xFFD1FAE5)),
    OUTHERS("Outros", Icons.Default.Star, Color(0xFFFBCFE8)),

    OUTFIT("Roupas", Icons.Default.Checkroom, Color(0xFFFBCFE8)),
    FURNITURE("Móveis", Icons.Default.Chair, Color(0xFFFDE68A)),
    SERVICES("Serviços", Icons.Default.Build, Color(0xFFBAE6FD)),
    ALLTYPES("Todos", Icons.Default.List, Color(0xFFD6E4FF))
}
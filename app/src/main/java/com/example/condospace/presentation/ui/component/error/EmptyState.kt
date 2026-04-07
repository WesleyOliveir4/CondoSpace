package com.example.condospace.presentation.ui.component.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.SearchOff,
    title: String,
    subtitle: String ?= null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF1A56B3) // Blue color from the image
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A56B3),
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        if (!subtitle.isNullOrEmpty()){
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF1A56B3),
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    CondoSpaceTheme {
        EmptyState(
            title = "Nenhuma publicação\npara o seu condomínio.",
            subtitle = "Venha ser o primeiro!"
        )
    }
}

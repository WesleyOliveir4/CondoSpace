package com.example.condospace.ui.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.condospace.ui.theme.CondoSpaceTheme

@Composable
fun ProfileScreen() {
    CondoSpaceTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(4285563448)
        ) {
            ProfileScreenContent()
        }
    }
}


@Composable
fun ProfileScreenContent() {
    Column(
        modifier = Modifier
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 85.dp),
            text = "Profile Screen",
            textAlign = TextAlign.Center,
            fontSize = 35.sp,
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    CondoSpaceTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(4285563448)
        ) {
            ProfileScreenContent()
        }
    }
}
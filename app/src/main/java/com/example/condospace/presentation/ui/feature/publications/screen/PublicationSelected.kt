package com.example.condospace.presentation.ui.feature.publications.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.publications.components.BottomContactBar
import com.example.condospace.presentation.ui.feature.publications.components.PublicationImage
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun PublicationSelectedScreen(navController: NavHostController) {
    CondoSpaceTheme {
        PublicationSelectedScreenContent(
            navController,
            PublicationsMocks().getFavoritedPublications()[0]
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationSelectedScreenContent(
    navController: NavHostController,
    publication: PublicationUiModel,
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Publicação",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomContactBar(
                price = publication.price,
                onClick = {

                }
            )
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                PublicationImage(publication.imageUrlList?.first()?.url ?: "")

                PublicationContent(publication)
            }
        }
    }

}


@Composable
fun PublicationContent(publication: PublicationUiModel) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = publication.title,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LikesRow(publication.likes)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = publication.date,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = publication.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LikesRow(likes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.Red
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(text = "$likes curtidas")
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationSelectedScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        PublicationSelectedScreenContent(
            navController,
            PublicationsMocks().getFavoritedPublications()[0]
        )
    }
}
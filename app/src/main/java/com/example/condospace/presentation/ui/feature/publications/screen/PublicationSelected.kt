package com.example.condospace.presentation.ui.feature.publications.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.PublicationUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.publications.components.BottomContactBar
import com.example.condospace.presentation.ui.feature.publications.components.PublicationDetails
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

                val imageUrls = publication.imageUrlList?.map { it.url } ?: emptyList()
                PublicationImage(imageUrls = imageUrls)

                PublicationDetails(
                    publication = publication,
                    isFavorite = true,
                    onLikeClick = {},
                    modifier = Modifier
                )
            }
        }
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
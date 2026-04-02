package com.example.condospace.presentation.ui.feature.publications.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.publish.components.PublicationForm
import com.example.condospace.presentation.ui.feature.publish.components.PublicationType
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme


@Composable
fun EditPublicationScreen(
    navController: NavHostController,
    publicationId: String
) {
    CondoSpaceTheme {
        EditPublicationScreenContent(
            navController,
            publicationId
        )
    }
}


@Composable
fun EditPublicationScreenContent(
    navController: NavHostController,
    publicationId: String,
) {

    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Editar publicação",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->


        val publication = remember(publicationId) {
            PublicationsMocks().getPublications().find { it.id == publicationId }
                ?: PublicationsMocks().getPublications().first()
        }

        val currentPublicationType = remember(publication.publicationType) {
            PublicationType.entries.find { it.value == publication.publicationType }
                ?: PublicationType.PRODUCT
        }

//        val initialImages = remember {
//            if (publication.imageUrl.isNotBlank()) {
//                listOf(publication.imageUrl.toUri())
//            } else emptyList()
//        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(
                    vertical = 16.dp,
                )
        ) {

            PublicationForm(
                title = if (currentPublicationType == PublicationType.SERVICE)
                    "Informações do serviço"
                else
                    "Informações do anúncio",

                publicationType = currentPublicationType,

                initialImages = emptyList(),

                onPublish = { updatedPublication ->
                    val finalPublication = updatedPublication.copy(
                        id = publication.id,
                        likes = publication.likes,
                        date = publication.date
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun  EditPublicationScreenPreview() {
    val first = PublicationsMocks().getPublications().first()
    val navController = rememberNavController()


    CondoSpaceTheme {
        EditPublicationScreenContent(navController, first.id)
    }
}
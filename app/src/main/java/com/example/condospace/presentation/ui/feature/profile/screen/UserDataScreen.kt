package com.example.condospace.presentation.ui.feature.profile.screen


import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.profile.components.UserDataHeader
import com.example.condospace.presentation.ui.feature.profile.components.UserDataInfoCard
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun UserDataScreen(
    navController: NavHostController,
) {
    CondoSpaceTheme {
        UserDataScreenContent(
            navController = navController,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun UserDataScreenContent(
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Meus dados",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            UserDetailsComponent(
                user = UserUiModel(
                    uuid = Uuid.random().toString(),
                    name = "João Silva",
                    phoneNumber = "(11) 98999-2000",
                    profilePicture = null,
                    email = "john.jay@example.com",
                    condominium = CondominiumUiModel(
                        name = "Condomínio Exemplo",
                        cep = "12345-678",
                    )
                )
            )
        }
    }

}

@Composable
fun UserDetailsComponent(
    user: UserUiModel
) {
    var profileImage by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) {
        UserDataHeader(
            user = user,
            profileImageUri = profileImage,
            isEditable = true,
            onImageSelected = { newUri ->
                profileImage = newUri
            }
        )

        UserDataInfoCard(
            user = user,
            onPhoneChangeConfirmed = { /* TODO: Implement update */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserDataScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        UserDataScreenContent(
            navController,
        )
    }
}

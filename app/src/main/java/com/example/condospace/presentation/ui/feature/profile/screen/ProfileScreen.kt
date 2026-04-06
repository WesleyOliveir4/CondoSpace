package com.example.condospace.presentation.ui.feature.profile.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.feature.profile.components.ContactCard
import com.example.condospace.presentation.ui.feature.profile.components.OptionsCard
import com.example.condospace.presentation.ui.feature.profile.components.ProfileHeader
import com.example.condospace.presentation.ui.feature.profile.state.ProfileUiState
import com.example.condospace.presentation.ui.feature.profile.viewmodel.ProfileViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    navigateToUserData: () -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        ProfileScreenContent(
            navController = navController,
            uiState = uiState,
            navigateToUserData = navigateToUserData,
            navigateToSelectCondominium = navigateToSelectCondominium
        )
    }
}


@Composable
fun ProfileScreenContent(
    navController: NavHostController,
    uiState: ProfileUiState,
    navigateToUserData: () -> Unit,
    navigateToSelectCondominium: (String) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Profile") },
        topBar = {
            CondoSpaceTopBar(
                condominiumName = uiState.condominiumName,
                residenceSelector = {
                    navigateToSelectCondominium(uiState.user.uuid)
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
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    ProfileHeader(
                        user = uiState.user,
                        condominiumName = uiState.condominiumName
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContactCard(user = uiState.user)

                    Spacer(modifier = Modifier.height(16.dp))

                    OptionsCard(
                        onMyDataClick = navigateToUserData,
                        onChangePasswordClick = { },
                        onSettingsClick = { }
                    )

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LogoutButton()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Condo Market v1.0.0",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun LogoutButton() {

    OutlinedButton(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = Color.Red,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {

        Icon(
            Icons.Default.Logout,
            contentDescription = null,
            tint = Color.Red
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            "Sair da conta",
            color = Color.Red
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        ProfileScreenContent(
            navController,
            uiState = ProfileUiState(
                user = UserUiModel(name = "Usuário Teste", email = "teste@email.com"),
                condominiumName = "Condomínio Exemplo"
            ),
            navigateToUserData = {},
            navigateToSelectCondominium = {}
        )
    }
}

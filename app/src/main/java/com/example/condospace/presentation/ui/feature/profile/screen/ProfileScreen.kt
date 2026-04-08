package com.example.condospace.presentation.ui.feature.profile.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.condospace.presentation.ui.component.error.ErrorDialog
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
    viewModel: ProfileViewModel = koinViewModel(),
    navigateToChangePassword: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CondoSpaceTheme {
        ProfileScreenContent(
            navController = navController,
            uiState = uiState,
            navigateToUserData = navigateToUserData,
            navigateToSelectCondominium = navigateToSelectCondominium,
            navigateToChangePassword = navigateToChangePassword,
            navigateToSettings = navigateToSettings,
            onClearError = { viewModel.clearError() },
            onLogoutConfirm = {
                viewModel.logout {
                    navigateToLogin()
                }
            }
        )
    }
}


@Composable
fun ProfileScreenContent(
    navController: NavHostController,
    uiState: ProfileUiState,
    navigateToUserData: () -> Unit,
    navigateToSelectCondominium: (String) -> Unit,
    navigateToChangePassword: () -> Unit,
    navigateToSettings: () -> Unit,
    onClearError: () -> Unit = {},
    onLogoutConfirm: () -> Unit
) {


    Scaffold(
        bottomBar = { NavBar(navController, "Profile") },
        topBar = {
            CondoSpaceTopBar(
                condominiumName = when(uiState) {
                    is ProfileUiState.Success -> uiState.condominiumName
                    else -> "Carregando..."
                },
                residenceSelector = {
                    if (uiState is ProfileUiState.Success) {
                        navigateToSelectCondominium(uiState.user.uuid)
                    }
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
            when (uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ProfileUiState.Error -> {
                    ErrorDialog(
                        message = uiState.message,
                        onDismiss = onClearError
                    )
                }

                is ProfileUiState.Success -> {
                    ProfileScreenBody(
                        uiState = uiState,
                        navigateToUserData = navigateToUserData,
                        navigateToChangePassword = navigateToChangePassword,
                        navigateToSettings = navigateToSettings,
                        onLogoutConfirm = onLogoutConfirm
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreenBody(
    uiState: ProfileUiState.Success,
    navigateToUserData: () -> Unit,
    navigateToChangePassword: () -> Unit,
    navigateToSettings: () -> Unit,
    onLogoutConfirm: () -> Unit
){

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogoutConfirm()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

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
            onChangePasswordClick = { navigateToChangePassword() },
            onSettingsClick = { navigateToSettings() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LogoutButton(onClick = { showLogoutDialog = true })

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Condo Market v1.0.0",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {

    OutlinedButton(
        onClick = onClick,
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

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Sair da conta") },
        text = { Text(text = "Tem certeza que deseja sair da sua conta?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Sair", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        ProfileScreenContent(
            navController,
            uiState = ProfileUiState.Success(
                user = UserUiModel(name = "Usuário Teste", email = "teste@email.com"),
                condominiumName = "Condomínio Exemplo"
            ),
            navigateToUserData = {},
            navigateToSelectCondominium = {},
            navigateToChangePassword = {},
            navigateToSettings = {},
            onLogoutConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenErrorPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        ProfileScreenContent(
            navController,
            uiState = ProfileUiState.Error(
                message = "Erro ao carregar perfil"
            ),
            navigateToUserData = {},
            navigateToSelectCondominium = {},
            navigateToChangePassword = {},
            navigateToSettings = {},
            onLogoutConfirm = {}
        )
    }
}

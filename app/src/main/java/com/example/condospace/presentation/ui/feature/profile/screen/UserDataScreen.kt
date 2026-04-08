package com.example.condospace.presentation.ui.feature.profile.screen


import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.profile.components.UserDataHeader
import com.example.condospace.presentation.ui.feature.profile.components.UserDataInfoCard
import com.example.condospace.presentation.ui.feature.profile.state.UserDataUiState
import com.example.condospace.presentation.ui.feature.profile.viewmodel.UserDataViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserDataScreen(
    navController: NavHostController,
    viewModel: UserDataViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CondoSpaceTheme {
        UserDataScreenContent(
            navController = navController,
            uiState = uiState,
            onNameChange = { viewModel.updateUserName(it) },
            onPhoneChange = { viewModel.updateUserPhone(it) },
            onImageSelected = { viewModel.updateProfilePicture(it) },
            onClearMessages = { viewModel.clearMessages() }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreenContent(
    navController: NavHostController,
    uiState: UserDataUiState,
    onNameChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onImageSelected: (Uri) -> Unit = {},
    onClearMessages: () -> Unit = {}
) {

    LaunchedEffect(uiState) {
        if (uiState is UserDataUiState.Success) {
            uiState.successMessage?.let {
                onClearMessages()
            }
            uiState.error?.let {
                onClearMessages()
            }
        }
    }

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
            when (uiState) {
                is UserDataUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is UserDataUiState.Error -> {
                    ErrorDialog(
                        title = "Erro",
                        message = uiState.message,
                        onDismiss = { onClearMessages() }
                    )
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }

                is UserDataUiState.Success -> {
                    UserDetailsComponent(
                        user = uiState.user,
                        onNameChange = onNameChange,
                        onPhoneChange = onPhoneChange,
                        onImageSelected = onImageSelected
                    )
                    
                    if (uiState.isUpdating) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailsComponent(
    user: UserUiModel,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onImageSelected: (Uri) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) {
        UserDataHeader(
            user = user,
            profileImageUri = null,
            isEditable = true,
            onImageSelected = onImageSelected,
        )

        UserDataInfoCard(
            user = user,
            onPhoneChangeConfirmed = onPhoneChange,
            onNameChangeConfirmed = onNameChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserDataScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        UserDataScreenContent(
            navController = navController,
            uiState = UserDataUiState.Success(
                user = UserUiModel(
                    name = "João Silva",
                    phoneNumber = "(11) 98999-2000",
                    email = "john.jay@example.com",
                    condominium = CondominiumUiModel(
                        name = "Condomínio Exemplo",
                        cep = "12345-678",
                    )
                )
            )
        )
    }
}

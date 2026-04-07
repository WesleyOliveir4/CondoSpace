package com.example.condospace.presentation.ui.feature.profile.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.condospace.presentation.ui.feature.profile.components.ChangePasswordCard
import com.example.condospace.presentation.ui.feature.profile.state.UserDataUiState
import com.example.condospace.presentation.ui.feature.profile.viewmodel.ChangePasswordViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavHostController,
    viewModel: ChangePasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    CondoSpaceTheme {
        ChangePasswordScreenContent(
            navController = navController,
            uiState = uiState,
            onPasswordChange = { viewModel.updatePassword(it) }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreenContent(
    navController: NavHostController,
    uiState: UserDataUiState,
    onPasswordChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Alterar Senha",
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
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                ChangePasswordComponent(onPasswordChange = onPasswordChange)
            }
        }
    }

}

@Composable
fun ChangePasswordComponent(onPasswordChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(top = 16.dp)
    ) {

        ChangePasswordCard(
            onPasswordChangeConfirmed = onPasswordChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        ChangePasswordScreenContent(
            navController = navController,
            uiState = UserDataUiState(
                user = UserUiModel(
                    name = "João Silva",
                    phoneNumber = "(11) 98999-2000",
                    email = "john.jay@example.com",
                    condominium = CondominiumUiModel(
                        name = "Condomínio Exemplo",
                        cep = "12345-678",
                    )
                )
            ),
            onPasswordChange = {}
        )
    }
}

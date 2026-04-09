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
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.profile.components.ChangePasswordCard
import com.example.condospace.presentation.ui.feature.profile.state.ChangePasswordUiState
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

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is ChangePasswordUiState.Success) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    CondoSpaceTheme {
        ChangePasswordScreenContent(
            navController = navController,
            uiState = uiState,
            onPasswordChange = { viewModel.updatePassword(it) },
            onResetState = { viewModel.resetState() }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreenContent(
    navController: NavHostController,
    uiState: ChangePasswordUiState,
    onPasswordChange: (String) -> Unit,
    onResetState: () -> Unit = {}
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
            when (uiState) {
                is ChangePasswordUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ChangePasswordUiState.Error -> {
                    ErrorDialog(
                        message = uiState.message,
                        onDismiss = onResetState
                    )
                    ChangePasswordComponent(onPasswordChange = onPasswordChange)
                }
                else -> {
                    ChangePasswordComponent(onPasswordChange = onPasswordChange)
                }
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
            uiState = ChangePasswordUiState.Idle,
            onPasswordChange = {}
        )
    }
}

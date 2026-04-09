package com.example.condospace.presentation.ui.feature.profile.screen

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.profile.components.SettingsCard
import com.example.condospace.presentation.ui.feature.profile.state.SettingsUiState
import com.example.condospace.presentation.ui.feature.profile.viewmodel.SettingsViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    CondoSpaceTheme {
        SettingsScreenContent(
            navController = navController,
            uiState = uiState,
            onNotificationsToggled = { viewModel.toggleNotifications(it) },
            onClearError = { viewModel.clearError() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    navController: NavHostController,
    uiState: SettingsUiState,
    onNotificationsToggled: (Boolean) -> Unit,
    onClearError: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Configurações",
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
                is SettingsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is SettingsUiState.Error -> {
                    ErrorDialog(
                        title = "Erro",
                        message = uiState.message,
                        onDismiss = onClearError
                    )
                    SettingsComponent(
                        notificationsEnabled = true,
                        onNotificationsToggled = onNotificationsToggled
                    )
                }

                is SettingsUiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SettingsComponent(
                            notificationsEnabled = uiState.notificationsEnabled,
                            onNotificationsToggled = onNotificationsToggled
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

                        uiState.error?.let {
                            ErrorDialog(
                                title = "Erro ao salvar",
                                message = it,
                                onDismiss = onClearError
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsComponent(
    notificationsEnabled: Boolean,
    onNotificationsToggled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(top = 16.dp)
    ) {
        SettingsCard(
            notificationsEnabled = notificationsEnabled,
            onNotificationsToggled = onNotificationsToggled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val navController = rememberNavController()
    CondoSpaceTheme {
        SettingsScreenContent(
            navController = navController,
            uiState = SettingsUiState.Success(notificationsEnabled = true),
            onNotificationsToggled = {}
        )
    }
}

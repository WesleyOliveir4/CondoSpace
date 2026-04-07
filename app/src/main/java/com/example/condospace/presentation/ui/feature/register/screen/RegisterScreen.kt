package com.example.condospace.presentation.ui.feature.register.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.register.components.RegisterComponent
import com.example.condospace.presentation.ui.feature.register.state.RegisterState
import com.example.condospace.presentation.ui.feature.register.viewmodel.RegisterViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    navigateToSelectCondominium: (String) -> Unit,
) {
    val registerViewModel: RegisterViewModel = koinViewModel()
    val registerState by registerViewModel.registerState.collectAsState()

    CondoSpaceTheme {
        RegisterScreenContent(
            navController = navController,
            navigateToSelectCondominium = navigateToSelectCondominium,
            registerViewModel = registerViewModel,
            registerState = registerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    navController: NavHostController,
    navigateToSelectCondominium: (String) -> Unit,
    registerViewModel: RegisterViewModel,
    registerState: RegisterState
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "",
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
            Box {
                RegisterComponent(
                    onCreateAccountClick = { name, email, password, phone ->
                        registerViewModel.signup(
                            email = email,
                            password = password,
                            name = name,
                            phone = phone
                        )
                    },
                    isLoading = registerState is RegisterState.Loading
                )

                when (registerState) {
                    is RegisterState.Registered -> {
                        navigateToSelectCondominium(registerState.userUuid)
                    }
                    is RegisterState.Error -> {
                        ErrorDialog(
                            message = registerState.message,
                            onDismiss = {
                                registerViewModel.dismissError()
                            }
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        RegisterScreenContent(
            navController = navController,
            navigateToSelectCondominium = {},
            registerViewModel = koinViewModel(),
            registerState = RegisterState.NotRegistered
        )
    }
}

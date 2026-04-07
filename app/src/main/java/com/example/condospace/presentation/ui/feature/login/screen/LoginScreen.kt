package com.example.condospace.presentation.ui.feature.login.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.error.ErrorDialog
import com.example.condospace.presentation.ui.feature.login.components.LoginComponent
import com.example.condospace.presentation.ui.feature.login.components.SplashLoadingScreen
import com.example.condospace.presentation.ui.feature.login.state.LoginState
import com.example.condospace.presentation.ui.feature.login.viewmodel.LoginViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
) {
    val loginViewModel: LoginViewModel = koinViewModel()
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Authenticated) {
            navigateToHome()
        }
    }

    CondoSpaceTheme {
        when (loginState) {
            is LoginState.Checking, is LoginState.Authenticated -> {
                SplashLoadingScreen()
            }

            else -> {
                LoginScreenContent(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    loginState = loginState,
                    navigateToRegister = navigateToRegister,
                    navigateToHome = navigateToHome
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    loginState: LoginState,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit
) {
    Scaffold(
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                LoginComponent(
                    onLoginClick = { email, password ->
                        loginViewModel.login(
                            email = email,
                            password = password
                        )
                    },
                    onRegisterClick = {
                        navigateToRegister()
                    },
                    onForgotPasswordClick = {

                    },
                    isLoading = loginState is LoginState.Loading
                )

                if (loginState is LoginState.Error) {
                    ErrorDialog(
                        message = loginState.message,
                        onDismiss = {
                            loginViewModel.dismissError()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        LoginScreenContent(
            navController = navController,
            loginViewModel = koinViewModel(),
            loginState = LoginState.Unauthenticated,
            navigateToRegister = {},
            navigateToHome = {}
        )
    }
}

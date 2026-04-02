package com.example.condospace.presentation.ui.feature.login.screen


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.R
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

    CondoSpaceTheme {
        LoginScreenContent(
            navController = navController,
            loginViewModel = loginViewModel,
            loginState = loginState,
            navigateToRegister = navigateToRegister,
            navigateToHome = navigateToHome
        )
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

                when (loginState) {
                    is LoginState.Error -> {
                        Text(
                            text = loginState.message,
                            color = Color.Red,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                    is LoginState.Authenticated -> {
                        navigateToHome()
                    }
                    else -> Unit
                }
            }
        }
    }

}

@Composable
fun LoginComponent(
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    isLoading: Boolean = false
){
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDFDFD)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_condospace_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(72.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CondoSpace",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF354EAB),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Marketplace do seu condomínio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {

                    Text("E-mail")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("seu@email.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Senha")
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Esqueci minha senha",
                        color = Color(0xFF2962FF),
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onLoginClick(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF354EAB)
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Entrar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Não tem uma conta? ")
                        Text(
                            text = "Cadastrar",
                            color = Color(0xFF2962FF),
                            modifier = Modifier.clickable { onRegisterClick() }
                        )
                    }
                }
            }
        }

}

@Preview(showBackground = true)
@Composable
fun  LoginScreenPreview() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = koinViewModel()


    CondoSpaceTheme {
        LoginScreenContent(
            navController = navController,
            loginViewModel = loginViewModel,
            loginState = LoginState.Unauthenticated,
            navigateToRegister = {},
            navigateToHome = {}
        )
    }
}

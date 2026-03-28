package com.example.condospace.presentation.ui.feature.register.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun RegisterScreen(
    navController: NavHostController,
) {
    CondoSpaceTheme {
        RegisterScreenContent(
            navController = navController,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    navController: NavHostController,
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
            RegisterComponent(
                onCreateAccountClick = { name, email, password, phone ->

                }
            )
        }
    }

}

@Composable
fun RegisterComponent(
    onCreateAccountClick: (name: String, email: String, password: String, phone: String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        // 🔵 Ícone + título
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                text = "Criar Conta",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF354EAB),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Junte-se ao marketplace do seu condomínio",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text("Nome do usuário")
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Seu nome completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    placeholder = { Text("Mínimo 6 caracteres") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Telefone")
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("(00) 00000-0000") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onCreateAccountClick(name, email, password, phone)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF354EAB)
                    ),
                    enabled = name.isNotBlank() &&
                            email.isNotBlank() &&
                            password.length >= 6 &&
                            phone.isNotBlank()
                ) {
                    Text("Criar conta")
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
            navController,
        )
    }
}

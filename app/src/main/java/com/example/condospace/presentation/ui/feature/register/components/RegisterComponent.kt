package com.example.condospace.presentation.ui.feature.register.components

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.R
import com.example.condospace.presentation.ui.component.OutlinedTextFieldCS
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import com.example.condospace.presentation.utils.PhoneUtils

@Composable
fun RegisterComponent(
    isLoading: Boolean = false,
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
                    contentDescription = stringResource(id = R.string.content_description_logo),
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.register_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF354EAB),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.register_subtitle),
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

                OutlinedTextFieldCS(
                    label = stringResource(id = R.string.register_username_label),
                    value = name,
                    onValueChange = { name = it },
                    placeholder = stringResource(id = R.string.register_username_placeholder),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextFieldCS(
                    label = stringResource(id = R.string.register_email_label),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = stringResource(id = R.string.register_email_placeholder),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextFieldCS(
                    label = stringResource(id = R.string.register_password_label),
                    value = password,
                    onValueChange = { password = it },
                    placeholder = stringResource(id = R.string.register_password_placeholder),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextFieldCS(
                    label = stringResource(id = R.string.register_phone_label),
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = stringResource(id = R.string.register_phone_placeholder),
                    visualTransformation = PhoneUtils.phoneVisualTransformation,
                    enabled = !isLoading
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
                    enabled = !isLoading && name.isNotBlank() &&
                            email.isNotBlank() &&
                            password.length >= 6 &&
                            phone.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(id = R.string.register_button_text))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterComponentPreview() {
    CondoSpaceTheme {
        RegisterComponent(
            isLoading = false,
            onCreateAccountClick = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterComponentLoadingPreview() {
    CondoSpaceTheme {
        RegisterComponent(
            isLoading = true,
            onCreateAccountClick = { _, _, _, _ -> }
        )
    }
}

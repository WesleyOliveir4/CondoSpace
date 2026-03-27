package com.example.condospace.presentation.ui.feature.profile.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.presentation.ui.component.CondoSpaceTopBar
import com.example.condospace.presentation.ui.feature.profile.components.ContactCard
import com.example.condospace.presentation.ui.feature.profile.components.OptionsCard
import com.example.condospace.presentation.ui.feature.profile.components.ProfileHeader
import com.example.condospace.presentation.ui.component.navBar.NavBar
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun ProfileScreen(
    navController: NavHostController,
    navigateToUserData: () -> Unit
) {
    CondoSpaceTheme {
        ProfileScreenContent(
            navController,
            navigateToUserData
        )
    }
}


@Composable
fun ProfileScreenContent(
    navController: NavHostController,
    navigateToUserData: () -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController, "Profile") },
        topBar = {
            CondoSpaceTopBar()
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                ProfileHeader()

                Spacer(modifier = Modifier.height(16.dp))

                ContactCard()

                Spacer(modifier = Modifier.height(16.dp))

                OptionsCard(
                    onMyDataClick = navigateToUserData,
                    onChangePasswordClick = {  },
                    onSettingsClick = {  }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LogoutButton()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Condo Market v1.0.0",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}

@Composable
fun LogoutButton() {

    OutlinedButton(
        onClick = { },
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
            ProfileScreenContent(navController, navigateToUserData = {})
    }
}
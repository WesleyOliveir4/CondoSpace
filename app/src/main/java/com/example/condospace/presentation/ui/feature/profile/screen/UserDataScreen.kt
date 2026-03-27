package com.example.condospace.presentation.ui.feature.profile.screen


import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.R
import com.example.condospace.domain.model.User
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.favorites.components.SearchPublications
import com.example.condospace.presentation.ui.feature.profile.components.InfoItem
import com.example.condospace.presentation.ui.feature.publications.components.EditableProfileImage
import com.example.condospace.presentation.ui.mocks.PublicationsMocks
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun UserDataScreen(
    navController: NavHostController,
) {
    CondoSpaceTheme {
        UserDataScreenContent(
            navController = navController,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreenContent(
    navController: NavHostController,
) {
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
            UserDetailsComponent(
                user = User(
                    id = 1,
                    name = "João Silva",
                    phoneNumber = "(11) 98999-2000",
                    profilePicture = R.drawable.img_person,
                    email = "john.jay@example.com",
                    cep = "04916020",
                    condominiumName = "Residencial Green Park"
                )
            )
        }
    }

}

@Composable
fun UserDetailsComponent(
    user: User
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2962FF),
                            Color(0xFF1E4ED8)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var profileImage by remember { mutableStateOf<Uri?>(null) }

                EditableProfileImage(
                    imageUri = profileImage,
                    imageRes = user.profilePicture,
                    onImageSelected = { newUri ->
                        profileImage = newUri
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = user.condominiumName,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                InfoItem(
                    icon = Icons.Default.Phone,
                    title = "Telefone",
                    value = user.phoneNumber,
                    showEditOption = true
                )

                InfoItem(
                    icon = Icons.Default.Email,
                    title = "E-mail",
                    value = user.email
                )

                InfoItem(
                    icon = Icons.Default.LocationOn,
                    title = "CEP",
                    value = user.cep
                )

                InfoItem(
                    icon = Icons.Default.Home,
                    title = "Condomínio",
                    value = user.condominiumName
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun  UserDataScreenPreview() {
    val navController = rememberNavController()

    CondoSpaceTheme {
        UserDataScreenContent(
            navController,
        )
    }
}

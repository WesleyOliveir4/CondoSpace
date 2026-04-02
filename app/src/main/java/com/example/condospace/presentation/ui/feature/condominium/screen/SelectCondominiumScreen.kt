package com.example.condospace.presentation.ui.feature.condominium.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.domain.model.Condominium
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.feature.condominium.state.CondominiumState
import com.example.condospace.presentation.ui.feature.condominium.viewmodel.SelectCondominiumViewModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SelectCondominiumScreen(
    navController: NavHostController,
    userId: String,
    navigateToLogin: () -> Unit,
    registerFlow: Boolean,
) {
    val condominiumViewModel : SelectCondominiumViewModel = koinViewModel()
    val condominiumState by condominiumViewModel.condominiumState.collectAsState()
    val searchResults by condominiumViewModel.searchResults.collectAsState()

    LaunchedEffect(userId) {
        condominiumViewModel.fetchUserCondominium(userId)
    }

    CondoSpaceTheme {
        SelectCondominiumScreenContent(
            navController = navController,
            registerFlow = registerFlow,
            navigateToLogin = navigateToLogin,
            condominiumState = condominiumState,
            searchResults = searchResults,
            onSearchClick = { cep ->
                condominiumViewModel.searchCondominiumByCep(cep)
            },
            onSaveCondominiumSelectedClick = { condo ->
                condominiumViewModel.saveCondominiumSelected(userId, condo)
            },
            onSaveCondominiumCreateClick = { condo ->
                condominiumViewModel.saveCondominiumCreated(userId, condo)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCondominiumScreenContent(
    navController: NavHostController,
    condominiumState: CondominiumState,
    searchResults: List<Condominium>,
    onSearchClick: (String) -> Unit,
    onSaveCondominiumSelectedClick: (Condominium) -> Unit,
    onSaveCondominiumCreateClick: (Condominium) -> Unit,
    registerFlow: Boolean,
    navigateToLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Endereço",
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (condominiumState) {
                    is CondominiumState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF354EAB))
                        }
                    }
                    is CondominiumState.CondominiumFound -> {
                        SelectCondominiumComponent(
                            initialCondo = condominiumState.condominium,
                            searchResults = searchResults,
                            onSearchClick = onSearchClick,
                            onSaveCondominiumSelectedClick = onSaveCondominiumSelectedClick,
                            onSaveCondominiumCreateClick = onSaveCondominiumCreateClick
                        )
                    }
                    is CondominiumState.CondominiumSaved -> {
                        if (registerFlow) {
                            navigateToLogin()
                        }else{
                            SelectCondominiumComponent(
                                initialCondo = condominiumState.condominium,
                                searchResults = searchResults,
                                onSearchClick = onSearchClick,
                                onSaveCondominiumSelectedClick = onSaveCondominiumSelectedClick,
                                onSaveCondominiumCreateClick = onSaveCondominiumCreateClick
                            )
                        }
                    }
                    is CondominiumState.CondominiumNotFound -> {
                        SelectCondominiumComponent(
                            initialCondo = null,
                            searchResults = searchResults,
                            onSearchClick = onSearchClick,
                            onSaveCondominiumSelectedClick = onSaveCondominiumSelectedClick,
                            onSaveCondominiumCreateClick = onSaveCondominiumCreateClick
                        )
                    }
                    is CondominiumState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = condominiumState.message, color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectCondominiumComponent(
    initialCondo: Condominium?,
    searchResults: List<Condominium>,
    onSearchClick: (String) -> Unit,
    onSaveCondominiumSelectedClick: (Condominium) -> Unit,
    onSaveCondominiumCreateClick: (Condominium) -> Unit
) {

    var selectedCondo by remember(initialCondo) { mutableStateOf<Condominium?>(initialCondo) }
    var isEditing by remember(initialCondo) { mutableStateOf(initialCondo == null) }

    var cep by remember { mutableStateOf("") }
    var manualName by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selecione seu condomínio",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF354EAB),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text("Seu condomínio", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(12.dp))

                if (selectedCondo != null) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(modifier = Modifier.weight(1f)) {

                            Text(selectedCondo!!.name)
                            Text(
                                selectedCondo!!.cep,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        IconButton(
                            onClick = { isEditing = true }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    }

                } else {

                    Text(
                        "Nenhum condomínio selecionado",
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isEditing) {

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Buscar condomínio", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cep,
                        onValueChange = { cep = it },
                        label = { Text("CEP") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (cep.isNotBlank()) {
                                onSearchClick(cep)
                                hasSearched = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF354EAB),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Pesquisar")
                    }

                    Spacer(Modifier.height(12.dp))

                    if (searchResults.isNotEmpty()) {

                        searchResults.forEach { condo ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSaveCondominiumSelectedClick(condo)
                                        isEditing = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column {
                                    Text(condo.name)
                                    Text(
                                        condo.cep,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                    } else if (hasSearched && searchResults.isEmpty()) {

                        Spacer(Modifier.height(12.dp))

                        Text("Não encontramos seu condomínio")

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = manualName,
                            onValueChange = { manualName = it },
                            label = { Text("Nome do condomínio") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (manualName.isNotBlank()) {
                                    onSaveCondominiumCreateClick(Condominium(manualName, cep))
                                    isEditing = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF354EAB),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Salvar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun  SelectCondominiumScreenPreview() {
    val navController = rememberNavController()


    CondoSpaceTheme {
        SelectCondominiumScreenContent(
            navController = navController,
            condominiumState = CondominiumState.CondominiumNotFound,
            searchResults = emptyList<Condominium>(),
            onSearchClick = {},
            onSaveCondominiumSelectedClick = {},
            onSaveCondominiumCreateClick = {},
            registerFlow = false,
            navigateToLogin = {}
        )
    }
}

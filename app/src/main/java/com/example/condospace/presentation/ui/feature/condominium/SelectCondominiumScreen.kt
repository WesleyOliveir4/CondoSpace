package com.example.condospace.presentation.ui.feature.condominium


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.condospace.domain.model.Condominium
import com.example.condospace.presentation.ui.component.TopBarReturn
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

@Composable
fun SelectCondominiumScreen(
    navController: NavHostController,
) {
    CondoSpaceTheme {
        SelectCondominiumScreenContent(
            navController = navController,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCondominiumScreenContent(
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            TopBarReturn(
                title = "Endereço",
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
            SelectCondominiumComponent()
        }
    }

}

@Composable
fun SelectCondominiumComponent() {

    var selectedCondo by remember { mutableStateOf<Condominium?>(null) }
    var isEditing by remember { mutableStateOf(selectedCondo == null) }

    var cep by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Condominium>>(emptyList()) }
    var manualName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(16.dp)
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
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
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Buscar condomínio", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(12.dp))

                    // CEP
                    OutlinedTextField(
                        value = cep,
                        onValueChange = { cep = it },
                        label = { Text("CEP") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            searchResults = if (cep == "12345678") {
                                listOf(
                                    Condominium("Residencial Green Park", cep),
                                    Condominium("Condomínio Bela Vista", cep)
                                )
                            } else emptyList()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2962FF),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Pesquisar")
                    }

                    Spacer(Modifier.height(12.dp))

                    // RESULTADOS
                    if (searchResults.isNotEmpty()) {

                        searchResults.forEach { condo ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCondo = condo
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

                    } else if (cep.isNotBlank()) {

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
                                val newCondo = Condominium(manualName, cep)
                                selectedCondo = newCondo
                                isEditing = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2962FF),
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
            navController,
        )
    }
}

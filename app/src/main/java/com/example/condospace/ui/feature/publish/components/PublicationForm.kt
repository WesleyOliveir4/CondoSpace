package com.example.condospace.ui.feature.publish.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PublicationForm(
    title: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color.Black),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            Text("Título")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Ex: Sofá 3 lugares semi-novo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Text("Categoria")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Selecione a categoria") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, null)
                }
            )

            Spacer(Modifier.height(12.dp))

            Text("Descrição")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Descreva seu produto ou serviço...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Fotos")
            Spacer(Modifier.height(6.dp))

            PhotoPicker()

            Spacer(Modifier.height(12.dp))

            Text("Localização")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Ex: Bloco A, Apto 101") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Text("Contato")
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Telefone ou e-mail") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF)
                )
            ) {
                Text("Publicar anúncio")
            }
        }
    }
}
@Composable
fun PhotoPicker() {

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { }
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = null
            )

            Text("Clique para adicionar fotos")
        }
    }
}
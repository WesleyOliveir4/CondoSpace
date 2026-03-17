package com.example.condospace.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CondoSpaceTopBar(){
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        modifier = Modifier.shadow(elevation = 2.dp, ambientColor = Color.Black),
        title = {
            ResidenceSelector(
                text = "Residencial Green Park",
                onClick = {
                    // abrir tela para selecionar seu condomínio
                }
            )
        },

        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = "Notificações"
                )
            }
        }
    )
}
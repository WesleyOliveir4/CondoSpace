package com.example.condospace.presentation.ui.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import com.example.condospace.presentation.utils.CepUtils.applyCepMask
import com.example.condospace.presentation.utils.PhoneUtils.applyPhoneMask

@Composable
fun ContactCard(user: UserUiModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-30).dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            ContactItem(
                icon = Icons.Default.Phone,
                label = "Telefone",
                value = user.phoneNumber.applyPhoneMask().ifBlank { "Não informado" }
            )
            ContactItem(
                icon = Icons.Default.Email,
                label = "E-mail",
                value = user.email.ifBlank { "Não informado" }
            )
            ContactItem(
                icon = Icons.Default.Home,
                label = "CEP",
                value = user.condominium?.cep?.applyCepMask()?.ifBlank { "Não informado" } ?: "Não informado"
            )
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, label: String, value: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F0FE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2962FF))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactCardPreview() {
    CondoSpaceTheme {
        ContactCard(
            user = UserUiModel(
                phoneNumber = "(11) 98888-7777",
                email = "joao.silva@email.com"
            )
        )
    }
}

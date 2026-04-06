package com.example.condospace.presentation.ui.feature.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.R
import com.example.condospace.presentation.model.UserUiModel
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme
import com.example.condospace.presentation.utils.PhoneUtils
import com.example.condospace.presentation.utils.PhoneUtils.applyPhoneMask

@Composable
fun UserDataInfoCard(
    user: UserUiModel,
    modifier: Modifier = Modifier,
    onNameChangeConfirmed: (String) -> Unit = {},
    onPhoneChangeConfirmed: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
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
                title = stringResource(R.string.phone),
                value = user.phoneNumber,
                showEditOption = true,
                visualTransformation = PhoneUtils.phoneVisualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChangeConfirmed = onPhoneChangeConfirmed
            )

            InfoItem(
                icon = Icons.Default.Person,
                title = stringResource(R.string.full_name),
                value = user.name,
                showEditOption = true,
                onValueChangeConfirmed = onNameChangeConfirmed
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDataInfoCardPreview() {
    CondoSpaceTheme {
        UserDataInfoCard(
            user = UserUiModel(
                name = "João Silva",
                phoneNumber = "11989992000"
            )
        )
    }
}

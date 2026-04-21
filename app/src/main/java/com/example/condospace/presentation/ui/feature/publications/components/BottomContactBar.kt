package com.example.condospace.presentation.ui.feature.publications.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.presentation.ui.enums.ServiceType
import com.example.condospace.presentation.utils.CurrencyUtils.formatToBRL

@Composable
fun BottomContactBar(
    price: Double,
    onClick: () -> Unit,
    categoryType: String? = null,
    couponCode: String? = null
) {
    val isExternal = categoryType == ServiceType.EXTERNAL.value
    val isService = categoryType == ServiceType.SERVICE.value || categoryType == ServiceType.RECOMMENDATION.value
    val priceText = if (price > 0.0) price.formatToBRL() else "Preço a combinar"

    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isExternal) "Cupom:" else "Preço:",
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = if (isExternal) (couponCode ?: "---") else priceText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF354EAB),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Black
                )
            ) {
                Text(
                    if (isExternal) "Ver localização"
                    else if(isService) "Falar com o prestador"
                    else "Falar com o vendedor")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomContactBarPreview() {
    BottomContactBar(price = 150.0, onClick = {}, categoryType = "Outros")
}

@Preview(showBackground = true)
@Composable
fun BottomContactBarZeroPreview() {
    BottomContactBar(price = 0.0, onClick = {}, categoryType = "Serviços")
}

@Preview(showBackground = true)
@Composable
fun BottomContactBarExternalPreview() {
    BottomContactBar(
        price = 0.0,
        onClick = {},
        categoryType = ServiceType.EXTERNAL.value,
        couponCode = "CONDO20"
    )
}

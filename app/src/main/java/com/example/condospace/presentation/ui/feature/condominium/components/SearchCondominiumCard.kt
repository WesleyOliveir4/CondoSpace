package com.example.condospace.presentation.ui.feature.condominium.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHomeWork
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.condospace.R
import com.example.condospace.presentation.model.CondominiumUiModel
import com.example.condospace.presentation.ui.component.OutlinedTextFieldCS
import com.example.condospace.presentation.utils.CepUtils
import com.example.condospace.presentation.utils.CepUtils.applyCepMask

@Composable
fun SearchCondominiumCard(
    searchResults: List<CondominiumUiModel>,
    isSearching: Boolean,
    isSaving: Boolean,
    onSearchClick: (String) -> Unit,
    onSaveCondominiumSelectedClick: (CondominiumUiModel) -> Unit,
    onSaveCondominiumCreateClick: (CondominiumUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var cep by remember { mutableStateOf("") }
    var manualName by remember { mutableStateOf("") }
    var hasSearchedOnce by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(id = R.string.condominium_search_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(Modifier.height(16.dp))

            OutlinedTextFieldCS(
                label = stringResource(id = R.string.condominium_cep_label),
                value = cep,
                onValueChange = {
                    val digits = it.filter { char -> char.isDigit() }
                    if (digits.length <= 8) cep = digits
                                },
                placeholder = stringResource(id = R.string.condominium_cep_placeholder),
                enabled = !isSearching && !isSaving,
                visualTransformation = CepUtils.cepVisualTransformation,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (cep.isNotBlank()) {
                        onSearchClick(cep)
                        hasSearchedOnce = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSearching && !isSaving && cep.length >= 8,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF354EAB))
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = R.string.condominium_search_button), fontWeight = FontWeight.SemiBold)
                }
            }

            AnimatedVisibility(
                visible = hasSearchedOnce && !isSearching,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    
                    if (searchResults.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.condominium_results_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        searchResults.forEach { condo ->
                            CondoItem(
                                condo = condo,
                                isSaving = isSaving,
                                onClick = { onSaveCondominiumSelectedClick(condo) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    } else if (hasSearchedOnce) {
                        EmptySearchResultsContent(
                            manualName = manualName,
                            onNameChange = { manualName = it },
                            isSaving = isSaving,
                            onSaveClick = {
                                if (manualName.isNotBlank()) {
                                    onSaveCondominiumCreateClick(CondominiumUiModel(name = manualName, cep = cep))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CondoItem(
    condo: CondominiumUiModel,
    isSaving: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSaving, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AddHomeWork,
            contentDescription = null,
            tint = Color(0xFF354EAB),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = condo.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(id = R.string.cep_label, condo.cep.applyCepMask()),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun EmptySearchResultsContent(
    manualName: String,
    onNameChange: (String) -> Unit,
    isSaving: Boolean,
    onSaveClick: () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.condominium_not_found),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextFieldCS(
            label = stringResource(id = R.string.condominium_name_label),
            value = manualName,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onNameChange,
            placeholder = stringResource(id = R.string.condominium_name_placeholder),
            enabled = !isSaving,
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF354EAB)),
            enabled = !isSaving && manualName.isNotBlank()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.condominium_register_select_button), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchCondominiumCardPreview() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            SearchCondominiumCard(
                searchResults = listOf(
                    CondominiumUiModel(name = "Residencial Topázio", cep = "01234-567"),
                    CondominiumUiModel(name = "Edifício Rubi", cep = "01234-567")
                ),
                isSearching = false,
                isSaving = false,
                onSearchClick = {},
                onSaveCondominiumSelectedClick = {},
                onSaveCondominiumCreateClick = {}
            )
        }
    }
}

package com.example.condospace.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    /**
     * Formata uma string numérica em formato de moeda Brasileira (Real).
     */
    fun formatToBRL(value: String): String {
        val cleanString = value.replace("[^\\d]".toRegex(), "")
        if (cleanString.isEmpty()) return ""

        return try {
            val parsed = cleanString.toDouble() / 100
            NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed)
                .replace("R$", "")
                .trim()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * VisualTransformation para exibir a máscara de moeda (R$) enquanto o usuário digita apenas números.
     */
    val currencyVisualTransformation = VisualTransformation { text ->
        val cleanString = text.text.replace("[^\\d]".toRegex(), "")
        
        val formatted = if (cleanString.isEmpty()) {
            ""
        } else {
            val parsed = cleanString.toDouble() / 100
            NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed)
                .replace("R$", "")
                .trim()
        }

        val currencyOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return cleanString.length
            }
        }

        TransformedText(AnnotatedString(formatted), currencyOffsetMapping)
    }

    fun Double.formatToBRLWithoutSymbol(): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            .format(this)
            .replace("R$", "")
            .trim()
    }

    fun Double.formatToBRL(): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
    }

    fun currencyToDouble(value: String): Double {
        val cleanString = value.replace("[^\\d]".toRegex(), "")
        return if (cleanString.isEmpty()) 0.0 else cleanString.toDouble() / 100
    }
}

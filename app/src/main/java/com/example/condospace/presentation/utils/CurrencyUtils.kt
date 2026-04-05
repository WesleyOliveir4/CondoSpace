package com.example.condospace.presentation.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    /**
     * Formata uma string numérica em formato de moeda Brasileira (Real).
     * Ex: "1250" -> "12,50" -> "R$ 12,50" (o R$ pode ser adicionado na UI se necessário)
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
     * Converte a string formatada de volta para Double para envio ao backend.
     * Ex: "1.250,50" -> 1250.5
     */
    fun currencyToDouble(value: String): Double {
        val cleanString = value.replace("[^\\d]".toRegex(), "")
        return if (cleanString.isEmpty()) 0.0 else cleanString.toDouble() / 100
    }
}

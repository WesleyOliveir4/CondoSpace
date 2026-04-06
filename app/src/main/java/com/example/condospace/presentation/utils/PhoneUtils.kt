package com.example.condospace.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText

object PhoneUtils {
    
    /**
     * Remove todos os caracteres não numéricos.
     */
    fun removeMask(value: String): String {
        return value.filter { it.isDigit() }
    }

    /**
     * VisualTransformation para aplicar a máscara (00) 00000-0000 sem alterar o valor real.
     */
    val phoneVisualTransformation = VisualTransformation { text ->
        val trimmed = if (text.text.length >= 11) text.text.substring(0..10) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            if (i == 2) out += ") "
            if (i == 7) out += "-"
            out += trimmed[i]
        }

        val phoneNumberOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                if (offset <= 2) return offset + 1
                if (offset <= 7) return offset + 3
                if (offset <= 11) return offset + 4
                return 15
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return offset
                if (offset <= 3) return offset - 1
                if (offset <= 10) return offset - 3
                if (offset <= 15) return offset - 4
                return 11
            }
        }

        TransformedText(AnnotatedString(out), phoneNumberOffsetMapping)
    }

    /**
     * Extension function para formatar uma String como telefone: (00) 00000-0000
     */
    fun String.applyPhoneMask(): String {
        val digits = this.filter { it.isDigit() }
        val trimmed = if (digits.length > 11) digits.substring(0, 11) else digits
        val out = StringBuilder()
        for (i in trimmed.indices) {
            if (i == 0) out.append("(")
            if (i == 2) out.append(") ")
            if (i == 7) out.append("-")
            out.append(trimmed[i])
        }
        return out.toString()
    }
}

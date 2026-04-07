package com.example.condospace.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object CepUtils {

    /**
     * VisualTransformation para aplicar a máscara 00000-000 enquanto o usuário digita.
     */
    val cepVisualTransformation = VisualTransformation { text ->
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            if (i == 5) out += "-"
            out += trimmed[i]
        }

        val cepOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 5) return offset.coerceAtMost(out.length)
                return (offset + 1).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 5) return offset.coerceAtMost(text.text.length)
                return (offset - 1).coerceAtMost(text.text.length)
            }
        }

        TransformedText(AnnotatedString(out), cepOffsetMapping)
    }

    /**
     * Extension function para formatar uma String como CEP: 00000-000
     */
    fun String.applyCepMask(): String {
        val digits = this.filter { it.isDigit() }
        val trimmed = if (digits.length > 8) digits.substring(0, 8) else digits
        val out = StringBuilder()
        for (i in trimmed.indices) {
            if (i == 5) out.append("-")
            out.append(trimmed[i])
        }
        return out.toString()
    }
}

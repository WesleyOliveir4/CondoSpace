package com.example.condospace.domain.usecase.publication

import android.net.Uri
import com.example.condospace.presentation.ui.enums.ServiceType

class GetContactUrlUseCase {
    operator fun invoke(
        contact: String?,
        publicationOwner: String,
        title: String,
        categoryType: String?
    ): String? {
        val contactValue = contact ?: return null

        return if (categoryType == ServiceType.EXTERNAL.value) {
            if (contactValue.startsWith("http://") || contactValue.startsWith("https://")) {
                contactValue
            } else {
                "https://$contactValue"
            }
        } else {
            val phoneDigits = contactValue.filter { it.isDigit() }
            val message = "Olá $publicationOwner, me interessei pelo anuncio $title!"
            val encodedMessage = Uri.encode(message)
            "https://wa.me/55$phoneDigits?text=$encodedMessage"
        }
    }
}

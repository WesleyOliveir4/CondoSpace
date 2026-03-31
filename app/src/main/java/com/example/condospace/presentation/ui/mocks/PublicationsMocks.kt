package com.example.condospace.presentation.ui.mocks

import com.example.condospace.domain.model.Publication

class PublicationsMocks() {

    fun getPublications(): List<Publication> {
        return listOf(
            Publication(
                id = 1,
                publicationOwnerUuid = "user_01",
                publicationOwner = "Ana Paula Silva",
                title = "Contadora",
                description = "Consultoria contábil para MEI e empresas. Ofereço serviços completos de contabilidade, declaração de imposto de renda e planejamento financeiro.",
                publicationType = "serviço",
                date = "10/05/2023",
                price = 120.0,
                likes = 127,
                imageUrlList = listOf("https://images.unsplash.com/photo-1554224155-6726b3ff858f?q=80&w=500")
            ),
            Publication(
                id = 2,
                publicationOwnerUuid = "user_02",
                publicationOwner = "Carlos Mendes",
                title = "Encanador",
                description = "Reparos hidráulicos em geral. Especialista em detecção de vazamentos, troca de tubulação e instalação de metais sanitários.",
                publicationType = "serviço",
                date = "12/05/2023",
                price = 150.0,
                likes = 94,
                imageUrlList = listOf("https://images.unsplash.com/photo-1581244277943-fe4a9c777189?q=80&w=500")
            ),
            Publication(
                id = 3,
                publicationOwnerUuid = "user_03",
                publicationOwner = "Paula Silva",
                title = "Diarista",
                description = "Limpeza residencial e comercial. Serviço de limpeza profunda ou manutenção, organização de armários e passadoria.",
                publicationType = "serviço",
                date = "15/05/2023",
                price = 180.0,
                likes = 150,
                imageUrlList = listOf("https://images.unsplash.com/photo-1581578731522-745d05ad9a2d?q=80&w=500")
            ),
            Publication(
                id = 4,
                publicationOwnerUuid = "user_04",
                publicationOwner = "Jonas Silveira",
                title = "Pedreiro",
                description = "Pequenas reformas e alvenaria. Experiência em assentamento de pisos, azulejos e reparos estruturais.",
                publicationType = "serviço",
                date = "18/05/2023",
                price = 200.0,
                likes = 82,
                imageUrlList = listOf("https://images.unsplash.com/photo-1504148455328-c376907d081c?q=80&w=500")
            )
        )
    }

    fun getExternalPublications(): List<Publication> {
        return listOf(
            Publication(
                id = 5,
                publicationOwnerUuid = "ext_01",
                publicationOwner = "Paulo Silva",
                title = "Pintor",
                description = "Pintura residencial interna e externa. Trabalho com texturas, grafiato e pintura fina.",
                publicationType = "serviço",
                date = "20/05/2023",
                price = 300.0,
                likes = 110,
                imageUrlList = listOf("https://images.unsplash.com/photo-1589939705384-5185137a7f0f?q=80&w=500")
            ),
            Publication(
                id = 6,
                publicationOwnerUuid = "ext_02",
                publicationOwner = "Matheus Silva",
                title = "Advogado",
                description = "Assessoria jurídica civil e família. Especialista em direito do consumidor e contratos.",
                publicationType = "serviço",
                date = "22/05/2023",
                price = 250.0,
                likes = 67,
                imageUrlList = listOf("https://images.unsplash.com/photo-1589829545856-d10d557cf95f?q=80&w=500")
            )
        )
    }

    fun getFavoritedPublications(): List<Publication> {
        return listOf(
            Publication(
                id = 10,
                publicationOwnerUuid = "user_05",
                publicationOwner = "Ricardo Gomes",
                title = "Sofá 3 lugares cinza",
                description = "Móveis em ótimo estado, pouco uso. Tecido impermeabilizado e sem manchas. Retirada no bloco B.",
                publicationType = "produto",
                date = "25/05/2023",
                price = 850.0,
                likes = 12,
                imageUrlList = listOf("https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=500")
            ),
            Publication(
                id = 11,
                publicationOwnerUuid = "user_06",
                publicationOwner = "Juliana Lins",
                title = "Cadeira de escritório",
                description = "Cadeira ergonômica com regulagem de altura e braços. Perfeita para home office.",
                publicationType = "produto",
                date = "26/05/2023",
                price = 450.0,
                likes = 8,
                imageUrlList = listOf("https://images.unsplash.com/photo-1505797149-43b007662c21?q=80&w=500")
            )
        )
    }
}

package com.example.condospace.ui.mocks

import com.example.condospace.R
import com.example.condospace.model.Publication

class PublicationsMocks() {

    public fun getPublications(): List<Publication> {
        return listOf(
            Publication(
                id = 1,
                publicationOwner = "Ana Paula Silva",
                title = "Contadora",
                description = "Consultoria contábil para MEI e empresas.",
                detailedDescription = "Ofereço serviços completos de contabilidade, declaração de imposto de renda e planejamento financeiro com foco em pequenos negócios.",
                publicationType = "serviço",
                score = 4.9,
                reviewsNumber = 127,
                imageRes = R.drawable.img_contadora,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 2,
                publicationOwner = "Carlos Mendes",
                title = "Encanador",
                description = "Reparos hidráulicos em geral.",
                detailedDescription = "Especialista em detecção de vazamentos, troca de tubulação e instalação de metais sanitários com garantia de serviço.",
                publicationType = "serviço",
                score = 4.8,
                reviewsNumber = 94,
                imageRes = R.drawable.img_encanador,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 3,
                publicationOwner = "Paula Silva",
                title = "Diarista",
                description = "Limpeza residencial e comercial.",
                detailedDescription = "serviço de limpeza profunda ou manutenção, organização de armários e passadoria. Disponibilidade imediata no condomínio.",
                publicationType = "serviço",
                score = 4.9,
                reviewsNumber = 127,
                imageRes = R.drawable.img_diarista,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 4,
                publicationOwner = "Jonas Silveira",
                title = "Pedreiro",
                description = "Pequenas reformas e alvenaria.",
                detailedDescription = "Experiência em assentamento de pisos, azulejos e reparos estruturais. Orçamento sem compromisso.",
                publicationType = "serviço",
                score = 4.8,
                reviewsNumber = 94,
                imageRes = R.drawable.img_pedreiro,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            )
        )

    }

    public fun getExternalPublications(): List<Publication> {
        return listOf(
            Publication(
                id = 5,
                publicationOwner = "Paulo Silva",
                title = "Pintor",
                description = "Pintura residencial interna e externa.",
                detailedDescription = "Trabalho com texturas, grafiato e pintura fina. Utilizo materiais de primeira linha para garantir durabilidade.",
                publicationType = "serviço",
                score = 4.9,
                reviewsNumber = 127,
                imageRes = R.drawable.img_pintor,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 6,
                publicationOwner = "Matheus Silva",
                title = "Advogado",
                description = "Assessoria jurídica civil e família.",
                detailedDescription = "Especialista em direito do consumidor, contratos e mediação de conflitos. Atendimento personalizado.",
                publicationType = "serviço",
                score = 4.8,
                reviewsNumber = 94,
                imageRes = R.drawable.img_advogado,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 7,
                publicationOwner = "Paula Silva",
                title = "Diarista",
                description = "Limpeza profissional.",
                detailedDescription = "Equipe treinada para limpeza pós-obra e pré-mudança. Atendemos toda a região metropolitana.",
                publicationType = "serviço",
                score = 4.9,
                reviewsNumber = 127,
                imageRes = R.drawable.img_diarista,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 8,
                publicationOwner = "Jonas Silveira",
                title = "Pedreiro",
                description = "Mestre de obras e reformas.",
                detailedDescription = "Gestão completa de obras residenciais do alicerce ao acabamento. Equipe qualificada.",
                publicationType = "serviço",
                score = 4.8,
                reviewsNumber = 94,
                imageRes = R.drawable.img_pedreiro,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            )
        )
    }

    public fun getFavoritedPublications(): List<Publication> {
        return listOf(
            Publication(
                id = 9,
                publicationOwner = "Paula Silva",
                title = "Contadora",
                description = "Atendimento a domicílio",
                detailedDescription = "Consultoria contábil completa com atendimento personalizado na sua residência ou via chamada de vídeo.",
                publicationType = "serviço",
                score = 4.8,
                reviewsNumber = 94,
                imageRes = R.drawable.img_contadora,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://wordpress-cms-revista-prod-assets.quero.space/legacy_posts/post_images/31496/73b63defedd6ae08f15d9509b7871444961b24ca.jpg?1600797248"
            ),
            Publication(
                id = 10,
                publicationOwner = "Ricardo Gomes",
                title = "Sofá 3 lugares cinza",
                description = "Móveis",
                detailedDescription = "Sofá em ótimo estado, pouco uso. Tecido impermeabilizado e sem manchas. Retirada no bloco B.",
                publicationType = "produto",
                imageRes = R.drawable.img_sofa,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 11,
                publicationOwner = "Juliana Lins",
                title = "Cadeira de escritório",
                description = "Móveis",
                detailedDescription = "Cadeira ergonômica com regulagem de altura e braços. Motivo da venda: troca por um modelo gamer.",
                publicationType = "produto",
                imageRes = R.drawable.img_cadeira_escritorio,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            ),
            Publication(
                id = 12,
                publicationOwner = "Fernanda Costa",
                title = "Televisão 55 polegadas Samsung",
                description = "Eletrônicos",
                detailedDescription = "Smart TV 4K em perfeito estado. Acompanha controle remoto original. Motivo: Não cabe na minha casa nova.",
                publicationType = "produto",
                imageRes = R.drawable.img_tv_samsung55,
                date = "10/05/2023",
                price = 120.0,
                likes = 10,
                imageUrl = "https://example.com/image1.jpg"
            )
        )
    }

}
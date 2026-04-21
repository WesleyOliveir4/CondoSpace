package com.example.condospace.data.mapper

import android.net.Uri
import com.example.condospace.data.model.Publication
import com.example.condospace.data.model.PublicationImage
import com.example.condospace.domain.entity.PublicationEntity
import com.example.condospace.domain.entity.PublicationImageEntity
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PublicationMapperTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `Publication toEntity should map all fields correctly`() {
        val mockUri = mockk<Uri>()
        val model = Publication(
            id = "1",
            publicationOwnerUuid = "owner123",
            publicationCondominiumId = "condo456",
            publicationOwner = "John Doe",
            serviceProvider = "true",
            contact = "123456789",
            imageUrlList = listOf(PublicationImage("url1", "id1")),
            imagesSelectList = listOf(mockUri),
            title = "Title",
            description = "Description",
            publicationType = "Venda",
            price = 100.0,
            likes = 5,
            date = "2023-10-10",
            coupon = "PROMO10"
        )

        val entity = model.toEntity()

        assertEquals(model.id, entity.id)
        assertEquals(model.publicationOwnerUuid, entity.publicationOwnerUuid)
        assertEquals(model.publicationCondominiumId, entity.publicationCondominiumId)
        assertEquals(model.publicationOwner, entity.publicationOwner)
        assertEquals(model.serviceProvider, entity.serviceProvider)
        assertEquals(model.contact, entity.contact)
        assertEquals(model.imageUrlList?.size, entity.imageUrlList?.size)
        assertEquals(model.imageUrlList?.get(0)?.url, entity.imageUrlList?.get(0)?.url)
        assertEquals(model.imagesSelectList, entity.imagesSelectList)
        assertEquals(model.title, entity.title)
        assertEquals(model.description, entity.description)
        assertEquals(model.publicationType, entity.publicationType)
        assertEquals(model.price, entity.price, 0.0)
        assertEquals(model.likes, entity.likes)
        assertEquals(model.date, entity.date)
        assertEquals(model.coupon, entity.coupon)
    }

    @Test
    fun `Publication toEntity should handle null imageUrlList`() {
        val model = Publication(id = "1", imageUrlList = null)
        val entity = model.toEntity()
        assertNull(entity.imageUrlList)
    }

    @Test
    fun `PublicationEntity toModel should map all fields correctly`() {
        val mockUri = mockk<Uri>()
        val entity = PublicationEntity(
            id = "1",
            publicationOwnerUuid = "owner123",
            publicationCondominiumId = "condo456",
            publicationOwner = "John Doe",
            serviceProvider = "true",
            contact = "123456789",
            imageUrlList = listOf(PublicationImageEntity("url1", "id1")),
            imagesSelectList = listOf(mockUri),
            title = "Title",
            description = "Description",
            publicationType = "Venda",
            price = 100.0,
            likes = 5,
            date = "2023-10-10",
            coupon = "PROMO10"
        )

        val model = entity.toModel()

        assertEquals(entity.id, model.id)
        assertEquals(entity.publicationOwnerUuid, model.publicationOwnerUuid)
        assertEquals(entity.publicationCondominiumId, model.publicationCondominiumId)
        assertEquals(entity.publicationOwner, model.publicationOwner)
        assertEquals(entity.serviceProvider, model.serviceProvider)
        assertEquals(entity.contact, model.contact)
        assertEquals(entity.imageUrlList?.size, model.imageUrlList?.size)
        assertEquals(entity.imageUrlList?.get(0)?.url, model.imageUrlList?.get(0)?.url)
        assertEquals(entity.imagesSelectList, model.imagesSelectList)
        assertEquals(entity.title, model.title)
        assertEquals(entity.description, model.description)
        assertEquals(entity.publicationType, model.publicationType)
        assertEquals(entity.price, model.price, 0.0)
        assertEquals(entity.likes, model.likes)
        assertEquals(entity.date, model.date)
        assertEquals(entity.coupon, model.coupon)
    }

    @Test
    fun `PublicationEntity toModel should handle null imageUrlList`() {
        val entity = PublicationEntity(
            id = "1",
            publicationOwnerUuid = "",
            publicationCondominiumId = "",
            publicationOwner = "",
            title = "",
            description = "",
            publicationType = "",
            price = 0.0,
            likes = 0,
            date = "",
            imageUrlList = null
        )
        val model = entity.toModel()
        assertNull(model.imageUrlList)
    }

    @Test
    fun `List of PublicationImage toEntity should map correctly`() {
        val list = listOf(PublicationImage("url", "id"))
        val entityList = list.toEntity()
        
        assertEquals(1, entityList.size)
        assertEquals("url", entityList[0].url)
        assertEquals("id", entityList[0].publicId)
    }

    @Test
    fun `List of PublicationImageEntity toModel should map correctly`() {
        val entityList = listOf(PublicationImageEntity("url", "id"))
        val list = entityList.toModel()
        
        assertEquals(1, list.size)
        assertEquals("url", list[0].url)
        assertEquals("id", list[0].publicId)
    }
}

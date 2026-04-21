package com.example.condospace.domain.usecase.publication

import android.net.Uri
import com.example.condospace.presentation.ui.enums.ServiceType
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetContactUrlUseCaseTest {

    private val useCase = GetContactUrlUseCase()

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        // Mocking Uri.encode for pure unit test (avoiding Robolectric for speed)
        every { Uri.encode(any()) } answers {
            val s = it.invocation.args[0] as String
            s.replace(" ", "%20").replace("!", "%21")
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `invoke should return null when contact is null`() {
        val result = useCase(null, "Owner", "Title", "internal")
        assertNull(result)
    }

    @Test
    fun `invoke should return wa me link for internal service`() {
        val contact = "(11) 99999-8888"
        val owner = "John"
        val title = "Sofa"
        val result = useCase(contact, owner, title, "internal")

        assertTrue(result?.contains("wa.me/5511999998888") == true)
        assertTrue(result?.contains("Olá%20John") == true)
        assertTrue(result?.contains("Sofa") == true)
    }

    @Test
    fun `invoke should return formatted url for external service when contact is a link`() {
        val contact = "www.google.com"
        val result = useCase(contact, "Owner", "Title", ServiceType.EXTERNAL.value)

        assertEquals("https://www.google.com", result)
    }

    @Test
    fun `invoke should return original url for external service when contact already has protocol`() {
        val contact = "https://www.google.com"
        val result = useCase(contact, "Owner", "Title", ServiceType.EXTERNAL.value)

        assertEquals("https://www.google.com", result)
    }
    
    @Test
    fun `invoke should return original url with http for external service when contact already has protocol`() {
        val contact = "http://www.google.com"
        val result = useCase(contact, "Owner", "Title", ServiceType.EXTERNAL.value)

        assertEquals("http://www.google.com", result)
    }
}

package com.example.condospace.data.datastore

import androidx.datastore.core.CorruptionException
import com.example.condospace.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class UserSerializerTest {

    private val serializer = UserSerializer

    @Test
    fun `readFrom should return user when input is valid json`() = runTest {
        val user = User(uuid = "123", name = "Test User")
        val json = """{"uuid":"123","name":"Test User","phoneNumber":"","profilePicture":null,"email":"","condominium":null,"publicationsIdFavored":null,"notificationsEnabled":true,"userIsLogged":true}"""
        val inputStream = ByteArrayInputStream(json.toByteArray())

        val result = serializer.readFrom(inputStream)

        assertEquals(user, result)
    }

    @Test(expected = CorruptionException::class)
    fun `readFrom should throw CorruptionException when input is invalid`() = runTest {
        val invalidJson = "invalid json"
        val inputStream = ByteArrayInputStream(invalidJson.toByteArray())

        serializer.readFrom(inputStream)
    }

    @Test
    fun `writeTo should write correct bytes to output stream`() = runTest {
        val user = User(uuid = "123", name = "Test User")
        val outputStream = ByteArrayOutputStream()

        serializer.writeTo(user, outputStream)

        val resultJson = outputStream.toByteArray().decodeToString()
        // We check if it can be read back to ensure validity
        val readBack = serializer.readFrom(ByteArrayInputStream(outputStream.toByteArray()))
        assertEquals(user, readBack)
    }

    @Test
    fun `writeTo should do nothing when user is null`() = runTest {
        val outputStream = ByteArrayOutputStream()

        serializer.writeTo(null, outputStream)

        assertEquals(0, outputStream.size())
    }

    @Test
    fun `defaultValue should be null`() {
        assertNull(serializer.defaultValue)
    }
}

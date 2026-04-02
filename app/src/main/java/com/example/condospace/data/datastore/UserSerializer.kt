package com.example.condospace.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.condospace.data.model.User
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<User?> {
    override val defaultValue: User? = null

    override suspend fun readFrom(input: InputStream): User? {
        try {
            return Json.decodeFromString(
                deserializer = User.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read User data", serialization)
        }
    }

    override suspend fun writeTo(user: User?, output: OutputStream) {
        user?.let {
            output.write(
                Json.encodeToString(
                    serializer = User.serializer(),
                    value = it
                ).encodeToByteArray()
            )
        }
    }
}

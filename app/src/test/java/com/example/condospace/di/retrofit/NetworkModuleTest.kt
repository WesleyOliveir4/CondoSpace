package com.example.condospace.di.retrofit

import com.example.condospace.data.remote.OpenCageService
import com.example.condospace.data.remote.ViaCepService
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class NetworkModuleTest : KoinTest {

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `verify network module definitions`() {
        networkModule.verify()
    }

    @Test
    fun `should provide network dependencies`() {
        val app = koinApplication {
            modules(networkModule)
        }.koin

        assertNotNull(app.get<Json>())
        assertNotNull(app.get<OkHttpClient>())
        assertNotNull(app.get<ViaCepService>())
        assertNotNull(app.get<OpenCageService>())
    }
}

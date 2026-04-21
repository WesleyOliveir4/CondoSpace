package com.example.condospace.di.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.condospace.data.model.User
import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.ExternalServiceRepository
import com.example.condospace.domain.repository.ImageRepository
import com.example.condospace.domain.repository.PublicationRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.condospace.di.retrofit.networkModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest

class DataModuleTest : KoinTest {

    @Before
    fun setUp() {
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkStatic(FirebaseAuth::class)
        unmockkStatic(FirebaseFirestore::class)
        stopKoin()
    }

    @Test
    fun `should resolve all data and repository definitions`() {
        val mockContext = mockk<Context>(relaxed = true)
        
        val app = koinApplication {
            androidContext(mockContext)
            modules(dataModule, networkModule)
        }.koin

        assertNotNull(app.get<AuthRepository>())
        assertNotNull(app.get<UserRepository>())
        assertNotNull(app.get<PublicationRepository>())
        assertNotNull(app.get<ImageRepository>())
        assertNotNull(app.get<UserPreferencesRepository>())
        assertNotNull(app.get<ExternalServiceRepository>())
        
        assertNotNull(app.get<FirebaseAuth>())
        assertNotNull(app.get<FirebaseFirestore>())
    }
}

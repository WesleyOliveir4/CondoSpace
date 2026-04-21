package com.example.condospace.data.repositoryImpl.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        auth = mockk(relaxed = true)
        repository = AuthRepositoryImpl(auth)
        
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `signUp should return success with uid when authentication is successful`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val uid = "user_uid_123"
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()
        val task = mockk<Task<AuthResult>>()

        every { auth.createUserWithEmailAndPassword(email, password) } returns task
        coEvery { task.await() } returns authResult
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns uid

        // Act
        val result = repository.signUp(email, password)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(uid, result.getOrNull())
        verify { auth.createUserWithEmailAndPassword(email, password) }
    }

    @Test
    fun `signUp should return failure when user is null`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val authResult = mockk<AuthResult>()
        val task = mockk<Task<AuthResult>>()

        every { auth.createUserWithEmailAndPassword(email, password) } returns task
        coEvery { task.await() } returns authResult
        every { authResult.user } returns null

        // Act
        val result = repository.signUp(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User is null", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signUp should return failure when exception occurs`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Sign up failed")
        val task = mockk<Task<AuthResult>>()

        every { auth.createUserWithEmailAndPassword(email, password) } returns task
        coEvery { task.await() } throws exception

        // Act
        val result = repository.signUp(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `signIn should return success when authentication is successful`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val authResult = mockk<AuthResult>()
        val task = mockk<Task<AuthResult>>()

        every { auth.signInWithEmailAndPassword(email, password) } returns task
        coEvery { task.await() } returns authResult

        // Act
        val result = repository.signIn(email, password)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `signIn should return failure when exception occurs`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Sign in failed")
        val task = mockk<Task<AuthResult>>()

        every { auth.signInWithEmailAndPassword(email, password) } returns task
        coEvery { task.await() } throws exception

        // Act
        val result = repository.signIn(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `updatePassword should return success when user is not null and update is successful`() = runTest {
        // Arrange
        val newPassword = "newPassword123"
        val firebaseUser = mockk<FirebaseUser>()
        val task = mockk<Task<Void>>()

        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.updatePassword(newPassword) } returns task
        coEvery { task.await() } returns mockk()

        // Act
        val result = repository.updatePassword(newPassword)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `updatePassword should return failure when user is null`() = runTest {
        // Arrange
        val newPassword = "newPassword123"
        every { auth.currentUser } returns null

        // Act
        val result = repository.updatePassword(newPassword)

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `getCurrentUserUid should return uid when user is logged in`() {
        // Arrange
        val uid = "user_uid_123"
        val firebaseUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns uid

        // Act
        val result = repository.getCurrentUserUid()

        // Assert
        assertEquals(uid, result)
    }

    @Test
    fun `getCurrentUserUid should return null when user is not logged in`() {
        // Arrange
        every { auth.currentUser } returns null

        // Act
        val result = repository.getCurrentUserUid()

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun `signOut should return success when signOut is called`() = runTest {
        // Act
        val result = repository.signOut()

        // Assert
        assertTrue(result.isSuccess)
        verify { auth.signOut() }
    }

    @Test
    fun `signOut should return failure when exception occurs`() = runTest {
        // Arrange
        val exception = Exception("Sign out error")
        every { auth.signOut() } throws exception

        // Act
        val result = repository.signOut()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `updatePassword should return failure when exception occurs`() = runTest {
        // Arrange
        val newPassword = "newPassword123"
        val firebaseUser = mockk<FirebaseUser>()
        val task = mockk<Task<Void>>()
        val exception = Exception("Update password failed")

        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.updatePassword(newPassword) } returns task
        coEvery { task.await() } throws exception

        // Act
        val result = repository.updatePassword(newPassword)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}

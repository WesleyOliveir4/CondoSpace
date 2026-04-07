package com.example.condospace.domain.usecase.login

import com.example.condospace.domain.repository.AuthRepository
import com.example.condospace.domain.repository.UserPreferencesRepository
import com.example.condospace.domain.repository.UserRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            val currentUser = userPreferencesRepository.getUserData()
            currentUser?.let {
                val updatedUser = it.copy(userIsLogged = false)
                
                // Update Firebase Firestore
                userRepository.updateUser(updatedUser)
                
                // Update Local DataStore
                userPreferencesRepository.saveUserData(updatedUser)
            }
            
            // Sign out from Firebase Auth
            authRepository.signOut()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.example.condospace.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    object LoginScreen : NavRoutes()

    @Serializable
    object RegisterScreen : NavRoutes()


    @Serializable
    object Home : NavRoutes()

    @Serializable
    object Favorites : NavRoutes()

    @Serializable
    object Publish : NavRoutes()

    @Serializable
    object Profile : NavRoutes()
    @Serializable
    object ChangePasswordScreen : NavRoutes()
    @Serializable
        object SettingsScreen : NavRoutes()

    @Serializable
    data class PublicationsList(val categoryType: String, val publicationsIds: List<String>? = null) : NavRoutes()
    
    @Serializable
    data class PublicationSelected(val publicationId: String, val categoryType: String? = null) : NavRoutes()
    
    @Serializable
    data class EditPublicationScreen(val publicationId: String) : NavRoutes()
    
    @Serializable
    object UserDataScreen : NavRoutes()
    @Serializable
    data class SelectCondominiumScreen(val userId: String, val registerFlow: Boolean = false) : NavRoutes()

}

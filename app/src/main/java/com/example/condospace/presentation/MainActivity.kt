package com.example.condospace.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.condospace.presentation.navigation.NavNavigation
import com.example.condospace.presentation.ui.theme.CondoSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CondoSpaceTheme {
                NavNavigation()
            }
        }
    }
}
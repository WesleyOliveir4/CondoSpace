package com.example.condospace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.condospace.navigation.NavNavigation
import com.example.condospace.ui.theme.CondoSpaceTheme

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
package com.example.jecpackcomposeno1.ui.theme.screen.swipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SwipeScreen() {
    Scaffold() { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
           Text(text = "File Screen", style = MaterialTheme.typography.headlineMedium)
        }
    }
}






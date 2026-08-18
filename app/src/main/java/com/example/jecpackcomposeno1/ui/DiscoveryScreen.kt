package com.example.jecpackcomposeno1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.jecpackcomposeno1.extensions.LocalNavController
import com.example.jecpackcomposeno1.navigation.AppDestination

fun NavGraphBuilder.discoveryScreen() {
    composable(AppDestination.DiscoveryScreen.route) {
        DiscoveryScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun PreviewDiscoveryContent() {
    DiscoveryScreenContent(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    )
}

@Composable
fun DiscoveryScreen(
    modifier: Modifier = Modifier
) {
    val localNavController = LocalNavController.current

    DiscoveryScreenContent(
        modifier = modifier
    )
}

@Composable
private fun DiscoveryScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

    }
}
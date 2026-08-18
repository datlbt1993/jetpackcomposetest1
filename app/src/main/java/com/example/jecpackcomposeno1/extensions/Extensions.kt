package com.example.jecpackcomposeno1.extensions

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> { error("NO navController found.") }
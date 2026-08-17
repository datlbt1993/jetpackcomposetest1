package com.example.jecpackcomposeno1.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun stringNavArgs(vararg names: String) = names.map { name ->
    navArgument(name) { type = NavType.StringType }
}

fun NavBackStackEntry.stringArg(name: String): String =
    arguments?.getString(name).orEmpty()

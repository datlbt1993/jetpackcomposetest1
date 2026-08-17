package com.example.jecpackcomposeno1.navigation

/** Tất cả màn hình có thể điều hướng tới trong app. */
sealed interface AppDestination {
    data object TabHome : AppDestination
    data object TabSwipe : AppDestination
    data object TabCompress : AppDestination

    data object Photos : AppDestination
    data object Videos : AppDestination
    data object Trash : AppDestination
    data class VideoPlayer(val uri: String) : AppDestination

    data object Back : AppDestination
}

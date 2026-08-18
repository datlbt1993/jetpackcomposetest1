package com.example.jecpackcomposeno1.navigation

/** Tất cả màn hình có thể điều hướng tới trong app. */
sealed interface AppDestination {
    val route: String

    data object TabHome : AppDestination {
        override val route: String = "tab_home"
    }

    data object TabSwipe : AppDestination {
        override val route: String
            get() = "tab_swipe"
    }

    data object TabCompress : AppDestination {
        override val route: String
            get() = "tab_compress"
    }

    data object Photos : AppDestination {
        override val route: String
            get() = "photos"
    }

    data object Videos : AppDestination {
        override val route: String
            get() = "videos"
    }

    data object Trash : AppDestination {
        override val route: String
            get() = "trash"
    }

    data class VideoPlayer(val uri: String) : AppDestination {
        override val route: String
            get() = "video_player"
    }

    data object Back : AppDestination {
        override val route: String
            get() = "back"
    }

    data object DiscoveryScreen: AppDestination {
        override val route: String
            get() = "discovery_screen"
    }
}

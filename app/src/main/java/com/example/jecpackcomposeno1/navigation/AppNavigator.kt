package com.example.jecpackcomposeno1.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.example.jecpackcomposeno1.ui.theme.screen.MainRoute

class AppNavigator(
    private val rootNav: NavHostController,
    val homeNav: NavHostController,
) {
    private var storageGate: ((String) -> Unit)? = null

    fun bindStorageGate(gate: ((String) -> Unit)?) {
        storageGate = gate
    }

    fun navigate(dest: AppDestination) {
        when (dest) {
            AppDestination.TabHome -> rootNav.navigateTab(MainRoute.Home)
            AppDestination.TabSwipe -> rootNav.navigateTab(MainRoute.Swipe)
            AppDestination.TabCompress -> rootNav.navigateTab(MainRoute.Compress)

            AppDestination.Photos -> openWithStorageGate(
                MainRoute.listPhotoVideo(MainRoute.MediaPhotos),
            )
            AppDestination.Videos -> openWithStorageGate(
                MainRoute.listPhotoVideo(MainRoute.MediaVideos),
            )
            AppDestination.Trash -> homeNav.navigateSafe(MainRoute.Trash)

            is AppDestination.VideoPlayer -> homeNav.navigateSafe(
                MainRoute.videoPlayer(dest.uri),
            )

            AppDestination.Back -> navigateBack()
        }
    }

    /** Mở route Home sau khi gate xin quyền xong. */
    fun openHomeRoute(route: String) {
        homeNav.navigateSafe(route) { launchSingleTop = true }
    }

    /**
     * Back: ưu tiên nav con (Home), hết mới tới nav gốc.
     *
     * Kiểm tra previousBackStackEntry chứ không dựa vào popBackStack() trả về false:
     * pop khi Home đang ở start destination vẫn trả về true nhưng làm RỖNG graph
     * -> NavHost không còn gì để vẽ, màn hình trắng.
     *
     * Và chỉ pop Home khi đang thật sự đứng ở tab Home: homeNav giữ nguyên back stack
     * kể cả khi user đã sang tab khác, nếu không kiểm tra thì back ở tab Swipe sẽ
     * lặng lẽ pop màn trong tab Home.
     */
    private fun navigateBack() {
        val onHomeTab = rootNav.currentDestination?.route == MainRoute.Home
        if (onHomeTab && homeNav.previousBackStackEntry != null) {
            homeNav.popBackStack()
        } else {
            rootNav.popBackStack()
        }
    }

    private fun openWithStorageGate(route: String) {
        val gate = storageGate
        if (gate != null) {
            gate(route)
        } else {
            // Lưới an toàn: gate luôn được bind khi tab Home đang hiển thị.
            homeNav.navigateSafe(route)
        }
    }
}

/**
 * Navigate an toàn: chống double-click (bấm nhanh 2 lần mở 2 màn chồng nhau).
 * Chỉ navigate khi màn hiện tại đang RESUMED — sau lần navigate đầu, entry cũ rời
 * RESUMED nên lần bấm thứ 2 bị chặn.
 *
 * private để mọi điều hướng buộc phải đi qua [AppNavigator.navigate].
 */
private fun NavHostController.navigateSafe(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route, builder)
    }
}

/**
 * Chuyển tab bottom nav: đủ 3 "thần chú" back stack
 * (popUpTo + saveState, singleTop, restoreState).
 */
private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true          // lưu state tab bị rời
        }
        launchSingleTop = true        // không tạo bản sao tab
        restoreState = true           // khôi phục state khi quay lại tab
    }
}

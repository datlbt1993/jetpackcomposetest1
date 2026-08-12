package com.example.jecpackcomposeno1.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder

/**
 * Navigate an toàn: chống double-click (bấm nhanh 2 lần mở 2 màn chồng nhau).
 * Cơ chế: chỉ navigate khi màn hiện tại đang RESUMED. Sau lần navigate đầu,
 * entry cũ rời RESUMED -> lần bấm thứ 2 bị chặn.
 */
fun NavController.navigateSafe(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route, builder)
    }
}

/**
 * Chuyển tab bottom nav: đủ 3 "thần chú" back stack (popUpTo + singleTop + saveState/restoreState).
 * Dùng cho bottom navigation.
 */
fun NavController.navigateTab(route: String) {
    // Không cần navigateSafe ở đây vì tab dùng launchSingleTop rồi
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true          // lưu state tab bị rời
        }
        launchSingleTop = true        // không tạo bản sao tab
        restoreState = true           // khôi phục state khi quay lại tab
    }
}

/**
 * Điều hướng sau đăng nhập / xóa back stack tới màn gốc.
 * Ví dụ: từ Login sang Home, không cho back về Login.
 */
fun NavController.navigateAndClearBackStack(
    route: String,
    popUpToRoute: String
) {
    navigate(route) {
        popUpTo(popUpToRoute) { inclusive = true }   // xóa luôn màn cũ
        launchSingleTop = true
    }
}

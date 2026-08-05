package com.example.jecpackcomposeno1.ui.theme.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.screen.compress.CompressScreen
import com.example.jecpackcomposeno1.ui.theme.screen.home.HomeScreen
import com.example.jecpackcomposeno1.ui.theme.screen.swipe.SwipeScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Lấy destination hiện tại để biết tab nào đang chọn
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                // Xoá back stack về màn gốc để không chồng vô hạn
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true      // lưu state của tab bị rời
                                }
                                launchSingleTop = true    // không tạo bản sao nếu đã ở tab đó
                                restoreState = true       // khôi phục state khi quay lại tab
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = tab.icon),
                                contentDescription = stringResource(id = tab.label)
                            )
                        },
                        label = {
                            Text(text = stringResource(id = tab.label))
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(BottomTab.Home.route) { HomeScreen() }
            composable(BottomTab.Swipe.route) { SwipeScreen() }
            composable(BottomTab.Compress.route) { CompressScreen() }
        }
    }
}

sealed class BottomTab(
    val route: String,
    val label: Int,
    val icon: Int
) {
    data object Home : BottomTab("home", R.string.all_home, R.drawable.ic_home)
    data object Swipe : BottomTab("swipe", R.string.all_swipe, R.drawable.ic_file)
    data object Compress : BottomTab("compress", R.string.tv_compress, R.drawable.ic_setting)
}

private val bottomTabs = listOf(BottomTab.Home, BottomTab.Swipe, BottomTab.Compress)

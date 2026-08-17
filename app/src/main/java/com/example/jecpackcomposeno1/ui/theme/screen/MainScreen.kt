package com.example.jecpackcomposeno1.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jecpackcomposeno1.MainSharedViewModel
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.navigation.AppDestination
import com.example.jecpackcomposeno1.navigation.AppNavigator
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.screen.compress.CompressScreen
import com.example.jecpackcomposeno1.ui.theme.screen.home.HomeNavHost
import com.example.jecpackcomposeno1.ui.theme.screen.home.HomeRoute
import com.example.jecpackcomposeno1.ui.theme.screen.swipe.SwipeScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val homeNavController = rememberNavController()
    val sharedViewModel: MainSharedViewModel = hiltViewModel()

    // Một navigator duy nhất cho cả app: UI chỉ nói "đi đâu", không đụng NavController.
    val navigator = remember(navController, homeNavController) {
        AppNavigator(rootNav = navController, homeNav = homeNavController)
    }


    LifecycleStartEffect(sharedViewModel) {
        sharedViewModel.onAppStarted()
        onStopOrDispose { }
    }

    val currentRoute = navController.currentRoute()
    val homeRoute = homeNavController.currentRoute()
    val showBottomBar = shouldShowBottomBar(currentRoute, homeRoute)
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    currentRoute = currentRoute,
                    onTabClick = { tab ->
                        navigator.navigate(tab.destination)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable(BottomTab.Home.route) {
                HomeNavHost(
                    navigator = navigator,
                    sharedViewModel = sharedViewModel,
                )
            }
            composable(BottomTab.Swipe.route) { SwipeScreen() }
            composable(BottomTab.Compress.route) { CompressScreen() }
        }
    }
}

sealed class BottomTab(
    val route: String,
    val label: Int,
    val icon: Int,
    val destination: AppDestination,
) {
    data object Home : BottomTab(
        HomeRoute.Home, R.string.all_home, R.drawable.ic_home, AppDestination.TabHome
    )

    data object Swipe : BottomTab(
        HomeRoute.Swipe, R.string.all_swipe, R.drawable.ic_file, AppDestination.TabSwipe
    )

    data object Compress : BottomTab(
        HomeRoute.Compress, R.string.tv_compress, R.drawable.ic_setting, AppDestination.TabCompress
    )
}

private val bottomTabs = listOf(BottomTab.Home, BottomTab.Swipe, BottomTab.Compress)

@Composable
private fun NavController.currentRoute(): String? {
    val entry by currentBackStackEntryAsState()
    return entry?.destination?.route
}

/** Bottom bar chỉ hiện ở tab gốc. Sub-screen Home (list/trash/player) thì ẩn. */
private fun shouldShowBottomBar(currentRoute: String?, homeRoute: String?): Boolean {
    if (currentRoute == BottomTab.Swipe.route || currentRoute == BottomTab.Compress.route) {
        return true
    }
    return currentRoute == BottomTab.Home.route &&
        (homeRoute == null || homeRoute == HomeRoute.Main)
}

@Composable
fun CustomBottomBar(
    currentRoute: String?,
    onTabClick: (BottomTab) -> Unit
) {
    Column() {
        HorizontalDivider(
            thickness = 2.dp,
            color = colorResource(R.color.color_divider_1)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(colorResource(R.color.white)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomTabs.forEach { tab ->
                val selected = currentRoute == tab.route
                val color = if (selected) colorResource(R.color.primary_primary)
                else colorResource(R.color.color_on_surface_variant_2)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(
                                color = colorResource(R.color.color_on_surface_variant_2)
                            ),
                            onClick = { onTabClick(tab) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(tab.icon),
                        contentDescription = stringResource(tab.label),
                        tint = color
                    )
                    Text(
                        text = stringResource(tab.label),
                        color = color,
                        style = AppTextStyles.Size12Medium
                    )
                }
            }
        }
    }
}
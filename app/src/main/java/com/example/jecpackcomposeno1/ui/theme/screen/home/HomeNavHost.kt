package com.example.jecpackcomposeno1.ui.theme.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jecpackcomposeno1.MainSharedViewModel
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.navigation.AppNavigator
import com.example.jecpackcomposeno1.navigation.NavAnim
import com.example.jecpackcomposeno1.ui.theme.permission.manageStorageRationale
import com.example.jecpackcomposeno1.ui.theme.permission.rememberManageStorageRequester
import com.example.jecpackcomposeno1.ui.theme.screen.MainRoute
import com.example.jecpackcomposeno1.ui.theme.screen.home.trash.TrashRoute

@Composable
fun HomeNavHost(
    navigator: AppNavigator,
    sharedViewModel: MainSharedViewModel,
) {
    val photos by sharedViewModel.allImagesStateFlow.collectAsStateWithLifecycle()
    val videos by sharedViewModel.allVideoStateFlow.collectAsStateWithLifecycle()

    val appName = stringResource(R.string.app_name)
    val allFilesRationale = remember(appName) { manageStorageRationale(appName) }
    val requestStorage = rememberManageStorageRequester(
        rationale = allFilesRationale,
        onOpenTarget = navigator::openGatedDestination,
        onGranted = sharedViewModel::refreshStorage,
    )

    // Dialog xin quyền sống trong composition này -> unbind khi rời tab Home.
    DisposableEffect(navigator, requestStorage) {
        navigator.bindStorageGate(requestStorage)
        onDispose { navigator.bindStorageGate(null) }
    }

    NavHost(
        navController = navigator.homeNav,
        startDestination = MainRoute.Main,
        enterTransition = NavAnim.enter,
        exitTransition = NavAnim.exit,
        popEnterTransition = NavAnim.popEnter,
        popExitTransition = NavAnim.popExit,
    ) {
        composable(MainRoute.Main) {
            HomeScreen(
                photoCount = photos.size,
                videoCount = videos.size,
                onNavigate = navigator::navigate,
            )
        }
        listPhotoVideoGraph(navigator)
        composable(MainRoute.Trash) {
            TrashRoute(onNavigate = navigator::navigate)
        }
    }
}

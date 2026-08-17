package com.example.jecpackcomposeno1.ui.theme.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jecpackcomposeno1.MainSharedViewModel
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.navigation.NavAnim
import com.example.jecpackcomposeno1.navigation.navigateSafe
import com.example.jecpackcomposeno1.ui.theme.permission.manageStorageRationale
import com.example.jecpackcomposeno1.ui.theme.permission.rememberManageStorageRequester
import com.example.jecpackcomposeno1.ui.theme.screen.home.trash.TrashRoute
@Composable
fun HomeNavHost(
    navController: NavHostController,
    sharedViewModel: MainSharedViewModel,
) {
    val photos by sharedViewModel.allImagesStateFlow.collectAsStateWithLifecycle()
    val videos by sharedViewModel.allVideoStateFlow.collectAsStateWithLifecycle()

    val appName = stringResource(R.string.app_name)
    val allFilesRationale = remember(appName) { manageStorageRationale(appName) }
    val requestStorage = rememberManageStorageRequester(
        rationale = allFilesRationale,
        onOpenTarget = { route -> navController.navigateSafe(route) { launchSingleTop = true } },
        onGranted = sharedViewModel::refreshStorage,
    )

    NavHost(
        navController = navController,
        startDestination = HomeRoute.Main,
        enterTransition = NavAnim.enter,
        exitTransition = NavAnim.exit,
        popEnterTransition = NavAnim.popEnter,
        popExitTransition = NavAnim.popExit,
    ) {
        composable(HomeRoute.Main) {
            HomeScreen(
                photoCount = photos.size,
                videoCount = videos.size,
                onOpenPhotos = {
                    requestStorage(HomeRoute.listPhotoVideo(HomeRoute.MediaPhotos))
                },
                onOpenVideos = {
                    requestStorage(HomeRoute.listPhotoVideo(HomeRoute.MediaVideos))
                },
                onOpenTrash = {
                    navController.navigateSafe(HomeRoute.Trash)
                },
            )
        }
        listPhotoVideoGraph(navController)
        composable(HomeRoute.Trash) {
            TrashRoute(onBack = { navController.popBackStack() })
        }
    }
}

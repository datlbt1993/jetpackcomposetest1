package com.example.jecpackcomposeno1.ui.theme.screen

import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jecpackcomposeno1.MainSharedViewModel
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.mvi.CollectEffect
import com.example.jecpackcomposeno1.navigation.navigateSafe
import com.example.jecpackcomposeno1.navigation.navigateTab
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.screen.compress.CompressScreen
import com.example.jecpackcomposeno1.ui.theme.screen.home.HomeRoute
import com.example.jecpackcomposeno1.ui.theme.screen.home.HomeScreen
import com.example.jecpackcomposeno1.ui.theme.screen.home.ListPhotoVideo
import com.example.jecpackcomposeno1.ui.theme.screen.home.VideoPlayerScreen
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListEffect
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListIntent
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListViewModel
import com.example.jecpackcomposeno1.ui.theme.screen.home.trash.TrashEffect
import com.example.jecpackcomposeno1.ui.theme.screen.home.trash.TrashIntent
import com.example.jecpackcomposeno1.ui.theme.screen.home.trash.TrashScreen
import com.example.jecpackcomposeno1.ui.theme.screen.home.trash.TrashViewModel
import com.example.jecpackcomposeno1.ui.theme.screen.swipe.SwipeScreen

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    // Lift lên đây để Scaffold biết đang ở ListPhotoVideo và ẩn CustomBottomBar.
    val homeNavController = rememberNavController()

    val sharedViewModel: MainSharedViewModel = hiltViewModel()
    val photos by sharedViewModel.allImagesStateFlow.collectAsStateWithLifecycle()
    val videos by sharedViewModel.allVideoStateFlow.collectAsStateWithLifecycle()

    LifecycleStartEffect(sharedViewModel) {
        sharedViewModel.onAppStarted()
        onStopOrDispose { }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val homeBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val homeRoute = homeBackStackEntry?.destination?.route
    val showBottomBar =
        currentRoute == BottomTab.Swipe.route ||
            currentRoute == BottomTab.Compress.route ||
            (currentRoute == BottomTab.Home.route &&
                (homeRoute == null || homeRoute == HomeRoute.Main))

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    currentRoute = currentRoute,
                    onTabClick = { tab ->
                        navController.navigateTab(tab.route)
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
                NavHost(
                    navController = homeNavController,
                    startDestination = HomeRoute.Main,
                ) {
                    composable(HomeRoute.Main) {
                        HomeScreen(
                            photoCount = photos.size,
                            videoCount = videos.size,
                            onStorageGranted = sharedViewModel::refreshStorage,
                            onOpenPhotos = {
                                homeNavController.navigateSafe(
                                    HomeRoute.listPhotoVideo(HomeRoute.MediaPhotos)
                                )
                            },
                            onOpenVideos = {
                                homeNavController.navigateSafe(
                                    HomeRoute.listPhotoVideo(HomeRoute.MediaVideos)
                                )
                            },
                            onOpenTrash = {
                                homeNavController.navigateSafe(HomeRoute.Trash)
                            },
                        )
                    }
                    composable(
                        route = HomeRoute.ListPhotoVideo,
                        arguments = listOf(
                            navArgument("mediaType") { type = NavType.StringType }
                        ),
                    ) { backStackEntry ->
                        val mediaType = backStackEntry.arguments?.getString("mediaType")
                        val isPhotos = mediaType == HomeRoute.MediaPhotos

                        val mediaListViewModel: MediaListViewModel = hiltViewModel()
                        val mediaState by mediaListViewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(isPhotos) {
                            mediaListViewModel.onIntent(MediaListIntent.Init(isPhotos))
                        }

                        CollectEffect(mediaListViewModel.effect) { effect ->
                            when (effect) {
                                is MediaListEffect.NavigateToVideoPlayer -> {
                                    homeNavController.navigateSafe(
                                        HomeRoute.videoPlayer(effect.uri)
                                    )
                                }
                                is MediaListEffect.NavigateToPhotoDetail -> {
                                    // TODO: thêm màn PhotoDetail khi cần
                                }
                                is MediaListEffect.ShowMessage -> {
                                    android.widget.Toast.makeText(
                                        context,
                                        effect.message,
                                        android.widget.Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        }

                        ListPhotoVideo(
                            state = mediaState,
                            onIntent = mediaListViewModel::onIntent,
                            onBack = { homeNavController.popBackStack() },
                        )
                    }
                    composable(HomeRoute.Trash) {
                        val trashViewModel: TrashViewModel = hiltViewModel()
                        val trashState by trashViewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(Unit) {
                            trashViewModel.onIntent(TrashIntent.Load)
                        }

                        CollectEffect(trashViewModel.effect) { effect ->
                            when (effect) {
                                is TrashEffect.ShowMessage -> {
                                    android.widget.Toast.makeText(
                                        context,
                                        effect.message,
                                        android.widget.Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        }

                        TrashScreen(
                            state = trashState,
                            onIntent = trashViewModel::onIntent,
                            onBack = { homeNavController.popBackStack() },
                        )
                    }
                    composable(
                        route = HomeRoute.VideoPlayer,
                        arguments = listOf(
                            navArgument("videoUri") { type = NavType.StringType }
                        ),
                    ) { backStackEntry ->
                        val encoded = backStackEntry.arguments?.getString("videoUri").orEmpty()
                        val videoUri = Uri.decode(encoded)

                        VideoPlayerScreen(
                            videoUri = videoUri,
                            onBack = { homeNavController.popBackStack() },
                        )
                    }
                }
            }
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
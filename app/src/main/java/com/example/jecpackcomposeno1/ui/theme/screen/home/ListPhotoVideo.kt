package com.example.jecpackcomposeno1.ui.theme.screen.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.mvi.CollectEffect
import com.example.jecpackcomposeno1.navigation.AppDestination
import com.example.jecpackcomposeno1.navigation.AppNavigator
import com.example.jecpackcomposeno1.navigation.stringArg
import com.example.jecpackcomposeno1.navigation.stringNavArgs
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerHeight
import com.example.jecpackcomposeno1.ui.theme.component.hasManageExternalStoragePermission
import com.example.jecpackcomposeno1.ui.theme.data.ItemFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoFile
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListEffect
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListIntent
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListState
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListViewModel
import java.util.Locale
import java.util.concurrent.TimeUnit

fun NavGraphBuilder.listPhotoVideoGraph(navigator: AppNavigator) {
    composable(
        route = HomeRoute.ListPhotoVideo,
        arguments = stringNavArgs(HomeRoute.ArgMediaType),
    ) { backStackEntry ->
        ListPhotoVideoRoute(
            isPhotos = backStackEntry.stringArg(HomeRoute.ArgMediaType) == HomeRoute.MediaPhotos,
            onNavigate = navigator::navigate,
        )
    }
    composable(
        route = HomeRoute.VideoPlayer,
        arguments = stringNavArgs(HomeRoute.ArgVideoUri),
    ) { backStackEntry ->
        VideoPlayerScreen(
            videoUri = Uri.decode(backStackEntry.stringArg(HomeRoute.ArgVideoUri)),
            onBack = { navigator.navigate(AppDestination.Back) },   // màn "câm", chỉ có 1 lối ra
        )
    }
}

@Composable
fun ListPhotoVideoRoute(
    isPhotos: Boolean,
    onNavigate: (AppDestination) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: MediaListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isPhotos) {
        viewModel.onIntent(MediaListIntent.Init(isPhotos))
    }

    var isFirstResume by rememberSaveable { mutableStateOf(true) }
    var hasStoragePermission by remember {
        mutableStateOf(context.hasManageExternalStoragePermission())
    }
    LifecycleResumeEffect(Unit) {
        hasStoragePermission = context.hasManageExternalStoragePermission()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            viewModel.onIntent(MediaListIntent.Refresh)
        }
        onPauseOrDispose { }
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is MediaListEffect.NavigateToVideoPlayer ->
                onNavigate(AppDestination.VideoPlayer(effect.uri))
            is MediaListEffect.NavigateToPhotoDetail -> {

            }
            is MediaListEffect.ShowMessage -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    ListPhotoVideo(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = { onNavigate(AppDestination.Back) },
        hasStoragePermission = hasStoragePermission,
    )
}
@Composable
fun ListPhotoVideo(
    state: MediaListState,
    onIntent: (MediaListIntent) -> Unit,
    onBack: () -> Unit,
    hasStoragePermission: Boolean = true,
) {
    BackHandler(onBack = onBack)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.surface_normal))
            .statusBarsPadding()
    ) {
        ToolbarHome(
            isShowTrash = true,
            onBackClick = onBack,
            title = if (state.isPhotos) {
                stringResource(R.string.tv_photos)
            } else {
                stringResource(R.string.tv_videos)
            },
        )

        SelectionActionsRow(
            state = state,
            onIntent = onIntent,
        )

        CommonSpacerHeight(8)

        MediaListContent(
            state = state,
            hasStoragePermission = hasStoragePermission,
            onIntent = onIntent,
            modifier = Modifier.weight(1f),
        )

        if (state.selectedCount > 0) {
            Button(
                onClick = { onIntent(MediaListIntent.DeleteSelected) },
                enabled = !state.isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.primary_primary),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.all_delete) + " (${state.selectedCount})",
                    color = Color.White,
                    style = AppTextStyles.Size16SemiBold,
                )
            }
        }
    }
}

@Composable
private fun MediaListContent(
    state: MediaListState,
    hasStoragePermission: Boolean,
    onIntent: (MediaListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when {
            !hasStoragePermission -> {
                MediaListMessage(text = stringResource(R.string.tv_you_have_not_granted))
            }

            state.isLoading && state.items.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(R.color.primary_primary))
                }
            }

            state.error != null && state.items.isEmpty() -> {
                MediaListMessage(
                    text = state.error ?: "Error",
                    onClick = { onIntent(MediaListIntent.Refresh) },
                )
            }

            state.items.isEmpty() -> {
                MediaListMessage(
                    text = if (state.isPhotos) "No photos" else "No videos",
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = state.items,
                        key = { it.pathFile },
                    ) { item ->
                        MediaGridItem(
                            item = item,
                            selected = item.pathFile in state.selectedPaths,
                            onOpen = { onIntent(MediaListIntent.ClickItem(item)) },
                            onToggleSelect = {
                                onIntent(MediaListIntent.ToggleSelect(item.pathFile))
                            },
                        )
                    }
                }
            }
        }

        if (state.isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = colorResource(R.color.primary_primary))
            }
        }
    }
}

@Composable
private fun MediaListMessage(
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppTextStyles.Size14Medium,
            color = colorResource(R.color.color_on_surface_variant_2),
        )
    }
}

@Composable
private fun SelectionActionsRow(
    state: MediaListState,
    onIntent: (MediaListIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (state.selectedCount > 0) {
                "${state.selectedCount}/${state.items.size} selected"
            } else {
                "${state.items.size} items"
            },
            style = AppTextStyles.Size16SemiBold,
            color = colorResource(R.color.color_on_surface),
            modifier = Modifier.weight(1f),
        )

        // Checkbox All — Off / Indeterminate (chọn một phần) / On (chọn hết)
        val allState = when {
            state.selectedPaths.isEmpty() -> ToggleableState.Off
            state.isAllSelected -> ToggleableState.On
            else -> ToggleableState.Indeterminate
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                enabled = state.items.isNotEmpty(),
                onClick = { onIntent(MediaListIntent.SelectAll) },
            ),
        ) {
            Text(
                text = "All",
                style = AppTextStyles.Size14Medium,
                color = colorResource(R.color.color_on_surface),
            )
            TriStateCheckbox(
                state = allState,
                onClick = { onIntent(MediaListIntent.SelectAll) },
                enabled = state.items.isNotEmpty(),
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(R.color.primary_primary),
                    uncheckedColor = colorResource(R.color.color_on_surface_variant_2),
                    checkmarkColor = Color.White,
                ),
            )
        }
    }
}

@Composable
private fun MediaGridItem(
    item: ItemFile,
    selected: Boolean,
    onOpen: () -> Unit,
    onToggleSelect: () -> Unit,
) {
    val context = LocalContext.current
    val imageModel = if (item is VideoFile) {
        ImageRequest.Builder(context)
            .data(item.pathFile)
            .decoderFactory(VideoFrameDecoder.Factory())
            .videoFrameMillis(1_000)
            .crossfade(true)
            .build()
    } else {
        item.pathFile
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.color_on_surface_variant_5))
            .clickable(onClick = onOpen),
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        if (item is VideoFile) {
            if (item.duration > 0L) {
                Text(
                    text = formatDuration(item.duration),
                    style = AppTextStyles.Size12Medium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }

        Checkbox(
            checked = selected,
            onCheckedChange = { onToggleSelect() },
            colors = CheckboxDefaults.colors(
                checkedColor = colorResource(R.color.primary_primary),
                uncheckedColor = Color.White,
                checkmarkColor = Color.White,
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp),
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMs).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

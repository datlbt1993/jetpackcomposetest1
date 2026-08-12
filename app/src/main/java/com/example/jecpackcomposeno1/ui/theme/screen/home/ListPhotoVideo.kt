package com.example.jecpackcomposeno1.ui.theme.screen.home

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerHeight
import com.example.jecpackcomposeno1.ui.theme.data.ItemFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoFile
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListIntent
import com.example.jecpackcomposeno1.ui.theme.screen.home.media.MediaListState
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ListPhotoVideo(
    state: MediaListState,
    onIntent: (MediaListIntent) -> Unit,
    onBack: () -> Unit,
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

        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading && state.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(R.color.primary_primary),
                        )
                    }
                }

                state.error != null && state.items.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .clickable { onIntent(MediaListIntent.Refresh) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = state.error ?: "Error",
                            style = AppTextStyles.Size14Medium,
                            color = colorResource(R.color.color_on_surface_variant_2),
                        )
                    }
                }

                state.items.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (state.isPhotos) "No photos" else "No videos",
                            style = AppTextStyles.Size14Medium,
                            color = colorResource(R.color.color_on_surface_variant_2),
                        )
                    }
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

        // Checkbox luôn hiện sẵn khi vào màn
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

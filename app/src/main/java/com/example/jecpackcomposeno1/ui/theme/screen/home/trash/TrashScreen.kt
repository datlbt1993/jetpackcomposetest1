package com.example.jecpackcomposeno1.ui.theme.screen.home.trash

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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.mvi.CollectEffect
import com.example.jecpackcomposeno1.navigation.AppDestination
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerHeight
import com.example.jecpackcomposeno1.ui.theme.data.TrashFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoType
import com.example.jecpackcomposeno1.ui.theme.screen.home.ToolbarHome
import java.io.File

/** Entry gắn NavHost: ViewModel + Effect. NavHost chỉ truyền một cửa [onNavigate]. */
@Composable
fun TrashRoute(
    onNavigate: (AppDestination) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: TrashViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) {
        viewModel.onIntent(TrashIntent.Load)
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is TrashEffect.ShowMessage -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    TrashScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = { onNavigate(AppDestination.Back) },
    )
}

@Composable
fun TrashScreen(
    state: TrashState,
    onIntent: (TrashIntent) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.surface_normal))
            .statusBarsPadding()
    ) {
        ToolbarHome(
            isShowTrash = true,
            onBackClick = onBack,
            title = stringResource(R.string.text_trash),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.items.isNotEmpty()) {
                TextButton(onClick = { onIntent(TrashIntent.EmptyTrash) }) {
                    Text(
                        text = stringResource(R.string.text_empty_trash),
                        color = colorResource(R.color.primary_primary),
                        style = AppTextStyles.Size14Medium,
                    )
                }

                val allState = when {
                    state.selectedPaths.isEmpty() -> ToggleableState.Off
                    state.isAllSelected -> ToggleableState.On
                    else -> ToggleableState.Indeterminate
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onIntent(TrashIntent.SelectAll) },
                ) {
                    Text(
                        text = "All",
                        style = AppTextStyles.Size14Medium,
                        color = colorResource(R.color.color_on_surface),
                    )
                    TriStateCheckbox(
                        state = allState,
                        onClick = { onIntent(TrashIntent.SelectAll) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.primary_primary),
                            uncheckedColor = colorResource(R.color.color_on_surface_variant_2),
                            checkmarkColor = Color.White,
                        ),
                    )
                }
            }
        }

        CommonSpacerHeight(8)
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading && state.items.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorResource(R.color.primary_primary))
                    }
                }

                state.items.isEmpty() -> {
                    Row() {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.swipe_trash_empty),
                                style = AppTextStyles.Size14Medium,
                                color = colorResource(R.color.color_on_surface_variant_2),
                            )
                        }
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.swipe_trash_empty),
                                style = AppTextStyles.Size14Medium,
                                color = colorResource(R.color.color_on_surface_variant_2),
                            )
                        }
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.swipe_trash_empty),
                                style = AppTextStyles.Size14Medium,
                                color = colorResource(R.color.color_on_surface_variant_2),
                            )
                        }
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
                        items(state.items, key = { it.pathFile }) { item ->
                            TrashGridItem(
                                item = item,
                                selected = item.pathFile in state.selectedPaths,
                                onToggle = {
                                    onIntent(TrashIntent.ToggleSelect(item.pathFile))
                                },
                            )
                        }
                    }
                }
            }

            if (state.isDeleting) {
                Box(
                    Modifier
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
                onClick = { onIntent(TrashIntent.DeleteSelectedForever) },
                enabled = !state.isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.primary_primary),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.text_delete_permanently) +
                            " (${state.selectedCount})",
                    color = Color.White,
                    style = AppTextStyles.Size16SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TrashGridItem(
    item: TrashFile,
    selected: Boolean,
    onToggle: () -> Unit,
) {
    val context = LocalContext.current
    val file = File(item.pathFile)
    val imageModel = ImageRequest.Builder(context)
        .data(file)
        .apply {
            if (item.fileType is VideoType) {
                decoderFactory(VideoFrameDecoder.Factory())
                videoFrameMillis(1_000)
            }
        }
        .crossfade(true)
        .build()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.color_on_surface_variant_5))
            .clickable(onClick = onToggle),
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = "${item.remainingDays}d",
            style = AppTextStyles.Size12Medium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
        )
        Checkbox(
            checked = selected,
            onCheckedChange = { onToggle() },
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

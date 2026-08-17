package com.example.jecpackcomposeno1.ui.theme.screen.home.media

import com.example.jecpackcomposeno1.mvi.MviViewModel
import com.example.jecpackcomposeno1.ui.theme.data.ItemFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoFile
import com.example.jecpackcomposeno1.ui.theme.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class MediaListViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
) : MviViewModel<MediaListState, MediaListIntent, MediaListEffect>(MediaListState()) {

    override fun handleIntent(intent: MediaListIntent) {
        when (intent) {
            is MediaListIntent.Init -> {
                setState {
                    copy(
                        isPhotos = intent.isPhotos,
                        selectedPaths = emptySet(),
                    )
                }
                loadMedia()
            }
            MediaListIntent.Load, MediaListIntent.Refresh -> loadMedia()
            is MediaListIntent.ClickItem -> onClickItem(intent.item)
            is MediaListIntent.ToggleSelect -> toggleSelect(intent.pathFile)
            MediaListIntent.SelectAll -> {
                setState {
                    if (isAllSelected) {
                        copy(selectedPaths = emptySet())
                    } else {
                        copy(selectedPaths = items.map { it.pathFile }.toSet())
                    }
                }
            }
            MediaListIntent.ClearSelection -> {
                setState { copy(selectedPaths = emptySet()) }
            }
            MediaListIntent.DeleteSelected -> deleteSelected()
        }
    }

    private fun onClickItem(item: ItemFile) {
        if (item is VideoFile) {
            sendEffect(MediaListEffect.NavigateToVideoPlayer(item.pathFile))
        } else {
            sendEffect(MediaListEffect.NavigateToPhotoDetail(item.pathFile))
        }
    }

    private fun toggleSelect(pathFile: String) {
        setState {
            val next = if (pathFile in selectedPaths) {
                selectedPaths - pathFile
            } else {
                selectedPaths + pathFile
            }
            copy(selectedPaths = next)
        }
    }

    private fun deleteSelected() {
        val state = uiState.value
        val selectedItems = state.items.filter { it.pathFile in state.selectedPaths }
        if (selectedItems.isEmpty()) return

        launchTask(
            onLoading = { setState { copy(isDeleting = true, error = null) } },
            onError = {
                setState { copy(isDeleting = false, error = it.message) }
                sendEffect(MediaListEffect.ShowMessage(it.message ?: "Move to Trash failed"))
            },
        ) {
            val moved = storageRepository.moveToTrash(selectedItems)
            val paths = selectedItems.map { it.pathFile }.toSet()
            setState {
                copy(
                    isDeleting = false,
                    selectedPaths = emptySet(),
                    items = items.filterNot { it.pathFile in paths },
                )
            }
            sendEffect(MediaListEffect.ShowMessage("Moved $moved item(s) to Trash"))
        }
    }

    private fun loadMedia() {
        launchExclusive(
            onLoading = { setState { copy(isLoading = true, error = null) } },
            onError = { setState { copy(isLoading = false, error = it.message) } },
        ) {
            val isPhotos = uiState.value.isPhotos
            val flow = if (isPhotos) {
                storageRepository.fetchPhotos()
            } else {
                storageRepository.fetchVideos()
            }
            flow
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collect { data ->
                    setState {
                        val stillSelected = selectedPaths.intersect(data.map { it.pathFile }.toSet())
                        copy(
                            isLoading = false,
                            items = data,
                            selectedPaths = stillSelected,
                            error = null,
                        )
                    }
                }
        }
    }
}

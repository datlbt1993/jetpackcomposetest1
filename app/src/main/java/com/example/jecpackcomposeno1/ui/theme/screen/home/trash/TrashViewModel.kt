package com.example.jecpackcomposeno1.ui.theme.screen.home.trash

import com.example.jecpackcomposeno1.mvi.MviViewModel
import com.example.jecpackcomposeno1.ui.theme.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
) : MviViewModel<TrashState, TrashIntent, TrashEffect>(TrashState()) {

    override fun handleIntent(intent: TrashIntent) {
        when (intent) {
            TrashIntent.Load -> loadTrash()
            is TrashIntent.ToggleSelect -> {
                setState {
                    val next = if (intent.pathFile in selectedPaths) {
                        selectedPaths - intent.pathFile
                    } else {
                        selectedPaths + intent.pathFile
                    }
                    copy(selectedPaths = next)
                }
            }
            TrashIntent.SelectAll -> {
                setState {
                    if (isAllSelected) copy(selectedPaths = emptySet())
                    else copy(selectedPaths = items.map { it.pathFile }.toSet())
                }
            }
            TrashIntent.DeleteSelectedForever -> deleteSelected()
            TrashIntent.EmptyTrash -> emptyTrash()
        }
    }

    private fun loadTrash() {
        launchTask(
            onLoading = { setState { copy(isLoading = true, error = null) } },
            onError = { setState { copy(isLoading = false, error = it.message) } },
        ) {
            val items = storageRepository.getTrashItems()
            setState {
                copy(
                    isLoading = false,
                    items = items,
                    selectedPaths = selectedPaths.intersect(items.map { it.pathFile }.toSet()),
                )
            }
        }
    }

    private fun deleteSelected() {
        val paths = uiState.value.selectedPaths.toList()
        if (paths.isEmpty()) return
        launchTask(
            onLoading = { setState { copy(isDeleting = true) } },
            onError = {
                setState { copy(isDeleting = false) }
                sendEffect(TrashEffect.ShowMessage(it.message ?: "Delete failed"))
            },
        ) {
            val deleted = storageRepository.permanentlyDeleteFromTrash(paths)
            val items = storageRepository.getTrashItems()
            setState {
                copy(
                    isDeleting = false,
                    items = items,
                    selectedPaths = emptySet(),
                )
            }
            sendEffect(TrashEffect.ShowMessage("Permanently deleted $deleted item(s)"))
        }
    }

    private fun emptyTrash() {
        val paths = uiState.value.items.map { it.pathFile }
        if (paths.isEmpty()) return
        launchTask(
            onLoading = { setState { copy(isDeleting = true) } },
            onError = {
                setState { copy(isDeleting = false) }
                sendEffect(TrashEffect.ShowMessage(it.message ?: "Empty trash failed"))
            },
        ) {
            val deleted = storageRepository.permanentlyDeleteFromTrash(paths)
            setState {
                copy(
                    isDeleting = false,
                    items = emptyList(),
                    selectedPaths = emptySet(),
                )
            }
            sendEffect(TrashEffect.ShowMessage("Emptied trash ($deleted)"))
        }
    }
}

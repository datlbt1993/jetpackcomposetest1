package com.example.jecpackcomposeno1.ui.theme.screen.home.media

import com.example.jecpackcomposeno1.ui.theme.data.ItemFile

data class MediaListState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isPhotos: Boolean = true,
    val items: List<ItemFile> = emptyList(),
    val selectedPaths: Set<String> = emptySet(),
    val error: String? = null,
) {
    val selectedCount: Int get() = selectedPaths.size

    val isAllSelected: Boolean
        get() = items.isNotEmpty() && selectedPaths.size == items.size

    /** Đã chọn một phần (chưa hết) */
    val isPartiallySelected: Boolean
        get() = selectedCount > 0 && !isAllSelected
}

sealed interface MediaListIntent {
    data class Init(val isPhotos: Boolean) : MediaListIntent
    data object Load : MediaListIntent
    data object Refresh : MediaListIntent
    data class ClickItem(val item: ItemFile) : MediaListIntent
    data class ToggleSelect(val pathFile: String) : MediaListIntent
    data object SelectAll : MediaListIntent
    data object ClearSelection : MediaListIntent
    data object DeleteSelected : MediaListIntent
}

sealed interface MediaListEffect {
    data class NavigateToVideoPlayer(val uri: String) : MediaListEffect
    data class NavigateToPhotoDetail(val uri: String) : MediaListEffect
    data class ShowMessage(val message: String) : MediaListEffect
}

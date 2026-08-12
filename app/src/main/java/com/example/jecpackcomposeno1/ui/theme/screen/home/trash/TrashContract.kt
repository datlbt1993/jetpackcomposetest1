package com.example.jecpackcomposeno1.ui.theme.screen.home.trash

import com.example.jecpackcomposeno1.ui.theme.data.TrashFile

data class TrashState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val items: List<TrashFile> = emptyList(),
    val selectedPaths: Set<String> = emptySet(),
    val error: String? = null,
) {
    val selectedCount: Int get() = selectedPaths.size
    val isAllSelected: Boolean
        get() = items.isNotEmpty() && selectedPaths.size == items.size
    val isPartiallySelected: Boolean
        get() = selectedCount > 0 && !isAllSelected
}

sealed interface TrashIntent {
    data object Load : TrashIntent
    data class ToggleSelect(val pathFile: String) : TrashIntent
    data object SelectAll : TrashIntent
    data object DeleteSelectedForever : TrashIntent
    data object EmptyTrash : TrashIntent
}

sealed interface TrashEffect {
    data class ShowMessage(val message: String) : TrashEffect
}

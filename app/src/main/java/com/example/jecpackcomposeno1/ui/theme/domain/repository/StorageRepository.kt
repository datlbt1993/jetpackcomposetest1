package com.example.jecpackcomposeno1.ui.theme.domain.repository

import com.example.jecpackcomposeno1.ui.theme.data.ItemFile
import com.example.jecpackcomposeno1.ui.theme.data.PhotoFile
import com.example.jecpackcomposeno1.ui.theme.data.TrashFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoFile
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    fun fetchPhotos(): Flow<List<PhotoFile>>

    fun fetchVideos(): Flow<List<VideoFile>>

    /** Chuyển media vào Trash app (giữ 30 ngày). Trả về số item thành công. */
    suspend fun moveToTrash(items: List<ItemFile>): Int

    suspend fun getTrashItems(): List<TrashFile>

    /** Xóa vĩnh viễn khỏi Trash app. */
    suspend fun permanentlyDeleteFromTrash(paths: List<String>): Int

    /** Xóa các item trong Trash đã quá 30 ngày. */
    suspend fun purgeExpiredTrash(): Int
}

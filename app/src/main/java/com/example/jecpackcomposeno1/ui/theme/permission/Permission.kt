package com.example.jecpackcomposeno1.ui.theme.permission

import android.Manifest
import android.os.Build

sealed class Permission {
    abstract val key: String
    abstract val manifestPermissions: List<String>

    data object Notification : Permission() {
        override val key = "android.permission.POST_NOTIFICATIONS"
        override val manifestPermissions: List<String>
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                emptyList()
            }
    }

    data object Storage : Permission() {
        override val key = "android.permission.READ_EXTERNAL_STORAGE"
        override val manifestPermissions: List<String>
            get() = when {
                // Android 14+: có thêm "Select photos" (partial access)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                )
                // Android 13
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                )
                // Android 12 trở xuống
                else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
    }

    data object ManageExternalStorage : Permission() {
        override val key = "android.permission.MANAGE_EXTERNAL_STORAGE"
        override val manifestPermissions: List<String>
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                emptyList()
            } else {
                listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
    }

    data object Calendar : Permission() {
        override val key = "android.permission.READ_CALENDAR"

        // hasCalendarPermissions() yêu cầu CẢ read + write nên phải xin cả 2
        override val manifestPermissions: List<String>
            get() = listOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
            )
    }

    data object Contact : Permission() {
        override val key = Manifest.permission.READ_CONTACTS

        // hasFullContactPermission() yêu cầu CẢ read + write
        override val manifestPermissions: List<String>
            get() = listOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
            )
    }
}
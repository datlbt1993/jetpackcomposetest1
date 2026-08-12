package com.example.jecpackcomposeno1.ui.theme.component

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.jecpackcomposeno1.ui.theme.permission.Permission

fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}

fun Context.isPermissionGranted(permission: Permission): Boolean = when (permission) {
    Permission.Notification -> isNotificationPermissionsGranted()
    Permission.Storage -> hasStoragePermissions()
    Permission.Calendar -> hasCalendarPermissions()
    Permission.Contact -> hasFullContactPermission()
    Permission.ManageExternalStorage -> hasManageExternalStoragePermission()
}


fun Context.isNotificationPermissionsGranted(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}


fun Context.hasStoragePermissions(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        && (ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED)
    ) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else if (ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else {
        false
    }
}

fun Context.hasManageExternalStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.isLimitedMediaLibraryAccess(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return false
    if (!hasStoragePermissions()) return false
    val fullImages = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED
    val fullVideo = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_MEDIA_VIDEO
    ) == PackageManager.PERMISSION_GRANTED
    if (fullImages && fullVideo) return false
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasCalendarPermissions(): Boolean {
    val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
    val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
    return read == PackageManager.PERMISSION_GRANTED &&
            write == PackageManager.PERMISSION_GRANTED
}

fun Context.hasFullContactPermission(): Boolean {
    val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
    val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
    return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
}

fun Context.checkPermissionsGranted(permission: Permission): Boolean {
    return when (permission) {

        Permission.Notification -> isNotificationPermissionsGranted()

        Permission.Storage -> hasStoragePermissions()

        Permission.ManageExternalStorage -> hasManageExternalStoragePermission()

        Permission.Calendar -> hasCalendarPermissions()

        Permission.Contact -> hasFullContactPermission()
    }
}

fun Context.resolveContentSizeBytes(contentUri: Uri, reportedSizeBytes: Long): Long {
    if (reportedSizeBytes > 0L) return reportedSizeBytes

    val fromColumns = runCatching {
        contentResolver.query(
            contentUri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (idx < 0) return@use null
            if (!cursor.moveToFirst()) return@use null
            cursor.getLong(idx).takeIf { it > 0L }
        }
    }.getOrNull()
    if (fromColumns != null) return fromColumns

    val fromFd = runCatching {
        contentResolver.openAssetFileDescriptor(contentUri, "r")?.use { afd ->
            afd.length.takeIf { it > 0L }
        }
    }.getOrNull()
    return fromFd ?: reportedSizeBytes
}


fun Context.resolveVideoDurationMs(contentUri: Uri, reportedDurationMs: Long): Long {
    if (reportedDurationMs > 0L) return reportedDurationMs

    val fromColumns = runCatching {
        contentResolver.query(
            contentUri,
            arrayOf(MediaStore.Video.VideoColumns.DURATION),
            null,
            null,
            null
        )?.use { cursor ->
            val idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)
            if (idx < 0) return@use null
            if (!cursor.moveToFirst()) return@use null
            cursor.getLong(idx).takeIf { it > 0L }
        }
    }.getOrNull()
    if (fromColumns != null) return fromColumns

    val fromRetriever = runCatching {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(this, contentUri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?.takeIf { it > 0L }
        } finally {
            retriever.release()
        }
    }.getOrNull()

    return fromRetriever ?: reportedDurationMs
}
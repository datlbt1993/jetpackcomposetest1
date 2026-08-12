package com.example.jecpackcomposeno1.ui.theme.domain.repository

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.example.jecpackcomposeno1.ui.theme.component.checkPermissionsGranted
import com.example.jecpackcomposeno1.ui.theme.component.resolveContentSizeBytes
import com.example.jecpackcomposeno1.ui.theme.component.resolveVideoDurationMs
import com.example.jecpackcomposeno1.ui.theme.component.toFormattedDateTime
import com.example.jecpackcomposeno1.ui.theme.data.ItemFile
import com.example.jecpackcomposeno1.ui.theme.data.PhotoFile
import com.example.jecpackcomposeno1.ui.theme.data.PhotoType
import com.example.jecpackcomposeno1.ui.theme.data.TrashFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoFile
import com.example.jecpackcomposeno1.ui.theme.data.VideoType
import com.example.jecpackcomposeno1.ui.theme.permission.Permission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImp @Inject constructor(
    @ApplicationContext private val context: Context,
): StorageRepository {

    companion object {
        private const val TRASH_DIR_NAME = "app_trash"
        private const val TRASH_INDEX_NAME = "trash_index.json"

        private val DIRECTORY_ANDROID_PATH = Environment.getExternalStoragePublicDirectory(
            "Android"
        ).absolutePath + File.separator

        val DIRECTORY_ANDROID_MEDIA_PATH = DIRECTORY_ANDROID_PATH + "media" + File.separator

        private val DIRECTORY_MUSIC_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC
        ).absolutePath + File.separator

        private val DIRECTORY_PODCASTS_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PODCASTS
        ).absolutePath + File.separator

        private val DIRECTORY_RINGTONES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_RINGTONES
        ).absolutePath + File.separator

        private val DIRECTORY_ALARMS_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_ALARMS
        ).absolutePath + File.separator

        private val DIRECTORY_NOTIFICATIONS_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_NOTIFICATIONS
        ).absolutePath + File.separator

        private val DIRECTORY_PICTURES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).absolutePath + File.separator

        private val DIRECTORY_MOVIES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES
        ).absolutePath + File.separator

        private val DIRECTORY_DOWNLOADS_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        ).absolutePath + File.separator

        private val DIRECTORY_DCIM_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).absolutePath + File.separator

        private val DIRECTORY_DCIM_DOCUMENT = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        ).absolutePath + File.separator


        val EXTERNAL_STORAGE_PUBLIC_DIRECTORIES by lazy {
            val list = mutableListOf(
                DIRECTORY_ANDROID_PATH,
                DIRECTORY_MUSIC_PATH,
                DIRECTORY_PODCASTS_PATH,
                DIRECTORY_RINGTONES_PATH,
                DIRECTORY_ALARMS_PATH,
                DIRECTORY_NOTIFICATIONS_PATH,
                DIRECTORY_PICTURES_PATH,
                DIRECTORY_MOVIES_PATH,
                DIRECTORY_DOWNLOADS_PATH,
                DIRECTORY_DCIM_PATH,
                DIRECTORY_DCIM_DOCUMENT,
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val directoryScreenshotsPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_SCREENSHOTS
                ).absolutePath + File.separator

                val directoryAudiBooksPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_AUDIOBOOKS
                ).absolutePath + File.separator
                list.add(directoryScreenshotsPath)
                list.add(directoryAudiBooksPath)
            }
            list
        }
    }

    @OptIn(FlowPreview::class)
    override fun fetchPhotos(): Flow<List<PhotoFile>> = callbackFlow {
        if (!context.checkPermissionsGranted(Permission.ManageExternalStorage)) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val imageObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                launch {
                    trySend(fetchAllImages())
                }
            }
        }
        context.contentResolver.registerContentObserver(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            },
            true,
            imageObserver
        )
        launch {
            trySend(fetchAllImages())
        }
        awaitClose {
            context.contentResolver.unregisterContentObserver(imageObserver)
        }
    }.debounce(500).flowOn(Dispatchers.IO)

    @OptIn(FlowPreview::class)
    override fun fetchVideos(): Flow<List<VideoFile>> = callbackFlow {
        if (!context.checkPermissionsGranted(Permission.ManageExternalStorage)) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val videoObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                launch {
                    trySend(fetchAllVideo())
                }
            }
        }
        context.contentResolver.registerContentObserver(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            },
            true,
            videoObserver
        )
        launch {
            trySend(fetchAllVideo())
        }
        awaitClose {
            context.contentResolver.unregisterContentObserver(videoObserver)
        }
    }.debounce(500).flowOn(Dispatchers.IO)

//    override fun getTotalStorages(): Flow<NormalizedStorage> = flow {
//        val stat = StatFs(Environment.getDataDirectory().path)
//        val totalBytes = stat.blockSizeLong * stat.blockCountLong
//        emit(totalBytes.normalizeStorageSize())
//    }.flowOn(Dispatchers.IO)
//
//    override fun getStorageUsedFlow(): Flow<Long> = callbackFlow {
//        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
//            override fun onChange(selfChange: Boolean) {
//                super.onChange(selfChange)
//                trySend(getStorageUsed())
//            }
//        }
//
//        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        } else {
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        }
//        context.contentResolver.registerContentObserver(
//            imageUri, true, observer
//        )
//
//        val videoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        } else {
//            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//        }
//        context.contentResolver.registerContentObserver(
//            videoUri, true, observer
//        )
//
//        trySend(getStorageUsed())
//        awaitClose {
//            context.contentResolver.unregisterContentObserver(observer)
//        }
//    }.flowOn(Dispatchers.IO)

    private suspend fun fetchAllImages(): List<PhotoFile> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val allImages = mutableListOf<PhotoFile>()

        if (!context.checkPermissionsGranted(Permission.ManageExternalStorage)) {
            return@withContext emptyList()
        }

        context.contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(collectionUri, id)
                val sizeBytes = context.resolveContentSizeBytes(uri, cursor.getLong(sizeColumn))
                allImages.add(
                    PhotoFile(
                        id = id,
                        name = cursor.getString(nameColumn) ?: "",
                        dateCreate = cursor.getLong(dateColumn),
                        dateDisplay = cursor.getLong(dateColumn).toFormattedDateTime(),
                        pathFile = uri.toString(),
                        sizeFile = sizeBytes,
                        isHiddenOrNoExtension = false,
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn)
                    )
                )
            }
        }

        return@withContext allImages
    }

    private suspend fun fetchAllVideo(): List<VideoFile> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val allVideo = mutableListOf<VideoFile>()

        if (!context.checkPermissionsGranted(Permission.ManageExternalStorage)) {
            return@withContext emptyList()
        }

        context.contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColum = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(collectionUri, id)
                val sizeBytes = context.resolveContentSizeBytes(uri, cursor.getLong(sizeColumn))
                val durationMs = context.resolveVideoDurationMs(uri, cursor.getLong(durationColum))
                allVideo.add(
                    VideoFile(
                        id = id,
                        name = cursor.getString(nameColumn) ?: "",
                        dateCreate = cursor.getLong(dateColumn),
                        pathFile = uri.toString(),
                        dateDisplay = cursor.getLong(dateColumn).toFormattedDateTime(),
                        isHiddenOrNoExtension = false,
                        duration = durationMs,
                        sizeFile = sizeBytes,
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                    )
                )
            }
        }
        return@withContext allVideo
    }

    private fun imagesContentUri(): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    private fun videosContentUri(): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    private fun String.isAcceptableMediaPath(): Boolean {
        if (isBlank()) return false
        if (startsWith(DIRECTORY_ANDROID_PATH)) return false
        if (contains("/.thumbnails/") ||
            contains("/.trashed-") ||
            contains("/.pending-") ||
            contains("/.Trash/") ||
            contains("/.trash/") ||
            contains(".hwRecycle", ignoreCase = true) ||
            contains("/HuaweiRecycle/", ignoreCase = true) ||
            contains("/HonorRecycle/", ignoreCase = true) ||
            contains("/RecycleBin/", ignoreCase = true)
        ) return false
        return true
    }

    private fun excludeTrashedMediaSelectionSuffix(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            " AND (${MediaStore.MediaColumns.IS_TRASHED} IS NULL OR ${MediaStore.MediaColumns.IS_TRASHED} = 0)"
        } else {
            ""
        }

    private val trashDir: File
        get() = File(context.filesDir, TRASH_DIR_NAME).also { if (!it.exists()) it.mkdirs() }

    private val trashIndexFile: File
        get() = File(trashDir, TRASH_INDEX_NAME)

    private val trashMutex = Mutex()

    override suspend fun moveToTrash(items: List<ItemFile>): Int = withContext(Dispatchers.IO) {
        trashMutex.withLock {
            var moved = 0
            val index = readTrashIndexLocked()
            val nowSeconds = System.currentTimeMillis() / 1_000L

            items.forEach { item ->
                runCatching {
                    val sourceUri = Uri.parse(item.pathFile)
                    val ext = item.name.substringAfterLast('.', missingDelimiterValue = "")
                    val localName = buildString {
                        append(UUID.randomUUID().toString())
                        if (ext.isNotBlank()) append('.').append(ext)
                    }
                    val destFile = File(trashDir, localName)

                    context.contentResolver.openInputStream(sourceUri)?.use { input ->
                        destFile.outputStream().use { output -> input.copyTo(output) }
                    } ?: error("Cannot open input: ${item.pathFile}")

                    val deletedRows = context.contentResolver.delete(sourceUri, null, null)
                    if (deletedRows <= 0) {
                        destFile.delete()
                        error("Cannot delete original: ${item.pathFile}")
                    }

                    val entry = JSONObject()
                        .put("name", item.name)
                        .put("pathFile", destFile.absolutePath)
                        .put("dateCreate", item.dateCreate)
                        .put("dateDisplay", item.dateDisplay)
                        .put("sizeFile", if (item.sizeFile > 0) item.sizeFile else destFile.length())
                        .put("duration", item.duration)
                        .put("fileType", if (item is VideoFile) "video" else "photo")
                        .put("trashAtSeconds", nowSeconds)
                    index.put(entry)
                    moved++
                }
            }
            writeTrashIndexLocked(index)
            moved
        }
    }

    override suspend fun getTrashItems(): List<TrashFile> = withContext(Dispatchers.IO) {
        trashMutex.withLock {
            purgeExpiredTrashLocked()
            loadTrashListLocked()
        }
    }

    override suspend fun permanentlyDeleteFromTrash(paths: List<String>): Int =
        withContext(Dispatchers.IO) {
            trashMutex.withLock {
                val pathSet = paths.toSet()
                val index = readTrashIndexLocked()
                var deleted = 0
                val next = JSONArray()
                for (i in 0 until index.length()) {
                    val obj = index.getJSONObject(i)
                    val path = obj.optString("pathFile")
                    if (path in pathSet) {
                        File(path).delete()
                        deleted++
                    } else {
                        next.put(obj)
                    }
                }
                writeTrashIndexLocked(next)
                deleted
            }
        }

    override suspend fun purgeExpiredTrash(): Int = withContext(Dispatchers.IO) {
        trashMutex.withLock { purgeExpiredTrashLocked() }
    }

    private fun purgeExpiredTrashLocked(): Int {
        val nowSeconds = System.currentTimeMillis() / 1_000L
        val retention = 30L * 24 * 60 * 60
        val index = readTrashIndexLocked()
        var purged = 0
        val next = JSONArray()
        for (i in 0 until index.length()) {
            val obj = index.getJSONObject(i)
            val trashAt = obj.optLong("trashAtSeconds", 0L)
            val path = obj.optString("pathFile")
            if (trashAt > 0 && nowSeconds >= trashAt + retention) {
                File(path).delete()
                purged++
            } else {
                next.put(obj)
            }
        }
        writeTrashIndexLocked(next)
        return purged
    }

    private fun loadTrashListLocked(): List<TrashFile> {
        val index = readTrashIndexUnlocked()
        val list = mutableListOf<TrashFile>()
        for (i in 0 until index.length()) {
            val obj = index.getJSONObject(i)
            val path = obj.optString("pathFile")
            if (path.isBlank() || !File(path).exists()) continue
            val type = when (obj.optString("fileType")) {
                "video" -> VideoType
                else -> PhotoType
            }
            list.add(
                TrashFile(
                    name = obj.optString("name"),
                    pathFile = path,
                    dateCreate = obj.optLong("dateCreate"),
                    dateDisplay = obj.optString("dateDisplay"),
                    sizeFile = obj.optLong("sizeFile"),
                    duration = obj.optLong("duration"),
                    fileType = type,
                    trashAtSeconds = obj.optLong("trashAtSeconds"),
                )
            )
        }
        return list.sortedByDescending { it.trashAtSeconds }
    }

    private fun readTrashIndexLocked(): JSONArray = readTrashIndexUnlocked()

    private fun readTrashIndexUnlocked(): JSONArray {
        if (!trashIndexFile.exists()) return JSONArray()
        val text = trashIndexFile.readText()
        if (text.isBlank()) return JSONArray()
        return JSONArray(text)
    }

    private fun writeTrashIndexLocked(array: JSONArray) {
        trashIndexFile.writeText(array.toString())
    }
}
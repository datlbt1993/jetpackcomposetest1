package com.example.jecpackcomposeno1.ui.theme.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
sealed class ItemFile : Parcelable {
    abstract val name: String
    abstract val pathFile: String
    abstract val dateCreate: Long
    abstract val dateDisplay: String
    abstract val sizeFile: Long
    abstract val duration: Long
    abstract val fileType: FileType
    abstract val isThumbnail: Boolean
    abstract val isHiddenOrNoExtension: Boolean
    abstract var isChecked: Boolean
    var isFistItemInList = false

    fun getAdapterItemId() = pathFile.hashCode().toLong()
}

data class PhotoFile(
    val id: Long = -1,
    override val name: String,
    override val pathFile: String,
    override val dateCreate: Long,
    override val dateDisplay: String,
    override val sizeFile: Long,
    override val isHiddenOrNoExtension: Boolean,
    override val fileType: FileType = PhotoType,
    override val duration: Long = 0L,
    override val isThumbnail: Boolean = false,
    override var isChecked: Boolean = false,
    val width: Int = 0,
    val height: Int = 0
): ItemFile() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhotoFile) return false
        return id == other.id && isChecked == other.isChecked
    }

    override fun hashCode(): Int {
        return Objects.hash(id, isChecked)
    }

    fun toMediaFile(): MediaItemFile {
        return MediaItemFile(
            name = name,
            pathFile = pathFile,
            dateCreate = dateCreate,
            dateDisplay = dateDisplay,
            sizeFile = sizeFile,
            isHiddenOrNoExtension = isHiddenOrNoExtension,
            fileType = fileType,
            duration = duration,
            isThumbnail = isThumbnail,
            isChecked = isChecked
        )
    }
}

data class VideoFile(
    val id: Long = -1,
    override val name: String,
    override val pathFile: String,
    override val dateCreate: Long,
    override val dateDisplay: String,
    override val sizeFile: Long,
    override val isHiddenOrNoExtension: Boolean,
    override val fileType: FileType = VideoType,
    override val isThumbnail: Boolean = false,
    override val duration: Long,
    override var isChecked: Boolean = false,
    val width: Int,
    val height: Int,
    val embedding: FloatArray? = null
): ItemFile() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VideoFile) return false
        return id == other.id &&
                isChecked == other.isChecked
    }

    override fun hashCode(): Int {
        return Objects.hash(id, isChecked)
    }

    fun toMediaFile(): MediaItemFile {
        return MediaItemFile(
            name = name,
            pathFile = pathFile,
            dateCreate = dateCreate,
            dateDisplay = dateDisplay,
            sizeFile = sizeFile,
            isHiddenOrNoExtension = isHiddenOrNoExtension,
            fileType = fileType,
            duration = duration,
            isThumbnail = isThumbnail,
            isChecked = isChecked,
            width = width,
            height = height
        )
    }
}

data class OtherFile(
    override val name: String,
    override val pathFile: String,
    override val dateCreate: Long,
    override val dateDisplay: String,
    override val sizeFile: Long,
    override val isHiddenOrNoExtension: Boolean,
    override val fileType: FileType = OtherType,
    override val duration: Long = 0L,
    override val isThumbnail: Boolean = false,
    override var isChecked: Boolean = false,
    val  formatOfFile: FormatOfFile,
): ItemFile()

data class AlbumHeader(
    override val name: String,
    override val pathFile: String,
    override val dateCreate: Long,
    override val dateDisplay: String,
    override val sizeFile: Long,
    override val isHiddenOrNoExtension: Boolean = false,
    override val fileType: FileType = OtherType,
    override val duration: Long = 0L,
    override val isThumbnail: Boolean = false,
    override var isChecked: Boolean = false,
    val groupId: Int = 0,
    val formatOfFile: FormatOfFile
): ItemFile()

data class TrashFile(
    override val name: String,
    override val pathFile: String,
    override val dateCreate: Long,
    override val dateDisplay: String,
    override val sizeFile: Long,
    override val duration: Long,
    override val fileType: FileType,
    override val isThumbnail: Boolean = false,
    override val isHiddenOrNoExtension: Boolean = false,
    override var isChecked: Boolean = false,
    val trashAtSeconds: Long = 0

): ItemFile() {
    companion object {
        private const val RETENTION_SECONDS = 30 * 24 * 60 * 60
        private fun Long.msToSeconds() = this / 1_000L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrashFile) return false
        return pathFile == other.pathFile &&
                isChecked == other.isChecked
    }

    override fun hashCode(): Int {
        return Objects.hash(pathFile, isChecked)
    }

    private val trashedAt: Long
        get() = if (trashAtSeconds > 0) trashAtSeconds else dateCreate.msToSeconds()

    val expiredAtSeconds: Long get() = trashedAt + RETENTION_SECONDS

    val remainingDays: Int
        get() = maxOf(0, ((expiredAtSeconds - (System.currentTimeMillis() / 1_000L)) / 86_400).toInt())

    val isExpired: Boolean
        get() = System.currentTimeMillis() / 1_000L >= expiredAtSeconds
}

data class MediaItemFile(
    override val name: String,
    override val pathFile: String,
    override val dateCreate: Long,
    override val dateDisplay: String,
    override val sizeFile: Long,
    override val isHiddenOrNoExtension: Boolean,
    override val fileType: FileType = AllFile,
    override val duration: Long = 0L,
    override val isThumbnail: Boolean = false,
    override var isChecked: Boolean = false,
    val width: Int = 0,
    val height: Int = 0,
    val dateModified: Long = 0
): ItemFile() {
    fun toPhotoFile(): PhotoFile = PhotoFile(
        name = name,
        pathFile = pathFile,
        dateCreate = dateCreate,
        dateDisplay = dateDisplay,
        sizeFile = sizeFile,
        isHiddenOrNoExtension = isHiddenOrNoExtension,
        fileType = fileType,
        duration = duration,
        isChecked = isChecked
    )

    fun toVideoFile() = VideoFile(
        name = name,
        pathFile = pathFile,
        dateCreate = dateCreate,
        dateDisplay = dateDisplay,
        sizeFile = sizeFile,
        isHiddenOrNoExtension = isHiddenOrNoExtension,
        fileType = fileType,
        duration = duration,
        isChecked = isChecked,
        width = width,
        height = height
    )
}

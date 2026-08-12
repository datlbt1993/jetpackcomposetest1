package com.example.jecpackcomposeno1.ui.theme.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class FileType : Parcelable {
    abstract val viewType: Int
    abstract val eventSuffix: String
}

@Parcelize
data object PhotoType : FileType() {
    override val viewType = 1
    override val eventSuffix = "photo_"
}

@Parcelize
data object VideoType : FileType() {
    override val viewType = 2
    override val eventSuffix = "video_"
}


@Parcelize
data object OtherType : FileType() {
    override val viewType = 3
    override val eventSuffix = "other_"
}

@Parcelize
data object AllFile : FileType() {
    override val viewType = 15
    override val eventSuffix = "all_"
}

@Parcelize
data object AlbumPhotoHeader : FileType() {
    override val viewType = 0
    override val eventSuffix = "album_photo_header"
}

@Parcelize
data object AlbumVideoHeader : FileType() {
    override val viewType = 4
    override val eventSuffix = "album_video_header"
}

@Parcelize
data object PdfType : FileType() {
    override val viewType = 5
    override val eventSuffix = "pdf_"
}

@Parcelize
data object DocumentType : FileType() {
    override val viewType = 6
    override val eventSuffix = "doc_"
}

@Parcelize
data object RarType : FileType() {
    override val viewType = 7
    override val eventSuffix = "rar_"
}

@Parcelize
data object BinType : FileType() {
    override val viewType = 8
    override val eventSuffix = "bin_"
}

@Parcelize
data object XlsType : FileType() {
    override val viewType = 9
    override val eventSuffix = "xls_"
}

@Parcelize
data object TextType : FileType() {
    override val viewType = 10
    override val eventSuffix = "txt_"
}

@Parcelize
data object ZipType : FileType() {
    override val viewType = 11
    override val eventSuffix = "zip_"
}

@Parcelize
data object Mp3Type : FileType() {
    override val viewType = 12
    override val eventSuffix = "mp3_"
}

@Parcelize
data object ApkType : FileType() {
    override val viewType = 13
    override val eventSuffix = "apk_"
}


@Parcelize
data object PptType : FileType() {
    override val viewType = 14
    override val eventSuffix = "ppt_"
}

@Parcelize
data object JunkType : FileType() {
    override val viewType = 16
    override val eventSuffix = "junk_"
}
//
//@DrawableRes
//fun getResourceByFileType(fileType: FileType): Int {
//    return when (fileType) {
//        is PdfType -> R.drawable.ic_pdf_file
//        is DocumentType -> R.drawable.ic_doc_file
//        is RarType -> R.drawable.ic_rar_file
//        is JunkType -> R.drawable.ic_junk_file
//        is XlsType -> R.drawable.ic_xls_file
//        is TextType -> R.drawable.ic_txt_file
//        is ZipType -> R.drawable.ic_zip_file
//        is Mp3Type -> R.drawable.ic_mp3_file
//        is ApkType -> R.drawable.ic_apk_file
//        is PptType -> R.drawable.ic_ppt_file
//        is OtherType -> R.drawable.ic_unknown_file
//        else -> R.drawable.ic_unknown_file
//    }
//}

val FileType.mimeType: String
    get() = when (this) {
        is PdfType -> "application/pdf"
        is DocumentType -> "application/msword"
        is XlsType -> "application/vnd.ms-excel"
        is PptType -> "application/vnd.ms-powerpoint"
        is ZipType -> "application/zip"
        is RarType -> "application/x-rar-compressed"
        is TextType -> "text/plain"
        is Mp3Type -> "audio/mpeg"
        is ApkType -> "application/vnd.android.package-archive"
        is PhotoType -> "image/*"
        is VideoType -> "video/*"
        else -> "application/octet-stream"
    }
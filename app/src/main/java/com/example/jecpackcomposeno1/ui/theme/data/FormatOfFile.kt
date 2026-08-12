package com.example.jecpackcomposeno1.ui.theme.data

import android.os.Parcelable
import com.example.jecpackcomposeno1.R
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class FormatOfFile : Parcelable {
    abstract val iconFormatResId: Int

    @Parcelize
    data object Wav: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Mp3: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Txt: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Doc: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Xls: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Ppt: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Pdf: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Zip: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Apk: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    @Parcelize
    data object Unknown: FormatOfFile() {
        override val iconFormatResId = R.drawable.ic_file
    }

    companion object {

//        fun getFormatOfFileBy(path: String): FormatOfFile {
//            return when {
//                path.isAudioFormat() -> Mp3
//                path.isFileFormat() -> Txt
//                path.isGoogleDocFormat() -> Doc
//                path.isGoogleExcelFormat() -> Xls
//                path.isGoogleSlideFormat() -> Ppt
//                path.isPdfFormat() -> Pdf
//                path.isApkFormat() -> Apk
//                path.isZipFormat() -> Zip
//                else -> Unknown
//            }
//        }

    }
}

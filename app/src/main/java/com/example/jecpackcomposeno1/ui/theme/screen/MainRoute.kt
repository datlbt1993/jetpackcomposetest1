package com.example.jecpackcomposeno1.ui.theme.screen

import android.net.Uri

object MainRoute {
    const val Main = "home_main"
    const val Home = "home"
    const val Swipe = "swipe"
    const val Compress = "compress"
    const val ArgMediaType = "mediaType"
    const val ArgVideoUri = "videoUri"

    const val ListPhotoVideo = "list_photo_video/{$ArgMediaType}"
    const val VideoPlayer = "video_player/{$ArgVideoUri}"
    const val Trash = "trash"

    const val MediaPhotos = "photos"
    const val MediaVideos = "videos"

    fun listPhotoVideo(mediaType: String) = "list_photo_video/$mediaType"

    fun videoPlayer(uri: String) = "video_player/${Uri.encode(uri)}"
}

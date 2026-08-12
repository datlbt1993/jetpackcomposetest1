package com.example.jecpackcomposeno1.ui.theme.screen.home

import android.net.Uri

object HomeRoute {
    const val Main = "home_main"
    const val ListPhotoVideo = "list_photo_video/{mediaType}"
    const val VideoPlayer = "video_player/{videoUri}"
    const val Trash = "trash"

    const val MediaPhotos = "photos"
    const val MediaVideos = "videos"

    fun listPhotoVideo(mediaType: String) = "list_photo_video/$mediaType"

    fun videoPlayer(uri: String) = "video_player/${Uri.encode(uri)}"
}

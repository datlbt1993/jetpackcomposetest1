package com.example.jecpackcomposeno1

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jecpackcomposeno1.ui.theme.domain.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    init {
        android.util.Log.e("PROOF", "VM INIT vm=${hashCode()}")
        viewModelScope.launch {
            runCatching { storageRepository.purgeExpiredTrash() }
        }
    }

    private val refreshMemorySizesState = MutableStateFlow(0)

    // Eagerly (không phải WhileSubscribed): yêu cầu là cấp quyền xong FETCH LUÔN, không đợi
    // màn hình nào collect. WhileSubscribed(5000) sẽ khiến refreshStorage() bump vào chỗ
    // không ai nghe -> upstream không chạy -> không fetch.
    // Đánh đổi: ContentObserver trong repository sống suốt đời ViewModel (đổi lại được
    // cập nhật tự động khi user thêm/xoá ảnh).
    @OptIn(ExperimentalCoroutinesApi::class)
    val allImagesStateFlow =
        refreshMemorySizesState.flatMapLatest { storageRepository.fetchPhotos() }
            .onEach { android.util.Log.e("PROOF", "FETCH images -> ${it.size}") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allVideoStateFlow =
        refreshMemorySizesState.flatMapLatest { storageRepository.fetchVideos() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    /**
     * Bắt fetch lại photos + videos.
     *
     * Bắt buộc phải gọi sau khi user vừa cấp MANAGE_EXTERNAL_STORAGE: lúc chưa có quyền,
     * [StorageRepository.fetchPhotos] gửi emptyList rồi close() flow luôn — flatMapLatest
     * chỉ chờ upstream emit tiếp, nên nếu không bump ở đây thì state kẹt emptyList vĩnh viễn.
     *
     * Dùng `it + 1` chứ không set cùng một giá trị, vì MutableStateFlow bỏ qua value bằng nhau.
     */
    fun refreshStorage() {
        refreshMemorySizesState.update { it + 1 }
    }

    /**
     * Gọi ở mỗi ON_START của app (xem LifecycleStartEffect trong MainScreen) để data luôn
     * mới mỗi lần mở app — kể cả khi quay lại từ background mà Activity chưa bị destroy,
     * lúc đó ViewModel còn sống nên stateIn(Eagerly) không chạy lại.
     *
     * Bỏ qua lần ON_START ĐẦU TIÊN sau khi VM được tạo, vì stateIn(Eagerly) đã fetch ngay
     * lúc init rồi — không bỏ thì mỗi lần cold start sẽ query MediaStore 2 lần.
     * Cờ này nằm ở ViewModel nên sống qua config change và qua background/foreground.
     */
    fun onAppStarted() {
        android.util.Log.e("PROOF", "onAppStarted vm=${hashCode()} initialFetchDone=$initialFetchDone")
        if (!initialFetchDone) {
            initialFetchDone = true
            return
        }
        refreshStorage()
    }

    private var initialFetchDone = false
}




package com.example.jecpackcomposeno1.ui.theme.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.component.CommonDialog
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerHeight
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerWidth
import com.example.jecpackcomposeno1.ui.theme.component.isPermissionGranted
import com.example.jecpackcomposeno1.ui.theme.component.openAppSettings
import com.example.jecpackcomposeno1.ui.theme.prefer.AppPreferences
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val Permission.supportsRetry: Boolean
    get() = when (this) {
        Permission.Storage, Permission.Calendar, Permission.Contact -> true
        else -> false
    }

val Permission.showRationaleFirst: Boolean
    get() = when (this) {
        Permission.Contact -> false      // Contact: KHÔNG hiện dialog giải thích, bung popup luôn
        else -> true                     // Storage/Calendar...: CÓ hiện dialog giải thích trước
    }

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    companion object {
        const val MAX_REQUEST = 2
    }

    private val _uiState = MutableStateFlow<PermissionUiState>(PermissionUiState.Idle)
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    // Bộ đếm IN-FLOW (transient) — giữ trong ViewModel, KHÔNG cần prefs.
    // Sống qua config change (vì ở ViewModel), reset mỗi khi bắt đầu flow mới.
    private val inFlowDenyCount = mutableMapOf<Permission, Int>()

    // StateFlow conflate giá trị bằng nhau -> LaunchSystem lần 2 (retry) sẽ KHÔNG emit.
    // requestId làm cho mỗi lần yêu cầu là một value khác nhau.
    private var requestId = 0

    // Đang chờ user quay lại từ màn Settings hệ thống
    private var awaitingSettings = false

    // ---- Bộ đếm BỀN (prefs) ----
    private fun deniedCount(p: Permission): Int = when (p) {
        Permission.Storage, Permission.ManageExternalStorage -> appPreferences.countStoragePermission
        Permission.Calendar -> appPreferences.countCalendarPermission
        Permission.Contact -> appPreferences.countContactPermission
        Permission.Notification -> appPreferences.countNotificationPermission
    }

    private fun incrementDenied(p: Permission) = when (p) {
        Permission.Storage, Permission.ManageExternalStorage -> appPreferences.countStoragePermission++
        Permission.Calendar -> appPreferences.countCalendarPermission++
        Permission.Contact -> appPreferences.countContactPermission++
        Permission.Notification -> appPreferences.countNotificationPermission++
    }

    private fun resetDenied(p: Permission) = when (p) {
        Permission.Storage, Permission.ManageExternalStorage -> appPreferences.countStoragePermission = 0
        Permission.Calendar -> appPreferences.countCalendarPermission = 0
        Permission.Contact -> appPreferences.countContactPermission = 0
        Permission.Notification -> appPreferences.countNotificationPermission = 0
    }

    // ============ LẦN BẤM NÚT ĐẦU (chưa qua popup hệ thống) ============
    fun onRequest(permission: Permission, isGranted: Boolean) {
        inFlowDenyCount[permission] = 0     // bắt đầu flow mới -> reset in-flow
        when {
            isGranted -> {
                resetDenied(permission)
                _uiState.value = PermissionUiState.Granted(permission, nextId())
            }
            // Đã từ chối đủ 2 lần trong đời -> Settings
            deniedCount(permission) >= MAX_REQUEST ->
                _uiState.value = PermissionUiState.ShowGoToSettings(permission, nextId())
            permission.showRationaleFirst ->
                _uiState.value = PermissionUiState.ShowRationale(permission, nextId())

            else ->
                _uiState.value = PermissionUiState.LaunchSystem(permission, nextId())
        }
    }

    /** User bấm nút "Allow" trong dialog giải thích -> bung popup hệ thống. */
    fun onRationaleAccepted(permission: Permission) {
        _uiState.value = PermissionUiState.LaunchSystem(permission, nextId())
    }

    // ============ SAU KHI POPUP HỆ THỐNG TRẢ KẾT QUẢ ============
    fun onSystemResult(permission: Permission, isGranted: Boolean) {
        if (isGranted) {
            inFlowDenyCount[permission] = 0
            resetDenied(permission)
            _uiState.value = PermissionUiState.Granted(permission, nextId())
            return
        }

        // Từ chối
        incrementDenied(permission)                          // bộ đếm bền ++
        val inFlow = (inFlowDenyCount[permission] ?: 0) + 1   // bộ đếm in-flow ++
        inFlowDenyCount[permission] = inFlow

        when {
            // RETRY: từ chối lần 1 (in-flow == 1) và quyền hỗ trợ retry -> bắn lại popup một lần
            permission.supportsRetry && inFlow == 1 ->
                _uiState.value = PermissionUiState.LaunchSystem(permission, nextId())
            // Lần 2 (hoặc không retry) -> reset in-flow, mở Settings
            else -> {
                inFlowDenyCount[permission] = 0
                _uiState.value = PermissionUiState.ShowGoToSettings(permission, nextId())
            }
        }
    }

    /** User bấm "Open setting" -> đóng dialog và ghi nhớ để check lại khi quay về app. */
    fun onOpenSettings() {
        awaitingSettings = true
        _uiState.value = PermissionUiState.Idle
    }

    /** Gọi ở ON_RESUME: nếu vừa quay về từ Settings và đã được cấp -> báo Granted. */
    fun onResumeFromSettings(permission: Permission, isGranted: Boolean) {
        if (!awaitingSettings) return
        awaitingSettings = false
        if (isGranted) {
            inFlowDenyCount[permission] = 0
            resetDenied(permission)
            _uiState.value = PermissionUiState.Granted(permission, nextId())
        }
    }

    fun dismiss() {
        _uiState.value = PermissionUiState.Idle
    }

    private fun nextId(): Int = ++requestId
}

sealed interface PermissionUiState {
    data object Idle : PermissionUiState
    data class Granted(val p: Permission, val id: Int) : PermissionUiState
    data class ShowRationale(val p: Permission, val id: Int) : PermissionUiState
    data class ShowGoToSettings(val p: Permission, val id: Int) : PermissionUiState
    data class LaunchSystem(val p: Permission, val id: Int) : PermissionUiState
}

data class RationaleUi(
    val image: Int,                            // R.drawable... (ảnh minh họa trong dialog)
    val title: Int,                            // R.string... (tiêu đề)
    val description: Int,                      // R.string... (mô tả)
    val buttonText: Int,                       // R.string... (chữ trên nút, vd "Allow")
    val titleArgs: List<Any> = emptyList(),    // format args cho title, vd app_name cho "%s"
)

/**
 * Cài toàn bộ flow xin quyền vào cây Compose và trả về hàm để BẮN flow đó.
 *
 * Composable này tự vẽ các dialog (rationale / go-to-settings) khi cần, còn UI kích hoạt
 * (nút, card, item...) là của bạn — chỉ cần gọi lambda được trả về trong `onClick`.
 *
 * ```
 * val requestStorage = rememberPermissionRequester(
 *     permission = Permission.Storage,
 *     rationale  = RationaleUi(...),
 *     onGranted  = { /* điều hướng sang màn Photos */ }
 * )
 * ItemPhotoOrVideHome(onClick = requestStorage, ...)
 * ```
 *
 * @param scopeKey phân biệt các flow độc lập trong cùng một screen. Mặc định theo
 *   [Permission.key], nên 2 chỗ cùng xin Storage sẽ DÙNG CHUNG state — truyền key khác
 *   nhau nếu muốn tách riêng.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberPermissionRequester(
    permission: Permission,
    rationale: RationaleUi,
    scopeKey: String = permission.key,
    onGranted: () -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val viewModel: PermissionViewModel = hiltViewModel(key = "permission_$scopeKey")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionState = rememberMultiplePermissionsState(
        permissions = permission.manifestPermissions
    ) { _ ->
        // Không tin map kết quả của Accompanist: tự check lại theo rule của app
        // (vd Storage chỉ cần IMAGES hoặc VIDEO là đủ).
        viewModel.onSystemResult(permission, context.isPermissionGranted(permission))
    }

    // Quay lại app sau khi user cấp quyền trong Settings -> tự nhận Granted
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, permission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResumeFromSettings(permission, context.isPermissionGranted(permission))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (val s = uiState) {
        is PermissionUiState.Granted ->
            LaunchedEffect(s) {
                viewModel.dismiss()
                onGranted()
            }

        is PermissionUiState.LaunchSystem ->
            LaunchedEffect(s) {
                permissionState.launchMultiplePermissionRequest()
                // KHÔNG dismiss ngay — chờ callback onSystemResult xử lý tiếp
            }

        is PermissionUiState.ShowRationale ->
            CommonDialog(
                image = rationale.image,
                title = rationale.title,
                titleArgs = rationale.titleArgs,
                description = rationale.description,
                buttonText = rationale.buttonText,
                onButtonClick = { viewModel.onRationaleAccepted(s.p) },
                onDismiss = { viewModel.dismiss() }
            )

        is PermissionUiState.ShowGoToSettings ->
            GoToSettingsDialog(
                permission = s.p,
                onOpenSetting = {
                    viewModel.onOpenSettings()
                    context.openAppSettings()
                },
                onDismiss = { viewModel.dismiss() }
            )

        PermissionUiState.Idle -> Unit
    }

    return remember(viewModel, permission, context) {
        { viewModel.onRequest(permission, context.isPermissionGranted(permission)) }
    }
}

@Composable
fun GoToSettingsDialog(
    permission: Permission,
    onOpenSetting: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title = when (permission) {
        Permission.Storage -> stringResource(
            R.string.tv_title_dialog_open_setting_photo,
            stringResource(R.string.app_name)
        )

        Permission.Calendar -> stringResource(R.string.tv_title_dialog_open_setting_calendar)
        Permission.Contact -> stringResource(R.string.text_allow_contacts_access)
        else -> stringResource(R.string.dialog_request_permission_post_notification_title)
    }
    val stepDetail = when (permission) {
        Permission.Calendar -> stringResource(R.string.dialog_request_permission_calendar_step_3)
        Permission.Contact -> stringResource(R.string.tv_read_contact_permission_step_3)
        else -> stringResource(R.string.dialog_request_permission_post_notification_step_3)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Box {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = colorResource(R.color.color_on_surface_variant_2),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                        .clickable { onDismiss() }
                )

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = AppTextStyles.Size18SemiBold,
                        color = colorResource(R.color.black),
                        textAlign = TextAlign.Center
                    )
                    CommonSpacerHeight(16)

                    SettingStep(
                        number = "1",
                        text = stringResource(R.string.request_permission_step_1)
                    )
                    CommonSpacerHeight(8)
                    SettingStep(
                        number = "2",
                        text = stringResource(R.string.dialog_request_permission_post_notification_step_2)
                    )
                    CommonSpacerHeight(8)
                    SettingStep(number = "3", text = stepDetail)
                    CommonSpacerHeight(20)

                    Text(
                        text = stringResource(R.string.all_open_setting),
                        style = AppTextStyles.Size16SemiBold,
                        color = colorResource(R.color.primary_primary),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable { onOpenSetting() }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

// Một dòng bước: [số] + mô tả
@Composable
private fun SettingStep(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(colorResource(R.color.primary_primary), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number,
                color = colorResource(R.color.white),
                style = AppTextStyles.Size12SemiBold
            )
        }
        CommonSpacerWidth(12)
        Text(
            text = text,
            style = AppTextStyles.Size14Medium,
            color = colorResource(R.color.color_on_surface_variant_2),
            modifier = Modifier.weight(1f)
        )
    }
}
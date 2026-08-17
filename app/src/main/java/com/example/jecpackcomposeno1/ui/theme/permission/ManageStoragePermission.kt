package com.example.jecpackcomposeno1.ui.theme.permission

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.component.CommonDialog
import com.example.jecpackcomposeno1.ui.theme.component.hasManageExternalStoragePermission
import com.example.jecpackcomposeno1.ui.theme.component.openAppSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

fun manageStorageRationale(appName: String) = RationaleUi(
    image = R.drawable.ic_photo_dialog_home,
    title = R.string.tv_dialog_allow_manage_external_storage_title,
    titleArgs = listOf(appName),
    description = R.string.tv_dialog_allow_manage_external_storage_description,
    buttonText = R.string.text_allow_full_access,
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberManageStorageRequester(
    rationale: RationaleUi,
    onOpenTarget: (route: String) -> Unit,
    onGranted: () -> Unit,
): (route: String) -> Unit {
    val context = LocalContext.current
    val currentOnOpenTarget by rememberUpdatedState(onOpenTarget)
    val currentOnGranted by rememberUpdatedState(onGranted)

    val rationaleRoute = rememberSaveable { mutableStateOf<String?>(null) }
    /** Đang ở màn Settings, và ĐÃ điều hướng sang màn đích. */
    val awaitingSettings = rememberSaveable { mutableStateOf(false) }
    /** Route chờ kết quả popup hệ thống (API < 30). Nhánh này CHƯA điều hướng. */
    val legacyRoute = rememberSaveable { mutableStateOf<String?>(null) }

    /**
     * Quay về từ Settings. Đã đứng sẵn ở màn đích nên không điều hướng gì nữa — có quyền
     * thì nạp dữ liệu, không có thì để màn đích tự hiện trạng thái rỗng.
     */
    fun settleReturnFromSettings() {
        if (!awaitingSettings.value) return
        awaitingSettings.value = false
        ManageStorageGrantPoller.stop()
        if (context.hasManageExternalStoragePermission()) currentOnGranted()
    }

    val legacyPermissionState = rememberMultiplePermissionsState(
        permissions = Permission.ManageExternalStorage.manifestPermissions
    ) { _ ->
        val route = legacyRoute.value ?: return@rememberMultiplePermissionsState
        legacyRoute.value = null
        if (context.hasManageExternalStoragePermission()) {
            currentOnGranted()
            currentOnOpenTarget(route)
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { settleReturnFromSettings() }

    // Lưới an toàn: nếu không kéo được app lên và user tự bấm back thì bắt ở đây.
    // Bình thường result của launcher về TRƯỚC onResume nên nhánh này no-op.
    LifecycleResumeEffect(Unit) {
        settleReturnFromSettings()
        onPauseOrDispose { }
    }

    rationaleRoute.value?.let { route ->
        CommonDialog(
            image = rationale.image,
            title = rationale.title,
            titleArgs = rationale.titleArgs,
            description = rationale.description,
            buttonText = rationale.buttonText,
            onButtonClick = {
                rationaleRoute.value = null
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    legacyRoute.value = route
                    legacyPermissionState.launchMultiplePermissionRequest()
                    return@CommonDialog
                }
                context.findActivity()?.let(ManageStorageGrantPoller::start)
                if (settingsLauncher.launchFirstAvailable(context.allFilesAccessIntents())) {
                    awaitingSettings.value = true
                    // Điều hướng NGAY: Activity vẫn đang RESUMED (transaction pause chỉ về
                    // ở frame sau) nên navigateSafe chắc chắn đi được, và màn Settings sẽ
                    // che toàn bộ animation chuyển màn.
                    currentOnOpenTarget(route)
                } else {
                    ManageStorageGrantPoller.stop()
                    context.openAppSettings()
                }
            },
            onDismiss = { rationaleRoute.value = null }
        )
    }

    return remember(context) {
        { route ->
            if (context.hasManageExternalStoragePermission()) {
                currentOnGranted()
                currentOnOpenTarget(route)
            } else {
                rationaleRoute.value = route
            }
        }
    }
}

/** Bắn intent đầu tiên mở được. Trả về false nếu máy không có màn nào nhận. */
private fun ActivityResultLauncher<Intent>.launchFirstAvailable(intents: List<Intent>): Boolean {
    intents.forEach { intent ->
        try {
            launch(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            // thử intent kế tiếp
        }
    }
    return false
}

internal object ManageStorageGrantPoller {
    private const val POLL_MS = 250L
    /** Bỏ cuộc nếu user bỏ đi luôn, tránh Handler quay mãi trong background. */
    private const val TIMEOUT_MS = 3 * 60 * 1000L
    private val handler = Handler(Looper.getMainLooper())
    private var app: Application? = null
    private var activityClass: Class<out Activity>? = null
    private var elapsedMs = 0L

    fun start(activity: Activity) {
        app = activity.application
        activityClass = activity.javaClass
        elapsedMs = 0L
        handler.removeCallbacks(poll)
        handler.postDelayed(poll, POLL_MS)
    }

    fun stop() {
        app = null
        activityClass = null
        handler.removeCallbacks(poll)
    }

    private val poll = object : Runnable {
        override fun run() {
            val app = app ?: return
            val cls = activityClass ?: return
            if (app.hasManageExternalStoragePermission()) {
                app.bringToFront(cls)
                stop()
                return
            }
            elapsedMs += POLL_MS
            if (elapsedMs >= TIMEOUT_MS) {
                stop()
                return
            }
            handler.postDelayed(this, POLL_MS)
        }
    }
}

private fun Application.bringToFront(activityClass: Class<out Activity>) {
    runCatching {
        startActivity(
            Intent(this, activityClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        )
    }
}

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.R)
private fun Context.allFilesAccessIntents(): List<Intent> = listOf(
    Intent(
        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
        "package:$packageName".toUri()
    ),
    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
)
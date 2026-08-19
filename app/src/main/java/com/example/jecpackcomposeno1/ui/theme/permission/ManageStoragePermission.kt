package com.example.jecpackcomposeno1.ui.theme.permission

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.navigation.AppDestination
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
    onOpenTarget: (target: AppDestination) -> Unit,
    onGranted: () -> Unit,
): (target: AppDestination) -> Unit {
    val context = LocalContext.current
    val currentOnOpenTarget by rememberUpdatedState(onOpenTarget)
    val currentOnGranted by rememberUpdatedState(onGranted)

    val rationaleTarget = remember { mutableStateOf<AppDestination?>(null) }
    /** Đang ở màn Settings, và ĐÃ điều hướng sang màn đích. */
    val awaitingSettings = rememberSaveable { mutableStateOf(false) }
    /** Đích chờ kết quả popup hệ thống (API < 30). Nhánh này CHƯA điều hướng. */
    val legacyTarget = remember { mutableStateOf<AppDestination?>(null) }

    fun settleReturnFromSettings() {
        if (!awaitingSettings.value) return
        awaitingSettings.value = false
        ManageStorageGrantWatcher.stop()
        if (context.hasManageExternalStoragePermission()) currentOnGranted()
    }

    val legacyPermissionState = rememberMultiplePermissionsState(
        permissions = Permission.ManageExternalStorage.manifestPermissions
    ) { _ ->
        val target = legacyTarget.value ?: return@rememberMultiplePermissionsState
        legacyTarget.value = null
        if (context.hasManageExternalStoragePermission()) {
            currentOnGranted()
            currentOnOpenTarget(target)
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { settleReturnFromSettings() }

    LifecycleResumeEffect(Unit) {
        settleReturnFromSettings()
        onPauseOrDispose { }
    }

    rationaleTarget.value?.let { target ->
        CommonDialog(
            image = rationale.image,
            title = rationale.title,
            titleArgs = rationale.titleArgs,
            description = rationale.description,
            buttonText = rationale.buttonText,
            onButtonClick = {
                rationaleTarget.value = null
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    legacyTarget.value = target
                    legacyPermissionState.launchMultiplePermissionRequest()
                    return@CommonDialog
                }
                context.findActivity()?.let { activity ->
                    val app = activity.application
                    val activityClass = activity.javaClass
                    ManageStorageGrantWatcher.start(app) { app.bringToFront(activityClass) }
                }
                if (settingsLauncher.launchFirstAvailable(context.allFilesAccessIntents())) {
                    awaitingSettings.value = true
                    currentOnOpenTarget(target)
                } else {
                    ManageStorageGrantWatcher.stop()
                    context.openAppSettings()
                }
            },
            onDismiss = { rationaleTarget.value = null }
        )
    }

    return remember(context) {
        { target ->
            if (context.hasManageExternalStoragePermission()) {
                currentOnGranted()
                currentOnOpenTarget(target)
            } else {
                rationaleTarget.value = target
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

/**
 * Theo dõi appop MANAGE_EXTERNAL_STORAGE của chính app để biết NGAY khi user bật
 * "All files access" trong Settings.
 */
internal object ManageStorageGrantWatcher {
    private var appOps: AppOpsManager? = null
    private var listener: AppOpsManager.OnOpChangedListener? = null

    private val opName: String =
        AppOpsManager.permissionToOp(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            ?: "android:manage_external_storage"

    @MainThread
    fun start(app: Application, onGranted: () -> Unit) {
        stop()
        val ops = app.getSystemService(AppOpsManager::class.java) ?: return
        val opListener = AppOpsManager.OnOpChangedListener { _, pkg ->
            if (pkg == app.packageName && app.hasManageExternalStoragePermission()) {
                onGranted()
                stop()
            }
        }
        val watching = runCatching {
            ops.startWatchingMode(opName, app.packageName, opListener)
        }.isSuccess
        if (!watching) return

        appOps = ops
        listener = opListener
    }

    @MainThread
    fun stop() {
        listener?.let { current -> runCatching { appOps?.stopWatchingMode(current) } }
        listener = null
        appOps = null
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
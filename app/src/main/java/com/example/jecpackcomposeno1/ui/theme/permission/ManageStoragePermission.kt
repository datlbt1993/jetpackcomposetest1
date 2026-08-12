package com.example.jecpackcomposeno1.ui.theme.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.component.CommonDialog
import com.example.jecpackcomposeno1.ui.theme.component.hasManageExternalStoragePermission
import com.example.jecpackcomposeno1.ui.theme.component.openAppSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Rationale mặc định cho All files access — dùng đúng bộ string base code đã có.
 * Truyền [appName] để điền vào "%s" của title.
 */
fun manageStorageRationale(appName: String) = RationaleUi(
    image = R.drawable.ic_photo_dialog_home,
    title = R.string.tv_dialog_allow_manage_external_storage_title,
    titleArgs = listOf(appName),
    description = R.string.tv_dialog_allow_manage_external_storage_description,
    buttonText = R.string.text_allow_full_access,
)

/**
 * Flow xin MANAGE_EXTERNAL_STORAGE (All files access), trả về lambda để gắn vào onClick.
 *
 * Khác [rememberPermissionRequester] ở chỗ đây KHÔNG phải runtime permission:
 *  - Không có popup hệ thống, không có callback báo "user đã từ chối".
 *  - Android 11+ : mở màn Settings "All files access" bằng Intent, user tự bật toggle.
 *  - Android 10  : quyền chưa tồn tại -> fallback xin READ_EXTERNAL_STORAGE runtime.
 *
 * Vì không có tín hiệu "denied", flow này KHÔNG dùng bộ đếm 2 lần + GoToSettingsDialog
 * như Contact/Calendar — bản thân nó đã dẫn user tới Settings ngay từ đầu.
 * val requestAllFiles = rememberManageStorageRequester(
 *     rationale = manageStorageRationale(stringResource(R.string.app_name)),
 *     onGranted = { /* mở màn Photos */ }
 * )
 * ItemPhotoOrVideHome(onClick = requestAllFiles, ...)
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberManageStorageRequester(
    rationale: RationaleUi,
    onGranted: () -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val showRationale = remember { mutableStateOf(false) }
    val currentOnGranted by rememberUpdatedState(onGranted)
    val awaitingReturn = remember { mutableStateOf(false) }

    fun finishIfGranted() {
        awaitingReturn.value = false
        if (context.hasManageExternalStoragePermission()) currentOnGranted()
    }

    // API < 30: MANAGE_EXTERNAL_STORAGE chưa tồn tại -> xin READ_EXTERNAL_STORAGE runtime
    val legacyPermissionState = rememberMultiplePermissionsState(
        permissions = Permission.ManageExternalStorage.manifestPermissions
    ) { _ -> finishIfGranted() }

    // API 30+: mở màn Settings "All files access".
    // resultCode LUÔN là RESULT_CANCELED dù user có bật toggle hay không -> phải tự check.
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { finishIfGranted() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && awaitingReturn.value) finishIfGranted()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showRationale.value) {
        CommonDialog(
            image = rationale.image,
            title = rationale.title,
            titleArgs = rationale.titleArgs,
            description = rationale.description,
            buttonText = rationale.buttonText,
            onButtonClick = {
                showRationale.value = false
                awaitingReturn.value = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val launched = context.allFilesAccessIntents().any { intent ->
                        try {
                            settingsLauncher.launch(intent); true
                        } catch (_: ActivityNotFoundException) {
                            false
                        }
                    }
                    if (!launched) {
                        awaitingReturn.value = false
                        context.openAppSettings()
                    }
                } else {
                    legacyPermissionState.launchMultiplePermissionRequest()
                }
            },
            onDismiss = { showRationale.value = false }
        )
    }

    return remember(context) {
        {
            if (context.hasManageExternalStoragePermission()) {
                currentOnGranted()
            } else {
                showRationale.value = true
            }
        }
    }
}

private fun Context.allFilesAccessIntents(): List<Intent> = listOf(
    Intent(
        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
        "package:$packageName".toUri()
    ),
    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
)
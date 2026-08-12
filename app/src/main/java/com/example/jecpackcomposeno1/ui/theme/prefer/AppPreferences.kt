package com.example.jecpackcomposeno1.ui.theme.prefer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext private val applicationContext: Context) {

    companion object {
        private const val KEY_USER_PAID_TO_REMOVE_ADS = "KEY_USER_PAID_TO_REMOVE_ADS"
        private const val KEY_IS_USER_CAME_FROM_ORGANIC = "KEY_IS_USER_CAME_FROM_ORGANIC"

        const val SHARED_NAME = "oracle_shared"
        private const val KEY_RATED_APP = "KEY_RATED_APP"
        private const val KEY_IS_USER_RATING_ONE_STAR = "KEY_IS_USER_RATING_ONE_STAR"
        private const val KEY_FLASH_CAMERA_ID = "KEY_FLASH_CAMERA_ID"
        private const val KEY_IS_SCREEN_ON = "KEY_IS_SCREEN_ON"
        private const val KEY_USER_OPEN_APP_COUNT = "KEY_USER_OPEN_APP_COUNT"
        private const val KEY_NUMBER_OF_REQUEST_NOTIFICATION_PERMISSION = "KEY_NUMBER_OF_REQUEST_NOTIFICATION_PERMISSION"
        private const val KEY_SHOW_PERMISSION_DIALOG = "KEY_SHOW_PERMISSION_DIALOG"
        private const val KEY_NUMBER_OF_REQUEST_STORAGE_PERMISSION = "KEY_NUMBER_OF_REQUEST_STORAGE_PERMISSION"
        private const val KEY_SWIPE_INTRO_VIDEO_SHOWN = "KEY_SWIPE_INTRO_VIDEO_SHOWN"
        private const val KEY_SWIPE_INTRO_PHOTO_SHOWN = "KEY_SWIPE_INTRO_PHOTO_SHOWN"
        private const val KEY_IS_STORAGE_RUNNING = "KEY_IS_STORAGE_RUNNING"
        private const val KEY_COUNT_STORAGE_PERMISSION = "KEY_COUNT_STORAGE_PERMISSION"
        private const val KEY_COUNT_STORAGE_SYSTEM_DENY_IN_FLOW = "KEY_COUNT_STORAGE_SYSTEM_DENY_IN_FLOW"
        private const val KEY_COUNT_CALENDAR_PERMISSION = "KEY_COUNT_CALENDAR_PERMISSION"
        private const val KEY_COUNT_CALENDAR_SYSTEM_DENY_IN_FLOW = "KEY_COUNT_CALENDAR_SYSTEM_DENY_IN_FLOW"
        private const val KEY_COUNT_CONTACT_PERMISSION = "KEY_COUNT_CONTACT_PERMISSION"
        private const val KEY_COUNT_CONTACT_SYSTEM_DENY_IN_FLOW = "KEY_COUNT_CONTACT_SYSTEM_DENY_IN_FLOW"
        private const val KEY_CALENDAR_DELETE_QUOTA_DATE = "KEY_CALENDAR_DELETE_QUOTA_DATE"
        private const val KEY_CALENDAR_DELETE_QUOTA_USED = "KEY_CALENDAR_DELETE_QUOTA_USED"

        private const val KEY_VIDEO_COMPRESS_SUCCESS_COUNT = "KEY_VIDEO_COMPRESS_SUCCESS_COUNT"
        private const val KEY_VIDEO_COMPRESS_SAVED_BYTES = "KEY_VIDEO_COMPRESS_SAVED_BYTES"
        private const val KEY_VIDEO_COMPRESS_ORIGINAL_BYTES = "KEY_VIDEO_COMPRESS_ORIGINAL_BYTES"
        private const val KEY_VIDEO_COMPRESS_PENDING_URIS = "KEY_VIDEO_COMPRESS_PENDING_URIS"
        private const val KEY_VIDEO_COMPRESS_DELETED_ORIGINAL_URIS = "KEY_VIDEO_COMPRESS_DELETED_ORIGINAL_URIS"
        private const val KEY_VIDEO_COMPRESS_IS_LARGER_THAN_ORIGINAL = "KEY_VIDEO_COMPRESS_IS_LARGER_THAN_ORIGINAL"
        private const val KEY_PENDING_COMPRESS_FAILED_DIALOG = "KEY_PENDING_COMPRESS_FAILED_DIALOG"

        private const val KEY_SCHEDULE_DAILY_IS_ENABLE = "KEY_SCHEDULE_DAILY_IS_ENABLE"
        private const val KEY_IS_SCHEDULE_NOTIFICATION = "KEY_IS_SCHEDULE_NOTIFICATION"
        private const val KEY_SCHEDULE_DAILY_CONTENT_SCENARIO_VERSION = "KEY_SCHEDULE_DAILY_CONTENT_SCENARIO_VERSION"
        private const val KEY_SCHEDULE_DAILY_NOTICE_FIRST_AFTER_NUMBER_OF_DAY = "KEY_SCHEDULE_DAILY_NOTICE_FIRST_AFTER_NUMBER_OF_DAY"
        private const val KEY_SCHEDULE_DAILY_NOTICE_HOUR_OF_DAY = "KEY_SCHEDULE_DAILY_NOTICE_HOUR_OF_DAY"
        private const val KEY_SCHEDULE_DAILY_NOTICE_MINUTE = "KEY_SCHEDULE_DAILY_NOTICE_MINUTE"
        private const val KEY_SCHEDULE_DAILY_NOTICE_DAY_INTERVAL = "KEY_SCHEDULE_DAILY_NOTICE_DAY_INTERVAL"
        private const val KEY_IS_SCHEDULE_NOTIFICATION_FIRST = "KEY_IS_SCHEDULE_NOTIFICATION_FIRST"
        private const val KEY_CLEAN_FILES_COUNT = "KEY_CLEAN_FILES_COUNT"
        private const val KEY_LAST_DATE_CLEAN_FILES = "KEY_LAST_DATE_CLEAN_FILES"
        private const val KEY_DAILY_MERGE_CONTACT_COUNT = "KEY_DAILY_MERGE_CONTACT_COUNT"
        private const val KEY_LAST_DATE_MERGE_CONTACT = "KEY_LAST_DATE_MERGE_CONTACT"
        private const val KEY_DAILY_DELETE_CONTACTS = "KEY_DAILY_DELETE_CONTACTS"
        private const val KEY_LAST_DATE_DELETE_CONTACT = "KEY_LAST_DATE_DELETE_CONTACT"
        private const val KEY_DAILY_RESTORE_CONTACT_COUNT = "KEY_DAILY_RESTORE_CONTACT_COUNT"
        private const val KEY_LAST_DATE_RESTORE_CONTACT = "KEY_LAST_DATE_RESTORE_CONTACT"
        private const val AUTO_BACKUP = "AUTO_BACKUP"
        private const val CONNECT_GOOGLE_DRIVE = "CONNECT_GOOGLE_DRIVE"
        private const val GOOGLE_DRIVE_EMAIL = "GOOGLE_DRIVE_EMAIL"
        private const val GOOGLE_DRIVE_ACCOUNT_NAME = "GOOGLE_DRIVE_ACCOUNT_NAME"
        private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
        private const val KEY_ID_TOKEN = "KEY_ID_TOKEN"
        private const val KEY_CONTACT_BACKUP_STAGING_INITIALIZED = "KEY_CONTACT_BACKUP_STAGING_INITIALIZED"
        private const val KEY_CONTACT_BACKUP_PENDING_DRIVE_UPLOAD = "KEY_CONTACT_BACKUP_PENDING_DRIVE_UPLOAD"
        private const val KEY_CONTACT_BACKUP_PENDING_DRIVE_FILE = "KEY_CONTACT_BACKUP_PENDING_DRIVE_FILE"
    }

    private val prefs: SharedPreferences =
        applicationContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    var isFirstOpenApp = false

    var isUserRated by prefs.boolean(
        key = { KEY_RATED_APP },
        defaultValue = false
    )

    var isUserRatingOneStar by prefs.boolean(
        key = { KEY_IS_USER_RATING_ONE_STAR },
        defaultValue = false
    )

    var isUserPaidToRemoveAds by prefs.boolean(
        key = { KEY_USER_PAID_TO_REMOVE_ADS },
        defaultValue = false
    )

    var isUserCameFromOrganic by prefs.boolean(
        key = { KEY_IS_USER_CAME_FROM_ORGANIC },
        defaultValue = true
    )

    var isScreenOn by prefs.boolean(
        key = { KEY_IS_SCREEN_ON },
        defaultValue = true
    )

    var flashCameraId by prefs.string(
        key = { KEY_FLASH_CAMERA_ID },
        defaultValue = "0"
    )

    var userOpenAppCount by prefs.int(
        key = { KEY_USER_OPEN_APP_COUNT },
        defaultValue = 0
    )

    var countNotificationPermission by prefs.int(
        key = { KEY_NUMBER_OF_REQUEST_NOTIFICATION_PERMISSION },
        defaultValue = 0
    )

    var timeOfFirstAdClicked by prefs.long()

    var expiredTimeDisableLoadBannerAdsHighFloor by prefs.long()
    var expiredTimeDisableLoadBannerAdsMediumFloor by prefs.long()

    var expiredTimeDisableLoadNativeAdsHighFloor by prefs.long()
    var expiredTimeDisableLoadNativeAdsMediumFloor by prefs.long()

    var expiredTimeDisableLoadInterstitialAdsHighFloor by prefs.long()
    var expiredTimeDisableLoadInterstitialAdsMediumFloor by prefs.long()

    var expiredTimeDisableLoadRewardedInterstitialAdsHighFloor by prefs.long()
    var expiredTimeDisableLoadRewardedInterstitialAdsMediumFloor by prefs.long()

    var expiredTimeDisableLoadRewardedAdsHighFloor by prefs.long()
    var expiredTimeDisableLoadRewardedAdsMediumFloor by prefs.long()

    var expiredTimeDisableLoadAppOpenAdsHighFloor by prefs.long()
    var expiredTimeDisableLoadAppOpenAdsMediumFloor by prefs.long()

    var adClickedCount by prefs.int()

    var isRecentlyAdClicked by prefs.boolean()

    var isScheduleDailyIsEnable by prefs.boolean(
        key = { KEY_SCHEDULE_DAILY_IS_ENABLE }, defaultValue = true
    )

    var isScheduleNotification by prefs.boolean(
        key = { KEY_IS_SCHEDULE_NOTIFICATION }, defaultValue = false
    )

    var isScheduleDailyContentScenarioVersion by prefs.int(
        key = { KEY_SCHEDULE_DAILY_CONTENT_SCENARIO_VERSION }, defaultValue = 1
    )

    var isScheduleDailyFirstAfterNumberOfDay by prefs.int(
        key = { KEY_SCHEDULE_DAILY_NOTICE_FIRST_AFTER_NUMBER_OF_DAY },
        defaultValue = 0
    )

    var isScheduleDailyHourOfDay by prefs.int(
        key = { KEY_SCHEDULE_DAILY_NOTICE_HOUR_OF_DAY },
        defaultValue = 20
    )

    var isScheduleDailyMinute by prefs.int(
        key = { KEY_SCHEDULE_DAILY_NOTICE_MINUTE },
        defaultValue = 30
    )

    var isScheduleDailyDayInterval by prefs.int(
        key = { KEY_SCHEDULE_DAILY_NOTICE_DAY_INTERVAL },
        defaultValue = 1
    )

    var isScheduleNotificationFirst by prefs.boolean(
        key = { KEY_IS_SCHEDULE_NOTIFICATION_FIRST }, defaultValue = false
    )

    var isShowPermissionDialog by prefs.boolean(
        key = { KEY_SHOW_PERMISSION_DIALOG },
        defaultValue = false
    )

    var numberOfRequestStoragePermission by prefs.int(
        key = { KEY_NUMBER_OF_REQUEST_STORAGE_PERMISSION },
        defaultValue = 0
    )

    var swipeIntroPhotoShown by prefs.boolean(
        key = { KEY_SWIPE_INTRO_PHOTO_SHOWN },
        defaultValue = false
    )

    var swipeIntroVideoShown by prefs.boolean(
        key = { KEY_SWIPE_INTRO_VIDEO_SHOWN },
        defaultValue = false
    )

    var isStorageRunning by prefs.boolean(
        key = { KEY_IS_STORAGE_RUNNING },
        defaultValue = false
    )

    var countStoragePermission by prefs.int(
        key = { KEY_COUNT_STORAGE_PERMISSION },
        defaultValue = 0
    )

    var countStorageSystemDenyInFlow by prefs.int(
        key = { KEY_COUNT_STORAGE_SYSTEM_DENY_IN_FLOW },
        defaultValue = 0
    )

    var countCalendarPermission by prefs.int(
        key = { KEY_COUNT_CALENDAR_PERMISSION },
        defaultValue = 0
    )

    var countCalendarSystemDenyInFlow by prefs.int(
        key = { KEY_COUNT_CALENDAR_SYSTEM_DENY_IN_FLOW },
        defaultValue = 0
    )

    var countContactPermission by prefs.int(
        key = { KEY_COUNT_CONTACT_PERMISSION },
        defaultValue = 0
    )

    var countContactSystemDenyInFlow by prefs.int(
        key = { KEY_COUNT_CONTACT_SYSTEM_DENY_IN_FLOW },
        defaultValue = 0
    )

    var calendarDeleteQuotaDate by prefs.string(
        key = { KEY_CALENDAR_DELETE_QUOTA_DATE },
        defaultValue = "",
    )

    var calendarDeleteQuotaUsedCount by prefs.int(
        key = { KEY_CALENDAR_DELETE_QUOTA_USED },
        defaultValue = 0,
    )

    var pendingCompressFailedDialog by prefs.boolean(
        key = { KEY_PENDING_COMPRESS_FAILED_DIALOG },
        defaultValue = false
    )

    fun saveVideoCompressResult(
        successCount: Int,
        totalSavedBytes: Long,
        totalOriginalBytes: Long,
        pendingDeleteUris: List<Uri>?,
        isLargerThanOriginal: Boolean = false,
        deletedOriginalUris: List<Uri>? = null,
    ) {
        prefs.edit {
            putInt(KEY_VIDEO_COMPRESS_SUCCESS_COUNT, successCount)
            putLong(KEY_VIDEO_COMPRESS_SAVED_BYTES, totalSavedBytes)
            putLong(KEY_VIDEO_COMPRESS_ORIGINAL_BYTES, totalOriginalBytes)
            putStringSet(
                KEY_VIDEO_COMPRESS_PENDING_URIS,
                pendingDeleteUris?.map { it.toString() }?.toSet()
            )
            if (deletedOriginalUris.isNullOrEmpty()) {
                remove(KEY_VIDEO_COMPRESS_DELETED_ORIGINAL_URIS)
            } else {
                putStringSet(
                    KEY_VIDEO_COMPRESS_DELETED_ORIGINAL_URIS,
                    deletedOriginalUris.map { it.toString() }.toSet()
                )
            }
            putBoolean(KEY_VIDEO_COMPRESS_IS_LARGER_THAN_ORIGINAL, isLargerThanOriginal)
        }
    }



    var lastDateCleanFile by prefs.string(
        key = { KEY_LAST_DATE_CLEAN_FILES },
        defaultValue = ""
    )

    var lastDateMergeContact by prefs.string(
        key = { KEY_LAST_DATE_MERGE_CONTACT },
        defaultValue = ""
    )


    var lastDateRestoreContact by prefs.string(
        key = { KEY_LAST_DATE_RESTORE_CONTACT },
        defaultValue = ""
    )

    var autoBackup by prefs.boolean(
        key = { AUTO_BACKUP },
        defaultValue = true
    )

    var contactBackupStagingInitialized by prefs.boolean(
        key = { KEY_CONTACT_BACKUP_STAGING_INITIALIZED },
        defaultValue = false
    )

    var contactBackupPendingDriveUpload by prefs.boolean(
        key = { KEY_CONTACT_BACKUP_PENDING_DRIVE_UPLOAD },
        defaultValue = false
    )

    var contactBackupPendingDriveFileName by prefs.string(
        key = { KEY_CONTACT_BACKUP_PENDING_DRIVE_FILE },
        defaultValue = ""
    )

    var isConnectGoogleDrive by prefs.boolean(
        key = { CONNECT_GOOGLE_DRIVE },
        defaultValue = false
    )

    var googleDriveEmail by prefs.string(
        key = { GOOGLE_DRIVE_EMAIL },
        defaultValue = ""
    )
    var googleDriveAccountName by prefs.string(
        key = { GOOGLE_DRIVE_ACCOUNT_NAME },
        defaultValue = ""
    )
    var accessToken by prefs.string(
        key = { KEY_ACCESS_TOKEN },
        defaultValue = ""
    )

    var idToken by prefs.string(
        key = { KEY_ID_TOKEN },
        defaultValue = ""
    )

    fun clearGoogleDriveSession() {
        isConnectGoogleDrive = false
        googleDriveEmail = ""
        googleDriveAccountName = ""
        idToken = ""
        accessToken = ""
    }
}

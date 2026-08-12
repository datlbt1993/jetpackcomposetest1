package com.example.jecpackcomposeno1.ui.theme.component

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Cache SimpleDateFormat theo (pattern, locale) qua ThreadLocal.
 * SimpleDateFormat KHÔNG thread-safe nên dùng ThreadLocal cho mỗi thread,
 * tránh allocate mỗi call (ANR risk khi adapter render hàng ngàn row).
 */
private val dateFormatCache = ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>>()

private fun cachedDateFormat(pattern: String, locale: Locale): SimpleDateFormat {
    val key = pattern + '|' + locale.toLanguageTag()
    val holder = dateFormatCache.getOrPut(key) {
        ThreadLocal.withInitial { SimpleDateFormat(pattern, locale) }
    }
    return holder.get() ?: SimpleDateFormat(pattern, locale).also { holder.set(it) }
}

const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
const val DATE_TIME_FORMAT_DD_MM_YYY = "dd/MM/yyyy"
const val DATE_TIME_FORMAT_MM_DD_YYYY = "MM/dd/yyyy"
const val DATE_TIME_FORMAT_HOUR_MINUTE = "hh:mm a"
const val DATE_TIME_FORMAT_MMM_DD_YYYY = "MMM dd, yyyy"
const val DATE_TIME_FORMAT_HH_MM = "HH:mm"
const val DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyyMMdd_HHmmss"

private const val ONE_DAY = 1L
private const val ONE_MONTH_AGO = 1L
private const val SIX_DAYS_AGO = 6L
private const val SIX_MONTHS_AGO = 6L
private const val ONE_YEAR_AGO = 1L

enum class TimeRangeGroup {
    LAST_WEEK,
    ONE_WEEK_AGO,
    ONE_MONTH_AGO,
    SIX_MONTHS_AGO,
    ONE_YEAR_AGO
}

fun getCurrentTimeInSecond() = System.currentTimeMillis() / 1000

fun getCurrentTimeMillis() = System.currentTimeMillis()

fun Int.toMillis() = this * 1000L

fun Long.toMillis() = this * 1000L

fun Long.toFormattedDateTime(
    pattern: String = DATE_TIME_FORMAT,
    locale: Locale = Locale.getDefault()
): String {
    return try {
        cachedDateFormat(pattern, locale).format(this)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        "Invalid Date"
    }
}

fun Long.toFormattedDateTime(
    pattern: String = DATE_TIME_FORMAT,
    locale: Locale = Locale.getDefault(),
    addHours: Int = 0
): String {
    return try {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = this@toFormattedDateTime
            add(Calendar.HOUR_OF_DAY, addHours)
        }
        cachedDateFormat(pattern, locale).format(calendar.time)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        "Invalid Date"
    }
}


fun String.toFormattedBarcodeDateTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        val date = inputFormat.parse(this) ?: return ""
        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun Calendar.toEndOfTodayString(locale: Locale = Locale.getDefault()): String {
    return try {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)

        val formatter = SimpleDateFormat(DATE_TIME_FORMAT, locale)
        formatter.format(time)
    } catch (e: Exception) {
        "Invalid Date"
    }
}

fun Calendar.toEndDateOfTodayString(): String {
    return try {
        this.set(Calendar.HOUR_OF_DAY, 23)
        this.set(Calendar.MINUTE, 59)
        this.set(Calendar.SECOND, 59)
        this.set(Calendar.MILLISECOND, 999)

        val sdf = SimpleDateFormat(DATE_TIME_FORMAT_DD_MM_YYY, Locale.getDefault())
        sdf.format(this.time)
    } catch (e: Exception) {
        "Invalid Date"
    }
}

fun Calendar.toStartDateOfTodayString(): String {
    return try {
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)

        val sdf = SimpleDateFormat(DATE_TIME_FORMAT_DD_MM_YYY, Locale.getDefault())
        sdf.format(this.time)
    } catch (e: Exception) {
        "Invalid Date"
    }
}


fun Long.toDateTimeFormatHourMinute(): String {
    return cachedDateFormat(DATE_TIME_FORMAT_HOUR_MINUTE, Locale.ENGLISH).format(Date(this))
}

fun String.convertPatternToMillis(): Long {
    return try {
        val date = cachedDateFormat(DATE_TIME_FORMAT_HOUR_MINUTE, Locale.ENGLISH).parse(this)
        date?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

fun getCurrentDay(): String {
    return cachedDateFormat("EEE", Locale.ENGLISH).format(Date(getCurrentTimeMillis()))
}

/** Group key for calendar/list items: `"year-month"` (month is 0-based, [Calendar.MONTH]). */
fun yearMonthKey(timeMs: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timeMs }
    return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
}

/** Month section title, e.g. "May, 2026". */
fun formatMonthHeader(timeMs: Long, locale: Locale = Locale.getDefault()): String {
    return cachedDateFormat("MMMM, yyyy", locale).format(Date(timeMs))
}

fun formatMonthHeaderLabel(monthKey: String, locale: Locale = Locale.getDefault()): String {
    val parts = monthKey.split('-')
    if (parts.size != 2) return monthKey
    val year = parts[0].toIntOrNull() ?: return monthKey
    val month = parts[1].toIntOrNull() ?: return monthKey
    val timeMs = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    return formatMonthHeader(timeMs, locale)
}

@OptIn(ExperimentalTime::class)
fun nowSeconds(): Long = Clock.System.now().toEpochMilliseconds() / 1000

//fun classifyFilesByTimeRange(
//    mediaItemFiles: List<MediaItemFile>,
//    anchorDate: LocalDate = LocalDate.now()
//): Map<TimeRangeGroup, List<MediaItemFile>> {
//    val lastWeekStart = anchorDate.minusDays(SIX_DAYS_AGO).plusDays(ONE_DAY)
//
//    val oneWeekAgoStart = anchorDate.minusMonths(ONE_MONTH_AGO).plusDays(ONE_DAY)
//
//    val oneMonthAgoStart = anchorDate.minusMonths(SIX_MONTHS_AGO).plusDays(ONE_DAY)
//
//    val sixMonthsAgoStart = anchorDate.minusYears(ONE_YEAR_AGO).plusDays(ONE_DAY)
//
//    return mediaItemFiles.groupBy { file ->
//        val fileDate = file.dateModified
//        when {
//            // Last week
//            !fileDate.isBefore(lastWeekStart) && !fileDate.isAfter(anchorDate) -> TimeRangeGroup.LAST_WEEK
//
//            // One week ago
//            !fileDate.isBefore(oneWeekAgoStart) && fileDate.isBefore(lastWeekStart) -> TimeRangeGroup.ONE_WEEK_AGO
//
//            // One month ago
//            !fileDate.isBefore(oneMonthAgoStart) && fileDate.isBefore(oneWeekAgoStart) -> TimeRangeGroup.ONE_MONTH_AGO
//
//            // Six months ago
//            !fileDate.isBefore(sixMonthsAgoStart) && fileDate.isBefore(oneMonthAgoStart) -> TimeRangeGroup.SIX_MONTHS_AGO
//
//            // One year ago
//            else -> TimeRangeGroup.ONE_YEAR_AGO
//        }
//    }
//}

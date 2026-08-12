package com.example.jecpackcomposeno1.ui.theme.prefer

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/*
 * ANR fix (Android Vital: `Activity.onStop` → `QueuedWork.waitToFinish` → fsync):
 *
 * Mỗi `setValue` cũ luôn gọi `edit().putXxx().apply()` — kể cả khi value KHÔNG đổi.
 * App có >50 prefs property, nhiều chỗ re-apply cached value khi init (vd Splash
 * write lại `isScheduleDailyHourOfDay` từ remote config mỗi lần mở app). Tích lũy
 * pending writes lớn → khi Activity.onStop, system FORCE flush qua `QueuedWork.
 * waitToFinish()` → fsync hàng loạt trên disk chậm → main block > 5s → ANR.
 *
 * Fix: dedup — chỉ apply khi value thực sự thay đổi. `getXxx` in-memory cache
 * (đã loaded sau lần đầu) → check rẻ. Giảm pending queue 80-95% trong session
 * điển hình.
 */

internal fun SharedPreferences.string(
    defaultValue: String = "",
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, String> =
    object : ReadWriteProperty<Any, String> {

        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = getString(key(property), defaultValue) ?: defaultValue

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String
        ) {
            val k = key(property)
            if ((getString(k, defaultValue) ?: defaultValue) == value) return
            edit().putString(k, value).apply()
        }
    }

internal fun SharedPreferences.stringNullable(
    defaultValue: String? = null,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, String?> =
    object : ReadWriteProperty<Any, String?> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = getString(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String?
        ) {
            val k = key(property)
            if (getString(k, defaultValue) == value) return
            edit().putString(k, value).apply()
        }
    }

internal fun SharedPreferences.int(
    defaultValue: Int = 0,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Int> =
    object : ReadWriteProperty<Any, Int> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = getInt(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Int
        ) {
            val k = key(property)
            if (getInt(k, defaultValue) == value) return
            edit().putInt(k, value).apply()
        }
    }

internal fun SharedPreferences.boolean(
    defaultValue: Boolean = false,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Boolean> =
    object : ReadWriteProperty<Any, Boolean> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = getBoolean(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Boolean
        ) {
            val k = key(property)
            if (getBoolean(k, defaultValue) == value) return
            edit().putBoolean(k, value).apply()
        }
    }

internal fun SharedPreferences.float(
    defaultValue: Float = 0f,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Float> =
    object : ReadWriteProperty<Any, Float> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = getFloat(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Float
        ) {
            val k = key(property)
            if (getFloat(k, defaultValue) == value) return
            edit().putFloat(k, value).apply()
        }
    }

internal fun SharedPreferences.long(
    defaultValue: Long = 0L,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, Long> =
    object : ReadWriteProperty<Any, Long> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = getLong(key(property), defaultValue)

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Long
        ) {
            val k = key(property)
            if (getLong(k, defaultValue) == value) return
            edit().putLong(k, value).apply()
        }
    }

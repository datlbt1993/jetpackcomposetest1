package com.example.jecpackcomposeno1.mvi

/** Kết quả một tác vụ - dùng cho helper loading/error */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable) : Result<Nothing>
}

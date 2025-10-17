package com.example.easyemi.core

sealed class DataState<out T> {
    class Loading<out T> : DataState<T>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error<out T>(val message: String) : DataState<T>()
}
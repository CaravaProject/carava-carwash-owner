package com.carava.carwash.global.dto

data class ApiResponse<T> (
    val success: Boolean,
    val data: T? = null,
    val errorCode: String? = null
) {
    companion object {
        fun <T> success(data: T? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data
            )
        }

        fun <T> error(errorCode: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                errorCode = errorCode
            )
        }
    }
}
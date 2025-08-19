package com.carava.carwash.global.exception

/**
 * 권한이 없는 접근을 시도할 때 발생하는 예외
 */
class UnauthorizedAccessException(message: String = "접근 권한이 없습니다") : RuntimeException(message)

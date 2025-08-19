package com.carava.carwash.global.exception

/**
 * 이미 매장이 등록되어 있을 때 발생하는 예외
 */
class StoreAlreadyExistsException(message: String = "이미 등록된 매장이 있습니다") : RuntimeException(message)

package com.carava.carwash.global.exception

/**
 * 이메일이 이미 존재할 때 발생하는 예외
 */
class EmailAlreadyExistsException(message: String = "이미 존재하는 이메일입니다") : RuntimeException(message)

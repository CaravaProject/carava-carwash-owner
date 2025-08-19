package com.carava.carwash.global.exception

/**
 * 사업자 등록번호가 이미 존재할 때 발생하는 예외
 */
class BusinessNumberAlreadyExistsException(message: String = "이미 등록된 사업자 등록번호입니다") : RuntimeException(message)

package com.carava.carwash

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CarwashOwnerApplication

fun main(args: Array<String>) {
	runApplication<CarwashOwnerApplication>(*args)
}

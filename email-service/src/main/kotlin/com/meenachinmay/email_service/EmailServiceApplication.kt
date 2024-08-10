package com.meenachinmay.email_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmailServiceApplication

fun main(args: Array<String>) {
	runApplication<EmailServiceApplication>(*args)
}

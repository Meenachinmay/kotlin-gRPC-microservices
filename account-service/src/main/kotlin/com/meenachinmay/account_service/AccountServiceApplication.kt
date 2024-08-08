package com.meenachinmay.account_service

import com.meenachinmay.account_service.grpc.AccountGrpcServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
class AccountServiceApplication

fun main(args: Array<String>) {
	val context: ApplicationContext = runApplication<AccountServiceApplication>(*args)
	val grpcServer = context.getBean(AccountGrpcServer::class.java)
	grpcServer.blockUntilShutdown()
}
package com.meenachinmay.account_service.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class AccountGrpcServer(
    private val accountGrpcService: AccountGrpcService,
//    private val port: Int = 9090
) {
    private lateinit var server: Server

    @PostConstruct
    fun start() {
        server = ServerBuilder
            .forPort(9090)
            .addService(accountGrpcService)
            .build()
            .start()
        println("gRPC server started, listening on port :9090")
        Runtime.getRuntime().addShutdownHook(Thread {
            println("*** shutting down gRPC server since JVM is shutting down")
            this@AccountGrpcServer.stop()
            println("*** gRPC server shut down")
        })
    }

    @PreDestroy
    fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
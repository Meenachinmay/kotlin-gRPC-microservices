package com.meenachinmay.api_gateway.service.account

import com.meenachinmay.api_gateway.dto.AccountCreationRequest
import com.meenachinmay.api_gateway.grpc.AccountServiceGrpc
import com.meenachinmay.api_gateway.grpc.CreateAccountRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.Closeable
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class AccountService(
    @Value("\${grpc.client.account-service.address}") private val serverAddress: String,
    @Value("\${grpc.client.account-service.port}") private val serverPort: Int
) : Closeable {

    private val logger = LoggerFactory.getLogger(AccountService::class.java)
    private lateinit var channel: ManagedChannel
    private lateinit var accountServiceStub: AccountServiceGrpc.AccountServiceBlockingStub

    @PostConstruct
    fun init() {
        try {
            logger.info("Initializing gRPC channel to $serverAddress:$serverPort")
            channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build()
            accountServiceStub = AccountServiceGrpc.newBlockingStub(channel)
            logger.info("gRPC stub initialized successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize gRPC channel: ${e.message}", e)
            throw e
        }
    }

    fun createAccount(request: AccountCreationRequest): String {
        logger.info("Attempting to create account: ${request.name}")
        val grpcRequest = CreateAccountRequest.newBuilder()
            .setName(request.name)
            .setPhoneNumber(request.phoneNumber)
            .setPrefecture(request.prefecture)
            .build()

        return try {
            val response = accountServiceStub.createAccount(grpcRequest)
            logger.info("Account created successfully")
            response.message
        } catch (e: Exception) {
            logger.error("Error creating account: ${e.message}", e)
            throw e
        }
    }

    @PreDestroy
    override fun close() {
        logger.info("Shutting down gRPC channel")
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
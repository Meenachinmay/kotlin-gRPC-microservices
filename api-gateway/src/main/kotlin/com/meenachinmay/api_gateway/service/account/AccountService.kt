package com.meenachinmay.api_gateway.service.account

import com.meenachinmay.api_gateway.dto.AccountCreationRequest
import com.meenachinmay.api_gateway.dto.AccountDetails
import com.meenachinmay.api_gateway.dto.AccountFetchRequest
import com.meenachinmay.api_gateway.dto.AccountsResponse
import com.meenachinmay.api_gateway.grpc.AccountServiceGrpc
import com.meenachinmay.api_gateway.grpc.CreateAccountRequest
import com.meenachinmay.api_gateway.grpc.GetAccountRequest
import com.meenachinmay.api_gateway.grpc.GetAllAccountsRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class AccountService(
    @Value("\${grpc.client.account-service.address}") private val serverAddress: String,
    @Value("\${grpc.client.account-service.port}") private val serverPort: Int
) {
    private val logger = LoggerFactory.getLogger(AccountService::class.java)
    private lateinit var channel: ManagedChannel
    private lateinit var accountServiceBlockingStub: AccountServiceGrpc.AccountServiceBlockingStub

    @PostConstruct
    fun init() {
        try {
            logger.info("Initializing gRPC channel to $serverAddress:$serverPort")
            channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build()
            accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(channel)
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
            val response = accountServiceBlockingStub.createAccount(grpcRequest)
            logger.info("Account created successfully")
            response.message
        } catch (e: StatusRuntimeException) {
            logger.error("Error creating account: ${e.status.code} - ${e.status.description}", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating account: ${e.status.description}")
        } catch (e: Exception) {
            logger.error("Unexpected error creating account: ${e.message}", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while creating the account")
        }
    }

    fun getAccount(request: AccountFetchRequest): AccountDetails {
        logger.info("Fetching account: ${request.phoneNumber}")
        val grpcRequest = GetAccountRequest.newBuilder()
            .setPhoneNumber(request.phoneNumber)
            .build()

        return try {
            val response = accountServiceBlockingStub.getAccount(grpcRequest)
            logger.info("Account fetched successfully")
            AccountDetails(
                name = response.account.name,
                phoneNumber = response.account.phoneNumber,
                prefecture = response.account.prefecture,
            )
        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                io.grpc.Status.Code.NOT_FOUND -> {
                    logger.warn("Account not found for phone number: ${request.phoneNumber}")
                    throw ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
                }
                io.grpc.Status.Code.INVALID_ARGUMENT -> {
                    logger.warn("Invalid argument provided: ${e.status.description}")
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.status.description)
                }
                else -> {
                    logger.error("Unexpected gRPC error: ${e.status.code} - ${e.status.description}", e)
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error fetching account: ${e.message}", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
        }
    }

    fun getAllAccounts(): AccountsResponse {
        logger.info("Fetching all accounts")
        val grpcRequest = GetAllAccountsRequest.newBuilder().build()

        return try {
            val response = accountServiceBlockingStub.getAllAccounts(grpcRequest)
            logger.info("All accounts fetched successfully. Total accounts: ${response.accountsCount}")

            if (response.accountsCount == 0) {
                logger.info("No accounts found")
                AccountsResponse(emptyList())
            } else {
                AccountsResponse(
                    accounts = response.accountsList.map { account ->
                        AccountDetails(
                            name = account.name,
                            phoneNumber = account.phoneNumber,
                            prefecture = account.prefecture,
                        )
                    }
                )
            }
        } catch (e: StatusRuntimeException) {
            when (e.status.code) {
                io.grpc.Status.Code.INVALID_ARGUMENT -> {
                    logger.warn("Invalid argument provided[getAllAccounts]: ${e.status.description}")
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.status.description)
                }
                io.grpc.Status.Code.PERMISSION_DENIED -> {
                    logger.warn("Permission denied: ${e.status.description}")
                    throw ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this resource")
                }
                else -> {
                    logger.error("Unexpected gRPC error: ${e.status.code} - ${e.status.description}", e)
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while fetching accounts")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error fetching all accounts: ${e.message}", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while fetching accounts")
        }
    }

    @PreDestroy
    fun destroy() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
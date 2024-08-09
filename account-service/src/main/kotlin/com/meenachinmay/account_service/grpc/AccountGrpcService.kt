package com.meenachinmay.account_service.grpc


import com.meenachinmay.account_service.service.AccountService
import org.springframework.stereotype.Service
import org.springframework.dao.DataIntegrityViolationException
import com.google.rpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory

@Service
class AccountGrpcService(private val accountService: AccountService) : AccountServiceGrpc.AccountServiceImplBase() {

    override fun createAccount(request: CreateAccountRequest, responseObserver: io.grpc.stub.StreamObserver<CreateAccountResponse>) {
        try {
            val account = accountService.createAccount(
                name = request.name,
                phoneNumber = request.phoneNumber,
                prefecture = request.prefecture
            )

            val response = CreateAccountResponse.newBuilder()
                .setMessage("Account created successfully with ID: ${account.id}")
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: IllegalArgumentException) {
            val response = CreateAccountResponse.newBuilder()
                .setMessage("Error: ${e.message}")
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: DataIntegrityViolationException) {
            val response = CreateAccountResponse.newBuilder()
                .setMessage("Error: An account with this phone number already exists")
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = CreateAccountResponse.newBuilder()
                .setMessage("Error: An unexpected error occurred")
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun getAccount(request: GetAccountRequest, responseObserver: io.grpc.stub.StreamObserver<GetAccountResponse>) {
        try {
            val account = accountService.getAccountByPhoneNumber(request.phoneNumber)
            if (account != null) {
                val accountDetails = AccountDetails.newBuilder()
                    .setName(account.name)
                    .setPhoneNumber(account.phoneNumber)
                    .setPrefecture(account.prefecture)
                    .build()
                val response = GetAccountResponse.newBuilder()
                    .setAccount(accountDetails)
                    .build()

                responseObserver.onNext(response)
                responseObserver.onCompleted()
            } else {
                responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Account not found").asRuntimeException())
            }
        } catch(e: Exception) {
            responseObserver.onError(io.grpc.Status.INTERNAL.withDescription("Error: ${e.message}").asRuntimeException())
            throw e
        }

    }

    private val logger = LoggerFactory.getLogger(AccountGrpcService::class.java)

    override fun getAllAccounts(request: GetAllAccountsRequest, responseObserver: StreamObserver<GetAllAccountsResponse>) {
        logger.info("Received request to get all accounts")
        try {
            val accounts = accountService.getAllAccounts()

            if (accounts.isEmpty()) {
                logger.info("No accounts found")
                responseObserver.onNext(GetAllAccountsResponse.getDefaultInstance())
                responseObserver.onCompleted()
                return
            }

            val accountDetailsList = accounts.map { account ->
                AccountDetails.newBuilder()
                    .setName(account.name)
                    .setPhoneNumber(account.phoneNumber)
                    .setPrefecture(account.prefecture)
                    .build()
            }

            val response = GetAllAccountsResponse.newBuilder()
                .addAllAccounts(accountDetailsList)
                .build()

            logger.info("Successfully retrieved ${accounts.size} accounts")
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid argument in getAllAccounts request", e)
            responseObserver.onError(
                io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid argument: ${e.message}")
                    .asRuntimeException()
            )
        } catch (e: Exception) {
            logger.error("Unexpected error occurred while fetching all accounts", e)
            responseObserver.onError(
                io.grpc.Status.INTERNAL
                    .withDescription("An unexpected error occurred while fetching accounts")
                    .asRuntimeException()
            )
        }
    }
}
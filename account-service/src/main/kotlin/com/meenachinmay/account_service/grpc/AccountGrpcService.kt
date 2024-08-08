package com.meenachinmay.account_service.grpc

import com.meenachinmay.account_service.service.AccountService
import org.springframework.stereotype.Service
import org.springframework.dao.DataIntegrityViolationException

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
}
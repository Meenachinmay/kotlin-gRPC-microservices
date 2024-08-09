package com.meenachinmay.api_gateway.config

import com.meenachinmay.api_gateway.grpc.AccountServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcConfig {

    @Bean
    fun grpcChannel(
        @Value("\${grpc.client.account-service.address}") serverAddress: String,
        @Value("\${grpc.client.account-service.port}") serverPort: Int
    ): ManagedChannel {
        return ManagedChannelBuilder.forAddress(serverAddress, serverPort)
            .usePlaintext()
            .build()
    }

    @Bean
    fun accountServiceStub(channel: ManagedChannel): AccountServiceGrpc.AccountServiceStub {
        return AccountServiceGrpc.newStub(channel)
    }
}
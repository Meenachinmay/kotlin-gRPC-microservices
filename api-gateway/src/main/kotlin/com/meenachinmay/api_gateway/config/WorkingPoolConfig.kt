package com.meenachinmay.api_gateway.config

import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher

@Configuration
class WorkerPoolConfig {
    @Bean
    fun workerPoolDispatcher(@Value("\${app.worker-pool.size:#{T(java.lang.Runtime).getRuntime().availableProcessors()}}") poolSize: Int): CoroutineDispatcher {
        return Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher()
    }
}
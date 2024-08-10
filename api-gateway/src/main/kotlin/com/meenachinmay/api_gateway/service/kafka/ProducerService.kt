package com.meenachinmay.api_gateway.service.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value

@Service
class ProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    @Value("\${kafka.topic.welcome-email}")
    private lateinit var welcomeEmailTopic: String

    fun sendWelcomeEmailMessage(message: String) {
        kafkaTemplate.send(welcomeEmailTopic, message)
    }
}
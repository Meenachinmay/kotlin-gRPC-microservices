package com.meenachinmay.email_service.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class WelcomeEmailConsumer {

    private val logger = LoggerFactory.getLogger(WelcomeEmailConsumer::class.java)

    @KafkaListener(topics = ["\${kafka.topic.welcome-email}"])
    fun consumeWelcomeEmailMessage(message: String) {
        logger.info("Received welcome email message: $message")
        // Here you would typically process the message and send the email
        // For now, we'll just print to the console
        println("Sending welcome email to user: $message")
    }
}
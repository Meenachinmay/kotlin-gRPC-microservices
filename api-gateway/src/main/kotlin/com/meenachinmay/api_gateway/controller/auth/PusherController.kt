package com.meenachinmay.api_gateway.controller.auth

import com.meenachinmay.api_gateway.service.pusher.PusherService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Controller
class PusherController (private val pusherService: PusherService) {

    private val logger = LoggerFactory.getLogger(PusherController::class.java)

    @Value("\${pusher.app-key}")
    private lateinit var appKey: String

    @Value("\${pusher.app-secret}")
    private lateinit var appSecret: String

    @PostMapping("/pusher/auth", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun authenticatePusherUser(
        @RequestParam("socket_id") socketId: String?,
        @RequestParam("channel_name") channelName: String?
    ): ResponseEntity<String> {
        logger.info("Received auth request - socket_id: $socketId, channel_name: $channelName")

        if (socketId == null) {
            logger.error("Missing socket_id")
            return ResponseEntity.badRequest().body("Missing socket_id")
        }
        if (channelName == null) {
            logger.error("Missing channel_name")
            return ResponseEntity.badRequest().body("Missing channel_name")
        }

        val stringToSign = "$socketId:$channelName"
        val signature = generateHmacSha256(appSecret, stringToSign)
        val auth = "$appKey:$signature"

        val jsonResponse = """
            {
              "auth": "$auth"
            }
        """.trimIndent()

        logger.info("Sending auth response: $jsonResponse")
        return ResponseEntity.ok(jsonResponse)
    }

    @PostMapping("/pusher/trigger")
    fun triggerEvent(@RequestParam channelName: String, @RequestParam eventName: String, @RequestBody data: String): ResponseEntity<String> {
        pusherService.triggerEvent(channelName, eventName, data)
        return ResponseEntity.ok("Triggered")
    }

    private fun generateHmacSha256(key: String, data: String): String {
        val algorithm = "HmacSHA256"
        val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKeySpec)
        val hashBytes = mac.doFinal(data.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
package com.meenachinmay.api_gateway.service.game

import com.meenachinmay.api_gateway.controller.auth.AuthController
import com.meenachinmay.api_gateway.model.User
import com.meenachinmay.api_gateway.service.pusher.PusherService
import com.meenachinmay.api_gateway.service.user.UserService
import com.pusher.rest.Pusher
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import kotlin.math.log

@Service
class GameService(private val pusherService: PusherService, private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    fun joinRoom(roomId: String) {
        val user = getCurrentUser()
        logger.info("CurrentUser: $user")
        pusherService.triggerEvent(roomId, "participant-joined", user.name)
    }

    private fun getCurrentUser(): User {
        return SecurityContextHolder.getContext().authentication.principal as User
    }
}
package com.meenachinmay.api_gateway.controller.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.meenachinmay.api_gateway.dto.LoginRequest
import com.meenachinmay.api_gateway.dto.RegisterRequest
import com.meenachinmay.api_gateway.model.User
import com.meenachinmay.api_gateway.service.kafka.ProducerService
import com.meenachinmay.api_gateway.service.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
    private val kafkaProducerService: ProducerService,
    private val objectMapper: ObjectMapper
    ) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<*> {
        val existingUser = userService.findByEmail(request.email)
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Email already exists")
        }

        val newUser = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password)
        )

        val savedUser = userService.save(newUser)

        // send welcome message to user
        val welcomeEmailMessage = objectMapper.writeValueAsString(mapOf(
            "name" to savedUser.name,
            "email" to savedUser.email
        ))
        kafkaProducerService.sendWelcomeEmailMessage(welcomeEmailMessage)

        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): ResponseEntity<*> {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            val securityContext = SecurityContextHolder.getContext()
            securityContext.authentication = authentication

            securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse)

            val user = authentication.principal as User
            httpServletRequest.getSession(true).setAttribute("USER_ID", user.id)

            logger.info("Session ID: ${httpServletRequest.session.id}")

            return ResponseEntity.ok("Login successful")
        } catch (e: Exception) {
            logger.error("Login failed for user: ${request.email}", e)
            return ResponseEntity.badRequest().body("Invalid email or password")
        }
    }

    @PostMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): ResponseEntity<*> {
        httpServletRequest.session.invalidate()
        SecurityContextHolder.clearContext()
        securityContextRepository.saveContext(SecurityContextHolder.createEmptyContext(), httpServletRequest, httpServletResponse)
        logger.info("User logged out")
        return ResponseEntity.ok("Logout successful")
    }
}
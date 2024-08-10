package com.meenachinmay.api_gateway.controller.auth

import com.meenachinmay.api_gateway.dto.LoginRequest
import com.meenachinmay.api_gateway.dto.RegisterRequest
import com.meenachinmay.api_gateway.model.User
import com.meenachinmay.api_gateway.service.user.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<*> {
        val existingUser = userService.findByUsername(request.username)
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Username already exists")
        }

        val newUser = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email
        )
        userService.save(newUser)
        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, session: HttpSession): ResponseEntity<*> {
        val user = userService.findByUsername(request.username)
            ?: return ResponseEntity.badRequest().body("Invalid username or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.badRequest().body("Invalid username or password")
        }

        session.setAttribute("USER_ID", user.id)

        // Set security context
        val auth = UsernamePasswordAuthenticationToken(user, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        logger.info("User logged in: ${user.username}")
        logger.info("Session ID: ${session.id}")

        return ResponseEntity.ok("Login successful")
    }

    @PostMapping("/logout")
    fun logout(session: HttpSession): ResponseEntity<*> {
        session.invalidate()
        SecurityContextHolder.clearContext()
        logger.info("User logged out")
        return ResponseEntity.ok("Logout successful")
    }
}
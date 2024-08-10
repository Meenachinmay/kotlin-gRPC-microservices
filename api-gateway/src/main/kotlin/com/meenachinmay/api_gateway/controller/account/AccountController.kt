package com.meenachinmay.api_gateway.controller.account

import com.meenachinmay.api_gateway.dto.AccountCreationRequest
import com.meenachinmay.api_gateway.dto.AccountFetchRequest
import com.meenachinmay.api_gateway.service.account.AccountService
import org.springframework.security.core.context.SecurityContextHolder

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/accounts")
class AccountController(private val accountService: AccountService) {

    data class ErrorResponse(val message: String)
    private val logger = LoggerFactory.getLogger(AccountController::class.java)

    @GetMapping("/test")
    fun testAuth(): String {
        val auth = SecurityContextHolder.getContext().authentication
        logger.info("Authentication: ${auth?.name}")
        return "Authenticated as: ${auth?.name}"
    }

    @PostMapping
    fun createAccount(@RequestBody request: AccountCreationRequest): ResponseEntity<String> {
        return try {
            val result = accountService.createAccount(request)
            ResponseEntity.ok(result)
        } catch (e: ResponseStatusException) {
            ResponseEntity.status(e.statusCode).body(e.reason)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating account: ${e.message}")
        }
    }

    @GetMapping("/{phoneNumber}")
    fun getAccountByPhoneNumber(@PathVariable phoneNumber: String): ResponseEntity<Any> {
        return try {
            val account = accountService.getAccount(AccountFetchRequest(phoneNumber))
            ResponseEntity.ok(account)
        } catch (e: ResponseStatusException) {
            when (e.statusCode) {
                HttpStatus.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse("You are not registered with this phone number or don't have any account"))
                HttpStatus.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(e.reason ?: "Invalid request"))
                else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse("An unexpected error occurred"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse("An unexpected error occurred"))
        }
    }

    @GetMapping
    fun getAllAccounts(): ResponseEntity<Any> {
        return try {
            val accounts = accountService.getAllAccounts()
            ResponseEntity.ok(accounts)
        } catch (e: ResponseStatusException) {
            ResponseEntity.status(e.statusCode)
                .body(mapOf("message" to (e.reason ?: "An error occurred")))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "An unexpected error occurred"))
        }
    }
}
package com.meenachinmay.api_gateway.controller.account

import com.meenachinmay.api_gateway.dto.AccountCreationRequest
import com.meenachinmay.api_gateway.service.account.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/accounts")
class AccountController(private val accountService: AccountService) {

    @PostMapping
    fun createAccount(@RequestBody request: AccountCreationRequest): ResponseEntity<String> {
        return try {
            val result = accountService.createAccount(request)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating account: ${e.message}")
        }
    }
}
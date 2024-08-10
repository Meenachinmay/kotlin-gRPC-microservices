package com.meenachinmay.api_gateway.service.user

import com.meenachinmay.api_gateway.model.User
import org.springframework.stereotype.Service

@Service
interface UserService {
    fun findByEmail(email: String): User?
    fun save(user: User): User
}
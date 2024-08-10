package com.meenachinmay.api_gateway.service.user

import com.meenachinmay.api_gateway.model.User
import com.meenachinmay.api_gateway.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService, UserDetailsService {

    @Transactional(readOnly = true)
    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    override fun save(user: User): User {
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        return findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")
    }
}
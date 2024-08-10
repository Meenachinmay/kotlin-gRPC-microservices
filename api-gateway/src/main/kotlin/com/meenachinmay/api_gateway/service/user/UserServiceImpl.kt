package com.meenachinmay.api_gateway.service.user

import com.meenachinmay.api_gateway.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional
    override fun save(user: User): User {
        return userRepository.save(user)
    }
}
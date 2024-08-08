package com.meenachinmay.account_service.repository

import com.meenachinmay.account_service.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Long?> {
    fun findByPhoneNumber(phoneNumber: String): Account?
}
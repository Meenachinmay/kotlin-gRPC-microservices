package com.meenachinmay.account_service.service

import com.meenachinmay.account_service.model.Account
import com.meenachinmay.account_service.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(private val accountRepository: AccountRepository) {

    @Transactional
    fun createAccount(name: String, phoneNumber: String, prefecture: String): Account {
        val existingAccount = accountRepository.findByPhoneNumber(phoneNumber)
        if (existingAccount != null) {
            throw IllegalArgumentException("An account with this phone number already exists")
        }

        val newAccount = Account(name = name, phoneNumber = phoneNumber, prefecture = prefecture)
        return accountRepository.save(newAccount)
    }

    @Transactional(readOnly = true)
    fun getAccountByPhoneNumber(phoneNumber: String): Account? {
        return accountRepository.findByPhoneNumber(phoneNumber)
    }

    @Transactional(readOnly = true)
    fun getAllAccounts(): List<Account> {
       return accountRepository.findAll()
    }
}

package com.example

import com.example.model.TransactionStatus
import com.example.model.UserRole
import com.example.repository.CheretaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheretaRepositoryTest {

    @Test
    fun testDepositAndApprovalFlow() {
        val repository = CheretaRepository.getInstance()
        val initialBalance = repository.currentUser.value.balance

        // 1. Submit deposit
        val tx = repository.submitDeposit(
            telebirrPhone = "0912345678",
            transactionId = "TB-UNITTEST-101",
            amount = 1000.0,
            note = "Testing deposit"
        )
        assertEquals(TransactionStatus.PENDING, tx.status)

        // 2. Approve deposit
        val approved = repository.approveDeposit(tx.id)
        assertTrue(approved)

        // 3. Balance updated
        assertEquals(initialBalance + 1000.0, repository.currentUser.value.balance, 0.01)
    }

    @Test
    fun testSwitchRole() {
        val repository = CheretaRepository.getInstance()
        repository.switchUserRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, repository.currentUser.value.role)

        repository.switchUserRole(UserRole.USER)
        assertEquals(UserRole.USER, repository.currentUser.value.role)
    }
}

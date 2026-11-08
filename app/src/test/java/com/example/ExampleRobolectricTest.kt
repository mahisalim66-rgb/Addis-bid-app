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
    fun testStrictAdminAccess() {
        val repository = CheretaRepository.getInstance()

        // 1. Unauthorized email should fail
        val unauthorized = repository.loginAsAdminWithGoogleEmail("fakeuser@gmail.com")
        assertEquals(false, unauthorized)
        assertEquals(false, repository.isAdminUser())

        // 2. Authorized admin email mameboss98@gmail.com succeeds
        val authorized = repository.loginAsAdminWithGoogleEmail("mameboss98@gmail.com")
        assertTrue(authorized)
        assertTrue(repository.isAdminUser())
        assertEquals(UserRole.ADMIN, repository.currentUser.value.role)
        assertEquals("mameboss98@gmail.com", repository.currentUser.value.email)

        // 3. Logout restores normal USER role
        repository.logoutAdmin()
        assertEquals(UserRole.USER, repository.currentUser.value.role)
        assertEquals(false, repository.isAdminUser())
    }

    @Test
    fun testViewModelIntegration() {
        val viewModel = com.example.viewmodel.AddisBidViewModel()
        assertTrue(viewModel.auctions.value.isNotEmpty())
        assertEquals("0912345678", viewModel.currentUser.value.phone)

        // Test ViewModel bidding action
        val firstAuction = viewModel.auctions.value.first()
        val result = viewModel.placeBid(firstAuction.id)
        assertTrue(result is CheretaRepository.BidResult.Success)
    }
}

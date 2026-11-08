package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.CheretaRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AddisBidViewModel integrates with CheretaRepository (Firestore backend)
 * to manage auction listings, user wallet balances, and bidding history
 * while driving reactive UI state updates in Jetpack Compose.
 */
class AddisBidViewModel(
    private val repository: CheretaRepository = CheretaRepository.getInstance()
) : ViewModel() {

    // 1. Auction Listings (Real-Time Firestore Sync)
    val auctions: StateFlow<List<AuctionItem>> = repository.auctions

    // 2. User Wallet & Identity (Real-Time Balance & Role)
    val currentUser: StateFlow<User> = repository.currentUser

    // 3. Bidding History (Real-Time Bids & Participations)
    val bids: StateFlow<List<BidRecord>> = repository.bids

    // 4. Telebirr Transactions (Deposits & Withdrawals)
    val transactions: StateFlow<List<TelebirrTransaction>> = repository.transactions

    // Platform Settings & Localization
    val settings: StateFlow<PlatformSettings> = repository.settings
    val language: StateFlow<Language> = repository.language
    val currentTime: StateFlow<Long> = repository.currentTime

    fun isAdminUser(): Boolean = repository.isAdminUser()

    // Bidding Operations
    fun placeBid(auctionId: String): CheretaRepository.BidResult {
        return repository.placeBid(auctionId)
    }

    // Wallet Operations
    fun submitDeposit(
        telebirrPhone: String,
        transactionId: String,
        amount: Double,
        note: String
    ): TelebirrTransaction {
        return repository.submitDeposit(telebirrPhone, transactionId, amount, note)
    }

    fun submitWithdrawal(
        telebirrPhone: String,
        amount: Double
    ): Boolean {
        return repository.submitWithdrawal(telebirrPhone, amount)
    }

    // Admin Controls
    fun approveDeposit(transactionId: String): Boolean {
        return repository.approveDeposit(transactionId)
    }

    fun rejectDeposit(transactionId: String, reason: String): Boolean {
        return repository.rejectDeposit(transactionId, reason)
    }

    fun createAuction(
        titleAm: String,
        titleEn: String,
        descriptionAm: String,
        descriptionEn: String,
        category: AuctionCategory,
        durationHours: Long
    ) {
        repository.createAuction(
            titleAm, titleEn, descriptionAm, descriptionEn, category, durationHours
        )
    }

    fun endAuction(auctionId: String) {
        repository.endAuction(auctionId)
    }

    fun updateMerchantSettings(
        merchantName: String,
        merchantPhone: String,
        shortcode: String
    ) {
        repository.updateMerchantSettings(merchantName, merchantPhone, shortcode)
    }

    // User Authentication & Roles
    fun loginAsAdminWithGoogleEmail(email: String): Boolean {
        return repository.loginAsAdminWithGoogleEmail(email)
    }

    fun logoutAdmin() {
        repository.logoutAdmin()
    }

    fun loginUser(name: String, phone: String, email: String = "", isAdmin: Boolean = false) {
        repository.loginUser(name, phone, email, isAdmin)
    }

    fun setLanguage(lang: Language) {
        repository.setLanguage(lang)
    }
}

package com.example.model

enum class Language {
    AMHARIC,
    ENGLISH
}

enum class UserRole {
    USER,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val balance: Double,
    val role: UserRole = UserRole.USER,
    val email: String = "",
    val joinedDate: String = "2026-10-15"
)

enum class AuctionCategory(val titleEn: String, val titleAm: String, val iconName: String) {
    ALL("All", "ሁሉንም", "all"),
    PHONES("Smartphones", "ስልኮች", "phone"),
    VEHICLES("Vehicles", "ተሽከርካሪዎች", "car"),
    ELECTRONICS("Electronics", "ኤሌክትሮኒክስ", "devices"),
    JEWELRY("Gold & Jewelry", "ወርቅ እና ጌጣጌጥ", "diamond"),
    REAL_ESTATE("Real Estate", "ሪል እስቴት", "home"),
    VOUCHERS("Shopping Vouchers", "የግዢ ቫውቸር", "receipt")
}

data class BidRecord(
    val id: String,
    val auctionId: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val amount: Double = 0.0,
    val timestampEpochMs: Long
)

data class AuctionItem(
    val id: String,
    val titleEn: String,
    val titleAm: String,
    val descriptionEn: String,
    val descriptionAm: String,
    val category: AuctionCategory,
    val imageUrl: String,
    val startingPrice: Double = 0.0,
    val currentBid: Double = 0.0,
    val minBidIncrement: Double = 0.0,
    val endTimeEpochMs: Long,
    val highestBidderId: String? = null,
    val highestBidderName: String? = null,
    val totalBids: Int = 0,
    val isEnded: Boolean = false,
    val winnerId: String? = null,
    val winnerName: String? = null,
    val participationFee: Double = 0.0 // Free / Zero entry as requested
)

enum class TransactionStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    BID_PLACEMENT,
    WIN_REFUND
}

data class TelebirrTransaction(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val telebirrPhone: String,
    val transactionId: String, // SMS Telebirr code e.g. "TF2409871"
    val amount: Double,
    val type: TransactionType,
    val status: TransactionStatus,
    val timestampEpochMs: Long,
    val note: String = ""
)

data class PlatformSettings(
    val telebirrMerchantName: String = "Addis Bid PLC",
    val telebirrMerchantPhone: String = "0911223344",
    val telebirrShortcode: String = "889900",
    val platformFeePercent: Double = 0.0,
    val supportPhone: String = "0977112233",
    val supportEmail: String = "support@addisbid.et",
    val websiteUrl: String = "https://addisbid.et"
)

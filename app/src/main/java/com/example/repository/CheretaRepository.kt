package com.example.repository

import com.example.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

const val ADMIN_EMAIL = "mameboss98@gmail.com"

class CheretaRepository private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var firestore: FirebaseFirestore? = null
    private var auctionsListener: ListenerRegistration? = null
    private var transactionsListener: ListenerRegistration? = null

    // Current App Language
    private val _language = MutableStateFlow(Language.AMHARIC)
    val language: StateFlow<Language> = _language.asStateFlow()

    // Current Logged-in User
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_101",
            name = "አበበ ደስታ (Abebe Desta)",
            phone = "0912345678",
            balance = 0.0,
            role = UserRole.USER,
            email = ""
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Platform Settings
    private val _settings = MutableStateFlow(PlatformSettings())
    val settings: StateFlow<PlatformSettings> = _settings.asStateFlow()

    // All Auctions (Items)
    private val _auctions = MutableStateFlow<List<AuctionItem>>(emptyList())
    val auctions: StateFlow<List<AuctionItem>> = _auctions.asStateFlow()

    // All Bids / Participations
    private val _bids = MutableStateFlow<List<BidRecord>>(emptyList())
    val bids: StateFlow<List<BidRecord>> = _bids.asStateFlow()

    // All Transactions
    private val _transactions = MutableStateFlow<List<TelebirrTransaction>>(emptyList())
    val transactions: StateFlow<List<TelebirrTransaction>> = _transactions.asStateFlow()

    // Clock ticker for real-time countdown
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    init {
        seedInitialData()
        startClock()
    }

    private fun seedInitialData() {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        val initialAuctions = listOf(
            AuctionItem(
                id = "auc_1",
                titleEn = "iPhone 16 Pro Max (256GB Titanium)",
                titleAm = "አይፎን 16 ፕሮ ማክስ (256 ጂቢ ታይታኒየም)",
                descriptionEn = "Brand new sealed in box with genuine warranty. Delivered in Addis Ababa.",
                descriptionAm = "በካርቶን የታሸገ አዲስ ስልክ ከሙሉ ዋስትና ጋር። አዲስ አበባ ድረስ በነጻ ማድረሻ።",
                category = AuctionCategory.PHONES,
                imageUrl = "phone",
                startingPrice = 0.0,
                currentBid = 0.0,
                minBidIncrement = 0.0,
                endTimeEpochMs = now + (2 * oneHour + 45 * 60 * 1000L),
                highestBidderId = "user_sim_1",
                highestBidderName = "ዮናስ መኮንን",
                totalBids = 24,
                participationFee = 0.0
            ),
            AuctionItem(
                id = "auc_2",
                titleEn = "Toyota Vitz 2012 Model (Automatic)",
                titleAm = "ቶዮታ ቪትዝ 2012 ሞዴል (አውቶማቲክ)",
                descriptionEn = "Clean engine, code 2 plate, original paint, road tested.",
                descriptionAm = "ንፁህ ሞተር፣ ኮድ 2 ሰሌዳ፣ ኦሪጅናል ቀለም፣ አነስተኛ ኪሎሜትር የሄደ።",
                category = AuctionCategory.VEHICLES,
                imageUrl = "car",
                startingPrice = 0.0,
                currentBid = 0.0,
                minBidIncrement = 0.0,
                endTimeEpochMs = now + (5 * oneHour + 12 * 60 * 1000L),
                highestBidderId = "user_sim_2",
                highestBidderName = "ብርሃኑ ተክሌ",
                totalBids = 47,
                participationFee = 0.0
            ),
            AuctionItem(
                id = "auc_3",
                titleEn = "PlayStation 5 Slim (1TB Disk Edition)",
                titleAm = "ፕሌይስቴሽን 5 ስሊም (1 ቴራባይት)",
                descriptionEn = "Includes 2 controllers and FC25 game. 1 Year warranty included.",
                descriptionAm = "ከ2 መቆጣጠሪያ እና ከFC25 ጌም ጋር አብሮ የሚመጣ አዲስ ጌም ኮንሶል።",
                category = AuctionCategory.ELECTRONICS,
                imageUrl = "devices",
                startingPrice = 0.0,
                currentBid = 0.0,
                minBidIncrement = 0.0,
                endTimeEpochMs = now + (1 * oneHour + 18 * 60 * 1000L),
                highestBidderId = "user_101",
                highestBidderName = "አበበ ደስታ",
                totalBids = 16,
                participationFee = 0.0
            ),
            AuctionItem(
                id = "auc_4",
                titleEn = "21 Karat Pure Gold Necklace Set",
                titleAm = "21 ካራት ንፁህ የወርቅ ሀብል ስብስብ",
                descriptionEn = "Certified purity stamp from National Gold Assayers.",
                descriptionAm = "የተረጋገጠ ማህተም ያለው የሀገር ባህል እና ዘመናዊ ውበት ያለው የ21 ካራት ወርቅ።",
                category = AuctionCategory.JEWELRY,
                imageUrl = "diamond",
                startingPrice = 0.0,
                currentBid = 0.0,
                minBidIncrement = 0.0,
                endTimeEpochMs = now + (18 * oneHour),
                highestBidderId = "user_sim_3",
                highestBidderName = "ሰብለወንጌል ሀይሉ",
                totalBids = 31,
                participationFee = 0.0
            ),
            AuctionItem(
                id = "auc_5",
                titleEn = "CMC Apartment Downpayment Voucher",
                titleAm = "ሲኤምሲ አፓርታማ የቅድመ ክፍያ ቫውቸር",
                descriptionEn = "Transferable real-estate construction credit for modern apartment.",
                descriptionAm = "ለዘመናዊ አፓርታማ የሚውል ሙሉ በሙሉ ወደ ሌላ ሰው የሚተላለፍ የቅድመ ክፍያ ቫውቸር።",
                category = AuctionCategory.REAL_ESTATE,
                imageUrl = "home",
                startingPrice = 0.0,
                currentBid = 0.0,
                minBidIncrement = 0.0,
                endTimeEpochMs = now + (2 * oneDay),
                highestBidderId = "user_sim_4",
                highestBidderName = "ዳዊት ታደሰ",
                totalBids = 19,
                participationFee = 0.0
            ),
            AuctionItem(
                id = "auc_6",
                titleEn = "Telebirr Supermarket Shopping Voucher",
                titleAm = "የሱፐርማርኬት ግዢ የቴሌብር ቫውቸር",
                descriptionEn = "Spendable at all partnered supermarkets across Ethiopia via Telebirr.",
                descriptionAm = "በቴሌብር አማካኝነት በሁሉም ሱፐርማርኬቶች የሚሰራ ሙሉ የግዢ ቫውቸር።",
                category = AuctionCategory.VOUCHERS,
                imageUrl = "receipt",
                startingPrice = 0.0,
                currentBid = 0.0,
                minBidIncrement = 0.0,
                endTimeEpochMs = now + (45 * 60 * 1000L),
                highestBidderId = "user_sim_5",
                highestBidderName = "ፋሲል ካሳሁን",
                totalBids = 12,
                participationFee = 0.0
            )
        )
        _auctions.value = initialAuctions

        // Initial Bids
        val initialBids = listOf(
            BidRecord(
                id = UUID.randomUUID().toString(),
                auctionId = "auc_1",
                userId = "user_sim_1",
                userName = "ዮናስ መኮንን (Yonas M.)",
                userPhone = "0944***12",
                amount = 0.0,
                timestampEpochMs = now - 5 * 60 * 1000L
            ),
            BidRecord(
                id = UUID.randomUUID().toString(),
                auctionId = "auc_3",
                userId = "user_101",
                userName = "አበበ ደስታ (Abebe Desta)",
                userPhone = "0912***78",
                amount = 0.0,
                timestampEpochMs = now - 20 * 60 * 1000L
            )
        )
        _bids.value = initialBids

        // Initial Sample Transactions
        val initialTx = listOf(
            TelebirrTransaction(
                id = "tx_101",
                userId = "user_101",
                userName = "አበበ ደስታ",
                userPhone = "0912345678",
                telebirrPhone = "0912345678",
                transactionId = "TB98451290AA",
                amount = 0.0,
                type = TransactionType.DEPOSIT,
                status = TransactionStatus.APPROVED,
                timestampEpochMs = now - 3 * oneHour,
                note = "Initial Telebirr Verification"
            )
        )
        _transactions.value = initialTx
    }

    private fun startClock() {
        scope.launch {
            while (true) {
                delay(1000)
                _currentTime.value = System.currentTimeMillis()
            }
        }
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
    }

    fun isAdminUser(): Boolean {
        val user = _currentUser.value
        return user.role == UserRole.ADMIN && user.email.trim().equals(ADMIN_EMAIL, ignoreCase = true)
    }

    fun loginAsAdminWithGoogleEmail(email: String): Boolean {
        val clean = email.trim().lowercase()
        if (clean == ADMIN_EMAIL.lowercase()) {
            _currentUser.value = User(
                id = "admin_mameboss",
                name = "Mame Boss (አድሚን)",
                phone = "0911000000",
                balance = 50000.0,
                role = UserRole.ADMIN,
                email = ADMIN_EMAIL
            )
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _currentUser.value = User(
            id = "user_101",
            name = "አበበ ደስታ (Abebe Desta)",
            phone = "0912345678",
            balance = 0.0,
            role = UserRole.USER,
            email = ""
        )
    }

    fun switchUserRole(role: UserRole) {
        val user = _currentUser.value
        if (role == UserRole.ADMIN) {
            // Strictly check if current user email is mameboss98@gmail.com
            if (user.email.trim().equals(ADMIN_EMAIL, ignoreCase = true)) {
                _currentUser.value = user.copy(
                    role = UserRole.ADMIN,
                    name = "Mame Boss (አድሚን)"
                )
            }
        } else {
            _currentUser.value = user.copy(
                role = UserRole.USER,
                name = if (user.name.contains("አድሚን")) "አበበ ደስታ (Abebe Desta)" else user.name
            )
        }
    }

    fun loginUser(name: String, phone: String, email: String = "", isAdmin: Boolean = false) {
        val canBeAdmin = isAdmin && email.trim().equals(ADMIN_EMAIL, ignoreCase = true)
        _currentUser.value = User(
            id = if (canBeAdmin) "admin_mameboss" else "user_${UUID.randomUUID().toString().take(6)}",
            name = name.ifBlank { if (canBeAdmin) "Mame Boss (አድሚን)" else "Addis Bid User" },
            phone = phone.ifBlank { "0911000000" },
            balance = if (canBeAdmin) 50000.0 else 0.0,
            role = if (canBeAdmin) UserRole.ADMIN else UserRole.USER,
            email = if (canBeAdmin) ADMIN_EMAIL else email.trim()
        )
    }

    // Place bid / Join participation
    sealed class BidResult {
        object Success : BidResult()
        object AuctionEnded : BidResult()
        data class Error(val message: String) : BidResult()
    }

    fun placeBid(auctionId: String): BidResult {
        val user = _currentUser.value
        val auction = _auctions.value.find { it.id == auctionId } ?: return BidResult.Error("Item not found")

        if (auction.isEnded || auction.endTimeEpochMs <= System.currentTimeMillis()) {
            return BidResult.AuctionEnded
        }

        // Record participation
        val updatedAuctions = _auctions.value.map { item ->
            if (item.id == auctionId) {
                item.copy(
                    highestBidderId = user.id,
                    highestBidderName = user.name,
                    totalBids = item.totalBids + 1
                )
            } else item
        }
        _auctions.value = updatedAuctions

        val bidRecord = BidRecord(
            id = UUID.randomUUID().toString(),
            auctionId = auctionId,
            userId = user.id,
            userName = user.name,
            userPhone = user.phone,
            amount = 0.0,
            timestampEpochMs = System.currentTimeMillis()
        )
        _bids.value = listOf(bidRecord) + _bids.value

        return BidResult.Success.also {
            firestore?.let { fs ->
                try {
                    val bidData = mapOf(
                        "id" to bidRecord.id,
                        "auctionId" to bidRecord.auctionId,
                        "userId" to bidRecord.userId,
                        "userName" to bidRecord.userName,
                        "userPhone" to bidRecord.userPhone,
                        "amount" to 0.0,
                        "timestampEpochMs" to bidRecord.timestampEpochMs
                    )
                    fs.collection("bids").document(bidRecord.id).set(bidData)
                    fs.collection("auctions").document(auctionId).update(
                        mapOf(
                            "highestBidderId" to user.id,
                            "highestBidderName" to user.name,
                            "totalBids" to (auction.totalBids + 1)
                        )
                    )
                } catch (e: Exception) {
                    Log.e("AddisBidRepo", "Error syncing bid to Firestore", e)
                }
            }
        }
    }

    // Submit Telebirr Deposit by user
    fun submitDeposit(
        telebirrPhone: String,
        transactionId: String,
        amount: Double,
        note: String
    ): TelebirrTransaction {
        val user = _currentUser.value
        val newTx = TelebirrTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            userName = user.name,
            userPhone = user.phone,
            telebirrPhone = telebirrPhone,
            transactionId = transactionId.trim().uppercase(),
            amount = amount,
            type = TransactionType.DEPOSIT,
            status = TransactionStatus.PENDING,
            timestampEpochMs = System.currentTimeMillis(),
            note = note
        )
        _transactions.value = listOf(newTx) + _transactions.value

        firestore?.let { fs ->
            try {
                val txData = mapOf(
                    "id" to newTx.id,
                    "userId" to newTx.userId,
                    "userName" to newTx.userName,
                    "userPhone" to newTx.userPhone,
                    "telebirrPhone" to newTx.telebirrPhone,
                    "transactionId" to newTx.transactionId,
                    "amount" to newTx.amount,
                    "type" to newTx.type.name,
                    "status" to newTx.status.name,
                    "timestampEpochMs" to newTx.timestampEpochMs,
                    "note" to newTx.note
                )
                fs.collection("transactions").document(newTx.id).set(txData)
            } catch (e: Exception) {
                Log.e("AddisBidRepo", "Error syncing deposit to Firestore", e)
            }
        }

        return newTx
    }

    // Submit Withdrawal Request
    fun submitWithdrawal(
        telebirrPhone: String,
        amount: Double
    ): Boolean {
        val user = _currentUser.value
        val newTx = TelebirrTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            userName = user.name,
            userPhone = user.phone,
            telebirrPhone = telebirrPhone,
            transactionId = "WTH-${UUID.randomUUID().toString().take(6).uppercase()}",
            amount = amount,
            type = TransactionType.WITHDRAWAL,
            status = TransactionStatus.PENDING,
            timestampEpochMs = System.currentTimeMillis(),
            note = "Withdrawal request to $telebirrPhone"
        )
        _transactions.value = listOf(newTx) + _transactions.value
        return true
    }

    // Admin: Approve Deposit
    fun approveDeposit(transactionId: String): Boolean {
        val tx = _transactions.value.find { it.id == transactionId } ?: return false
        if (tx.status != TransactionStatus.PENDING) return false

        val updatedTxList = _transactions.value.map {
            if (it.id == transactionId) it.copy(status = TransactionStatus.APPROVED, note = "Approved by Admin") else it
        }
        _transactions.value = updatedTxList

        if (tx.userId == _currentUser.value.id) {
            _currentUser.value = _currentUser.value.copy(
                balance = _currentUser.value.balance + tx.amount
            )
        }

        firestore?.let { fs ->
            try {
                fs.collection("transactions").document(transactionId).update(
                    mapOf(
                        "status" to TransactionStatus.APPROVED.name,
                        "note" to "Approved by Admin"
                    )
                )
            } catch (e: Exception) {
                Log.e("AddisBidRepo", "Error updating approval in Firestore", e)
            }
        }

        return true
    }

    // Admin: Reject Deposit
    fun rejectDeposit(transactionId: String, reason: String): Boolean {
        val tx = _transactions.value.find { it.id == transactionId } ?: return false
        val updatedTxList = _transactions.value.map {
            if (it.id == transactionId) it.copy(status = TransactionStatus.REJECTED, note = "Rejected: $reason") else it
        }
        _transactions.value = updatedTxList

        firestore?.let { fs ->
            try {
                fs.collection("transactions").document(transactionId).update(
                    mapOf(
                        "status" to TransactionStatus.REJECTED.name,
                        "note" to "Rejected: $reason"
                    )
                )
            } catch (e: Exception) {
                Log.e("AddisBidRepo", "Error updating rejection in Firestore", e)
            }
        }

        return true
    }

    // Admin: Create Auction Item
    fun createAuction(
        titleAm: String,
        titleEn: String,
        descriptionAm: String,
        descriptionEn: String,
        category: AuctionCategory,
        durationHours: Long
    ) {
        val now = System.currentTimeMillis()
        val newAuction = AuctionItem(
            id = "auc_${UUID.randomUUID().toString().take(8)}",
            titleEn = titleEn.ifBlank { titleAm },
            titleAm = titleAm,
            descriptionEn = descriptionEn.ifBlank { descriptionAm },
            descriptionAm = descriptionAm,
            category = category,
            imageUrl = category.iconName,
            startingPrice = 0.0,
            currentBid = 0.0,
            minBidIncrement = 0.0,
            endTimeEpochMs = now + (durationHours * 60 * 60 * 1000L),
            highestBidderId = null,
            highestBidderName = null,
            totalBids = 0,
            participationFee = 0.0
        )
        _auctions.value = listOf(newAuction) + _auctions.value

        firestore?.let { fs ->
            try {
                val data = mapOf(
                    "id" to newAuction.id,
                    "titleEn" to newAuction.titleEn,
                    "titleAm" to newAuction.titleAm,
                    "descriptionEn" to newAuction.descriptionEn,
                    "descriptionAm" to newAuction.descriptionAm,
                    "category" to newAuction.category.name,
                    "imageUrl" to newAuction.imageUrl,
                    "currentBid" to 0.0,
                    "endTimeEpochMs" to newAuction.endTimeEpochMs,
                    "totalBids" to 0,
                    "isEnded" to false
                )
                fs.collection("auctions").document(newAuction.id).set(data)
            } catch (e: Exception) {
                Log.e("AddisBidRepo", "Error saving auction to Firestore", e)
            }
        }
    }

    // Admin: End Auction manually
    fun endAuction(auctionId: String) {
        _auctions.value = _auctions.value.map { item ->
            if (item.id == auctionId) {
                item.copy(
                    isEnded = true,
                    winnerId = item.highestBidderId,
                    winnerName = item.highestBidderName
                )
            } else item
        }
        firestore?.collection("auctions")?.document(auctionId)?.update("isEnded", true)
    }

    // Admin: Update Merchant Settings
    fun updateMerchantSettings(merchantName: String, merchantPhone: String, shortcode: String) {
        _settings.value = _settings.value.copy(
            telebirrMerchantName = merchantName,
            telebirrMerchantPhone = merchantPhone,
            telebirrShortcode = shortcode
        )
    }

    fun initFirestore(databaseId: String) {
        if (firestore != null) return
        try {
            val fs = FirebaseFirestore.getInstance(databaseId)
            firestore = fs
            listenToCloudData(fs)
        } catch (e: Exception) {
            Log.e("AddisBidRepo", "Error initializing Firestore", e)
        }
    }

    private fun listenToCloudData(fs: FirebaseFirestore) {
        try {
            // Listen to real-time Auctions
            auctionsListener = fs.collection("auctions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AddisBidRepo", "Auctions listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val items = snapshot.documents.mapNotNull { doc ->
                            try {
                                AuctionItem(
                                    id = doc.getString("id") ?: doc.id,
                                    titleEn = doc.getString("titleEn") ?: "",
                                    titleAm = doc.getString("titleAm") ?: "",
                                    descriptionEn = doc.getString("descriptionEn") ?: "",
                                    descriptionAm = doc.getString("descriptionAm") ?: "",
                                    category = try {
                                        AuctionCategory.valueOf(doc.getString("category") ?: "PHONES")
                                    } catch (e: Exception) {
                                        AuctionCategory.PHONES
                                    },
                                    imageUrl = doc.getString("imageUrl") ?: "phone",
                                    startingPrice = 0.0,
                                    currentBid = doc.getDouble("currentBid") ?: 0.0,
                                    minBidIncrement = 0.0,
                                    endTimeEpochMs = doc.getLong("endTimeEpochMs") ?: (System.currentTimeMillis() + 86400000L),
                                    highestBidderId = doc.getString("highestBidderId"),
                                    highestBidderName = doc.getString("highestBidderName"),
                                    totalBids = (doc.getLong("totalBids") ?: 0L).toInt(),
                                    participationFee = 0.0,
                                    isEnded = doc.getBoolean("isEnded") ?: false,
                                    winnerId = doc.getString("winnerId"),
                                    winnerName = doc.getString("winnerName")
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (items.isNotEmpty()) {
                            _auctions.value = items
                        }
                    } else if (snapshot != null && snapshot.isEmpty) {
                        // Seed cloud Firestore with initial auctions
                        _auctions.value.forEach { item ->
                            val data = mapOf(
                                "id" to item.id,
                                "titleEn" to item.titleEn,
                                "titleAm" to item.titleAm,
                                "descriptionEn" to item.descriptionEn,
                                "descriptionAm" to item.descriptionAm,
                                "category" to item.category.name,
                                "imageUrl" to item.imageUrl,
                                "currentBid" to item.currentBid,
                                "endTimeEpochMs" to item.endTimeEpochMs,
                                "totalBids" to item.totalBids,
                                "isEnded" to item.isEnded
                            )
                            fs.collection("auctions").document(item.id).set(data, SetOptions.merge())
                        }
                    }
                }

            // Listen to real-time Transactions (Telebirr deposits)
            transactionsListener = fs.collection("transactions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("AddisBidRepo", "Transactions listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val txs = snapshot.documents.mapNotNull { doc ->
                            try {
                                TelebirrTransaction(
                                    id = doc.getString("id") ?: doc.id,
                                    userId = doc.getString("userId") ?: "",
                                    userName = doc.getString("userName") ?: "",
                                    userPhone = doc.getString("userPhone") ?: "",
                                    telebirrPhone = doc.getString("telebirrPhone") ?: "",
                                    transactionId = doc.getString("transactionId") ?: "",
                                    amount = doc.getDouble("amount") ?: 0.0,
                                    type = TransactionType.valueOf(doc.getString("type") ?: TransactionType.DEPOSIT.name),
                                    status = TransactionStatus.valueOf(doc.getString("status") ?: TransactionStatus.PENDING.name),
                                    timestampEpochMs = doc.getLong("timestampEpochMs") ?: System.currentTimeMillis(),
                                    note = doc.getString("note") ?: ""
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (txs.isNotEmpty()) {
                            _transactions.value = txs
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("AddisBidRepo", "Failed to setup Firestore listeners", e)
        }
    }

    companion object {
        @Volatile
        private var instance: CheretaRepository? = null

        fun getInstance(): CheretaRepository {
            return instance ?: synchronized(this) {
                instance ?: CheretaRepository().also { instance = it }
            }
        }
    }
}

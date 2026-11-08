package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.repository.CheretaRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    onNavigateToAuction: (AuctionItem) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { CheretaRepository.getInstance() }
    val lang by repository.language.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val auctions by repository.auctions.collectAsState()
    val settings by repository.settings.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    val isAdmin = repository.isAdminUser()

    if (!isAdmin) {
        AdminLockGate(
            lang = lang,
            onLoginSuccess = {
                Toast.makeText(context, if (lang == Language.AMHARIC) "እንኳን ደህና መጡ አድሚን!" else "Welcome Admin!", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    var selectedTab by remember { mutableStateOf(0) }
    var showCreateAuctionDialog by remember { mutableStateOf(false) }
    var rejectDialogTxId by remember { mutableStateOf<String?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    val pendingDeposits = remember(transactions) {
        transactions.filter { it.type == TransactionType.DEPOSIT && it.status == TransactionStatus.PENDING }
    }
    val pastTransactions = remember(transactions) {
        transactions.filter { it.status != TransactionStatus.PENDING }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_panel_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Admin Header with Role Indicator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = TelebirrGold
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                        .testTag("admin_header_icon")
                                )
                            }
                            Column {
                                Text(
                                    text = if (lang == Language.AMHARIC) "አዲስ ቢድ • አድሚን ፓነል" else "Addis Bid • Admin Console",
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "mameboss98@gmail.com",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = {
                                repository.logoutAdmin()
                                Toast.makeText(
                                    context,
                                    if (lang == Language.AMHARIC) "ከአድሚን ወጥተዋል" else "Logged out from Admin",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_logout_button")
                        ) {
                            Text(
                                text = if (lang == Language.AMHARIC) "ውጣ" else "Exit Admin",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Key metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (pendingDeposits.isNotEmpty()) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${pendingDeposits.size}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (pendingDeposits.isNotEmpty()) Color(0xFFB45309) else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC) "ያልተረጋገጡ ገቢዎች" else "Pending Deposits",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (pendingDeposits.isNotEmpty()) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${auctions.count { !it.isEnded }}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TelebirrPrimary
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC) "ንቁ እቃዎች" else "Active Items",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val totalEntries = auctions.sumOf { it.totalBids }
                                Text(
                                    text = "$totalEntries",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TelebirrGreen
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC) "ጠቅላላ ተሳታፊዎች" else "Total Entries",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(if (lang == Language.AMHARIC) "የቴሌብር ማረጋገጫ" else "Approvals")
                            if (pendingDeposits.isNotEmpty()) {
                                Badge(containerColor = Color(0xFFEF4444)) {
                                    Text("${pendingDeposits.size}", color = Color.White)
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (lang == Language.AMHARIC) "እቃዎችን አስተዳድር" else "Manage Items") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (lang == Language.AMHARIC) "የቴሌብር ቅንብር" else "Telebirr Setup") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Deposit Approvals Tab
                item {
                    Text(
                        text = if (lang == Language.AMHARIC)
                            "ማረጋገጫ የሚጠብቁ የቴሌብር ክፍያዎች (${pendingDeposits.size})"
                        else
                            "Pending Telebirr Deposits to Approve (${pendingDeposits.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                if (pendingDeposits.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TelebirrGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC)
                                        "በመጠባበቅ ላይ ያለ ምንም ክፍያ የለም! ሁሉም ተረጋግጠዋል።"
                                    else
                                        "All clear! No pending deposits waiting for verification.",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(pendingDeposits, key = { it.id }) { deposit ->
                        PendingDepositCard(
                            deposit = deposit,
                            lang = lang,
                            onApprove = {
                                val ok = repository.approveDeposit(deposit.id)
                                if (ok) {
                                    Toast.makeText(
                                        context,
                                        if (lang == Language.AMHARIC) "${deposit.userName} በ ${UiFormatters.formatETB(deposit.amount)} ተረጋግጧል!" else "Deposit approved and balance credited!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onReject = {
                                rejectDialogTxId = deposit.id
                            }
                        )
                    }
                }

                // History of past transactions
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (lang == Language.AMHARIC) "የተጠናቀቁ ክፍያዎች ታሪክ" else "Completed Transactions Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(pastTransactions.take(10), key = { it.id }) { tx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = tx.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "Ref: ${tx.transactionId} • ${tx.telebirrPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = UiFormatters.formatETB(tx.amount), fontWeight = FontWeight.Bold)
                                StatusBadge(status = tx.status, lang = lang)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Manage Items Tab (No prices, clean list)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == Language.AMHARIC) "እቃዎች (${auctions.size})" else "All Items (${auctions.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = { showCreateAuctionDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_new_auction_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (lang == Language.AMHARIC) "+ አዲስ እቃ" else "+ Add Item", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(auctions, key = { it.id }) { auction ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (lang == Language.AMHARIC) auction.titleAm else auction.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = if (auction.isEnded) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (auction.isEnded) "Ended" else "Active",
                                        color = if (auction.isEnded) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF065F46),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (auction.isEnded) "የተጠናቀቀ" else "ንቁ ጨረታ",
                                    color = if (auction.isEnded) MaterialTheme.colorScheme.onSurfaceVariant else TelebirrPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ተሳታፊዎች: ${auction.totalBids}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }

                            if (!auction.isEnded) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            repository.endAuction(auction.id)
                                            Toast.makeText(context, "እቃው ተጠናቋል! አሸናፊው ተመርጧል።", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(if (lang == Language.AMHARIC) "አጠናቅቅ" else "End & Finalize", fontSize = 12.sp)
                                    }
                                }
                            } else if (auction.winnerName != null) {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "🏆 Winner: ${auction.winnerName}",
                                        color = Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Telebirr Merchant Setup Tab
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        var merchantName by remember { mutableStateOf(settings.telebirrMerchantName) }
                        var merchantPhone by remember { mutableStateOf(settings.telebirrMerchantPhone) }
                        var shortcode by remember { mutableStateOf(settings.telebirrShortcode) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (lang == Language.AMHARIC) "የአዲስ ቢድ የክፍያ መረጃዎች" else "Addis Bid Payment Setup",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (lang == Language.AMHARIC)
                                    "ደንበኞች ክፍያ የሚልኩበትን የራስዎን የቴሌብር ስልክ ቁጥር ወይም አጭር ኮድ (Shortcode) እዚህ ያዘጋጁ።"
                                else
                                    "Set your official receiver Telebirr phone number or shortcode.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = merchantName,
                                onValueChange = { merchantName = it },
                                label = { Text(if (lang == Language.AMHARIC) "የንግድ ስም" else "Merchant Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = merchantPhone,
                                onValueChange = { merchantPhone = it },
                                label = { Text(if (lang == Language.AMHARIC) "የቴሌብር ስልክ ቁጥር" else "Telebirr Receiver Phone") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = shortcode,
                                onValueChange = { shortcode = it },
                                label = { Text(if (lang == Language.AMHARIC) "የቴሌብር አጭር ኮድ (Shortcode)" else "Telebirr Shortcode") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    repository.updateMerchantSettings(merchantName, merchantPhone, shortcode)
                                    Toast.makeText(context, if (lang == Language.AMHARIC) "ቅንብሩ ተቀምጧል!" else "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (lang == Language.AMHARIC) "መረጃውን መዝግብ" else "Save Settings", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Reason Dialog
    if (rejectDialogTxId != null) {
        AlertDialog(
            onDismissRequest = { rejectDialogTxId = null },
            title = { Text(if (lang == Language.AMHARIC) "ክፍያውን ውድቅ ያድርጉ" else "Reject Deposit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (lang == Language.AMHARIC) "ውድቅ የሚደረግበትን ምክንያት ያስገቡ:" else "Provide rejection reason:")
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = { Text("Reason") },
                        placeholder = { Text("e.g. Transaction ID not found / Amount mismatch") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = rejectDialogTxId ?: return@Button
                        repository.rejectDeposit(id, rejectionReason.ifBlank { "Unverified SMS code" })
                        rejectDialogTxId = null
                        rejectionReason = ""
                        Toast.makeText(context, "ክፍያው ውድቅ ተደርጓል", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(if (lang == Language.AMHARIC) "ውድቅ አድርግ" else "Confirm Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectDialogTxId = null }) {
                    Text(if (lang == Language.AMHARIC) "ተመለስ" else "Cancel")
                }
            }
        )
    }

    // Create Auction Dialog (Simplified: 0 ETB start, no market price)
    if (showCreateAuctionDialog) {
        CreateAuctionDialogClean(
            lang = lang,
            onDismiss = { showCreateAuctionDialog = false },
            onCreate = { titleAm, titleEn, descAm, descEn, category, hours ->
                repository.createAuction(
                    titleAm = titleAm,
                    titleEn = titleEn,
                    descriptionAm = descAm,
                    descriptionEn = descEn,
                    category = category,
                    durationHours = hours
                )
                showCreateAuctionDialog = false
                Toast.makeText(context, if (lang == Language.AMHARIC) "አዲስ እቃ በተሳካ ሁኔታ ተጀምሯል!" else "New item launched!", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAuctionDialogClean(
    lang: Language,
    onDismiss: () -> Unit,
    onCreate: (titleAm: String, titleEn: String, descAm: String, descEn: String, category: AuctionCategory, hours: Long) -> Unit
) {
    var titleAm by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var descAm by remember { mutableStateOf("") }
    var descEn by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AuctionCategory.PHONES) }
    var durationHoursStr by remember { mutableStateOf("24") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (titleAm.isBlank()) {
                        error = if (lang == Language.AMHARIC) "እባክዎ የእቃውን ስም ያስገቡ" else "Please enter item title"
                        return@Button
                    }
                    val hours = durationHoursStr.toLongOrNull() ?: 24L
                    onCreate(titleAm, titleEn, descAm, descEn, selectedCategory, hours)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (lang == Language.AMHARIC) "እቃውን ጀምር" else "Launch Item", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == Language.AMHARIC) "ተመለስ" else "Cancel")
            }
        },
        title = {
            Text(
                text = if (lang == Language.AMHARIC) "አዲስ እቃ ማዘጋጃ" else "Create New Item",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = titleAm,
                        onValueChange = { titleAm = it; error = null },
                        label = { Text(if (lang == Language.AMHARIC) "የእቃው ስም (በአማርኛ)" else "Item Title (Amharic)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = error != null
                    )
                }

                item {
                    OutlinedTextField(
                        value = titleEn,
                        onValueChange = { titleEn = it },
                        label = { Text("Item Title (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = descAm,
                        onValueChange = { descAm = it },
                        label = { Text(if (lang == Language.AMHARIC) "ዝርዝር መግለጫ (በአማርኛ)" else "Description (Amharic)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Text(
                        text = if (lang == Language.AMHARIC) "ምድብ ይምረጡ:" else "Select Category:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(AuctionCategory.PHONES, AuctionCategory.VEHICLES, AuctionCategory.ELECTRONICS, AuctionCategory.JEWELRY).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(if (lang == Language.AMHARIC) cat.titleAm else cat.titleEn, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = durationHoursStr,
                        onValueChange = { durationHoursStr = it.filter { c -> c.isDigit() } },
                        label = { Text(if (lang == Language.AMHARIC) "የሚቆይበት ጊዜ (ሰዓት)" else "Duration (Hours)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }
    )
}


@Composable
fun PendingDepositCard(
    deposit: TelebirrTransaction,
    lang: Language,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("pending_deposit_${deposit.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TelebirrLogoPill()
                    Text(
                        text = UiFormatters.formatDate(deposit.timestampEpochMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = deposit.status, lang = lang)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = deposit.userName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Sender: ${deposit.telebirrPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = UiFormatters.formatETB(deposit.amount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = TelebirrGreen
                )
            }

            // Transaction ID Highlight Box with Copy Button
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (lang == Language.AMHARIC) "የቴሌብር SMS ደረሰኝ ኮድ:" else "Telebirr SMS Ref ID:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = deposit.transactionId,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("TxID", deposit.transactionId))
                            Toast.makeText(context, "ኮፒ ተደርጓል!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))

            // Admin Action Buttons (Approve / Reject)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (lang == Language.AMHARIC) "ውድቅ አድርግ" else "Reject", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("approve_deposit_${deposit.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (lang == Language.AMHARIC) "አጽድቅ (ገንዘቡ ይግባ)" else "Approve & Credit", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdminLockGate(
    lang: Language,
    onLoginSuccess: () -> Unit
) {
    val repository = remember { CheretaRepository.getInstance() }
    var emailInput by remember { mutableStateOf("mameboss98@gmail.com") }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("admin_lock_gate"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = TelebirrGold.copy(alpha = 0.2f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text(
                    text = if (lang == Language.AMHARIC) "የአድሚን መቆጣጠሪያ ፓነል" else "Admin Console",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (lang == Language.AMHARIC)
                                "ይህ ገጽ የተፈቀደው ለዋናው አድሚን (mameboss98@gmail.com) ብቻ ነው።"
                            else
                                "This panel is strictly restricted to admin (mameboss98@gmail.com).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        errorMessage = null
                    },
                    label = { Text(if (lang == Language.AMHARIC) "የአድሚን ጎግል ኢሜይል" else "Admin Google Email") },
                    placeholder = { Text("mameboss98@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_email_input"),
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        errorMessage = null
                    },
                    label = { Text(if (lang == Language.AMHARIC) "የአድሚን የይለፍ ቃል (Password)" else "Admin Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_password_input"),
                    isError = errorMessage != null
                )

                Button(
                    onClick = {
                        if (passwordInput != "Mame@2026") {
                            errorMessage = if (lang == Language.AMHARIC)
                                "የተሳሳተ የይለፍ ቃል! እባክዎ ትክክለኛውን የአድሚን የይለፍ ቃል ያስገቡ።"
                            else
                                "Incorrect password! Please enter the valid admin password."
                            return@Button
                        }
                        val ok = repository.loginAsAdminWithGoogleEmail(emailInput)
                        if (ok) {
                            errorMessage = null
                            onLoginSuccess()
                        } else {
                            errorMessage = if (lang == Language.AMHARIC)
                                "ያልተፈቀደ ኢሜይል! ለአድሚኑ (mameboss98@gmail.com) ብቻ የተፈቀደ ነው።"
                            else
                                "Unauthorized email! Access permitted only for mameboss98@gmail.com"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_google_login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == Language.AMHARIC) "በጎግል ኢሜይል አድሚን ግባ" else "Sign In as Admin",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


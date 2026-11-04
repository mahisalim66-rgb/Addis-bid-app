package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.repository.CheretaRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { CheretaRepository.getInstance() }
    val lang by repository.language.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val settings by repository.settings.collectAsState()
    val transactions by repository.transactions.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var depositSubmittedSuccess by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }

    val userTransactions = remember(transactions, currentUser.id, selectedFilter) {
        transactions.filter {
            it.userId == currentUser.id && (selectedFilter == null || it.type == selectedFilter)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF003875), Color(0xFF0066CC), Color(0xFF0284C7))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                TelebirrLogoPill()
                                Text(
                                    text = if (lang == Language.AMHARIC) "የቴሌብር ዋሌት" else "Telebirr Wallet",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            if (repository.isAdminUser()) {
                                Surface(
                                    color = TelebirrGold,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable(onClick = onNavigateToAdmin)
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        color = Color.Black,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (lang == Language.AMHARIC) "አጠቃላይ ቀሪ ሒሳብ" else "Available Balance",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GoldCoinBadge(size = 32.dp)
                                Text(
                                    text = UiFormatters.formatETB(currentUser.balance),
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "${currentUser.name} • ${currentUser.phone}",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        // Deposit and Withdraw buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showDepositDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("deposit_telebirr_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGold),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == Language.AMHARIC) "በቴሌብር አስገባ" else "Deposit",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { showWithdrawDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("withdraw_telebirr_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(Color.White, Color.White))
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.ArrowOutward, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == Language.AMHARIC) "ገንዘብ አውጣ" else "Withdraw",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Notice for Admin Approval
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = if (lang == Language.AMHARIC) "የቴሌብር ማረጋገጫ ስርዓት" else "Admin Deposit Approval Flow",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (lang == Language.AMHARIC)
                                "በቴሌብር ያስገቡት ክፍያ በአድሚን እንደተረጋገጠ ወዲያውኑ ዋሌትዎ ውስጥ ይገባል።"
                            else
                                "Once you transfer via Telebirr, admin instantly verifies the SMS ref code and credits your wallet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Transactions Header & Filter
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (lang == Language.AMHARIC) "የሂሳብ እንቅስቃሴዎች (${userTransactions.size})" else "Transaction History (${userTransactions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text(if (lang == Language.AMHARIC) "ሁሉም" else "All") }
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionType.DEPOSIT,
                        onClick = { selectedFilter = TransactionType.DEPOSIT },
                        label = { Text(if (lang == Language.AMHARIC) "የገቡ ክፍያዎች" else "Deposits") }
                    )
                    FilterChip(
                        selected = selectedFilter == TransactionType.BID_PLACEMENT,
                        onClick = { selectedFilter = TransactionType.BID_PLACEMENT },
                        label = { Text(if (lang == Language.AMHARIC) "የጨረታ ክፍያ" else "Bids") }
                    )
                }
            }
        }

        // Transactions List
        if (userTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == Language.AMHARIC) "ምንም የሂሳብ እንቅስቃሴ የለም።" else "No transactions yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(userTransactions, key = { it.id }) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        when (tx.type) {
                                            TransactionType.DEPOSIT -> Color(0xFFD1FAE5)
                                            TransactionType.WITHDRAWAL -> Color(0xFFFEF3C7)
                                            TransactionType.BID_PLACEMENT -> Color(0xFFE0F2FE)
                                            TransactionType.WIN_REFUND -> Color(0xFFDCFCE7)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (tx.type) {
                                        TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                                        TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                                        TransactionType.BID_PLACEMENT -> Icons.Default.Gavel
                                        TransactionType.WIN_REFUND -> Icons.Default.EmojiEvents
                                    },
                                    contentDescription = null,
                                    tint = when (tx.type) {
                                        TransactionType.DEPOSIT -> Color(0xFF065F46)
                                        TransactionType.WITHDRAWAL -> Color(0xFF92400E)
                                        TransactionType.BID_PLACEMENT -> Color(0xFF0369A1)
                                        TransactionType.WIN_REFUND -> Color(0xFF15803D)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = when (tx.type) {
                                        TransactionType.DEPOSIT -> if (lang == Language.AMHARIC) "የቴሌብር ገቢ" else "Telebirr Deposit"
                                        TransactionType.WITHDRAWAL -> if (lang == Language.AMHARIC) "ገንዘብ ማውጣት" else "Withdrawal"
                                        TransactionType.BID_PLACEMENT -> if (lang == Language.AMHARIC) "የጨረታ መግቢያ" else "Bid Placement Fee"
                                        TransactionType.WIN_REFUND -> if (lang == Language.AMHARIC) "የጨረታ ገቢ" else "Auction Prize"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Ref: ${tx.transactionId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = UiFormatters.formatDate(tx.timestampEpochMs),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.WIN_REFUND) "+" else "-") +
                                        UiFormatters.formatETB(tx.amount),
                                fontWeight = FontWeight.ExtraBold,
                                color = if (tx.type == TransactionType.DEPOSIT) TelebirrGreen else MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusBadge(status = tx.status, lang = lang)
                        }
                    }
                }
            }
        }
    }

    // Telebirr Deposit Dialog
    if (showDepositDialog) {
        TelebirrDepositDialog(
            settings = settings,
            currentUser = currentUser,
            lang = lang,
            onDismiss = { showDepositDialog = false },
            onSubmit = { phone, txId, amount, note ->
                repository.submitDeposit(phone, txId, amount, note)
                showDepositDialog = false
                Toast.makeText(
                    context,
                    if (lang == Language.AMHARIC) "ክፍያዎ ተመዝግቧል። አድሚኑ አረጋግጦ ብሩን ወዲያውኑ ያስገባልዎታል!" else "Deposit submitted for Admin verification!",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // Telebirr Withdrawal Dialog
    if (showWithdrawDialog) {
        TelebirrWithdrawDialog(
            currentUser = currentUser,
            lang = lang,
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { phone, amount ->
                val ok = repository.submitWithdrawal(phone, amount)
                showWithdrawDialog = false
                if (ok) {
                    Toast.makeText(
                        context,
                        if (lang == Language.AMHARIC) "የማውጣት ጥያቄዎ ተልኳል!" else "Withdrawal requested!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        if (lang == Language.AMHARIC) "በቂ ሂሳብ የለዎትም!" else "Insufficient balance!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}

@Composable
fun TelebirrDepositDialog(
    settings: PlatformSettings,
    currentUser: User,
    lang: Language,
    onDismiss: () -> Unit,
    onSubmit: (phone: String, txId: String, amount: Double, note: String) -> Unit
) {
    val context = LocalContext.current
    var phoneInput by remember { mutableStateOf(currentUser.phone) }
    var txIdInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("250") }
    var noteInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    if (txIdInput.isBlank()) {
                        error = if (lang == Language.AMHARIC) "እባክዎ የቴሌብር ደረሰኝ ቁጥር (Transaction ID) ያስገቡ" else "Please enter Telebirr Transaction ID"
                    } else if (amount <= 0) {
                        error = if (lang == Language.AMHARIC) "ትክክለኛ የብር መጠን ያስገቡ" else "Please enter valid amount"
                    } else {
                        onSubmit(phoneInput, txIdInput, amount, noteInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("submit_deposit_button")
            ) {
                Text(if (lang == Language.AMHARIC) "ክፍያውን አረጋግጥ" else "Submit Verification", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == Language.AMHARIC) "ተመለስ" else "Cancel")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelebirrLogoPill()
                Text(
                    text = if (lang == Language.AMHARIC) "በቴሌብር ገንዘብ ማስገቢያ" else "Deposit via Telebirr",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Official Telebirr Merchant Box
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (lang == Language.AMHARIC) "ገንዘብ መላኪያ የቴሌብር ቁጥር (Merchant):" else "Send payment to Official Receiver:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = settings.telebirrMerchantPhone,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${settings.telebirrMerchantName} (Shortcode: ${settings.telebirrShortcode})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Telebirr Phone", settings.telebirrMerchantPhone))
                                    Toast.makeText(context, "ስልክ ቁጥሩ ኮፒ ተደርጓል!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Step by Step guide
                Text(
                    text = if (lang == Language.AMHARIC)
                        "ደረጃ 1፡ በቴሌብር አፕሊኬሽን ወይም በ *127# ወደ ላይኛው ቁጥር ገንዘቡን ይላኩ።\nደረጃ 2፡ የደረሰዎትን የSMS ደረሰኝ ቁጥር (Transaction ID) ከታች ያስገቡ።"
                    else
                        "Step 1: Send money via Telebirr app or *127# to the number above.\nStep 2: Enter your SMS Transaction ID below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Amount chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("100", "250", "500", "1000").forEach { amount ->
                        FilterChip(
                            selected = amountInput == amount,
                            onClick = { amountInput = amount },
                            label = { Text("$amount ብር", fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { c -> c.isDigit() } },
                    label = { Text(if (lang == Language.AMHARIC) "የተከፈለው መጠን (ብር)" else "Amount Paid (ETB)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = txIdInput,
                    onValueChange = {
                        txIdInput = it
                        error = null
                    },
                    label = { Text(if (lang == Language.AMHARIC) "የደረሰኝ ቁጥር (Transaction ID / Code)" else "Telebirr Transaction ID") },
                    placeholder = { Text("e.g. TB948120X") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_id_input"),
                    singleLine = true,
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text(if (lang == Language.AMHARIC) "የከፈሉበት ስልክ ቁጥር" else "Sender Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}

@Composable
fun TelebirrWithdrawDialog(
    currentUser: User,
    lang: Language,
    onDismiss: () -> Unit,
    onSubmit: (phone: String, amount: Double) -> Unit
) {
    var phoneInput by remember { mutableStateOf(currentUser.phone) }
    var amountInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    if (amount <= 0 || amount > currentUser.balance) {
                        error = if (lang == Language.AMHARIC) "ትክክለኛና ካለዎት ሂሳብ ያልበለጠ መጠን ያስገቡ" else "Enter valid amount within balance"
                    } else {
                        onSubmit(phoneInput, amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (lang == Language.AMHARIC) "ጥያቄውን ላክ" else "Request Withdrawal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == Language.AMHARIC) "ተመለስ" else "Cancel")
            }
        },
        title = {
            Text(
                text = if (lang == Language.AMHARIC) "ወደ ቴሌብር ገንዘብ ማውጣት" else "Withdraw to Telebirr",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (lang == Language.AMHARIC)
                        "ያለዎት ሂሳብ፡ ${UiFormatters.formatETB(currentUser.balance)}"
                    else
                        "Available Balance: ${UiFormatters.formatETB(currentUser.balance)}",
                    fontWeight = FontWeight.Bold,
                    color = TelebirrGreen
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = {
                        amountInput = it.filter { c -> c.isDigit() }
                        error = null
                    },
                    label = { Text(if (lang == Language.AMHARIC) "የሚወጣው የብር መጠን" else "Withdrawal Amount (ETB)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text(if (lang == Language.AMHARIC) "ገንዘቡ የሚላክበት የቴሌብር ስልክ" else "Recipient Telebirr Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}

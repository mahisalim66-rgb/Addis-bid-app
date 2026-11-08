package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun WalletDashboardCard(
    user: User,
    recentTransactions: List<TelebirrTransaction>,
    lang: Language,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
    onViewAllClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wallet_dashboard_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Telebirr Pill & User Title
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
                        text = if (lang == Language.AMHARIC) "የቴሌብር ዋሌት ሂሳብ" else "Telebirr Wallet Balance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (onViewAllClick != null) {
                    Text(
                        text = if (lang == Language.AMHARIC) "ሁሉንም እይ >" else "View All >",
                        color = TelebirrPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onViewAllClick() }
                    )
                }
            }

            // Hero Balance Gradient Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF003875),
                                Color(0xFF0066CC),
                                Color(0xFF0284C7)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (lang == Language.AMHARIC) "የሚገኝ ቀሪ ሂሳብ" else "Available Balance",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GoldCoinBadge(size = 32.dp)
                        Text(
                            text = "${String.format("%,.2f", user.balance)} ETB",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Action Buttons (Deposit / Withdraw)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDepositClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TelebirrGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("wallet_deposit_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == Language.AMHARIC) "ገንዘብ አስገባ" else "Deposit",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onWithdrawClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("wallet_withdraw_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == Language.AMHARIC) "ወጪ አድርግ" else "Withdraw",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Recent Transactions Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == Language.AMHARIC) "የቅርብ ጊዜ ዝውውሮች" else "Recent Transactions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${recentTransactions.take(3).size} ${if (lang == Language.AMHARIC) "ዝውውሮች" else "items"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (recentTransactions.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (lang == Language.AMHARIC) "ምንም የቅርብ ጊዜ ዝውውር የለም" else "No recent transactions",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC)
                                        "በቴሌብር ገንዘብ በማስገባት ጨረታዎችን መሳተፍ ይጀምሩ"
                                    else
                                        "Deposit funds via Telebirr to participate in auctions",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    recentTransactions.take(3).forEach { tx ->
                        RecentTransactionRow(tx = tx, lang = lang)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentTransactionRow(
    tx: TelebirrTransaction,
    lang: Language
) {
    val isPositive = tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.WIN_REFUND
    val amountPrefix = if (isPositive) "+" else "-"
    val amountColor = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626)

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isPositive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (tx.type) {
                                TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                                TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                                TransactionType.BID_PLACEMENT -> Icons.Default.Gavel
                                TransactionType.WIN_REFUND -> Icons.Default.Redeem
                            },
                            contentDescription = null,
                            tint = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = when (tx.type) {
                            TransactionType.DEPOSIT -> if (lang == Language.AMHARIC) "ገንዘብ ገቢ (Deposit)" else "Deposit"
                            TransactionType.WITHDRAWAL -> if (lang == Language.AMHARIC) "ገንዘብ ወጪ (Withdrawal)" else "Withdrawal"
                            TransactionType.BID_PLACEMENT -> if (lang == Language.AMHARIC) "የጨረታ ክፍያ" else "Bid Placement"
                            TransactionType.WIN_REFUND -> if (lang == Language.AMHARIC) "የጨረታ ምላሽ" else "Refund / Win"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${UiFormatters.formatDate(tx.timestampEpochMs)} • ${tx.transactionId.ifBlank { "telebirr" }}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$amountPrefix${String.format("%,.2f", tx.amount)} ETB",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.status) {
                        TransactionStatus.APPROVED -> Color(0xFFDCFCE7)
                        TransactionStatus.PENDING -> Color(0xFFFEF3C7)
                        TransactionStatus.REJECTED -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = when (tx.status) {
                            TransactionStatus.APPROVED -> if (lang == Language.AMHARIC) "ተጠናቋል" else "Completed"
                            TransactionStatus.PENDING -> if (lang == Language.AMHARIC) "በሂደት ላይ" else "Pending"
                            TransactionStatus.REJECTED -> if (lang == Language.AMHARIC) "ተሰርዟል" else "Rejected"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.status) {
                            TransactionStatus.APPROVED -> Color(0xFF166534)
                            TransactionStatus.PENDING -> Color(0xFF92400E)
                            TransactionStatus.REJECTED -> Color(0xFF991B1B)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

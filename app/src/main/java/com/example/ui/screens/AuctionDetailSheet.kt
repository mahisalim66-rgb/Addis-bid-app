package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.repository.CheretaRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionDetailSheet(
    auction: AuctionItem,
    currentTime: Long,
    lang: Language,
    onDismiss: () -> Unit,
    onNavigateToWallet: () -> Unit
) {
    val repository = remember { CheretaRepository.getInstance() }
    val currentUser by repository.currentUser.collectAsState()
    val allBids by repository.bids.collectAsState()

    val itemBids = remember(allBids, auction.id) {
        allBids.filter { it.auctionId == auction.id }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successCelebration by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("auction_detail_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Category & Live Timer
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CategoryIcon(auction.category, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (lang == Language.AMHARIC) auction.category.titleAm else auction.category.titleEn,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = if (auction.isEnded) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (!auction.isEnded) PulsingLiveDot()
                            Text(
                                text = UiFormatters.formatTimeRemaining(auction.endTimeEpochMs, currentTime, lang),
                                fontWeight = FontWeight.Bold,
                                color = if (auction.isEnded) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFDC2626),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Title & Visual Presentation
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (lang == Language.AMHARIC) auction.titleAm else auction.titleEn,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CategoryIcon(
                                category = auction.category,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(64.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                            ) {
                                TelebirrLogoPill()
                            }
                        }
                    }

                    Text(
                        text = if (lang == Language.AMHARIC) auction.descriptionAm else auction.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Participation & Action Section (0 ETB entry)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == Language.AMHARIC) "የጨረታ ሁኔታ:" else "Auction Status:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Surface(
                                color = TelebirrPrimary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (lang == Language.AMHARIC) "ክፍት ጨረታ" else "Active",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TelebirrPrimary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Success Celebration Banner
                        AnimatedVisibility(visible = successCelebration) {
                            Surface(
                                color = Color(0xFFD1FAE5),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TelebirrGreen)
                                    Text(
                                        text = if (lang == Language.AMHARIC)
                                            "እንኳን ደስ አለዎት! ተሳትፎዎ በተሳካ ሁኔታ ተመዝግቧል።"
                                        else
                                            "Congratulations! Your entry has been successfully registered.",
                                        color = Color(0xFF065F46),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Main Action Button
                        Button(
                            onClick = {
                                val result = repository.placeBid(auction.id)
                                when (result) {
                                    is CheretaRepository.BidResult.Success -> {
                                        errorMessage = null
                                        successCelebration = true
                                    }
                                    is CheretaRepository.BidResult.AuctionEnded -> {
                                        errorMessage = if (lang == Language.AMHARIC) "ይህ ጨረታ ተጠናቋል።" else "This auction has ended."
                                    }
                                    is CheretaRepository.BidResult.Error -> {
                                        errorMessage = result.message
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("place_bid_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TelebirrPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == Language.AMHARIC) "አሁን ተሳተፍ" else "Join Now",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Real-Time Participants List
            item {
                Text(
                    text = if (lang == Language.AMHARIC) "የተሳታፊዎች ዝርዝር (${itemBids.size})" else "Participants (${itemBids.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (itemBids.isEmpty()) {
                item {
                    Text(
                        text = if (lang == Language.AMHARIC) "እስካሁን ምንም ተሳታፊ የለም። የመጀመሪያው ይሁኑ!" else "No participants yet. Be the first to join!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(itemBids) { bid ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column {
                                Text(
                                    text = bid.userName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = UiFormatters.formatDate(bid.timestampEpochMs),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (lang == Language.AMHARIC) "ተሳታፊ" else "Joined",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

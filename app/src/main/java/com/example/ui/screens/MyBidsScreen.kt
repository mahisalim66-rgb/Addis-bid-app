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

@Composable
fun MyBidsScreen(
    onSelectAuction: (AuctionItem) -> Unit,
    onNavigateToAuctions: () -> Unit
) {
    val repository = remember { CheretaRepository.getInstance() }
    val lang by repository.language.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val auctions by repository.auctions.collectAsState()
    val allBids by repository.bids.collectAsState()
    val currentTime by repository.currentTime.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    // Auctions where user participated
    val myBidAuctionIds = remember(allBids, currentUser.id) {
        allBids.filter { it.userId == currentUser.id }.map { it.auctionId }.toSet()
    }

    val myActiveBiddedAuctions = remember(auctions, myBidAuctionIds) {
        auctions.filter { it.id in myBidAuctionIds && !it.isEnded }
    }

    val myWonAuctions = remember(auctions, currentUser.id) {
        auctions.filter { it.winnerId == currentUser.id || (it.highestBidderId == currentUser.id && it.isEnded) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_bids_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Tab Header
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "${if (lang == Language.AMHARIC) "የተሳተፍኩባቸው" else "My Entries"} (${myActiveBiddedAuctions.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (lang == Language.AMHARIC) "ያሸነፍኳቸው" else "Won Items",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                            if (myWonAuctions.isNotEmpty()) {
                                Surface(
                                    color = TelebirrGold,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "${myWonAuctions.size}",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Active Entries
                if (myActiveBiddedAuctions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.TouchApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC)
                                        "እስካሁን በምንም እቃ ላይ አልተሳተፉም።"
                                    else
                                        "You have not joined any active items yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = onNavigateToAuctions,
                                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (lang == Language.AMHARIC) "አሁን ተሳተፍ" else "Explore Items")
                                }
                            }
                        }
                    }
                } else {
                    items(myActiveBiddedAuctions, key = { it.id }) { auction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clickable { onSelectAuction(auction) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    Surface(
                                        color = Color(0xFFD1FAE5),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF065F46),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = if (lang == Language.AMHARIC) "ተሳትፈዋል" else "Joined",
                                                color = Color(0xFF065F46),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = UiFormatters.formatTimeRemaining(auction.endTimeEpochMs, currentTime, lang),
                                        color = Color(0xFFDC2626),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Text(
                                    text = if (lang == Language.AMHARIC) auction.titleAm else auction.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${auction.totalBids} ${if (lang == Language.AMHARIC) "ተሳታፊዎች" else "entries"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = { onSelectAuction(auction) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (lang == Language.AMHARIC) "ዝርዝሩን ይመልከቱ" else "View Details",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Won Items
                if (myWonAuctions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
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
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = TelebirrGold,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC)
                                        "እስካሁን ያሸነፉት እቃ የለም።"
                                    else
                                        "No won items yet. Join more to win!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(myWonAuctions, key = { it.id }) { auction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFB45309))
                                        Text(
                                            text = if (lang == Language.AMHARIC) "እንኳን ደስ አለዎት! ይህን እቃ አሸንፈዋል!" else "Congratulations! You won this item!",
                                            color = Color(0xFF92400E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Text(
                                    text = if (lang == Language.AMHARIC) auction.titleAm else auction.titleEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Voucher Code: AB-${auction.id.takeLast(4).uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (lang == Language.AMHARIC)
                                            "እቃውን ለመረከብ ወይም አድራሻዎን ለማረጋገጥ የአዲስ ቢድ ድጋፍ ክፍል በ 0977112233 ይደውሉ።"
                                        else
                                            "To claim your item, contact Addis Bid support at 0977112233.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

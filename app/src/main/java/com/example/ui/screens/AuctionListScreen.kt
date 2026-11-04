package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.repository.CheretaRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AuctionListScreen(
    onSelectAuction: (AuctionItem) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToGuide: () -> Unit
) {
    val repository = remember { CheretaRepository.getInstance() }
    val lang by repository.language.collectAsState()
    val auctions by repository.auctions.collectAsState()
    val currentTime by repository.currentTime.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AuctionCategory.ALL) }
    var showOnlyLive by remember { mutableStateOf(true) }

    val filteredAuctions = remember(auctions, searchQuery, selectedCategory, showOnlyLive) {
        auctions.filter { item ->
            val matchesCategory = (selectedCategory == AuctionCategory.ALL) || (item.category == selectedCategory)
            val matchesLive = if (showOnlyLive) !item.isEnded && item.endTimeEpochMs > System.currentTimeMillis() else true
            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isEmpty() ||
                    item.titleAm.lowercase().contains(query) ||
                    item.titleEn.lowercase().contains(query)
            matchesCategory && matchesLive && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auction_list_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Promotion Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(TelebirrPrimaryDark, TelebirrPrimary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TelebirrLogoPill()
                            PulsingLiveDot()
                            Text(
                                text = if (lang == Language.AMHARIC) "አዲስ ቢድ • የቀጥታ መድረክ" else "ADDIS BID • LIVE PLATFORM",
                                color = TelebirrGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = if (lang == Language.AMHARIC)
                                "አዲስ ቢድ - አሁኑኑ ይሳተፉ!"
                            else
                                "Addis Bid - Live Auctions Portal!",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = if (lang == Language.AMHARIC)
                                "ክፍትና ግልጽ የጨረታ እና የስጦታ መድረክ ከቴሌብር ጋር።"
                            else
                                "Transparent entries and bidding platform with Telebirr verification.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onNavigateToWallet,
                                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGold),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == Language.AMHARIC) "የቴሌብር ዋሌት" else "Telebirr Wallet",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onNavigateToGuide,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color.White, Color.White))),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == Language.AMHARIC) "ደንቦችና እርዳታ" else "Rules & Help",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Bar & Filter Tabs
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_auctions_input"),
                    placeholder = {
                        Text(
                            text = if (lang == Language.AMHARIC) "የሚፈልጉትን እቃ ይፈልጉ..." else "Search items...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                // Live vs All filter tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = showOnlyLive,
                        onClick = { showOnlyLive = true },
                        label = {
                            Text(
                                if (lang == Language.AMHARIC) "ንቁ እቃዎች (Live)" else "Active Items",
                                fontWeight = if (showOnlyLive) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            if (showOnlyLive) PulsingLiveDot()
                        }
                    )

                    FilterChip(
                        selected = !showOnlyLive,
                        onClick = { showOnlyLive = false },
                        label = {
                            Text(
                                if (lang == Language.AMHARIC) "ሁሉም" else "All Items",
                                fontWeight = if (!showOnlyLive) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                // Category Scrollable Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AuctionCategory.values().forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedCategory = category },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CategoryIcon(
                                    category = category,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (lang == Language.AMHARIC) category.titleAm else category.titleEn,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title & Counter
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == Language.AMHARIC)
                        "የእቃዎች ዝርዝር (${filteredAuctions.size})"
                    else
                        "Items Feed (${filteredAuctions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = TelebirrPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (lang == Language.AMHARIC) "ንቁ ጨረታዎች" else "Live",
                        style = MaterialTheme.typography.labelSmall,
                        color = TelebirrPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Auctions List
        if (filteredAuctions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (lang == Language.AMHARIC) "ምንም እቃ አልተገኘም።" else "No matching items found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(filteredAuctions, key = { it.id }) { auction ->
                AuctionCardClean(
                    auction = auction,
                    currentTime = currentTime,
                    lang = lang,
                    onClick = { onSelectAuction(auction) }
                )
            }
        }
    }
}

@Composable
fun AuctionCardClean(
    auction: AuctionItem,
    currentTime: Long,
    lang: Language,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .testTag("auction_card_${auction.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Category & Countdown Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryIcon(auction.category, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (lang == Language.AMHARIC) auction.category.titleAm else auction.category.titleEn,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = if (auction.isEnded) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!auction.isEnded) PulsingLiveDot()
                        Text(
                            text = UiFormatters.formatTimeRemaining(auction.endTimeEpochMs, currentTime, lang),
                            color = if (auction.isEnded) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Title & Snippet (No market price, no highest bid)
            Text(
                text = if (lang == Language.AMHARIC) auction.titleAm else auction.titleEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (lang == Language.AMHARIC) auction.descriptionAm else auction.descriptionEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))

            // Action Row with Participants & Enter Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${auction.totalBids} ${if (lang == Language.AMHARIC) "ተሳታፊዎች" else "entries"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == Language.AMHARIC) "ተሳተፍ" else "Join Now",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.viewmodel.AddisBidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            val dbId = getString(R.string.firestore_database_id)
            CheretaRepository.getInstance().initFirestore(dbId)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Firestore init skipped or failed", e)
        }

        setContent {
            CheretaTelebirrTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: AddisBidViewModel = viewModel()
) {
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()

    var selectedNavIndex by remember { mutableStateOf(0) }
    var selectedAuctionForDetail by remember { mutableStateOf<AuctionItem?>(null) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val pendingCount = remember(transactions) {
        transactions.count { it.type == TransactionType.DEPOSIT && it.status == TransactionStatus.PENDING }
    }

    // Handle back button: if in secondary tabs or sheets, go back to Auctions tab
    BackHandler(enabled = selectedNavIndex != 0 || selectedAuctionForDetail != null) {
        if (selectedAuctionForDetail != null) {
            selectedAuctionForDetail = null
        } else {
            selectedNavIndex = 0
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold"),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Transparent,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.addis_bid_clean_icon_1793805385988),
                                    contentDescription = "Addis Bid Logo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (lang == Language.AMHARIC) "አዲስ ቢድ" else "Addis Bid",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                }
                                Text(
                                    text = if (lang == Language.AMHARIC) "የጨረታ መድረክ" else "Bidding Platform",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Balance chip with Gold Coin
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { selectedNavIndex = 1 }
                                .padding(end = 6.dp)
                                .testTag("top_bar_balance_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                GoldCoinBadge(size = 20.dp)
                                Text(
                                    text = UiFormatters.formatETB(currentUser.balance),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Language Switcher Button (አማ / EN)
                        FilledTonalButton(
                            onClick = {
                                val nextLang = if (lang == Language.AMHARIC) Language.ENGLISH else Language.AMHARIC
                                viewModel.setLanguage(nextLang)
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("lang_toggle_button")
                        ) {
                            Text(
                                text = if (lang == Language.AMHARIC) "EN" else "አማ",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }

                        // Profile / Account switch button
                        IconButton(
                            onClick = { showAuthDialog = true },
                            modifier = Modifier.testTag("profile_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (currentUser.role == UserRole.ADMIN) TelebirrGold else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Icon(
                                    imageVector = if (currentUser.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = if (currentUser.role == UserRole.ADMIN) Color.Black else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                EthiopianTricolorBar()
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Tab 0: Auctions (ጨረታዎች)
                NavigationBarItem(
                    selected = selectedNavIndex == 0,
                    onClick = { selectedNavIndex = 0 },
                    icon = {
                        Icon(
                            if (selectedNavIndex == 0) Icons.Filled.GridView else Icons.Outlined.GridView,
                            contentDescription = "Items"
                        )
                    },
                    label = {
                        Text(
                            text = if (lang == Language.AMHARIC) "እቃዎች" else "Items",
                            fontSize = 11.sp,
                            fontWeight = if (selectedNavIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("nav_item_auctions")
                )

                // Tab 1: Wallet (ዋሌት)
                NavigationBarItem(
                    selected = selectedNavIndex == 1,
                    onClick = { selectedNavIndex = 1 },
                    icon = {
                        Icon(
                            if (selectedNavIndex == 1) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Wallet"
                        )
                    },
                    label = {
                        Text(
                            text = if (lang == Language.AMHARIC) "ዋሌት" else "Wallet",
                            fontSize = 11.sp,
                            fontWeight = if (selectedNavIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("nav_item_wallet")
                )

                // Tab 2: My Entries (የእኔ)
                NavigationBarItem(
                    selected = selectedNavIndex == 2,
                    onClick = { selectedNavIndex = 2 },
                    icon = {
                        Icon(
                            if (selectedNavIndex == 2) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "My Entries"
                        )
                    },
                    label = {
                        Text(
                            text = if (lang == Language.AMHARIC) "የእኔ" else "My Entries",
                            fontSize = 11.sp,
                            fontWeight = if (selectedNavIndex == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("nav_item_my_bids")
                )

                // Tab 3: Admin (አድሚን) - Visible ONLY when logged in as mameboss98@gmail.com
                if (currentUser.role == UserRole.ADMIN && currentUser.email.equals("mameboss98@gmail.com", ignoreCase = true)) {
                    NavigationBarItem(
                        selected = selectedNavIndex == 3,
                        onClick = { selectedNavIndex = 3 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingCount > 0) {
                                        Badge(containerColor = Color(0xFFEF4444)) {
                                            Text("$pendingCount", color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (selectedNavIndex == 3) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    contentDescription = "Admin"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = if (lang == Language.AMHARIC) "አድሚን" else "Admin",
                                fontSize = 11.sp,
                                fontWeight = if (selectedNavIndex == 3) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_item_admin")
                    )
                }

                // Tab 4: Guide (ደንቦችና እርዳታ)
                NavigationBarItem(
                    selected = selectedNavIndex == 4,
                    onClick = { selectedNavIndex = 4 },
                    icon = {
                        Icon(
                            if (selectedNavIndex == 4) Icons.Filled.HelpOutline else Icons.Outlined.HelpOutline,
                            contentDescription = "Help & Rules"
                        )
                    },
                    label = {
                        Text(
                            text = if (lang == Language.AMHARIC) "እርዳታ" else "Help",
                            fontSize = 11.sp,
                            fontWeight = if (selectedNavIndex == 4) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("nav_item_guide")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNavIndex) {
                0 -> AuctionListScreen(
                    onSelectAuction = { selectedAuctionForDetail = it },
                    onNavigateToWallet = { selectedNavIndex = 1 },
                    onNavigateToGuide = { selectedNavIndex = 4 }
                )
                1 -> WalletScreen(
                    onNavigateToAdmin = { selectedNavIndex = 3 }
                )
                2 -> MyBidsScreen(
                    onSelectAuction = { selectedAuctionForDetail = it },
                    onNavigateToAuctions = { selectedNavIndex = 0 }
                )
                3 -> AdminPanelScreen(
                    onNavigateToAuction = { selectedAuctionForDetail = it }
                )
                4 -> GuideAndHelpScreen(
                    onNavigateToAdmin = { selectedNavIndex = 3 }
                )
            }
        }
    }

    // Auction Detail Modal Sheet
    selectedAuctionForDetail?.let { auction ->
        AuctionDetailSheet(
            auction = auction,
            currentTime = currentTime,
            lang = lang,
            onDismiss = { selectedAuctionForDetail = null },
            onNavigateToWallet = {
                selectedAuctionForDetail = null
                selectedNavIndex = 1
            }
        )
    }

    // Profile & Role Switching Dialog
    if (showAuthDialog) {
        AuthDialog(
            lang = lang,
            onDismiss = { showAuthDialog = false }
        )
    }
}

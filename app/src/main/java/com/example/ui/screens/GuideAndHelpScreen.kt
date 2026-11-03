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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.repository.CheretaRepository
import com.example.ui.components.TelebirrLogoPill
import com.example.ui.theme.*

@Composable
fun GuideAndHelpScreen(
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { CheretaRepository.getInstance() }
    val lang by repository.language.collectAsState()
    val settings by repository.settings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("guide_help_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header (Addis Bid branding)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TelebirrPrimary
                        ) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (lang == Language.AMHARIC) "አዲስ ቢድ (Addis Bid)" else "Addis Bid Portal",
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TelebirrLogoPill()
                            }
                            Text(
                                text = if (lang == Language.AMHARIC)
                                    "የ GitHub እና የቴሌብር መተግበሪያ መመሪያ"
                                else
                                    "GitHub & Telebirr Deployment Center",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = if (lang == Language.AMHARIC)
                            "ኮዱን በቀጥታ GitHub ላይ ለመጫን፣ ዌብሳይት ላይ ለማገናኘትና ምንም ሳያስተካክሉ ዝግጁ በሆነ መልኩ ለመጠቀም የተዘጋጀ መመሪያ።"
                        else
                            "Ready-to-deploy GitHub repository guide, zero manual adjustments required.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // GitHub Repository Box (as requested: "እንደ ዌብሳይት ጊትሃብ ላይ ምናምን ነገር እናድርገው... ማስተካክለው ነገር የለም")
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = if (lang == Language.AMHARIC) "GitHub & Website መመሪያ (ምንም ማስተካከል አይጠበቅብዎትም)" else "GitHub & Website Setup (Zero Adjustments)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Text(
                        text = if (lang == Language.AMHARIC)
                            "ይህ ሙሉ ኮድ ለ GitHub ዝግጁ ሆኖ ተዋቅሯል (.gitignore, README, Gradle configurations)። ምንም ነገር መለወጥ ሳያስፈልግዎ በቀጥታ GitHub ላይ መጫን ይችላሉ።"
                        else
                            "This codebase is fully preconfigured for GitHub (.gitignore, README, Kotlin Gradle). You can push immediately without changing files.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Git Commands snippet with Copy
                    val gitCommands = """
git init
git add .
git commit -m "Initial release of Addis Bid App"
git branch -M main
git remote add origin ${settings.githubRepoUrl}.git
git push -u origin main
                    """.trimIndent()

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Git Terminal Commands:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Git Commands", gitCommands))
                                        Toast.makeText(context, "የትዕዛዝ ኮዶቹ ኮፒ ተደርገዋል!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text(
                                text = gitCommands,
                                fontSize = 12.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Next Steps
        item {
            GuideExpandableCard(
                icon = Icons.Default.DirectionsRun,
                titleAm = "1. አሁን ፋይሎቹን ከወሰዱ በኋላ ወዴት ነው የሚሄዱት?",
                titleEn = "1. Where do I go next after getting this app?",
                contentAm = """
                    • ደረጃ 1 (APK ማውረድ)፡ ከ AI Studio የላይኛው ሜኑ ወይም Settings በመግባት 'Download APK' ወይም 'Export Project' የሚለውን ይጫኑ። ይህ ዝግጁ የሆነውን የሞባይል መተግበሪያ (APK) ይሰጥዎታል።
                    
                    • ደረጃ 2 (በስልክዎ መሞከር)፡ የወረደውን .apk ፋይል በእርስዎ አንድሮይድ ስልክ ላይ ጭነው አዲስ ቢድ መተግበሪያን እና የአድሚን ፓነሉን በተግባር ይሞክሩት።
                    
                    • ደረጃ 3 (ቴሌግራም ወይም GitHub ላይ ማሰራጨት)፡ የራስዎን የቴሌግራም ቻናል ከፍተው የ APK ፋይሉን ይልቀቁ ወይም በ GitHub Releases ገጽ ላይ ለተጠቃሚዎች ያጋሩ።
                    
                    • ደረጃ 4 (Google Play Store)፡ መተግበሪያውን በይፋ በፕሌይ ስቶር ለማሰራጨት የGoogle Play Console አካውንት ($25 አንድ ጊዜ ብቻ የሚከፈል) በመክፈት APK/AAB ፋይሉን ይጫኑ።
                """.trimIndent(),
                contentEn = """
                    • Step 1 (Download APK): Export your ready-to-run APK from AI Studio Settings.
                    • Step 2 (Test on Phone): Install the APK on your device to test Addis Bid features.
                    • Step 3 (Share on Telegram or GitHub): Share your APK directly on your Telegram community channel or GitHub Releases.
                    • Step 4 (Publish on Google Play): Register a Google Play Console account ($25 one-time fee) to publish to millions of Android users.
                """.trimIndent(),
                lang = lang
            )
        }

        // Section 2: Telebirr Integration & Merchant Setup
        item {
            GuideExpandableCard(
                icon = Icons.Default.AccountBalance,
                titleAm = "2. ከኢትዮ ቴሌኮም ጋር የቴሌብር ነጋዴ (Merchant) መሆን",
                titleEn = "2. Becoming a Telebirr Merchant with Ethio Telecom",
                contentAm = """
                    • የንግድ ፈቃድ ማውጣት፡ በአቅራቢያዎ ወደሚገኝ ንግድና ቀጠናዊ ትስስር ቢሮ በመሄድ የኦንላይን አገልግሎት ወይም ዲጂታል መድረክ ፈቃድ ያውጡ።
                    
                    • ኢትዮ ቴሌኮም መሄድ፡ ንግድ ፈቃድዎን ይዘው በአቅራቢያዎ ወደሚገኝ የኢትዮ ቴሌኮም ዋና ቢሮ በመሄድ 'የቴሌብር ነጋዴ አካውንት (Merchant Account)' እና 'አጭር ኮድ (Shortcode)' እንዲሰጥዎ ይጠይቁ።
                    
                    • የቴሌብር ክፍያ መቀበያ ቁጥርዎን ማስገባት፡ ኢትዮ ቴሌኮም የሰጠዎትን የቴሌብር ስልክ ቁጥርና አጭር ኮድ በዚህ አፕሊኬሽን ውስጥ ባለው 'አድሚን ፓነል' -> 'የቴሌብር ቅንብር' ውስጥ ያስገቡት።
                    
                    • ደንበኞች በዚያ ቁጥር ሲልኩ የላኩበትን SMS ኮድ በአድሚን ፓነሉ አይተው በ1 ክሊክ 'አጽድቅ' ይሉታል።
                """.trimIndent(),
                contentEn = """
                    • Register business license: Get an e-commerce or digital platform trade license.
                    • Visit Ethio Telecom: Apply for a Telebirr Merchant Account & Shortcode with your trade documents.
                    • Enter your merchant details: Open this app's Admin Panel -> Telebirr Setup and enter your receiver phone & shortcode.
                    • Users will pay directly to your Telebirr account, and you verify in Admin Panel!
                """.trimIndent(),
                lang = lang
            )
        }

        // Section 3: Daily Admin Operations
        item {
            GuideExpandableCard(
                icon = Icons.Default.AdminPanelSettings,
                titleAm = "3. የእርስዎ የዕለት ተዕለት የአድሚን ስራ ምን ይመስላል?",
                titleEn = "3. Daily Admin Operations & Workflows",
                contentAm = """
                    1. የቴሌብር ማረጋገጫ (Deposit Approval):
                    ደንበኞች በቴሌብር የላኩትን ገንዘብና የደረሰኝ ቁጥር (Transaction ID) በአድሚን ፓነሉ ላይ ያያሉ። የእርስዎን የቴሌብር SMS በማየት ትክክለኛነቱን አረጋግጠው 'አጽድቅ' ሲሉት ለደንበኛው ዋሌት በሰከንዶች ውስጥ ይገባል።
                    
                    2. አዳዲስ እቃዎችን መክፈት:
                    በየቀኑ ወይም በየሳምንቱ ተወዳጅ እቃዎችን በ'አዲስ እቃ' ፎርም ይጀምሩ።
                    
                    3. አሸናፊዎችን ማስተናገድ:
                    ጨረታው ሲጠናቀቅ አሸናፊውን በመደወል ያነጋግሩና እቃውን ያስረክቡ።
                """.trimIndent(),
                contentEn = """
                    1. Verify Deposits: Cross-check user SMS Transaction IDs with your Telebirr receiver messages, click Approve to instantly credit wallet.
                    2. Launch Items: Keep your feed fresh by launching new attractive tech, vehicles, and voucher items.
                    3. Winner Fulfillment: Contact the winner to arrange item handover or courier delivery in Addis Ababa.
                """.trimIndent(),
                lang = lang
            )
        }

        // Jump to Admin Button
        item {
            Button(
                onClick = onNavigateToAdmin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == Language.AMHARIC) "ወደ አድሚን መቆጣጠሪያ ፓነል ሂድ" else "Go to Admin Panel Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}


@Composable
fun GuideExpandableCard(
    icon: ImageVector,
    titleAm: String,
    titleEn: String,
    contentAm: String,
    contentEn: String,
    lang: Language
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }

                    Text(
                        text = if (lang == Language.AMHARIC) titleAm else titleEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle"
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (lang == Language.AMHARIC) contentAm else contentEn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

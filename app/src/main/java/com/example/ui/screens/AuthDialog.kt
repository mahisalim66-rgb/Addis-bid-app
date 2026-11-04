package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.UserRole
import com.example.repository.CheretaRepository
import com.example.ui.components.TelebirrLogoPill
import com.example.ui.theme.*

@Composable
fun AuthDialog(
    lang: Language,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { CheretaRepository.getInstance() }
    val currentUser by repository.currentUser.collectAsState()
    val isAdmin = repository.isAdminUser()

    var phoneInput by remember { mutableStateOf(if (isAdmin) "0911000000" else currentUser.phone) }
    var pinInput by remember { mutableStateOf("1234") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (phoneInput.isBlank() || phoneInput.length < 9) {
                        Toast.makeText(
                            context,
                            if (lang == Language.AMHARIC) "ትክክለኛ ስልክ ቁጥር ያስገቡ" else "Please enter valid phone",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    val userName = if (phoneInput.contains("1234")) "አበበ ደስታ" else "ተጠቃሚ ${phoneInput.takeLast(4)}"
                    repository.loginUser(userName, phoneInput, "", false)
                    onDismiss()
                    Toast.makeText(
                        context,
                        if (lang == Language.AMHARIC) "እንኳን ደህና መጡ $userName!" else "Welcome $userName!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("auth_submit_button")
            ) {
                Text(
                    text = if (lang == Language.AMHARIC) "ይግቡ" else "Log In",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == Language.AMHARIC) "ተመለስ" else "Close")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelebirrLogoPill()
                Text(
                    text = if (lang == Language.AMHARIC) "መግቢያ (Login)" else "User Login",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If already logged in as Admin, show banner
                if (isAdmin) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFB45309))
                                Column {
                                    Text(
                                        text = if (lang == Language.AMHARIC) "ዋና አድሚን ነዎት" else "Active Admin",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF78350F)
                                    )
                                    Text(
                                        text = "mameboss98@gmail.com",
                                        fontSize = 11.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                            TextButton(
                                onClick = {
                                    repository.logoutAdmin()
                                    Toast.makeText(context, if (lang == Language.AMHARIC) "ከአድሚን ወጥተዋል" else "Logged out", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text(if (lang == Language.AMHARIC) "ውጣ" else "Logout", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 1-Tap Quick Switch card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (lang == Language.AMHARIC) "ፈጣን 1-ንክኪ መግቢያ (1-Tap Switch):" else "Instant 1-Tap Login:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    repository.loginUser("አበበ ደስታ (Abebe Desta)", "0912345678", "", false)
                                    onDismiss()
                                    Toast.makeText(
                                        context,
                                        if (lang == Language.AMHARIC) "በተጫራችነት ገብተዋል (አበበ ደስታ)" else "Logged in as Bidder",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (lang == Language.AMHARIC) "ተጫራች" else "Bidder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    repository.loginAsAdminWithGoogleEmail("mameboss98@gmail.com")
                                    onDismiss()
                                    Toast.makeText(
                                        context,
                                        if (lang == Language.AMHARIC) "እንኳን ደህና መጡ አድሚን (mameboss98@gmail.com)!" else "Welcome Admin (Mame Boss)!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGold),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (lang == Language.AMHARIC) "አድሚን (Mame)" else "Admin (Mame)",
                                    fontSize = 11.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Text(
                    text = if (lang == Language.AMHARIC) "ወይም በስልክ ቁጥርዎ ይግቡ፦" else "Or sign in with custom phone:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text(if (lang == Language.AMHARIC) "የቴሌብር ስልክ ቁጥር" else "Telebirr Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text(if (lang == Language.AMHARIC) "የይለፍ ፒን (4 አሃዝ)" else "Security PIN (4 digits)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}

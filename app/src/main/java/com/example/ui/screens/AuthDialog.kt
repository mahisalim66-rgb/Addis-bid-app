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

    var isSignUp by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(currentUser.name) }
    var phoneInput by remember { mutableStateOf(currentUser.phone) }
    var emailInput by remember { mutableStateOf(currentUser.email) }
    var pinInput by remember { mutableStateOf("1234") }
    var asAdmin by remember { mutableStateOf(currentUser.role == UserRole.ADMIN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (asAdmin && !emailInput.trim().equals("mameboss98@gmail.com", ignoreCase = true)) {
                        Toast.makeText(
                            context,
                            if (lang == Language.AMHARIC) "አድሚን መሆን የሚችለው mameboss98@gmail.com ብቻ ነው!" else "Admin is restricted to mameboss98@gmail.com only!",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    repository.loginUser(nameInput, phoneInput, emailInput, asAdmin)
                    onDismiss()
                    Toast.makeText(
                        context,
                        if (asAdmin) "በአድሚንነት (mameboss98@gmail.com) ገብተዋል!" else "እንኳን ደህና መጡ ${nameInput.take(15)}!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("auth_submit_button")
            ) {
                Text(
                    text = if (isSignUp)
                        (if (lang == Language.AMHARIC) "ይመዝገቡ" else "Register")
                    else
                        (if (lang == Language.AMHARIC) "ይግቡ" else "Log In"),
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
                    text = if (isSignUp)
                        (if (lang == Language.AMHARIC) "አዲስ መለያ መክፈቻ" else "Create Account")
                    else
                        (if (lang == Language.AMHARIC) "መግቢያ (Login)" else "User Login"),
                    fontWeight = FontWeight.Bold
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
                Text(
                    text = if (lang == Language.AMHARIC)
                        "የቴሌብር ስልክ ቁጥርዎን በማስገባት ይጫረቱ ወይም ክፍያዎን ያስተዳድሩ።"
                    else
                        "Enter your Telebirr phone number to participate or manage bids.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick Switch presets for testing
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (lang == Language.AMHARIC) "ፈጣን መግቢያ (Quick Switch):" else "Quick Test Accounts:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    nameInput = "አበበ ደስታ (Abebe Desta)"
                                    phoneInput = "0912345678"
                                    emailInput = ""
                                    asAdmin = false
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (lang == Language.AMHARIC) "ተጫራች (Abebe)" else "Bidder (Abebe)", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    nameInput = "Mame Boss"
                                    phoneInput = "0911000000"
                                    emailInput = "mameboss98@gmail.com"
                                    asAdmin = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGold),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (lang == Language.AMHARIC) "አድሚን (Mame)" else "Admin (Mame)", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (isSignUp || asAdmin) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(if (lang == Language.AMHARIC) "ሙሉ ስም" else "Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text(if (asAdmin) "የአድሚን ኢሜይል (mameboss98@gmail.com)" else "ኢሜይል (Email)") },
                        placeholder = { Text(if (asAdmin) "mameboss98@gmail.com" else "user@example.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text(if (lang == Language.AMHARIC) "የቴሌብር ስልክ ቁጥር" else "Telebirr Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text(if (lang == Language.AMHARIC) "የይለፍ ፒን (4 አሃዝ)" else "Security PIN (4 digits)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { isSignUp = !isSignUp }) {
                        Text(
                            text = if (isSignUp)
                                (if (lang == Language.AMHARIC) "መለያ አለዎት? ይግቡ" else "Have account? Log in")
                            else
                                (if (lang == Language.AMHARIC) "አዲስ መለያ ይክፈቱ" else "Need account? Sign up"),
                            fontSize = 12.sp
                        )
                    }

                    FilterChip(
                        selected = asAdmin,
                        onClick = { asAdmin = !asAdmin },
                        label = { Text("Admin", fontSize = 11.sp) }
                    )
                }
            }
        }
    )
}

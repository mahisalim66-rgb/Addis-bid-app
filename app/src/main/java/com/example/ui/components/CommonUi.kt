package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object UiFormatters {
    fun formatETB(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 2
        return "${formatter.format(amount)} ብር"
    }

    fun formatTimeRemaining(endEpochMs: Long, currentEpochMs: Long, lang: Language): String {
        val diff = endEpochMs - currentEpochMs
        if (diff <= 0) {
            return if (lang == Language.AMHARIC) "የተጠናቀቀ" else "Ended"
        }
        val seconds = (diff / 1000) % 60
        val minutes = (diff / (1000 * 60)) % 60
        val hours = (diff / (1000 * 60 * 60)) % 24
        val days = diff / (1000 * 60 * 60 * 24)

        return if (days > 0) {
            if (lang == Language.AMHARIC) "$days ቀን $hours ሰዓት" else "${days}d ${hours}h"
        } else {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    fun formatDate(epochMs: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }
}

@Composable
fun GoldCoinBadge(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 22.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color(0xFFFFD700),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD97706))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "ብር",
                fontSize = (size.value * 0.44).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF78350F),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TelebirrLogoPill(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(20.dp)),
        color = TelebirrPrimary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.White, CircleShape)
            )
            Text(
                text = "telebirr",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: TransactionStatus,
    lang: Language,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, labelAm, labelEn) = when (status) {
        TransactionStatus.PENDING -> Quadruple(
            Color(0xFFFEF3C7),
            Color(0xFF92400E),
            "በመጠባበቅ ላይ",
            "Pending"
        )
        TransactionStatus.APPROVED -> Quadruple(
            Color(0xFFD1FAE5),
            Color(0xFF065F46),
            "ተረጋግጧል",
            "Approved"
        )
        TransactionStatus.REJECTED -> Quadruple(
            Color(0xFFFEE2E2),
            Color(0xFF991B1B),
            "ውድቅ የተደረገ",
            "Rejected"
        )
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = bgColor
    ) {
        Text(
            text = if (lang == Language.AMHARIC) labelAm else labelEn,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun CategoryIcon(
    category: AuctionCategory,
    tint: Color = TelebirrPrimary,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (category) {
        AuctionCategory.ALL -> Icons.Default.AllInclusive
        AuctionCategory.PHONES -> Icons.Default.Smartphone
        AuctionCategory.VEHICLES -> Icons.Default.DirectionsCar
        AuctionCategory.ELECTRONICS -> Icons.Default.Devices
        AuctionCategory.JEWELRY -> Icons.Default.Diamond
        AuctionCategory.REAL_ESTATE -> Icons.Default.Apartment
        AuctionCategory.VOUCHERS -> Icons.Default.CardGiftcard
    }
    Icon(
        imageVector = icon,
        contentDescription = category.titleEn,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun PulsingLiveDot(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(10.dp)
            .scale(scale)
            .background(Color(0xFFEF4444), CircleShape)
    )
}

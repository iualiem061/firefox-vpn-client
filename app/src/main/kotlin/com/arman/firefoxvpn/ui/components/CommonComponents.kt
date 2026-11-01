package com.arman.firefoxvpn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.firefoxvpn.model.ConnectionStatus
import com.arman.firefoxvpn.ui.theme.*

/**
 * Soft Glassmorphism Card Container.
 * Features a frosted translucent surface with a delicate top-lit glass specular edge.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface == DarkSurface || MaterialTheme.colorScheme.background == DarkBackground

    val actualBg = if (backgroundColor != Color.Unspecified) {
        backgroundColor
    } else if (isDark) {
        Color(0xFF161829).copy(alpha = 0.65f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.78f)
    }

    val actualBorder = if (borderColor != Color.Unspecified) {
        borderColor
    } else if (isDark) {
        Color(0x38FFFFFF)
    } else {
        Color(0x3D6C5CE7)
    }

    // Top-lit frosted gradient
    val glassGradient = Brush.verticalGradient(
        colors = listOf(
            actualBg.copy(alpha = (actualBg.alpha * 1.15f).coerceIn(0.15f, 0.95f)),
            actualBg.copy(alpha = (actualBg.alpha * 0.85f).coerceIn(0.10f, 0.90f))
        )
    )

    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            actualBorder,
            actualBorder.copy(alpha = actualBorder.alpha * 0.35f)
        )
    )

    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                spotColor = if (isDark) Color(0x66000000) else Color(0x226C5CE7),
                ambientColor = if (isDark) Color(0x33000000) else Color(0x116C5CE7)
            )
            .clip(shape)
            .background(glassGradient)
            .border(width = 1.2.dp, brush = borderBrush, shape = shape),
        content = content
    )
}

/**
 * Enhanced Soft Glassmorphism Power Button with multi-wave pulsing rings and specular crystal lens.
 */
@Composable
fun PowerConnectButton(
    status: ConnectionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale2"
    )

    val (glowColor, buttonGradients, statusIconColor) = when (status) {
        ConnectionStatus.CONNECTED -> Triple(
            VpnEmerald,
            listOf(Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFF00897B)),
            Color.White
        )
        ConnectionStatus.CONNECTING -> Triple(
            VpnAmber,
            listOf(Color(0xFFFFB300), Color(0xFFFF8F00), Color(0xFFE65100)),
            Color.White
        )
        ConnectionStatus.ERROR -> Triple(
            VpnRose,
            listOf(Color(0xFFFF2A6D), Color(0xFFD50000), Color(0xFF880E4F)),
            Color.White
        )
        ConnectionStatus.DISCONNECTING -> Triple(
            Color(0xFF90A4AE),
            listOf(Color(0xFF78909C), Color(0xFF546E7A), Color(0xFF37474F)),
            Color.White
        )
        else -> Triple(
            VpnPrimary,
            listOf(Color(0xFF8A2387), Color(0xFF6C5CE7), Color(0xFF3B2F8F)),
            Color.White
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(175.dp)
            .testTag("power_connect_container")
    ) {
        // Outer ambient blur glow
        if (status == ConnectionStatus.CONNECTING || status == ConnectionStatus.CONNECTED) {
            Box(
                modifier = Modifier
                    .size(165.dp)
                    .scale(pulseScale2)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.18f))
            )
            Box(
                modifier = Modifier
                    .size(145.dp)
                    .scale(pulseScale1)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.32f))
            )
        } else {
            // Soft dormant aura
            Box(
                modifier = Modifier
                    .size(145.dp)
                    .clip(CircleShape)
                    .background(VpnPrimary.copy(alpha = 0.12f))
            )
        }

        // Frosted Glass Ring
        Box(
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.5.dp, Color.White.copy(alpha = 0.22f), CircleShape)
        )

        // Core Glowing Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(114.dp)
                .shadow(20.dp, CircleShape, spotColor = glowColor, ambientColor = glowColor.copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(Brush.sweepGradient(buttonGradients))
                .border(2.5.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0.15f))), CircleShape)
                .clickable { onClick() }
                .testTag("power_connect_button")
        ) {
            // Specular glass highlight reflection
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                            radius = 120f
                        )
                    )
            )

            Icon(
                imageVector = Icons.Outlined.PowerSettingsNew,
                contentDescription = "Toggle MaRy VPN Connection",
                tint = statusIconColor,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

/**
 * Soft Glassmorphism Status Badge
 */
@Composable
fun StatusBadge(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    val (text, color, bgColor) = when (status) {
        ConnectionStatus.CONNECTED -> Triple("PROTECTED", VpnEmerald, VpnEmerald.copy(alpha = 0.18f))
        ConnectionStatus.CONNECTING -> Triple("CONNECTING…", VpnAmber, VpnAmber.copy(alpha = 0.18f))
        ConnectionStatus.DISCONNECTING -> Triple("DISCONNECTING", Color(0xFFA4B0BE), Color(0xFFA4B0BE).copy(alpha = 0.18f))
        ConnectionStatus.ERROR -> Triple("ERROR", VpnRose, VpnRose.copy(alpha = 0.18f))
        ConnectionStatus.WAITING_FOR_NETWORK -> Triple("WAITING FOR NETWORK", VpnAmber, VpnAmber.copy(alpha = 0.18f))
        ConnectionStatus.DISCONNECTED -> Triple("DISCONNECTED", Color(0xFFA4B0BE), Color(0xFFA4B0BE).copy(alpha = 0.18f))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(bgColor)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("status_badge_${status.name.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
                .shadow(4.dp, CircleShape, spotColor = color)
        )
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp
        )
    }
}

/**
 * Circular Quota Meter with Glass Gradient
 */
@Composable
fun CircularQuotaGauge(
    usedPercentage: Float,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.5.dp,
    trackColor: Color = Color.White.copy(alpha = 0.12f),
    fillColor: Color = VpnPrimary
) {
    Canvas(modifier = modifier.size(size)) {
        val sweep = (usedPercentage.coerceIn(0f, 1f)) * 360f
        drawCircle(
            color = trackColor,
            style = Stroke(width = strokeWidth.toPx())
        )
        drawArc(
            color = fillColor,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

package com.arman.firefoxvpn.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.firefoxvpn.model.ConnectionStatus
import com.arman.firefoxvpn.model.LogLevel
import com.arman.firefoxvpn.ui.MainViewModel
import com.arman.firefoxvpn.ui.components.CircularQuotaGauge
import com.arman.firefoxvpn.ui.components.GlassCard
import com.arman.firefoxvpn.ui.components.PowerConnectButton
import com.arman.firefoxvpn.ui.components.StatusBadge
import com.arman.firefoxvpn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    viewModel: MainViewModel,
    onNavigateToServers: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status by viewModel.status.collectAsState()
    val server by viewModel.selectedServer.collectAsState()
    val traffic by viewModel.trafficStats.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val showIp by viewModel.showPublicIp.collectAsState()
    val isTesting by viewModel.isTestingConnection.collectAsState()
    val testResult by viewModel.lastTestResult.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showTestDialog by remember { mutableStateOf(false) }

    val activeAccount = accounts.find { it.isActive } ?: accounts.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("overview_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Soft Glass Brand Header
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("brand_header_card"),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(8.dp, CircleShape, spotColor = VpnPrimary)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(NeonCyan, VpnPrimary, NeonPurple))
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "MaRy VPN Logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "MaRy VPN",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Soft Glass Security Tunnel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    StatusBadge(status = status)
                }
            }
        }

        // 2. Main Hero Power Card (Soft Glassmorphic)
        item {
            GlassCard(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_power_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PowerConnectButton(
                        status = status,
                        onClick = { viewModel.toggleConnection() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (status) {
                            ConnectionStatus.CONNECTED -> "Your Connection is Encrypted"
                            ConnectionStatus.CONNECTING -> "Establishing Secure Glass Tunnel…"
                            ConnectionStatus.DISCONNECTING -> "Tearing Down Tunnel…"
                            ConnectionStatus.ERROR -> "Connection Failed · Tap to Retry"
                            ConnectionStatus.WAITING_FOR_NETWORK -> "Awaiting Network Connection…"
                            ConnectionStatus.DISCONNECTED -> "Tap Power to Protect Device"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when (status) {
                            ConnectionStatus.CONNECTED -> VpnEmerald
                            ConnectionStatus.CONNECTING -> VpnAmber
                            ConnectionStatus.ERROR -> VpnRose
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Location Card Inside Glass
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
                            .clickable { onNavigateToServers() }
                            .testTag("selected_server_row"),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = server.flagEmoji,
                                    fontSize = 24.sp
                                )
                                Column {
                                    Text(
                                        text = "${server.city}, ${server.countryName}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (showIp) "Exit IP: ${server.ipAddress}" else "IP Hidden · Secure",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = VpnEmerald.copy(alpha = 0.18f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VpnEmerald.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "${server.latencyMs} ms",
                                        color = VpnEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Change Server",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.runConnectionTest()
                                showTestDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_connection_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White.copy(alpha = 0.06f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTesting) "Testing…" else "Test Speed")
                        }

                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(server.ipAddress))
                                Toast.makeText(context, "Copied IP: ${server.ipAddress}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_ip_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White.copy(alpha = 0.06f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                tint = VpnPrimaryLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy IP")
                        }
                    }
                }
            }
        }

        // 3. Live Stats KPI Cards Grid (Soft Glass)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // KPI: Speed
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Live Bandwidth",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Download",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = MainViewModel.formatRate(traffic.downloadRateBps),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Upload",
                                tint = NeonMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = MainViewModel.formatRate(traffic.uploadRateBps),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // KPI: Duration & Volume
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Session Time",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = MainViewModel.formatDuration(traffic.durationSeconds),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Transferred: ${MainViewModel.formatBytes(traffic.totalDownloadBytes + traffic.totalUploadBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Active Account Summary (Soft Glass)
        if (activeAccount != null) {
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAccounts() }
                        .testTag("overview_active_account_card"),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularQuotaGauge(
                                usedPercentage = if (activeAccount.quotaTotalBytes > 0) {
                                    activeAccount.quotaUsedBytes.toFloat() / activeAccount.quotaTotalBytes.toFloat()
                                } else 0.15f,
                                size = 46.dp
                            )
                            Column {
                                Text(
                                    text = activeAccount.email,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${activeAccount.planName} · ${activeAccount.connectedDevices}/${activeAccount.maxDevices} devices",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Manage Account",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 5. Recent Activity Logs Card (Soft Glass)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Activity Audit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = onNavigateToLogs,
                            modifier = Modifier.testTag("view_all_logs_button")
                        ) {
                            Text("All Logs", color = VpnPrimaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    logs.take(3).forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = log.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (log.level) {
                                    LogLevel.INFO -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                    LogLevel.WARN -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    LogLevel.ERROR -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                    LogLevel.DEBUG -> Color(0xFF94A3B8).copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = log.level.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (log.level) {
                                        LogLevel.INFO -> Color(0xFF60A5FA)
                                        LogLevel.WARN -> Color(0xFFFBBF24)
                                        LogLevel.ERROR -> Color(0xFFF87171)
                                        LogLevel.DEBUG -> Color(0xFF94A3B8)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = log.message,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Diagnostics Dialog with Glass Styling
    if (showTestDialog) {
        AlertDialog(
            onDismissRequest = { if (!isTesting) showTestDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = NeonCyan
                    )
                    Text("Connection Diagnostic")
                }
            },
            text = {
                if (isTesting) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = NeonCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sending probes through encrypted tunnel…")
                    }
                } else {
                    testResult?.let { res ->
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Total Latency: ${res.totalMs} ms (Exit: ${res.exitCountry})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = VpnEmerald
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                            res.stages.forEach { stage ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(stage.name, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${stage.latencyMs} ms",
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (stage.isSuccess) VpnEmerald else VpnRose
                                    )
                                }
                            }
                        }
                    } ?: Text("No test results yet.")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showTestDialog = false },
                    enabled = !isTesting
                ) {
                    Text("Close", color = VpnPrimaryLight)
                }
            },
            dismissButton = {
                if (!isTesting) {
                    TextButton(onClick = { viewModel.runConnectionTest() }) {
                        Text("Retest", color = NeonCyan)
                    }
                }
            }
        )
    }
}

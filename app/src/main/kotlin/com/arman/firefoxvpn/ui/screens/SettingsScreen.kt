package com.arman.firefoxvpn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AltRoute
import androidx.compose.material.icons.automirrored.outlined.CallSplit
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
import com.arman.firefoxvpn.model.ConnectionMode
import com.arman.firefoxvpn.model.DnsProvider
import com.arman.firefoxvpn.model.TransportProtocol
import com.arman.firefoxvpn.ui.MainViewModel
import com.arman.firefoxvpn.ui.components.GlassCard
import com.arman.firefoxvpn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToRouting: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionMode by viewModel.connectionMode.collectAsState()
    val transportProtocol by viewModel.transportProtocol.collectAsState()
    val dnsProvider by viewModel.dnsProvider.collectAsState()
    val customDns by viewModel.customDnsAddress.collectAsState()
    val proxyPort by viewModel.proxyPort.collectAsState()
    val showIp by viewModel.showPublicIp.collectAsState()
    val showSpeed by viewModel.showSpeedInNotification.collectAsState()
    val autoConnect by viewModel.autoConnect.collectAsState()
    val killSwitch by viewModel.killSwitch.collectAsState()

    var showPortDialog by remember { mutableStateOf(false) }
    var tempPortText by remember { mutableStateOf(proxyPort.toString()) }

    var showTransportDialog by remember { mutableStateOf(false) }
    var showDnsDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Connection & Protocol
        item {
            Text(
                text = "Connection & Protocol",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        item {
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Outlined.AltRoute,
                        title = "Transport Protocol",
                        subtitle = transportProtocol.label,
                        onClick = { showTransportDialog = true },
                        testTag = "settings_transport_row"
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(start = 56.dp))
                    SettingsRow(
                        icon = Icons.Outlined.VpnLock,
                        title = "Connection Mode",
                        subtitle = connectionMode.label,
                        onClick = { showModeDialog = true },
                        testTag = "settings_mode_row"
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(start = 56.dp))
                    SettingsRow(
                        icon = Icons.Outlined.SettingsEthernet,
                        title = "Local Proxy Port",
                        subtitle = "127.0.0.1:$proxyPort",
                        onClick = {
                            tempPortText = proxyPort.toString()
                            showPortDialog = true
                        },
                        testTag = "settings_port_row"
                    )
                }
            }
        }

        // Section: Routing & Split Tunneling
        item {
            Text(
                text = "Application Routing",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        item {
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingsRow(
                    icon = Icons.AutoMirrored.Outlined.CallSplit,
                    title = "App Permissions (Split Tunnel)",
                    subtitle = "Configure which apps bypass MaRy VPN",
                    onClick = onNavigateToRouting,
                    testTag = "settings_routing_row"
                )
            }
        }

        // Section: Privacy & Security
        item {
            Text(
                text = "Privacy & Security",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        item {
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Outlined.Dns,
                        title = "Secure DNS Provider",
                        subtitle = dnsProvider.label,
                        onClick = { showDnsDialog = true },
                        testTag = "settings_dns_row"
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(start = 56.dp))
                    SettingsToggleRow(
                        icon = Icons.Outlined.Shield,
                        title = "Kill Switch",
                        subtitle = "Block all unencrypted traffic when VPN drops",
                        checked = killSwitch,
                        onCheckedChange = { viewModel.setKillSwitch(it) },
                        testTag = "settings_kill_switch_toggle"
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(start = 56.dp))
                    SettingsToggleRow(
                        icon = Icons.Outlined.Wifi,
                        title = "Auto-Connect",
                        subtitle = "Automatically secure untrusted Wi-Fi networks",
                        checked = autoConnect,
                        onCheckedChange = { viewModel.setAutoConnect(it) },
                        testTag = "settings_auto_connect_toggle"
                    )
                }
            }
        }

        // Section: Interface & Display
        item {
            Text(
                text = "Display & Notifications",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        item {
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Outlined.Public,
                        title = "Show Public IP",
                        subtitle = "Display exit server IP address on home screen",
                        checked = showIp,
                        onCheckedChange = { viewModel.setShowPublicIp(it) },
                        testTag = "settings_show_ip_toggle"
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(start = 56.dp))
                    SettingsToggleRow(
                        icon = Icons.Outlined.Speed,
                        title = "Show Speed in Notification",
                        subtitle = "Display live transfer rate in system status bar",
                        checked = showSpeed,
                        onCheckedChange = { viewModel.setShowSpeedInNotification(it) },
                        testTag = "settings_show_speed_toggle"
                    )
                }
            }
        }

        // Section: About
        item {
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingsRow(
                    icon = Icons.Outlined.Info,
                    title = "About MaRy VPN",
                    subtitle = "Version 3.5.0 · Soft Glass Security Tunnel",
                    onClick = onNavigateToAbout,
                    testTag = "settings_about_row"
                )
            }
        }
    }

    // Transport Dialog
    if (showTransportDialog) {
        AlertDialog(
            onDismissRequest = { showTransportDialog = false },
            title = { Text("Select Transport Protocol") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TransportProtocol.values().forEach { proto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTransportProtocol(proto)
                                    showTransportDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = transportProtocol == proto,
                                onClick = {
                                    viewModel.setTransportProtocol(proto)
                                    showTransportDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = proto.label, fontWeight = FontWeight.Bold)
                                Text(
                                    text = proto.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTransportDialog = false }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }

    // Mode Dialog
    if (showModeDialog) {
        AlertDialog(
            onDismissRequest = { showModeDialog = false },
            title = { Text("Connection Mode") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConnectionMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setConnectionMode(mode)
                                    showModeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = connectionMode == mode,
                                onClick = {
                                    viewModel.setConnectionMode(mode)
                                    showModeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = mode.label, fontWeight = FontWeight.Bold)
                                Text(
                                    text = mode.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModeDialog = false }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }

    // DNS Dialog
    if (showDnsDialog) {
        AlertDialog(
            onDismissRequest = { showDnsDialog = false },
            title = { Text("DNS Provider") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DnsProvider.values().forEach { dns ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDnsProvider(dns)
                                    showDnsDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = dnsProvider == dns,
                                onClick = {
                                    viewModel.setDnsProvider(dns)
                                    showDnsDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = dns.label, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (dns.primary.isNotBlank()) "Primary: ${dns.primary}" else "Default provider",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDnsDialog = false }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }

    // Port Dialog
    if (showPortDialog) {
        AlertDialog(
            onDismissRequest = { showPortDialog = false },
            title = { Text("SOCKS5 / HTTP Proxy Port") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Specify local listen port (1024 - 65535):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = tempPortText,
                        onValueChange = { tempPortText = it.filter { ch -> ch.isDigit() } },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("proxy_port_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val port = tempPortText.toIntOrNull()
                        if (port != null && port in 1024..65535) {
                            viewModel.setProxyPort(port)
                            showPortDialog = false
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VpnPrimary),
                    modifier = Modifier.testTag("submit_proxy_port_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPortDialog = false },
                    modifier = Modifier.testTag("cancel_proxy_port_button")
                ) {
                    Text("Cancel", color = NeonCyan)
                }
            }
        )
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
            )
        )
    }
}

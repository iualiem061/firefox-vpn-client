package com.arman.firefoxvpn.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.firefoxvpn.model.FirefoxAccount
import com.arman.firefoxvpn.ui.MainViewModel
import com.arman.firefoxvpn.ui.components.CircularQuotaGauge
import com.arman.firefoxvpn.ui.components.GlassCard
import com.arman.firefoxvpn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }

    val activeAccount = accounts.find { it.isActive } ?: accounts.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("accounts_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Account Soft Glass Hero Banner
        if (activeAccount != null) {
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_account_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                    borderColor = NeonCyan.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonCyan, VpnPrimary, NeonPurple)))
                                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = activeAccount.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = activeAccount.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = VpnEmerald.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VpnEmerald.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = VpnEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Quota & Devices Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularQuotaGauge(
                                usedPercentage = if (activeAccount.quotaTotalBytes > 0) {
                                    activeAccount.quotaUsedBytes.toFloat() / activeAccount.quotaTotalBytes.toFloat()
                                } else 0.15f,
                                size = 54.dp,
                                strokeWidth = 5.5.dp,
                                fillColor = NeonCyan
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (activeAccount.quotaTotalBytes > 0) {
                                        "${MainViewModel.formatBytes(activeAccount.quotaUsedBytes)} of ${MainViewModel.formatBytes(activeAccount.quotaTotalBytes)} used"
                                    } else {
                                        "${MainViewModel.formatBytes(activeAccount.quotaUsedBytes)} used · Unlimited Glass Plan"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Plan: ${activeAccount.planName} · MaRy Tier",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Next billing cycle: ${activeAccount.resetDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Device connectivity pill with frosted background
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Smartphone,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "${activeAccount.connectedDevices} of ${activeAccount.maxDevices} device slots used",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Accounts List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configured MaRy Accounts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VpnPrimary),
                    modifier = Modifier.testTag("add_account_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Account")
                }
            }
        }

        // Account Items
        items(accounts, key = { it.id }) { acc ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.switchActiveAccount(acc.id) }
                    .testTag("account_card_${acc.id}"),
                shape = RoundedCornerShape(18.dp),
                borderColor = if (acc.isActive) NeonCyan else Color.White.copy(alpha = 0.16f),
                backgroundColor = if (acc.isActive) VpnPrimaryDark.copy(alpha = 0.25f) else Color.Unspecified
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = acc.isActive,
                            onClick = { viewModel.switchActiveAccount(acc.id) },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonCyan),
                            modifier = Modifier.testTag("radio_account_${acc.id}")
                        )
                        Column {
                            Text(
                                text = acc.displayName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = acc.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = acc.planName,
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                        }
                    }

                    if (accounts.size > 1) {
                        IconButton(
                            onClick = { viewModel.removeAccount(acc.id) },
                            modifier = Modifier.testTag("delete_account_${acc.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Remove Account",
                                tint = VpnRose.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Account Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Sign in with MaRy Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Connect your MaRy Account to authenticate encrypted tunnel credentials and sync multi-device subscriptions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Email address") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_account_email_input")
                    )
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Profile display name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_account_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEmail.isNotBlank()) {
                            viewModel.addAccount(newEmail.trim(), newName.trim())
                            Toast.makeText(context, "Account connected!", Toast.LENGTH_SHORT).show()
                            newEmail = ""
                            newName = ""
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VpnPrimary),
                    modifier = Modifier.testTag("submit_add_account_button")
                ) {
                    Text("Sign in")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    modifier = Modifier.testTag("cancel_add_account_button")
                ) {
                    Text("Cancel", color = NeonCyan)
                }
            }
        )
    }
}

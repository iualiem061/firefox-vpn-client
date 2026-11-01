package com.arman.firefoxvpn.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.firefoxvpn.model.LogLevel
import com.arman.firefoxvpn.ui.MainViewModel
import com.arman.firefoxvpn.ui.components.GlassCard
import com.arman.firefoxvpn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.logs.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<LogLevel?>(null) }

    val filteredLogs = remember(logs, searchQuery, selectedLevel) {
        logs.filter { log ->
            val matchesLevel = selectedLevel == null || log.level == selectedLevel
            val matchesSearch = searchQuery.isBlank() ||
                log.message.contains(searchQuery, ignoreCase = true) ||
                log.tag.contains(searchQuery, ignoreCase = true)
            matchesLevel && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("logs_screen")
    ) {
        // Actions and Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter logs…") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.08f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedBorderColor = NeonCyan.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("logs_search_input")
            )

            IconButton(
                onClick = {
                    val formatted = filteredLogs.joinToString("\n") { "[${it.timestamp}] [${it.level}] [${it.tag}] ${it.message}" }
                    clipboard.setText(AnnotatedString(formatted))
                    Toast.makeText(context, "Copied ${filteredLogs.size} log entries", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                    .testTag("copy_logs_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy Logs",
                    tint = NeonCyan
                )
            }

            IconButton(
                onClick = { viewModel.clearLogs() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(VpnRose.copy(alpha = 0.12f))
                    .border(1.dp, VpnRose.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .testTag("clear_logs_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Logs",
                    tint = VpnRose
                )
            }
        }

        // Level Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isSelected = selectedLevel == null
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) VpnPrimary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else Color.White.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedLevel = null }
                        .testTag("log_filter_all")
                ) {
                    Text(
                        text = "ALL (${logs.size})",
                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            items(LogLevel.values()) { level ->
                val count = logs.count { it.level == level }
                val isSelected = selectedLevel == level
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) VpnPrimary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else Color.White.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedLevel = if (selectedLevel == level) null else level }
                        .testTag("log_filter_${level.name.lowercase()}")
                ) {
                    Text(
                        text = "${level.name} ($count)",
                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Log Console List (Soft Glassmorphic cards)
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredLogs, key = { it.id }) { log ->
                val (levelColor, levelBg) = when (log.level) {
                    LogLevel.ERROR -> Pair(VpnRose, VpnRose.copy(alpha = 0.2f))
                    LogLevel.WARN -> Pair(VpnAmber, VpnAmber.copy(alpha = 0.2f))
                    LogLevel.INFO -> Pair(NeonCyan, NeonCyan.copy(alpha = 0.2f))
                    LogLevel.DEBUG -> Pair(Color(0xFF94A3B8), Color(0xFF94A3B8).copy(alpha = 0.15f))
                }

                GlassCard(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = log.timestamp,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = levelBg
                        ) {
                            Text(
                                text = log.level.name,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = levelColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = log.tag,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = log.message,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

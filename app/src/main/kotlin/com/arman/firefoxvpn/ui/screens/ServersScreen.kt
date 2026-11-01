package com.arman.firefoxvpn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arman.firefoxvpn.model.ServerLocation
import com.arman.firefoxvpn.ui.MainViewModel
import com.arman.firefoxvpn.ui.components.GlassCard
import com.arman.firefoxvpn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersScreen(
    viewModel: MainViewModel,
    onServerSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val servers by viewModel.servers.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Recommended", "Favorites", "Americas", "Europe", "Asia-Pacific")

    val filteredServers = remember(servers, searchQuery, selectedFilter) {
        servers.filter { server ->
            val matchesQuery = searchQuery.isBlank() ||
                server.countryName.contains(searchQuery, ignoreCase = true) ||
                server.city.contains(searchQuery, ignoreCase = true) ||
                server.countryCode.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Recommended" -> server.isRecommended
                "Favorites" -> server.isFavorite
                "Americas" -> server.region == "Americas"
                "Europe" -> server.region == "Europe"
                "Asia-Pacific" -> server.region == "Asia-Pacific"
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("servers_screen")
    ) {
        // Frosted Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by country, city, or code…") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeonCyan
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedBorderColor = NeonCyan.copy(alpha = 0.6f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("server_search_input")
        )

        // Filter chips row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) VpnPrimary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = filter }
                        .testTag("filter_chip_${filter.lowercase()}")
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Servers count banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredServers.size} Locations available",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "MaRy Ultra-Mesh Active",
                style = MaterialTheme.typography.labelSmall,
                color = VpnEmerald,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Server list
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredServers, key = { it.id }) { server ->
                val isSelected = server.id == selectedServer.id
                ServerListItem(
                    server = server,
                    isSelected = isSelected,
                    onSelect = {
                        viewModel.selectServer(server)
                        onServerSelected()
                    },
                    onToggleFavorite = {
                        viewModel.toggleFavorite(server.id)
                    }
                )
            }
        }
    }
}

@Composable
fun ServerListItem(
    server: ServerLocation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("server_item_${server.id}"),
        shape = RoundedCornerShape(18.dp),
        borderColor = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.18f),
        backgroundColor = if (isSelected) VpnPrimaryDark.copy(alpha = 0.35f) else Color.Unspecified
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 26.sp
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = server.city,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (server.isRecommended) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonCyan.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "FAST",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${server.countryName} · Load ${server.loadPercentage}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Latency Badge
                val latencyColor = when {
                    server.latencyMs < 50 -> VpnEmerald
                    server.latencyMs < 100 -> VpnAmber
                    else -> VpnRose
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = latencyColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, latencyColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "${server.latencyMs} ms",
                        color = latencyColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favorite Toggle Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("favorite_button_${server.id}")
                ) {
                    Icon(
                        imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (server.isFavorite) VpnAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

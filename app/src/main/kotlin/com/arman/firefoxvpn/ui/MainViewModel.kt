package com.arman.firefoxvpn.ui

import androidx.lifecycle.ViewModel
import com.arman.firefoxvpn.data.VpnRepository
import com.arman.firefoxvpn.model.*
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class MainViewModel(private val repository: VpnRepository = VpnRepository()) : ViewModel() {

    val status: StateFlow<ConnectionStatus> = repository.status
    val servers: StateFlow<List<ServerLocation>> = repository.servers
    val selectedServer: StateFlow<ServerLocation> = repository.selectedServer
    val accounts: StateFlow<List<FirefoxAccount>> = repository.accounts
    val trafficStats: StateFlow<TrafficStats> = repository.trafficStats
    val connectionMode: StateFlow<ConnectionMode> = repository.connectionMode
    val transportProtocol: StateFlow<TransportProtocol> = repository.transportProtocol
    val splitTunnelMode: StateFlow<SplitTunnelMode> = repository.splitTunnelMode
    val dnsProvider: StateFlow<DnsProvider> = repository.dnsProvider
    val customDnsAddress: StateFlow<String> = repository.customDnsAddress
    val proxyPort: StateFlow<Int> = repository.proxyPort
    val showPublicIp: StateFlow<Boolean> = repository.showPublicIp
    val showSpeedInNotification: StateFlow<Boolean> = repository.showSpeedInNotification
    val autoConnect: StateFlow<Boolean> = repository.autoConnect
    val killSwitch: StateFlow<Boolean> = repository.killSwitch
    val installedApps: StateFlow<List<InstalledApp>> = repository.installedApps
    val logs: StateFlow<List<ConnectionLog>> = repository.logs
    val lastTestResult: StateFlow<ConnectionTestResult?> = repository.lastTestResult
    val isTestingConnection: StateFlow<Boolean> = repository.isTestingConnection

    fun toggleConnection() = repository.toggleConnection()
    fun selectServer(server: ServerLocation) = repository.selectServer(server)
    fun toggleFavorite(serverId: String) = repository.toggleFavorite(serverId)
    fun runConnectionTest() = repository.runConnectionTest()
    fun addAccount(email: String, name: String) = repository.addAccount(email, name)
    fun switchActiveAccount(id: String) = repository.switchActiveAccount(id)
    fun removeAccount(id: String) = repository.removeAccount(id)
    fun toggleAppBypass(packageName: String) = repository.toggleAppBypass(packageName)
    fun setConnectionMode(mode: ConnectionMode) = repository.setConnectionMode(mode)
    fun setTransportProtocol(protocol: TransportProtocol) = repository.setTransportProtocol(protocol)
    fun setSplitTunnelMode(mode: SplitTunnelMode) = repository.setSplitTunnelMode(mode)
    fun setDnsProvider(provider: DnsProvider) = repository.setDnsProvider(provider)
    fun setCustomDnsAddress(address: String) = repository.setCustomDnsAddress(address)
    fun setProxyPort(port: Int) = repository.setProxyPort(port)
    fun setShowPublicIp(show: Boolean) = repository.setShowPublicIp(show)
    fun setShowSpeedInNotification(show: Boolean) = repository.setShowSpeedInNotification(show)
    fun setAutoConnect(auto: Boolean) = repository.setAutoConnect(auto)
    fun setKillSwitch(kill: Boolean) = repository.setKillSwitch(kill)
    fun clearLogs() = repository.clearLogs()

    companion object {
        fun formatRate(bps: Long): String {
            return when {
                bps >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB/s", bps / (1024.0 * 1024.0))
                bps >= 1024 -> "${bps / 1024} KB/s"
                else -> "$bps B/s"
            }
        }

        fun formatBytes(bytes: Long): String {
            return when {
                bytes >= 1024L * 1024L * 1024L -> String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
                bytes >= 1024L * 1024L -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
                bytes >= 1024L -> "${bytes / 1024} KB"
                else -> "$bytes B"
            }
        }

        fun formatDuration(totalSeconds: Long): String {
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
        }
    }
}

package com.arman.firefoxvpn.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR,
    WAITING_FOR_NETWORK
}

enum class TransportProtocol(val label: String, val description: String) {
    AUTO("Automatic", "Best available protocol based on network conditions"),
    HTTP2("HTTP/2 over TLS", "Reliable TCP multiplexing, works through restrictive firewalls"),
    HTTP3("HTTP/3 (QUIC)", "High-performance UDP transport with zero-RTT resumption")
}

enum class ConnectionMode(val label: String, val description: String) {
    FULL_VPN("Full VPN", "All device traffic is routed securely through the encrypted tunnel"),
    LOCAL_PROXY("Local Proxy", "Runs a local SOCKS5/HTTP proxy for configured applications")
}

enum class SplitTunnelMode(val label: String, val description: String) {
    EXCLUDE("Bypass selected apps", "All apps use VPN except those selected below"),
    INCLUDE("Only selected apps", "Only selected apps use VPN, others connect directly")
}

enum class DnsProvider(val label: String, val primary: String, val secondary: String) {
    MOZILLA("Mozilla Secure DNS (Default)", "172.19.0.2", ""),
    CLOUDFLARE("Cloudflare (1.1.1.1)", "1.1.1.1", "1.0.0.1"),
    QUAD9("Quad9 (Malware blocking)", "9.9.9.9", "149.112.112.112"),
    ADGUARD("AdGuard DNS (Ad-blocking)", "94.140.14.14", "94.140.15.15"),
    CUSTOM("Custom DNS", "", "")
}

data class ServerLocation(
    val id: String,
    val countryCode: String,
    val countryName: String,
    val city: String,
    val flagEmoji: String,
    val ipAddress: String,
    val latencyMs: Int,
    val loadPercentage: Int,
    val isFavorite: Boolean = false,
    val isRecommended: Boolean = false,
    val region: String = "Europe" // Europe, Americas, Asia-Pacific, Other
)

data class FirefoxAccount(
    val id: String,
    val email: String,
    val displayName: String,
    val planName: String,
    val quotaUsedBytes: Long,
    val quotaTotalBytes: Long, // -1 for unlimited
    val resetDate: String,
    val isActive: Boolean = true,
    val isEnabled: Boolean = true,
    val needsSignIn: Boolean = false,
    val connectedDevices: Int = 2,
    val maxDevices: Int = 5
)

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean = false,
    val isSelected: Boolean = false
)

data class ConnectionLog(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String,
    val level: LogLevel,
    val tag: String,
    val message: String
)

enum class LogLevel {
    INFO,
    WARN,
    ERROR,
    DEBUG
}

data class TrafficStats(
    val uploadRateBps: Long = 0,
    val downloadRateBps: Long = 0,
    val totalUploadBytes: Long = 0,
    val totalDownloadBytes: Long = 0,
    val durationSeconds: Long = 0
)

data class ConnectionTestStage(
    val name: String,
    val latencyMs: Int,
    val isSuccess: Boolean
)

data class ConnectionTestResult(
    val totalMs: Int,
    val exitIp: String,
    val exitCountry: String,
    val stages: List<ConnectionTestStage>,
    val timestamp: String
)

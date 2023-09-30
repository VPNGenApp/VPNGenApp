package com.vpngen.app.utils.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LastConfig(
    @SerialName("BrowserSig") val browserSig: String,
    @SerialName("EncryptionMethod") val encryptionMethod: String,
    @SerialName("NumConn") val numConn: Int,
    @SerialName("ProxyMethod") val proxyMethod: String,
    @SerialName("PublicKey") val publicKey: String,
    @SerialName("RemoteHost") val remoteHost: String,
    @SerialName("RemotePort") val remotePort: String,
    @SerialName("ServerName") val serverName: String,
    @SerialName("StreamTimeout") val streamTimeout: Int,
    @SerialName("Transport") val transport: String,
    @SerialName("UID") val uid: String
)

@Serializable
data class Cloak(
    var lastConfig: LastConfig? = null,
    @SerialName("last_config") val lastConfigString: String,
    val port: String,
    @SerialName("transport_proto") val transportProto: String
)

@Serializable
data class OpenVPN(
    @SerialName("last_config") val lastConfig: String
)

@Serializable
data class ShadowSocks(
    @SerialName("last_config") val lastConfig: String
)

@Serializable
data class Container(
    val container: String,
    val cloak: Cloak,
    val openvpn: OpenVPN,
    val shadowsocks: ShadowSocks
)

@Serializable
data class Config(
    val containers: List<Container>,
    @SerialName("defaultContainer") val defaultContainer: String,
    val description: String,
    val dns1: String,
    val dns2: String,
    @SerialName("hostName") val hostName: String
)

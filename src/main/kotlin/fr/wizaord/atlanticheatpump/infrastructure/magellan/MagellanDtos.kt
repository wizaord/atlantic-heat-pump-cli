package fr.wizaord.atlanticheatpump.infrastructure.magellan

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MagellanTokenResponse(
    val access_token: String = "",
    val token_type: String = "",
    val expires_in: Int = 0,
)

@Serializable
data class MagellanSetup(
    val id: Int = 0,
    val name: String = "",
    val devices: List<MagellanDevice> = emptyList(),
)

@Serializable
data class MagellanDevice(
    val deviceId: Int,
    val name: String = "",
    val gatewaySerialNumber: String = "",
    val modelId: Int = 0,
    val productId: Int = 0,
    val zoneId: Int = 0,
    val capabilities: List<MagellanCapability> = emptyList(),
)

@Serializable
data class MagellanCapability(
    val capabilityId: Int,
    val value: String? = null,
)

@Serializable
data class WriteCapabilityRequest(
    val capabilityId: Int,
    val deviceId: Int,
    val value: String,
)

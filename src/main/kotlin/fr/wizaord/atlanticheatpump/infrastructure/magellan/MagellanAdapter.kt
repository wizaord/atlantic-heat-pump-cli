package fr.wizaord.atlanticheatpump.infrastructure.magellan

import fr.wizaord.atlanticheatpump.domain.model.AcDevice
import fr.wizaord.atlanticheatpump.domain.model.AcMode
import fr.wizaord.atlanticheatpump.domain.model.AcState
import fr.wizaord.atlanticheatpump.domain.model.FanSpeed
import fr.wizaord.atlanticheatpump.domain.port.AcPort
import org.slf4j.LoggerFactory

/**
 * AC capability IDs (for modelIds 557-561):
 *  7      = HVAC mode (0=OFF, 1=AUTO, 3=COOL, 4=HEAT, 7=FAN_ONLY, 8=DRY)
 *  40     = target temperature in heating mode
 *  73     = last active HVAC mode before turning off (used to pick the right target when OFF)
 *  117    = current temperature zone 1
 *  172    = fan speed reported by unit (always 7=auto regardless of user setting - not usable)
 *  177    = target cool temperature
 *  184    = prog mode (on/off)
 *  100801 = fan speed set by user (1=QUIET, 2=SPEED_2, 3=SPEED_3, 4=SPEED_4, 5=AUTO)
 */
class MagellanAdapter(private val client: MagellanClient) : AcPort {

    private val logger = LoggerFactory.getLogger(MagellanAdapter::class.java)

    // Known AC model IDs (Atlantic/Fujitsu AC units)
    private val acModelIds = setOf(557, 558, 559, 560, 561)

    // Capability IDs for AC devices
    private val capHvacMode = 7
    private val capLastActiveMode = 73  // last active mode before turning off
    private val capCurrentTemp = 117
    private val capTargetTempHeat = 40   // target temperature in heating mode
    private val capTargetTempCool = 177  // target temperature in cooling mode
    private val capFanSpeed = 100801     // fan speed set by user

    // HVAC mode values
    private val hvacOff = "0"
    private val hvacAuto = "1"
    private val hvacCool = "3"
    private val hvacHeat = "4"

    override suspend fun listDevices(): List<AcDevice> {
        val setup = client.getSetup()
        return setup.devices
            .filter { it.modelId in acModelIds }
            .map { device ->
                AcDevice(
                    url = device.deviceId.toString(),
                    label = device.name,
                    type = "AC (model ${device.modelId})",
                )
            }
    }

    override suspend fun getStatus(deviceUrl: String): AcState {
        val deviceId = deviceUrl.toInt()
        val capabilities = client.getCapabilities(deviceId)
        return mapToAcState(capabilities)
    }

    override suspend fun turnOn(deviceUrl: String) {
        val deviceId = deviceUrl.toInt()
        // Set HVAC mode to AUTO to turn on
        client.writeCapability(deviceId, capHvacMode, hvacAuto)
    }

    override suspend fun turnOff(deviceUrl: String) {
        val deviceId = deviceUrl.toInt()
        client.writeCapability(deviceId, capHvacMode, hvacOff)
    }

    override suspend fun setMode(deviceUrl: String, mode: AcMode) {
        val deviceId = deviceUrl.toInt()
        val value = when (mode) {
            AcMode.HEATING -> hvacHeat
            AcMode.COOLING -> hvacCool
            AcMode.AUTO -> hvacAuto
        }
        client.writeCapability(deviceId, capHvacMode, value)
    }

    override suspend fun setTemperature(deviceUrl: String, temperature: Double) {
        val deviceId = deviceUrl.toInt()
        val capabilities = client.getCapabilities(deviceId)
        val capMap = capabilities.associateBy { it.capabilityId }
        val hvacModeValue = capMap[capHvacMode]?.value
        val targetCapId = if (hvacModeValue == hvacHeat) capTargetTempHeat else capTargetTempCool
        client.writeCapability(deviceId, targetCapId, temperature.toString())
    }

    override suspend fun setFanSpeed(deviceUrl: String, fanSpeed: FanSpeed) {
        val deviceId = deviceUrl.toInt()
        client.writeCapability(deviceId, capFanSpeed, fanSpeed.apiValue.toString())
    }

    private fun mapToAcState(capabilities: List<MagellanCapability>): AcState {
        val capMap = capabilities.associateBy { it.capabilityId }

        val hvacModeValue = capMap[capHvacMode]?.value
        val isOn = hvacModeValue != null && hvacModeValue != hvacOff
        // When OFF, use the last active mode (cap 73) to reflect the actual last used mode
        val effectiveModeValue = if (hvacModeValue == hvacOff) capMap[capLastActiveMode]?.value else hvacModeValue
        val mode = when (effectiveModeValue) {
            hvacHeat -> AcMode.HEATING
            hvacCool -> AcMode.COOLING
            else -> AcMode.AUTO
        }

        val currentTemp = capMap[capCurrentTemp]?.value?.toDoubleOrNull()
        val targetCapId = if (effectiveModeValue == hvacHeat) capTargetTempHeat else capTargetTempCool
        val targetTemp = capMap[targetCapId]?.value?.toDoubleOrNull()
        val fanSpeed = capMap[capFanSpeed]?.value?.toIntOrNull()?.let { FanSpeed.fromValue(it) }

        logger.debug("Capabilities: hvacMode={}, effectiveMode={}, currentTemp={}, targetTemp={} (cap {}), fanSpeed={}", hvacModeValue, effectiveModeValue, currentTemp, targetTemp, targetCapId, fanSpeed)

        return AcState(
            isOn = isOn,
            currentTemp = currentTemp,
            targetTemp = targetTemp,
            mode = mode,
            fanSpeed = fanSpeed,
        )
    }

    fun close() {
        client.close()
    }
}

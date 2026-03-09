package fr.wizaord.atlanticheatpump.infrastructure.magellan

import fr.wizaord.atlanticheatpump.domain.model.AcDevice
import fr.wizaord.atlanticheatpump.domain.model.AcMode
import fr.wizaord.atlanticheatpump.domain.model.AcState
import fr.wizaord.atlanticheatpump.domain.port.AcPort
import org.slf4j.LoggerFactory

/**
 * AC capability IDs (for modelIds 557-561):
 *  7/8 = HVAC mode (0=OFF, 1=AUTO, 3=COOL, 4=HEAT, 7=FAN_ONLY, 8=DRY)
 *  117 = current temperature zone 1
 *  177 = target cool temperature
 *  184 = prog mode (on/off)
 */
class MagellanAdapter(private val client: MagellanClient) : AcPort {

    private val logger = LoggerFactory.getLogger(MagellanAdapter::class.java)

    // Known AC model IDs (Atlantic/Fujitsu AC units)
    private val acModelIds = setOf(557, 558, 559, 560, 561)

    // Capability IDs for AC devices
    private val capHvacMode = 7
    private val capCurrentTemp = 117
    private val capTargetTemp = 177

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
        client.writeCapability(deviceId, capTargetTemp, temperature.toString())
    }

    private fun mapToAcState(capabilities: List<MagellanCapability>): AcState {
        val capMap = capabilities.associateBy { it.capabilityId }

        val hvacModeValue = capMap[capHvacMode]?.value
        val isOn = hvacModeValue != null && hvacModeValue != hvacOff
        val mode = when (hvacModeValue) {
            hvacHeat -> AcMode.HEATING
            hvacCool -> AcMode.COOLING
            else -> AcMode.AUTO
        }

        val currentTemp = capMap[capCurrentTemp]?.value?.toDoubleOrNull()
        val targetTemp = capMap[capTargetTemp]?.value?.toDoubleOrNull()

        logger.debug("Capabilities: hvacMode={}, currentTemp={}, targetTemp={}", hvacModeValue, currentTemp, targetTemp)

        return AcState(
            isOn = isOn,
            currentTemp = currentTemp,
            targetTemp = targetTemp,
            mode = mode,
        )
    }

    fun close() {
        client.close()
    }
}

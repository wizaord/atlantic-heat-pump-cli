package fr.wizaord.atlanticheatpump

import fr.wizaord.atlanticheatpump.domain.model.AcConfig
import fr.wizaord.atlanticheatpump.domain.model.AcMode
import fr.wizaord.atlanticheatpump.domain.port.AcPort
import fr.wizaord.atlanticheatpump.infrastructure.magellan.MagellanAdapter
import fr.wizaord.atlanticheatpump.infrastructure.magellan.MagellanClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MagellanIntegrationTest {

    private lateinit var server: FakeMagellanServer
    private lateinit var acPort: AcPort

    @BeforeAll
    fun setup() {
        server = FakeMagellanServer()
        server.start()

        val config = AcConfig(cloudLogin = "test@test.com", cloudPassword = "secret")
        val client = MagellanClient(config, baseUrl = server.baseUrl)
        acPort = MagellanAdapter(client)
    }

    @AfterAll
    fun teardown() {
        server.stop()
    }

    @BeforeEach
    fun resetServer() {
        server.reset()
    }

    @Test
    fun `listDevices retourne des AcDevice domain`() = runBlocking {
        val devices = acPort.listDevices()

        assertEquals(2, devices.size)
        assertEquals("100", devices[0].url)
        assertEquals("Clim Salon", devices[0].label)
        assertEquals("AC (model 557)", devices[0].type)
        assertEquals("Clim Chambre", devices[1].label)
    }

    @Test
    fun `getStatus mappe les capabilities vers AcState`() = runBlocking {
        val status = acPort.getStatus("100")

        assertTrue(status.isOn)
        assertEquals(22.5, status.currentTemp)
        assertEquals(24.0, status.targetTemp)
        assertEquals(AcMode.HEATING, status.mode)
    }

    @Test
    fun `turnOn envoie HVAC mode AUTO`() = runBlocking {
        acPort.turnOn("100")

        assertEquals(1, server.capturedCommands.size)
        val cmd = server.capturedCommands[0]
        assertEquals(100, cmd.deviceId)
        assertEquals(7, cmd.capabilityId)
        assertEquals("1", cmd.value) // AUTO
    }

    @Test
    fun `turnOff envoie HVAC mode OFF`() = runBlocking {
        acPort.turnOff("100")

        assertEquals(1, server.capturedCommands.size)
        val cmd = server.capturedCommands[0]
        assertEquals(7, cmd.capabilityId)
        assertEquals("0", cmd.value) // OFF
    }

    @Test
    fun `setMode heat envoie HVAC mode 4`() = runBlocking {
        acPort.setMode("100", AcMode.HEATING)

        assertEquals(1, server.capturedCommands.size)
        val cmd = server.capturedCommands[0]
        assertEquals(7, cmd.capabilityId)
        assertEquals("4", cmd.value) // HEAT
    }

    @Test
    fun `setMode cool envoie HVAC mode 3`() = runBlocking {
        acPort.setMode("100", AcMode.COOLING)

        assertEquals(1, server.capturedCommands.size)
        val cmd = server.capturedCommands[0]
        assertEquals(7, cmd.capabilityId)
        assertEquals("3", cmd.value) // COOL
    }

    @Test
    fun `setTemperature envoie capabilityId 177`() = runBlocking {
        acPort.setTemperature("100", 21.5)

        assertEquals(1, server.capturedCommands.size)
        val cmd = server.capturedCommands[0]
        assertEquals(177, cmd.capabilityId)
        assertEquals("21.5", cmd.value)
    }
}

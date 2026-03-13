package fr.wizaord.atlanticheatpump

import fr.wizaord.atlanticheatpump.domain.model.FanSpeed
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FanSpeedTest {

    @Test
    fun `fromValue 1 retourne QUIET`() {
        assertEquals(FanSpeed.QUIET, FanSpeed.fromValue(1))
    }

    @Test
    fun `fromValue 2 retourne SPEED_2`() {
        assertEquals(FanSpeed.SPEED_2, FanSpeed.fromValue(2))
    }

    @Test
    fun `fromValue 3 retourne SPEED_3`() {
        assertEquals(FanSpeed.SPEED_3, FanSpeed.fromValue(3))
    }

    @Test
    fun `fromValue 4 retourne SPEED_4`() {
        assertEquals(FanSpeed.SPEED_4, FanSpeed.fromValue(4))
    }

    @Test
    fun `fromValue 5 retourne AUTO`() {
        assertEquals(FanSpeed.AUTO, FanSpeed.fromValue(5))
    }

    @Test
    fun `fromValue inconnu retourne UNKNOWN`() {
        assertEquals(FanSpeed.UNKNOWN, FanSpeed.fromValue(99))
    }

    @Test
    fun `fromValue 0 retourne UNKNOWN`() {
        assertEquals(FanSpeed.UNKNOWN, FanSpeed.fromValue(0))
    }

    @Test
    fun `apiValue est coherent avec fromValue pour chaque vitesse`() {
        FanSpeed.entries
            .filter { it != FanSpeed.UNKNOWN }
            .forEach { speed ->
                assertEquals(speed, FanSpeed.fromValue(speed.apiValue),
                    "fromValue(${speed.apiValue}) devrait retourner $speed")
            }
    }
}

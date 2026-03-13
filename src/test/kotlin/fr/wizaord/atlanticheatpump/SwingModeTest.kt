package fr.wizaord.atlanticheatpump

import fr.wizaord.atlanticheatpump.domain.model.SwingMode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SwingModeTest {

    @Test
    fun `fromValues avec swing actif retourne SWING`() {
        assertEquals(SwingMode.SWING, SwingMode.fromValues(swingOn = true, position = 1))
    }

    @Test
    fun `fromValues avec swing actif ignore la position`() {
        assertEquals(SwingMode.SWING, SwingMode.fromValues(swingOn = true, position = 3))
    }

    @Test
    fun `fromValues position 1 retourne POSITION_1`() {
        assertEquals(SwingMode.POSITION_1, SwingMode.fromValues(swingOn = false, position = 1))
    }

    @Test
    fun `fromValues position 2 retourne POSITION_2`() {
        assertEquals(SwingMode.POSITION_2, SwingMode.fromValues(swingOn = false, position = 2))
    }

    @Test
    fun `fromValues position 3 retourne POSITION_3`() {
        assertEquals(SwingMode.POSITION_3, SwingMode.fromValues(swingOn = false, position = 3))
    }

    @Test
    fun `fromValues position 4 retourne POSITION_4`() {
        assertEquals(SwingMode.POSITION_4, SwingMode.fromValues(swingOn = false, position = 4))
    }

    @Test
    fun `fromValues position inconnue retourne UNKNOWN`() {
        assertEquals(SwingMode.UNKNOWN, SwingMode.fromValues(swingOn = false, position = 99))
    }

    @Test
    fun `fromValues position 0 retourne UNKNOWN`() {
        assertEquals(SwingMode.UNKNOWN, SwingMode.fromValues(swingOn = false, position = 0))
    }

    @Test
    fun `positionValue est coherent avec fromValues pour chaque position fixe`() {
        SwingMode.entries
            .filter { it != SwingMode.UNKNOWN && it != SwingMode.SWING }
            .forEach { mode ->
                assertEquals(mode, SwingMode.fromValues(swingOn = false, position = mode.positionValue),
                    "fromValues(false, ${mode.positionValue}) devrait retourner $mode")
            }
    }
}

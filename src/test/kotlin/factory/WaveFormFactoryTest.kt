package factory

import org.junit.jupiter.api.DisplayName
import strategy.SawtoothWaveStrategy
import strategy.SineWaveStrategy
import strategy.SquareWaveStrategy
import strategy.WhiteNoiseStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@DisplayName("WaveFormFactory")
class WaveFormFactoryTest {
    @Test
    @DisplayName("Each Waveform token maps to its strategy")
    fun testDispatch(){
        assertTrue(WaveFormFactory().create("sin", 1) is SineWaveStrategy)
        assertTrue(WaveFormFactory().create("square", 1) is SquareWaveStrategy)
        assertTrue(WaveFormFactory().create("saw", 1) is SawtoothWaveStrategy)
        assertTrue(WaveFormFactory().create("whitenoise", 1) is WhiteNoiseStrategy)
    }

    @Test
    @DisplayName("Unknown Waveform throws exception")
    fun testUnknownWaveForm(){
        val e = assertFailsWith<IllegalArgumentException> { WaveFormFactory().create("randomguy342", 1) }
        assertEquals(e.message?.contains("randomguy342"), true)
        assertEquals(e.message?.contains("1"), true)
    }


}
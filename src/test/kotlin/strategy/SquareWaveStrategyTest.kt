package strategy

import kotlin.test.Test
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals

@DisplayName("Square Wave Strategy")
class SquareWaveStrategyTest {

    @Test
    @DisplayName("Every Sample is exactly 1.0 or -1.0")
    fun testOnlyTwoLevels(){
        val samples = SquareWaveStrategy().generate(440.0, 44100, 44100)

        for(n in samples.indices){
            var failed = false
            if(samples[n] != 1.0 && samples[n] != -1.0){
                failed = true

            }
            assertEquals(false, failed, "Sample $n is not 1.0 or -1.0, it is: " + samples[n])
        }

    }

    @Test
    @DisplayName("First Sample is 1.0")
    fun testFirstSample() {
        // First sample must be 1.0 because its looking for if sin(0) >= 0 and it always will be
        val samples = SquareWaveStrategy().generate(440.0, 44100, 10)
        assertEquals(1.0, samples[0])
    }

    @Test
    @DisplayName("Wave looks correct high for half period and low for half period")
    fun testWaveformShape() {
        val samples = SquareWaveStrategy().generate(441.0, 44100, 100)
        for(n in 0..49){
            assertEquals(1.0, samples[n], "Sample $n should be high")
        }
        for(n in 51..99){
            assertEquals(-1.0, samples[n], "Sample $n should be low")
        }
    }
    @Test
    @DisplayName("Expected number of samples are generated")
    fun testExpectedNumberOfSamples(){
        val samples = SquareWaveStrategy().generate(440.0, 44100, 12345)
        assertEquals(12345, samples.size)
    }

    @Test
    @DisplayName("Zero Samples requested returns an empty array")
    fun testZeroSamplesRequestedReturnsEmptyArray(){
        val samples = SquareWaveStrategy().generate(440.0, 44100, 0)
        assertEquals(0, samples.size)
    }

}
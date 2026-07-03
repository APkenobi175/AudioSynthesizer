package strategy

import kotlin.test.Test
import org.junit.jupiter.api.DisplayName
import java.security.KeyStore
import kotlin.test.assertEquals

class SineWaveStrategyTest {

    @Test
    @DisplayName("First Sample of sin wave is zero")
    // Implementation starts at phase 0 so sample 0 must be sin(0) = 0
    fun testFirstSampleIsZero(){
        val samples = SineWaveStrategy().generate(440.0, 44100, 100)
        assertEquals(0.0, samples[0], 0.000001)
    }

    @Test
    @DisplayName("All Samples are within -1.0 and 1.0")
    fun testSamplesInRange(){
        val samples = SineWaveStrategy().generate(440.0, 44100, 100)
        var failed: Boolean = false
        for (sample in samples) {
            if(sample > 1.0 || sample < -1.0){
               failed = true
                break // break out of loop early if value found
            }

        }
        assertEquals(false, failed)
    }

    @Test
    @DisplayName("Sine Reaches peak of 1.0 at quarter period")
    // Verifies frequency to phaseIncrement math is right
    // 441hz at 44100 samples/sec gives us 100 samples. so that means a quarter period is 25 samples. sin(2 * PI * 1/4) = sin(PI/2) = 1.0
    fun testSineReachesOne(){
        val samples = SineWaveStrategy().generate(441.0, 44100, 200)
        assertEquals(1.0, samples[25], 0.001)
    }


    @Test
    @DisplayName("Expected number of samples are generated")
    fun testExpectedNumberOfSamples(){
        val samples = SineWaveStrategy().generate(441.0, 44100, 12345)
        assertEquals(12345, samples.size)
    }

    @Test
    @DisplayName("Zero Samples requested returns an empty array")
    fun testZeroSamplesRequestedReturnsEmptyArray(){
        val samples = SineWaveStrategy().generate(441.0, 44100, 0)
        assertEquals(0, samples.size)
    }
}
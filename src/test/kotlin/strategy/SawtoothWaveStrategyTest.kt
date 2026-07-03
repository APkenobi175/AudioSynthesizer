package strategy

import kotlin.test.Test
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertTrue
import kotlin.test.assertEquals

@DisplayName("Sawtooth Wave Strategy")
class SawtoothWaveStrategyTest {

    @Test
    @DisplayName("Period starts at -1.0")
    fun testExpectedPeriodStartsAt() {
        // Based on the implementation it should always start at -1.0
        val samples = SawtoothWaveStrategy().generate(441.0, 44100, 200)
        assertEquals(-1.0, samples[0], 0.00001)

    }

    @Test
    @DisplayName("Ramp Reaches ~0.0 at half period")
    fun testExpectedRampReachesAtHalfPeriod(){
        // halfway through 100 samples at 441hz should be 2*0.5 - 1 = 0.
        val samples = SawtoothWaveStrategy().generate(441.0, 44100, 200)
        assertEquals(0.0, samples[50], 0.00001)
    }

    @Test
    @DisplayName("Samples should increase linearly until peak")
    fun testExpectedIncrease(){
        val samples = SawtoothWaveStrategy().generate(441.0, 44100, 200)
        for(n in 0..98){
            assertTrue(samples[n+1] > samples[n], "Ramp should rise at sample $n but it doesn't")
        }
    }

    @Test
    @DisplayName("Wave drops back to -1.0 at peak")
    fun testExpectedWaveDropsBackAtPeak(){
        val samples = SawtoothWaveStrategy().generate(441.0, 44100, 200)
        assertEquals(-1.0, samples[100], 0.00001)
    }

    @Test
    @DisplayName("Expected number of samples are generated")
    fun testExpectedNumberOfSamples(){
        val samples = SawtoothWaveStrategy().generate(441.0, 44100, 12345)
        assertEquals(12345, samples.size)
    }

    @Test
    @DisplayName("Zero Samples requested returns an empty array")
    fun testZeroSamplesRequestedReturnsEmptyArray(){
        val samples = SawtoothWaveStrategy().generate(441.0, 44100, 0)
        assertEquals(0, samples.size)
    }

    @Test
    @DisplayName("All Samples are within -1.0 and 1.0")
    fun testSamplesInRange(){
        val samples = SawtoothWaveStrategy().generate(440.0, 44100, 100)
        var failed: Boolean = false
        for (sample in samples) {
            if(sample > 1.0 || sample < -1.0){
                failed = true
                break // break out of loop early if value found
            }

        }
        assertEquals(false, failed)
    }


}
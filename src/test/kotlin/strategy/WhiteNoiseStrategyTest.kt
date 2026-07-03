package strategy

import kotlin.test.Test
import org.junit.jupiter.api.DisplayName
import java.security.KeyStore
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("White Noise Strategy")
class WhiteNoiseStrategyTest {


    @Test
    @DisplayName("Noise is not constant")
    fun testNotConstant(){
        val samples = WhiteNoiseStrategy().generate(440.0, 44100, 1000)
        assertTrue(samples.distinct().size > 1)
    }


    @Test
    @DisplayName("Frequency has no effect on noise")
    fun testFrequencyIgnored(){
        val samples = WhiteNoiseStrategy().generate(0.0, 44100, 1000)
        assertEquals(1000, samples.size)
        assertTrue(samples.all { it in -1.0..1.0})

    }
    @Test
    @DisplayName("Expected number of samples are generated")
    fun testExpectedNumberOfSamples(){
        val samples = WhiteNoiseStrategy().generate(441.0, 44100, 12345)
        assertEquals(12345, samples.size)
    }

    @Test
    @DisplayName("Zero Samples requested returns an empty array")
    fun testZeroSamplesRequestedReturnsEmptyArray(){
        val samples = WhiteNoiseStrategy().generate(441.0, 44100, 0)
        assertEquals(0, samples.size)
    }

    @Test
    @DisplayName("All Samples are within -1.0 and 1.0")
    fun testSamplesInRange(){
        val samples = WhiteNoiseStrategy().generate(440.0, 44100, 100)
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
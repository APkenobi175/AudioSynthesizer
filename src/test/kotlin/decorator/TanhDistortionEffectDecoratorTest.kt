package decorator

import kotlin.math.tanh
import kotlin.test.Test
import kotlin.test.assertEquals

import org.junit.jupiter.api.DisplayName


class TanhDistortionEffectDecoratorTest {

    @Test
    @DisplayName("Ensure tanh(drive*Sample) is applied to every sample")
    fun testAppliesTanh(){
        // uses testing ConstantOnes strategy
        val decorated = TanhDistortionEffectDecorator(ConstantOnesStrategy(), 5.0)
        val samples = decorated.generate(440.0, 44100, 100)
        for(i in samples.indices){
            assertEquals(tanh(5.0), samples[i], 0.001, "Sample $i")
        }
    }
    @Test
    @DisplayName("Expected number of samples are generated")
    fun testExpectedNumberOfSamples(){
        val decorated = TanhDistortionEffectDecorator(ConstantOnesStrategy(), 1000.0)
        val samples = decorated.generate(441.0, 44100, 12345)
        assertEquals(12345, samples.size)
    }

    @Test
    @DisplayName("Zero Samples requested returns an empty array")
    fun testZeroSamplesRequestedReturnsEmptyArray(){
        val decorated = TanhDistortionEffectDecorator(ConstantOnesStrategy(), 1000.0)
        val samples = decorated.generate(441.0, 44100, 0)
        assertEquals(0, samples.size)
    }

    @Test
    @DisplayName("All Samples are within -1.0 and 1.0")
    fun testSamplesInRange(){
        val decorated = TanhDistortionEffectDecorator(ConstantOnesStrategy(), 1000.0)
        val samples = decorated.generate(440.0, 44100, 100)
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
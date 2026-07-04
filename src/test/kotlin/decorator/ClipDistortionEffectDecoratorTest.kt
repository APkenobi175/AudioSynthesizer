package decorator

import kotlin.test.Test
import kotlin.test.assertEquals

import org.junit.jupiter.api.DisplayName
import strategy.ConstantStrategy


@DisplayName("Clip Distortion Decorator")
class ClipDistortionEffectDecoratorTest {
    @Test
    @DisplayName("Samples above threshold are brought down to threshold")
    fun testSamplesClampHigh(){
        val decorated = ClipDistortionEffectDecorator(ConstantStrategy(1.0), 0.8)
        val samples = decorated.generate(440.0, 44100, 100)
        for(i in samples.indices){
            assertEquals(0.8, samples[i])
        }
    }

    @Test
    @DisplayName("Samples below negative threshold are brought up to threshold")
    fun testSamplesBelowNegative(){
        val decorated = ClipDistortionEffectDecorator(ConstantStrategy(-1.0), 0.8)
        val samples = decorated.generate(440.0, 44100, 100)
        for(i in samples.indices){
            assertEquals(-0.8, samples[i])
        }
    }

    @Test
    @DisplayName("Samples within the threshold are unchanged")
    fun testSamplesWithinThreshold(){
        val decorated = ClipDistortionEffectDecorator(ConstantStrategy(0.5), 0.8)
        val samples = decorated.generate(440.0, 44100, 100)
        for(i in samples.indices){
            assertEquals(0.5, samples[i])
        }
    }

    @Test
    @DisplayName("Samples EXACTLY at threshold are unchanged")
    fun testSamplesAtThreshold(){
        val decorated = ClipDistortionEffectDecorator(ConstantStrategy(0.8), 0.8)
        val samples = decorated.generate(440.0, 44100, 100)
        for(i in samples.indices){
            assertEquals(0.8, samples[i])
        }
    }

}
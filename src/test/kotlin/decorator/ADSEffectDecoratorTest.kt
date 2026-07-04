package decorator

import kotlin.test.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

import org.junit.jupiter.api.DisplayName
import strategy.ConstantOnesStrategy

@DisplayName("ADS Effect Decorator")
class ADSEffectDecoratorTest {

    @Test
    @DisplayName("Note Starts at 0 amplitude")
    // Attack ramp begins at 0
    fun testStartsAtZero(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.02, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 23000)
        assertEquals(0.0, samples[0])
    }

    @Test
    @DisplayName("Attack ramps linearly, at halfway point should be 0.5")
    fun testAttackMiddle(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.02, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 23000)
        assertEquals(0.5, samples[441])
    }

    @Test
    @DisplayName("Envelope peaks at 1.0 when attack ends")
    fun testAttackEndsAtOne(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.02, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 23000)
        assertEquals(1.0, samples[882])
    }

    @Test
    @DisplayName("Decay ramps linearly, at halfway through decay should be 0.8")
    fun testDecayMiddle(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.02, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 23000)
        assertEquals(0.8, samples[11466])
    }

    @Test
    @DisplayName("After decay ends sustain level holds")
    fun testSustainHolds(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.02, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 23000)
        assertEquals(0.6, samples[22050])
        assertEquals(0.6, samples[22999])
    }

    @Test
    @DisplayName("Instant attack (attackEnd=0) doesn't crash")
    fun testZeroAttack(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.0, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 1000)
        for(i in samples.indices){
            assertTrue(!samples[i].isNaN())
        }
        // Decay branch, progress 0 is full amplitude
        assertEquals(1.0, samples[0])
    }
    @Test
    @DisplayName("attackEnd == decayEnd doesn't crash")
    fun testZeroDecay(){
        val decorated = ADSEffectDecorator(ConstantOnesStrategy(), 0.5, 0.5, 0.6)
        val samples = decorated.generate(440.0, 44100, 44100)
        for(i in samples.indices){
            assertTrue(!samples[i].isNaN())
        }
        // Decay branch, progress 0 is full amplitude
        assertEquals(0.6, samples[22050])
    }

}
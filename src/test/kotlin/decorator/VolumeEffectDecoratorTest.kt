package decorator

import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.DisplayName
import strategy.ConstantStrategy

@DisplayName("Volume Effect Decorator")
class VolumeEffectDecoratorTest {

    @Test
    @DisplayName("Volume scales every sample by the level")
    fun testScalesByLevel() {
        // 1.0 * 0.8 = 0.8 exactly, for every sample.
        val decorated = VolumeEffectDecorator(ConstantStrategy(1.0), 0.8)
        val samples = decorated.generate(440.0, 44100, 100)
        for (n in samples.indices) {
            assertEquals(0.8, samples[n], 1e-9, "sample $n")
        }
    }

    @Test
    @DisplayName("Volume above 1.0 amplifies without clamping")
    fun testNoClamping() {
        // make sure that volume does NOT enforce the [-1,1] range because this is before normalizing
        val decorated = VolumeEffectDecorator(ConstantStrategy(1.0), 2.0)
        val samples = decorated.generate(440.0, 44100, 100)
        assertEquals(2.0, samples[0], 1e-9)
    }

    @Test
    @DisplayName("Volume of zero silences the signal")
    fun testZeroLevel() {
        // Edge of the parameter space: vol$0 should produce 0.0
        val decorated = VolumeEffectDecorator(ConstantStrategy(1.0), 0.0)
        val samples = decorated.generate(440.0, 44100, 100)
        for (n in samples.indices) {
            assertEquals(0.0, samples[n], 1e-9, "sample $n")
        }
    }

    @Test
    @DisplayName("Decorators chain: two volumes multiply together")
    fun testChaining() {
        // The is-a/has-a payoff: a decorator wrapping a decorator.
        // 1.0 * 0.5 * 0.5 = 0.25. This is the test that proves the
        // pattern itself works, not just one effect's math.
        val chained = VolumeEffectDecorator(VolumeEffectDecorator(ConstantStrategy(1.0), 0.5), 0.5)
        val samples = chained.generate(440.0, 44100, 100)
        assertEquals(0.25, samples[0], 1e-9)
    }
}
package factory

import decorator.*
import strategy.SineWaveStrategy
import kotlin.test.*
import org.junit.jupiter.api.DisplayName

class EffectFactoryTest {

    private val base = SineWaveStrategy()

    @Test
    @DisplayName("Each effect token wraps in its decorator")
    fun testDispatch() {
        assertTrue(EffectFactory().create("vol\$0.8", base, 1) is VolumeEffectDecorator)
        assertTrue(EffectFactory().create("ads\$.02\$.5\$.6", base, 1) is ADSEffectDecorator)
        assertTrue(EffectFactory().create("tanh\$5", base, 1) is TanhDistortionEffectDecorator)
        assertTrue(EffectFactory().create("clip\$.8", base, 1) is ClipDistortionEffectDecorator)
    }

    @Test
    @DisplayName("ADS with wrong arg count reports count, not an index crash")
    fun testAdsWrongArgCount() {
        // Regression guard: validation once indexed args[1] before checking
        // the count, so 'ads$.5' crashed with IndexOutOfBounds instead of
        // reporting the problem. Shape check must come first.
        val e = assertFailsWith<IllegalArgumentException> {
            EffectFactory().create("ads\$.5", base, 3)
        }
        assertTrue(e.message!!.contains("3 arguments"))
    }

    @Test
    @DisplayName("ADS rejects negative attackEnd at parse time")
    fun testAdsNegativeAttack() {
        val e = assertFailsWith<IllegalArgumentException> {
            EffectFactory().create("ads\$-1\$.5\$.6", base, 2)
        }
        assertTrue(e.message!!.contains("attackEnd"))
    }

    @Test
    @DisplayName("ADS rejects decayEnd earlier than attackEnd at parse time")
    fun testAdsInvertedPhases() {
        // Evalith finding: parser accepted any ADS numbers without validating
        // their relationship. Bad combos must never reach the decorator.
        val e = assertFailsWith<IllegalArgumentException> {
            EffectFactory().create("ads\$.5\$.2\$.6", base, 2)
        }
        assertTrue(e.message!!.contains("decayEnd"))
    }

    @Test
    @DisplayName("Unknown effect throws")
    fun testUnknownEffect() {
        assertFailsWith<IllegalArgumentException> {
            EffectFactory().create("reverb\$0.5", base, 1)
        }
    }

    @Test
    @DisplayName("Non-numeric effect argument throws")
    fun testNonNumericArg() {
        assertFailsWith<IllegalArgumentException> {
            EffectFactory().create("vol\$loud", base, 1)
        }
    }
}
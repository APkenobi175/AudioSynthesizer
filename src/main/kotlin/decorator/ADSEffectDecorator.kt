package decorator

import strategy.WaveFormStrategy
import kotlin.math.tanh

class ADSEffectDecorator(waveFormStrategy: WaveFormStrategy, private val attackEnd: Double, private val decayEnd: Double, private val sustain: Double): EffectDecorator(waveFormStrategy) {
    override fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray {

        val result = DoubleArray(samples.size)
        val attackSamples = (attackEnd * sampleRate).toInt()
        val decaySamples = (decayEnd * sampleRate).toInt()

        for (i in samples.indices) {
            // Attack branch
            if (i < attackSamples) {
                result[i] = samples[i] * (i.toDouble() / attackSamples)

            } else if (i < decaySamples) {
                // Decay branch
                val decayProgress = (i - attackSamples).toDouble() / (decaySamples - attackSamples)
                result[i] = samples[i] * (1.0 - decayProgress * (1.0 - sustain))

            }else{
                // Sustain branch
                result[i] = samples[i] * sustain
            }
        }
        return result
    }
}
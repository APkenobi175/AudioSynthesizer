package decorator

import strategy.WaveFormStrategy

class ADSEffectDecorator(waveFormStrategy: WaveFormStrategy, private val attackEnd: Double, private val decayEnd: Double, private val sustain: Double): EffectDecorator(waveFormStrategy) {
    override fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray {

        val result = DoubleArray(samples.size)
        // Evalith Suggestion: Coerce so params like (attack = 0 and decay ==attack) give us zero-length phases instead of relying
        // On the branch order, because that would be not ideal
        val attackSamples = (attackEnd * sampleRate).toInt().coerceAtLeast(0)
        val decaySamples = (decayEnd * sampleRate).toInt().coerceAtLeast(attackSamples)

        for (i in samples.indices) {
            // Attack branch
            if (attackSamples > 0 && i < attackSamples) {
                result[i] = samples[i] * (i.toDouble() / attackSamples)

            } else if (decaySamples > attackSamples && i < decaySamples) {
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
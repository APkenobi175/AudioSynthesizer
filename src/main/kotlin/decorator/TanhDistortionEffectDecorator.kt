package decorator

import strategy.WaveFormStrategy
import kotlin.math.tanh

class TanhDistortionEffectDecorator(waveFormStrategy: WaveFormStrategy, private val drive: Double): EffectDecorator(waveFormStrategy) {
    override fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray {
        val result = DoubleArray(samples.size)

        for (i in samples.indices) {
            // Apply tanh drive * sample to each sample
            result[i] = tanh(drive * samples[i])
        }

        return result
    }


}
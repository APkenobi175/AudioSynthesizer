package decorator

import strategy.WaveFormStrategy

abstract class EffectDecorator(private val waveFormStrategy: WaveFormStrategy) : WaveFormStrategy {
    final override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        val samples = waveFormStrategy.generate(frequency, sampleRate, numSamples)
        return applyEffect(samples, sampleRate)
    }

    protected abstract fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray
}
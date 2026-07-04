package decorator

import strategy.WaveFormStrategy

abstract class EffectDecorator(private val waveFormStrategy: WaveFormStrategy) : WaveFormStrategy {
    final override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        // All decorators inherit this exact generate method, so for example I would need to call VolumeEffectDecorator.generate()
        // Instantiate wave form strategy
        val samples = waveFormStrategy.generate(frequency, sampleRate, numSamples)
        // Apply effect
        return applyEffect(samples, sampleRate)
    }

    // applyEffect method that other decorators must implement
    protected abstract fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray
}
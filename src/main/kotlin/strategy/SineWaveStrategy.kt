package strategy

import kotlin.math.PI
import kotlin.math.sin
class SineWaveStrategy: WaveFormStrategy {
    override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        val phaseIncrement = 2 * PI * frequency / sampleRate

        val samples = DoubleArray(numSamples)

        for (n in samples.indices){
            samples[n] = sin(n * phaseIncrement)
        }

        return samples

    }
}
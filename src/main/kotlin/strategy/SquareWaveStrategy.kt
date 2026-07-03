package strategy

import kotlin.math.PI
import kotlin.math.sin

class SquareWaveStrategy: WaveFormStrategy {

    override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        val phaseIncrement = 2 * PI * frequency / sampleRate
        val samples = DoubleArray(numSamples)
        for (n in samples.indices){
            if (sin(n * phaseIncrement) >= 0){
                samples[n] = 1.0
            } else {
                samples[n] = -1.0
            }
        }

        return samples
    }
}
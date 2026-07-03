package strategy
import kotlin.random.Random

class WhiteNoiseStrategy: WaveFormStrategy {
    override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        val samples = DoubleArray(numSamples)
        for (n in samples.indices){
            samples[n] = Random.nextDouble(-1.0, 1.0)
        }
        return samples
    }

    }
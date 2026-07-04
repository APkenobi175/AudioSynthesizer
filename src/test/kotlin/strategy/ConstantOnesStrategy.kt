package strategy

// Fake strategy: every sample is 1.0. Makes decorator outputs predictable
// FOR UNIT TESTS
class ConstantOnesStrategy : WaveFormStrategy {
    override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        val samples = DoubleArray(numSamples)
        for (n in samples.indices) {
            samples[n] = 1.0
        }
        return samples
    }
}
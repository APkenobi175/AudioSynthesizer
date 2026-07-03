package strategy

interface WaveFormStrategy {
    fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray
}

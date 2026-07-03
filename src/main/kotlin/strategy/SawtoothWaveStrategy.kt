package strategy

class SawtoothWaveStrategy: WaveFormStrategy {

    override fun generate(frequency: Double, sampleRate: Int, numSamples: Int): DoubleArray {
        val samples = DoubleArray(numSamples)

        for (n in samples.indices){
            // instant drop if at peak looks like shark teeth
            val frac = (n * frequency / sampleRate) % 1.0
            samples[n] = 2 * frac - 1

        }

        return samples
    }

}
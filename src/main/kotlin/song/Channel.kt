package song

import strategy.WaveFormStrategy

class Channel (val waveForm: WaveFormStrategy, val measure: List<Measure>) {

    fun generateSamples(sampleRate: Int, secondsPerBeat: Double): DoubleArray{
        TODO("Bruh you needa do this")
    }
}
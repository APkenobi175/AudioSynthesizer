package decorator

import strategy.WaveFormStrategy

class VolumeEffectDecorator(waveFormStrategy: WaveFormStrategy, private val level: Double): EffectDecorator(waveFormStrategy) {
    override fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray{
        val result = DoubleArray(samples.size)

        //
        for (i in samples.indices){
            // Multiply sample by the volume level
            result[i] = samples[i] * level
        }
        return result
    }



}
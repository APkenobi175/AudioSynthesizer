package decorator

import strategy.WaveFormStrategy


class ClipDistortionEffectDecorator(waveFormStrategy: WaveFormStrategy, private val threshold: Double): EffectDecorator(waveFormStrategy) {
    override fun applyEffect(samples: DoubleArray, sampleRate: Int): DoubleArray {

        val result = DoubleArray(samples.size)

        for(i in samples.indices){
            if(samples[i] > threshold){
                result[i] = threshold
            }else if(samples[i] < -threshold){
                result[i] = -threshold
            }else{
                result[i] = samples[i]
            }
        }
        return result
    }


}
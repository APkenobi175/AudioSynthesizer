package song

import kotlin.math.abs

class Song (val sampleRate: Int, val beatsPerMeasure: Int, val tempo: Int, val channels: List<Channel>) {

    fun combineChannels(): DoubleArray{
        val secondsPerBeat = 60.0 / tempo

        // generate each channel's buffer
        val channelSamples = ArrayList<DoubleArray>()

        for (channel in channels){
            channelSamples.add(channel.generateSamples(sampleRate, secondsPerBeat))

        }

        // Output should be as long as the longest channel, this is where we need to be careful not to access elements that don't exist
        var maxLength = 0
        for (cs in channelSamples){
            if (cs.size > maxLength) maxLength = cs.size
        }

        // Create array of length max length
        val mixed = DoubleArray(maxLength)

        // Add the channels together
        for (cs in channelSamples){
            for(i in cs.indices){
                mixed[i] += cs[i]
            }
        }

        // Normalize mixed channel so we are within the -1.0 to 1.0 range
        normalize(mixed)
        return mixed
    }

    private fun normalize(samples: DoubleArray){

        // Find the PEAK across all samples
        var max = 0.0
        for (s in samples){
            val a = abs(s)
            if (a >  max) max = a
        }

        // Make sure max is less than 1.0, bail if it is because it doesn't need to be normalized at that point
        if (max <= 1.0) return // already in range

        // If it's not, then take each element and divide by peak to normalize
        for (i in samples.indices){
            samples[i] /= max
        }
    }
}
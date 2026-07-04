package song

import strategy.WaveFormStrategy

class Channel (val waveForm: WaveFormStrategy, val measures: List<Measure>) {

    fun generateSamples(sampleRate: Int, secondsPerBeat: Double): DoubleArray{
        var totalSamples = 0
        // 1. Calculate total number of samples
        for (measure in measures){
            for (note in measure.notes){
                totalSamples += (note.duration * secondsPerBeat * sampleRate).toInt()
            }
        }
        // 2. Define array the size of total sample amount
        val samples = DoubleArray(totalSamples)

        var offset = 0

        // 3. Second pass, generate samples for each note and fill the array
        for(measure in measures){
            for(note in measure.notes){
                // Calculate number of samples in this note
                val numSamples = (note.duration * secondsPerBeat * sampleRate).toInt()
                // Make sure note is not a rest
                if (!note.isRest()){
                    val noteSamples = waveForm.generate(note.frequency!!, sampleRate, numSamples)
                    for (i in noteSamples.indices){
                        samples[offset + i] = noteSamples[i]
                    }
                }
                // For rests, the array is already 0.0 so just skip and increment offset
                offset += numSamples
            }
        }
        return samples
    }
}
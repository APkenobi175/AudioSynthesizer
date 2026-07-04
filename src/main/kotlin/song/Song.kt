package song

class Song (val sampleRate: Int, val beatsPerMeasure: Int, val temp: Int, val channels: List<Channel>) {

    fun combineChannels(): DoubleArray{
        TODO("Add waves together here")

    }

    private fun normalize(samples: DoubleArray){
        TODO("Normalize waves here")
    }
}
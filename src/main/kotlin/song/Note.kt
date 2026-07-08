package song

class Note(private val frequency: Double?, val duration: Double) {

    fun isRest(): Boolean{
        return frequency == null
    }

    // Note encapsulation so we can get rid of the !!
    fun frequencyOrThrow(): Double{
        return frequency ?: error("Rest has no frequency")
    }

}
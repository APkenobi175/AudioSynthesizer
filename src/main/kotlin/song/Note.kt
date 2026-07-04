package song

class Note(val frequency: Double?, val duration: Double) {

    fun isRest(): Boolean{
        return frequency == null
    }

}
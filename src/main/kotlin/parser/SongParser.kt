package parser
import song.Channel
import song.Measure
import song.Note
import song.Song
import strategy.WaveFormStrategy
import java.io.File
import java.io.FileNotFoundException
import factory.WaveFormFactory
import factory.EffectFactory
import song.PianoNotes

class SongParser @Throws(IllegalArgumentException::class, FileNotFoundException::class) constructor() {


    fun parse(filePath: String): Song {
        val file = File(filePath)

        // 1. Make sure the file exists
        if (!file.exists()) {
            throw FileNotFoundException("File not found: $filePath")
        }

        // read lines that aren't blank
        val lines = file.readLines().filter { it.isNotBlank() }

        // 2. Make sure file is not empty
        if (lines.isEmpty()) {
            throw IllegalArgumentException("File is empty: $filePath")
        }

        // 3. Get the sample rate, beats per measure, and tempo from the header
        val sampleRate = getSampleRate(lines[0])
        val beatsPerMeasure = getBeatsPerMeasure(lines[0])
        val tempo = getTempo(lines[0])

        // 4. Create channels list, each line after the header is a channel
        val channels = ArrayList<Channel>()
        for (i in 1 until lines.size) {

            channels.add(getChannelFromLine(lines[i], i+1))
        }
        if (channels.isEmpty()){
            throw IllegalArgumentException("File has no channel lines: $filePath")
        }

        return Song(sampleRate, beatsPerMeasure, tempo, channels)



    }

    private fun getChannelFromLine(line: String, lineNumber: Int): Channel {
        // measures are separated by "|"
        var segments = line.split("|")

        // Trailing | is legal so drop the last one if its blank
        // These next three if statements are things evalith told me to fix. Evalith if you are reading this hi!
        if (segments.size > 1 && segments.last().isBlank()){
            segments = segments.dropLast(1)
        }

        if (segments[0].isBlank()){
            throw IllegalArgumentException("Line $lineNumber is missing channel settings before the first measure")
        }

        for (i in 1 until segments.size){
            if (segments[i].isBlank()){
                throw IllegalArgumentException("Line $lineNumber is containing an empty measure")
            }
        }

        if (segments.size < 2){
            throw IllegalArgumentException("Line $lineNumber channel has settings but no measures!")
        }

        val waveForm = getWaveFormStrategy(segments[0], lineNumber)
        val measures = ArrayList<Measure>()
        for (i in 1 until segments.size){
            measures.add(createMeasure(segments[i], lineNumber))
        }
        return Channel(waveForm, measures)
    }

    private fun createMeasure(segment: String, lineNumber: Int): Measure {
        // Notes are separated by ","
        val segments = segment.split(" ").filter {it.isNotBlank()}

        if(segments.isEmpty()){
            throw IllegalArgumentException("Line $lineNumber measure is empty: $segment")
        }

        if(segments.size % 2 != 0){
            throw IllegalArgumentException("Line $lineNumber has a measure without a duration ")
        }

        val notes = ArrayList<Note>()
        // Create a note with the Note, and duration
        for (i in 0 until segments.size step 2){
            val note = createNote(segments[i], segments[i + 1], lineNumber)
            notes.add(note)
        }

        return Measure(notes)
    }

    private fun createNote(noteSegment: String, durationSegment: String, lineNumber: Int): Note {
        val duration = durationSegment.toDoubleOrNull() ?: throw IllegalArgumentException("Line $lineNumber invalid duration '$durationSegment'")
        if (duration <= 0){
            throw IllegalArgumentException("Line $lineNumber duration $duration must be positive")
        }
        // Handle rests
        if (noteSegment == "-"){
            return Note(null, duration)
        }
        val frequency = PianoNotes.frequencies[noteSegment] ?: throw IllegalArgumentException("Line $lineNumber: Unknown note $noteSegment")
        return Note(frequency, duration)
    }
    private fun getWaveFormStrategy(segment: String, lineNumber: Int): WaveFormStrategy {
        // The wave form strategy and effects are at the start of a line separated by spaces
        val segments = segment.split(" ").filter { it.isNotBlank() }
        if(segments.isEmpty()) {
            throw IllegalArgumentException("Line $lineNumber is empty: $segment")
        }
        // Using factories because further decoupling is cool!!!
        var strategy = WaveFormFactory().create(segments[0], lineNumber)
        // 2. Apply all effects that are stacked
        for (i in 1 until segments.size){
            strategy = EffectFactory().create(segments[i], strategy, lineNumber)
        }

        return strategy
    }


    private fun getSampleRate(header: String): Int {
        val parts = header.trim().split(" ").filter {it.isNotBlank()}
        if (parts.size != 3){
            throw IllegalArgumentException("Invalid header, must contain 3 parts: sample rate, beats per measure, and tempo")
        }

        val sampleRate = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid sample rate in header")

        if(sampleRate <= 0){
            throw IllegalArgumentException("Sample rate must be positive")
        }

        return sampleRate


    }

    private fun getBeatsPerMeasure(header: String): Int{
        val parts = header.trim().split(" ").filter {it.isNotBlank()}
        if (parts.size != 3){
            throw IllegalArgumentException("Invalid header, must contain 3 parts: sample rate, beats per measure, and tempo")
        }

        val bpm =  parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid beats per measure in header")
        if(bpm <= 0){
            throw IllegalArgumentException("Beats per measure must be positive")
        }
        return bpm
    }

    private fun getTempo(header: String): Int{
        val parts = header.trim().split(" ").filter {it.isNotBlank()}
        if (parts.size != 3){
            throw IllegalArgumentException("Invalid header, must contain 3 parts: sample rate, beats per measure, and tempo")
        }
        val tempo = parts[2].toIntOrNull() ?: throw IllegalArgumentException("Invalid tempo in header")
        if(tempo <= 0){
            throw IllegalArgumentException("Tempo must be positive")
        }
        return tempo
    }
}
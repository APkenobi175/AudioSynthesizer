package parser
import decorator.ADSEffectDecorator
import decorator.ClipDistortionEffectDecorator
import decorator.TanhDistortionEffectDecorator
import decorator.VolumeEffectDecorator
import song.Channel
import song.Measure
import song.Note
import song.Song
import strategy.SawtoothWaveStrategy
import strategy.SineWaveStrategy
import strategy.SquareWaveStrategy
import strategy.WaveFormStrategy
import strategy.WhiteNoiseStrategy
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.pow

class SongParser @Throws(IllegalArgumentException::class, FileNotFoundException::class) constructor() {


    // Copied directly from canvas notes:

    object PianoNotes {
        val frequencies: Map<String, Double> = buildMap {
            val sharpNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
            val flatNames  = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

            for (key in 1..88) {
                val freq = 440.0 * 2.0.pow((key - 49) / 12.0)
                val semitonesFromC0 = key + 8
                val pitchClass = semitonesFromC0 % 12
                val octave = semitonesFromC0 / 12
                put("${sharpNames[pitchClass]}$octave", freq)
                if (flatNames[pitchClass] != sharpNames[pitchClass]) {
                    put("${flatNames[pitchClass]}$octave", freq)
                }
            }
            put("-", 0.0)
        }

        operator fun get(note: String): Double? = frequencies[note]
    }





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
            throw IllegalArgumentException("Channel is empty: $filePath")
        }

        return Song(sampleRate, beatsPerMeasure, tempo, channels)



    }

    private fun getChannelFromLine(line: String, lineNumber: Int): Channel {
        // measures are separated by "|"
        val segments = line.split("|").filter {it.isNotBlank()}

        if(segments.isEmpty()){
            throw IllegalArgumentException("Line $lineNumber is empty: $line")
        }
        if (segments.size < 2){
            throw IllegalArgumentException("Line $lineNumber channel has no measures")
        }

        // get the waveform strategy + effects from the first segment
        val waveForm = getWaveFormStrategy(segments[0], lineNumber)

        val measures = ArrayList<Measure>()
        // create measures and add to the list
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
        val duration = durationSegment.toDoubleOrNull() ?: throw IllegalArgumentException("Duration $durationSegment is empty")
        if (duration <= 0){
            throw IllegalArgumentException("Duration $duration must be positive")
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
        if(segments.isEmpty()){
            throw IllegalArgumentException("Line $lineNumber is empty: $segment")
        }

        // 1. Get the wave strategy
        var strategy: WaveFormStrategy
        if (segments[0] == "sin"){
            strategy = SineWaveStrategy()
        } else if (segments[0] == "square"){
            strategy = SquareWaveStrategy()
        } else if (segments[0] == "saw"){
            strategy = SawtoothWaveStrategy()
        } else if (segments[0] == "whitenoise"){
            strategy = WhiteNoiseStrategy()
        }else{
            throw IllegalArgumentException("Unable to parse Wave Strategy from Song File: $segment")
        }

        // 2. Apply all effects that are stacked
        for (i in 1 until segments.size){
            strategy = applyEffect(segments[i], strategy, lineNumber)
        }

        return strategy
    }

    private fun applyEffect(segment: String, strategy: WaveFormStrategy, lineNumber: Int): WaveFormStrategy {
        val segments = segment.split("$")

        // Left side of $ is the effect, right side is the arguments
        val args = ArrayList<Double>()
        // Start from second segment as first argument, and add to args array
        for (i in 1 until segments.size){
            val v = segments[i].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid effect argument in line $lineNumber: ${segments[i]}")
            args.add(v)
        }

        val numArgs = args.size
        // Get the effect
        if (segments[0] == "vol"){
            // Volume has one argument
            if(numArgs != 1) {
                throw IllegalArgumentException("Expected 1 argument for vol effect, got $numArgs in line $lineNumber")
            }

            return VolumeEffectDecorator(strategy, args[0])
        } else if (segments[0] == "ads"){
            // ADS has 3 arguments
            if (numArgs != 3){
                throw IllegalArgumentException("Expected 3 arguments for ads effect, got $numArgs in line $lineNumber")
            }

            return ADSEffectDecorator(strategy, args[0], args[1], args[2])
        } else if (segments[0] == "tanh"){
            // TANH has 1 argument
            if (numArgs != 1){
                throw IllegalArgumentException("Expected 1 argument for tanh effect, got $numArgs in line $lineNumber")
            }

            return TanhDistortionEffectDecorator(strategy, args[0])
        } else if (segments[0] == "clip"){
            // clip has 1 argument
            if (numArgs != 1){
                throw IllegalArgumentException("Expected 1 argument for clip effect, got $numArgs in line $lineNumber")
            }
            return ClipDistortionEffectDecorator(strategy, args[0])
        }else{
            throw IllegalArgumentException("Invalid effect argument in line $lineNumber: ${segments[0]}")
        }
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
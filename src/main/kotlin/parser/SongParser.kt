package parser

import decorator.ADSEffectDecorator
import decorator.ClipDistortionEffectDecorator
import decorator.TanhDistortionEffectDecorator
import decorator.VolumeEffectDecorator
import song.Channel
import strategy.SawtoothWaveStrategy
import strategy.SineWaveStrategy
import strategy.SquareWaveStrategy
import strategy.WaveFormStrategy
import strategy.WhiteNoiseStrategy
import java.io.File
import java.io.FileNotFoundException

class SongParser {
    fun parse(filePath: String) {
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
            TODO("Uncomment this")
            //channels.add(getChannelFromLine(lines[i], i+1))
        }



    }

    private fun getChannelFromLine(line: String, lineNumber: Int): Channel? {
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
        TODO("Finish this")
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

            VolumeEffectDecorator(strategy, args[0])
        } else if (segments[0] == "ads"){
            // ADS has 3 arguments
            if (numArgs != 3){
                throw IllegalArgumentException("Expected 3 arguments for ads effect, got $numArgs in line $lineNumber")
            }

            ADSEffectDecorator(strategy, args[0], args[1], args[2])
        } else if (segments[0] == "tanh"){
            // TANH has 1 argument
            if (numArgs != 1){
                throw IllegalArgumentException("Expected 1 argument for tanh effect, got $numArgs in line $lineNumber")
            }

            TanhDistortionEffectDecorator(strategy, args[0])
        } else if (segments[0] == "clip"){
            // clip has 1 argument
            if (numArgs != 1){
                throw IllegalArgumentException("Expected 1 argument for clip effect, got $numArgs in line $lineNumber")
            }
            ClipDistortionEffectDecorator(strategy, args[0])
        }else{
            throw IllegalArgumentException("Invalid effect argument in line $lineNumber: ${segments[0]}")
        }
        return strategy
    }

    private fun getSampleRate(header: String): Double {
        val parts = header.trim().split(" ").filter {it.isNotBlank()}
        if (parts.size != 3){
            throw IllegalArgumentException("Invalid header, must contain 3 parts: sample rate, beats per measure, and tempo")
        }

        val sampleRate = parts[0].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid sample rate in header")

        if(sampleRate <= 0.0){
            throw IllegalArgumentException("Sample rate must be positive")
        }

        return sampleRate


    }

    private fun getBeatsPerMeasure(header: String): Double{
        val parts = header.trim().split(" ").filter {it.isNotBlank()}
        if (parts.size != 3){
            throw IllegalArgumentException("Invalid header, must contain 3 parts: sample rate, beats per measure, and tempo")
        }

        val bpm =  parts[1].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid beats per measure in header")
        if(bpm <= 0.0){
            throw IllegalArgumentException("Beats per measure must be positive")
        }
        return bpm
    }

    private fun getTempo(header: String): Double{
        val parts = header.trim().split(" ").filter {it.isNotBlank()}
        if (parts.size != 3){
            throw IllegalArgumentException("Invalid header, must contain 3 parts: sample rate, beats per measure, and tempo")
        }
        val tempo = parts[2].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid tempo in header")
        if(tempo <= 0.0){
            throw IllegalArgumentException("Tempo must be positive")
        }
        return tempo
    }
}
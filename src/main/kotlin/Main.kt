import audio.AudioPlayer
import parser.SongParser
import java.io.FileNotFoundException

fun main(args: Array<String>) {
    try {
        val filePath = args.getOrNull(0) ?: throw IllegalArgumentException("Please provide a file path as the first argument.")
        val song = SongParser().parse(filePath)
        val samples = song.combineChannels()
        AudioPlayer().play(samples, song.sampleRate)
    } catch (e: FileNotFoundException) {
        println("Error: ${e.message}")
    } catch (e: IllegalArgumentException) {
        println("Invalid song file: ${e.message}")
    }
}
package audio

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

class AudioPlayer {
    fun play(samples: DoubleArray, sampleRate: Int) {
        // Convert -1.0..1.0 doubles to 16-bit little-endian PCM bytes
        val buffer = ByteArray(samples.size * 2)
        for (i in samples.indices) {
            val pcmValue = (samples[i] * Short.MAX_VALUE).toInt()
            buffer[i * 2] = pcmValue.toByte()              // low byte
            buffer[i * 2 + 1] = (pcmValue shr 8).toByte()  // high byte
        }

        val format = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val line: SourceDataLine = AudioSystem.getSourceDataLine(format)
        line.open(format)
        line.start()
        line.write(buffer, 0, buffer.size)
        line.drain()   // blocks until playback finishes -- fine for this program
        line.close()
    }
}
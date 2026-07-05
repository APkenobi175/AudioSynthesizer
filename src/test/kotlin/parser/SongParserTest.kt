package parser

import decorator.ADSEffectDecorator
import decorator.VolumeEffectDecorator
import song.Song
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import strategy.SawtoothWaveStrategy
import strategy.SquareWaveStrategy
import strategy.WhiteNoiseStrategy

class SongParserTest {

    // Writes song content to a temp file and parses it.
    private fun parseSong(content: String): Song {
        val file = kotlin.io.path.createTempFile(suffix = ".txt").toFile()
        file.deleteOnExit()
        file.writeText(content)
        return SongParser().parse(file.absolutePath)
    }


    @Test
    @DisplayName("Parses the amazing grace song correctly")
    fun testParsesAmazingGrace() {
        val song = parseSong(
            "44100 3 90\n" +
                    "sin vol\$0.8 ads\$.02\$.5\$.6|- 2 G3 1|C4 2 E4 1|C4 1 E4 1 D4 1|C4 2 A3 1|G3 3|A3 1 C4 2|C4 2 E4 1|D4 2 C4 1|C4 3|"
        )
        assertEquals(44100, song.sampleRate)
        assertEquals(3, song.beatsPerMeasure)
        assertEquals(90, song.tempo)
        assertEquals(1, song.channels.size)
        assertEquals(9, song.channels[0].measures.size)

        val firstMeasure = song.channels[0].measures[0]
        assertEquals(2, firstMeasure.notes.size)
        assertTrue(firstMeasure.notes[0].isRest())
        assertEquals(2.0, firstMeasure.notes[0].duration, 1e-9)
        assertEquals(196.0, firstMeasure.notes[1].frequency!!, 0.01)  // G3
        assertEquals(1.0, firstMeasure.notes[1].duration, 1e-9)
    }

    // Test decorators
    @Test
    @DisplayName("A single effect wraps the waveform in its decorator")
    fun testSingleEffectChain() {
        val song = parseSong("100 4 60\nsin vol\$0.8|C4 1|")
        assertTrue(song.channels[0].waveForm is VolumeEffectDecorator,
            "vol effect should produce a VolumeEffectDecorator, got ${song.channels[0].waveForm::class.simpleName}")
    }

    @Test
    @DisplayName("Multiple effects chain with the last effect outermost")
    fun testEffectOrdering() {
        // The outermost (what Channel holds) must be the LAST effect in the file.
        val song = parseSong("100 4 60\nsin vol\$0.8 ads\$.02\$.5\$.6|C4 1|")
        assertTrue(song.channels[0].waveForm is ADSEffectDecorator,
            "last effect in file should be outermost")
    }

    @Test
    @DisplayName("A channel with no effects is the bare strategy")
    fun testNoEffects() {
        val song = parseSong("100 4 60\nsin|C4 1|")
        assertTrue(song.channels[0].waveForm is strategy.SineWaveStrategy)
    }


    // Test rests
    @Test
    @DisplayName("A rest parses as null frequency, not the map's 0.0 entry")
    fun testRestIsNullNotZero() {
        // BEFORE consulting the map. Frequency 0.0 would break square/saw/
        // whitenoise rests (constant DC / full static).
        val song = parseSong("100 4 60\nsin|- 1|")
        val rest = song.channels[0].measures[0].notes[0]
        assertTrue(rest.isRest())
    }


    @Test
    @DisplayName("Missing file throws FileNotFoundException")
    fun testMissingFile() {
        assertFailsWith<FileNotFoundException> {
            SongParser().parse("/definitely/a/real/path.txt")
        }
    }

    @Test
    @DisplayName("Empty file throws with a helpful message")
    fun testEmptyFile() {
        val e = assertFailsWith<IllegalArgumentException> { parseSong("") }
        assertTrue(e.message!!.contains("empty", ignoreCase = true))
    }

    @Test
    @DisplayName("Header with wrong number of values throws")
    fun testHeaderWrongCount() {
        assertFailsWith<IllegalArgumentException> { parseSong("44100 3\nsin|C4 1|") }
    }

    @Test
    @DisplayName("Non-numeric header value throws")
    fun testHeaderNotNumeric() {
        assertFailsWith<IllegalArgumentException> { parseSong("fast 3 90\nsin|C4 1|") }
    }

    @Test
    @DisplayName("Zero tempo throws")
    fun testZeroTempo() {
        // tempo=0 would become secondsPerBeat = 60/0 = Infinity downstream;
        // the parser is the boundary that must catch it.
        assertFailsWith<IllegalArgumentException> { parseSong("44100 3 0\nsin|C4 1|") }
    }

    @Test
    @DisplayName("Header-only file (no channels) throws")
    fun testNoChannels() {
        assertFailsWith<IllegalArgumentException> { parseSong("44100 3 90") }
    }

    @Test
    @DisplayName("Unknown waveform names the offender in the message")
    fun testUnknownWaveform() {
        val e = assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nbanjo|C4 1|")
        }
        assertTrue(e.message!!.contains("banjo"))
    }

    @Test
    @DisplayName("Unknown effect throws")
    fun testUnknownEffect() {
        assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin reverb\$0.5|C4 1|")
        }
    }

    @Test
    @DisplayName("Wrong effect argument count throws")
    fun testWrongArgCount() {
        assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin vol\$0.5\$0.7|C4 1|")
        }
    }

    @Test
    @DisplayName("Non-numeric effect argument throws")
    fun testNonNumericEffectArg() {
        assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin vol\$loud|C4 1|")
        }
    }

    @Test
    @DisplayName("Unknown note name throws with line context")
    fun testUnknownNote() {
        val e = assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin|X4 1|")
        }
        assertTrue(e.message!!.contains("X4"))
    }

    @Test
    @DisplayName("Note without a duration throws")
    fun testOddMeasureTokens() {
        assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin|C4 1 E4|")
        }
    }

    @Test
    @DisplayName("Non-numeric duration throws")
    fun testBadDuration() {
        assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin|C4 long|")
        }
    }

    @Test
    @DisplayName("Channel with settings but no measures throws")
    fun testSettingsOnlyChannel() {
        assertFailsWith<IllegalArgumentException> {
            parseSong("100 4 60\nsin vol\$0.8")
        }
    }

    @Test
    @DisplayName("Each waveform name maps to its strategy")
    fun testWaveformDispatch() {
        assertTrue(parseSong("100 4 60\nsquare|C4 1|").channels[0].waveForm is SquareWaveStrategy)
        assertTrue(parseSong("100 4 60\nsaw|C4 1|").channels[0].waveForm is SawtoothWaveStrategy)
        assertTrue(parseSong("100 4 60\nwhitenoise|C4 1|").channels[0].waveForm is WhiteNoiseStrategy)
    }
}
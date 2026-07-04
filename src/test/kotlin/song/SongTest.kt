package song

import org.junit.jupiter.api.DisplayName
import strategy.ConstantStrategy
import strategy.WaveFormStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@DisplayName("Song")
class SongTest {
    // helper function to create channels faster
    private fun channelOf(strategy: WaveFormStrategy, beats: Double) = Channel(strategy, listOf(Measure(listOf(Note(440.0, beats)))))

    @Test
    @DisplayName("Channel sums element wise correctly")
    fun channelSum(){
        // gonna create two waves one with all 0.3 and one with all 0.4
        // 0.3 + 0.4 = 0.7 everywhere, so it doesn't need to be normalized when added, they should all just equal 0.7
        // Will use Test functions channelOf and testStrategy ConstantStrategy

        val song = Song(100, 4, 60, listOf(
            channelOf(ConstantStrategy(0.3), 1.0),
            channelOf(ConstantStrategy(0.4), 1.0)
        ))
        val mixed = song.combineChannels()
        assertEquals(100, mixed.size)
        for(i in mixed.indices){
            assertEquals(0.7, mixed[i])
        }
    }

    @Test
    @DisplayName("Sums exceeding 1.0 are normalized to peak at exactly 1.0")
    fun testNormalizesLoudMix(){
        // 1.0 + 1.0 = 2.0, which is above 1.0 so it will be normalized to 1.0
        val song = Song(100, 4, 60, listOf(
            channelOf(ConstantStrategy(1.0), 1.0),
            channelOf(ConstantStrategy(1.0), 1.0)
        ))
        val mixed = song.combineChannels()
        for(i in mixed.indices){
            assertEquals(1.0, mixed[i])
        }
    }

    @Test
    @DisplayName("Negative peaks are also normalized by magnitude (Absolute Value")
    fun testNormalizesByAbsoluteValue(){
        // a peak of -2.0 will normalize to -1.0 not 1.0
        val song = Song(100, 4, 60, listOf(
            channelOf(ConstantStrategy(-1.0), 1.0),
            channelOf(ConstantStrategy(-1.0), 1.0)
        ))
        val mixed = song.combineChannels()
        for (i in mixed.indices){
            assertEquals(-1.0, mixed[i])
        }
    }

    @Test
    @DisplayName("Shorter channels pad with 0.0s to longest channels length")
    fun testShorterChannelPad(){
        val song = Song(100, 4, 60, listOf(
            channelOf(ConstantStrategy(0.4), 2.0), // longer channel
            channelOf(ConstantStrategy(0.4), 1.0) // shorter channel
        ))
        val mixed = song.combineChannels()
        for (i in 0..99){
            assertEquals(0.8, mixed[i]) // first 100 notes will be combined, but the next 100 will only be the first channels so 0.4
        }
        for (i in 100..199){
            assertEquals(0.4, mixed[i])
        }
    }

    @Test
    @DisplayName("All silent input produces silence, no errors or NaN")
    fun testSilence(){
        val song = Song(100, 4, 60, listOf(
            channelOf(ConstantStrategy(0.0), 1.0),
        ))
        val mixed = song.combineChannels()
        for(i in mixed.indices){
            assertTrue(!mixed[i].isNaN())
            assertEquals(0.0, mixed[i])
        }
    }
}